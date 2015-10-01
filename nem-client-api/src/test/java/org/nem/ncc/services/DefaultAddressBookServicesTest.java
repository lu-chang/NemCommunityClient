package org.nem.ncc.services;

import org.hamcrest.core.*;
import org.junit.*;
import org.mockito.Mockito;
import org.nem.ncc.addressbook.*;
import org.nem.ncc.addressbook.storage.*;
import org.nem.ncc.exceptions.NccException;
import org.nem.ncc.storable.entity.storage.*;
import org.nem.ncc.test.*;

import java.io.*;
import java.util.*;

public class DefaultAddressBookServicesTest {
	private static final AddressBookFileExtension FILE_EXTENSION = new AddressBookFileExtension();

	//region get

	@Test
	public void getCanReturnOpenAddressBook() {
		// Arrange:
		final TestContext context = new TestContext();

		// Act:
		final AddressBook addressBook1 = context.addressBookServices.open(context.pair);
		final AddressBook addressBook2 = context.addressBookServices.get(context.pair.getName());

		// Assert:
		Assert.assertThat(addressBook2, IsEqual.equalTo(addressBook1));
	}

	@Test
	public void getCannotReturnClosedAddressBook() {
		// Arrange:
		final TestContext context = new TestContext();

		// Act:
		context.addressBookServices.open(context.pair);
		context.addressBookServices.close(context.pair.getName());

		// Act:
		ExceptionAssert.assertThrowsNccException(
				v -> context.addressBookServices.get(context.pair.getName()),
				NccException.Code.ADDRESS_BOOK_IS_NOT_OPEN);
	}

	//endregion

	//region open

	@Test
	public void openLoadsAddressBookIfAddressBookIsNotOpened() {
		// Arrange:
		final TestContext context = new TestContext();

		// Act:
		final AddressBook addressBook = context.addressBookServices.open(context.pair);

		// Assert:
		Assert.assertThat(addressBook.getName(), IsEqual.equalTo(context.originalAddressBook.getName()));
		Assert.assertThat(
				context.addressBookServices.getOpenAddressBookNames(),
				IsEquivalent.equivalentTo(Collections.singletonList(context.originalAddressBook.getName())));
		Mockito.verify(context.descriptorFactory, Mockito.times(1)).openExisting(context.pair, FILE_EXTENSION);
		Mockito.verify(context.repository, Mockito.times(1)).load(context.descriptor);
	}

	@Test
	public void openReturnsCachedAddressBookIfAddressBookIsCached() {
		// Arrange:
		final TestContext context = new TestContext();

		// Act:
		context.addressBookServices.open(context.pair);
		final AddressBook addressBook = context.addressBookServices.open(context.pair);

		// Assert:
		Assert.assertThat(
				context.addressBookServices.getOpenAddressBookNames(),
				IsEquivalent.equivalentTo(Collections.singletonList(context.originalAddressBook.getName())));
		Assert.assertThat(addressBook.getName(), IsEqual.equalTo(context.originalAddressBook.getName()));
		Mockito.verify(context.descriptorFactory, Mockito.times(2)).openExisting(context.pair, FILE_EXTENSION);
		Mockito.verify(context.repository, Mockito.times(2)).load(context.descriptor);
	}

	@Test
	public void openFailsIfCachedAddressBookPasswordIsIncorrect() {
		// Arrange:
		final TestContext context = new TestContext();
		context.addressBookServices.open(context.pair); // cache the addressBook
		Mockito.when(context.repository.load(context.descriptor))
				.thenThrow(new StorableEntityStorageException(StorableEntityStorageException.Code.STORABLE_ENTITY_PASSWORD_INCORRECT));

		// Act:
		ExceptionAssert.assertThrowsStorableEntityStorageException(
				v -> context.addressBookServices.open(context.pair),
				StorableEntityStorageException.Code.STORABLE_ENTITY_PASSWORD_INCORRECT);
	}

	@Test
	public void openReturnsAutoSavingAddressBook() {
		// Arrange:
		final TestContext context = new TestContext();

		// Act:
		final AddressBook addressBook = context.addressBookServices.open(context.pair);
		addressBook.addLabel(new AccountLabel(Utils.generateRandomAddress(), "foo", "bar"));

		// Assert:
		Mockito.verify(context.repository, Mockito.times(1)).save(context.descriptor, context.originalAddressBook);
	}

	//endregion

	//region create

	@Test
	public void createCreatesNewAddressBook() {
		// Arrange:
		final TestContext context = new TestContext();

		// Act:
		final AddressBook addressBook = context.addressBookServices.create(context.pair);

		// Assert:
		Assert.assertThat(
				context.addressBookServices.getOpenAddressBookNames(),
				IsEquivalent.equivalentTo(Collections.singletonList(context.originalAddressBook.getName())));
		Assert.assertThat(addressBook.getName(), IsEqual.equalTo(context.originalAddressBook.getName()));
		Mockito.verify(context.descriptorFactory, Mockito.times(1)).createNew(context.pair, FILE_EXTENSION);
		Mockito.verify(context.repository, Mockito.times(0)).load(context.descriptor);
	}

	@Test
	public void createSavesNewAddressBook() {
		// Arrange:
		final TestContext context = new TestContext();

		// Act:
		context.addressBookServices.create(context.pair);

		// Assert:
		// TODO BR: Fix this
		//Mockito.verify(context.repository, Mockito.times(1)).save(context.descriptor, context.originalAddressBook);
		Mockito.verify(context.repository, Mockito.times(1)).save(Mockito.any(), Mockito.any());
	}

	@Test
	public void createReturnsAutoSavingAddressBook() {
		// Arrange:
		final TestContext context = new TestContext();

		// Act:
		final AddressBook addressBook = context.addressBookServices.create(context.pair);
		addressBook.addLabel(new AccountLabel(Utils.generateRandomAddress(), "foo", "bar"));

		// Assert:
		// TODO BR: Fix this
		//Mockito.verify(context.repository, Mockito.times(2)).save(context.descriptor, addressBook);
		Mockito.verify(context.repository, Mockito.times(2)).save(Mockito.any(), Mockito.any());
	}

	//endregion

	//region close

	@Test
	public void closeRemovesAddressBookFromOpenedList() {
		// Arrange:
		final TestContext context = new TestContext();

		// Act:
		context.addressBookServices.open(context.pair);
		context.addressBookServices.close(context.pair.getName());

		// Assert:
		Assert.assertThat(
				context.addressBookServices.getOpenAddressBookNames(),
				IsEquivalent.equivalentTo(new AddressBookName[] {}));
		Mockito.verify(context.descriptorFactory, Mockito.times(1)).openExisting(context.pair, FILE_EXTENSION);
		Mockito.verify(context.repository, Mockito.times(1)).load(context.descriptor);
	}

	//endregion

	//region move

	@Test
	public void canChangeAddressBookName() {
		// Arrange:
		final AddressBookNamePasswordPair pair2 = createPair("n2", "p");
		final AddressBookDescriptor descriptor2 = createDescriptor("n2");
		final TestContext context = new TestContext();
		Mockito.when(context.descriptorFactory.createNew(pair2, FILE_EXTENSION)).thenReturn(descriptor2);

		// Act:
		context.addressBookServices.move(context.pair, pair2);

		// Assert:
		// - the original is opened and the target is created
		Mockito.verify(context.descriptorFactory, Mockito.times(1)).openExisting(context.pair, FILE_EXTENSION);
		Mockito.verify(context.descriptorFactory, Mockito.times(1)).createNew(pair2, FILE_EXTENSION);

		// - the original is loaded
		Mockito.verify(context.repository, Mockito.times(1)).load(context.descriptor);

		// - the target is saved
		final AddressBook updatedAddressBook = context.addressBookServices.get(new AddressBookName("n2"));
		Assert.assertThat(updatedAddressBook.getName(), IsEqual.equalTo(new AddressBookName("n2")));
		// TODO BR: Fix this
		//Mockito.verify(context.repository, Mockito.times(1)).save(descriptor2, updatedAddressBook);
		Mockito.verify(context.repository, Mockito.times(1)).save(Mockito.any(), Mockito.any());
		Mockito.verify(context.repository, Mockito.times(1)).save(Mockito.any(), Mockito.any());

		// - the original addressBook is deleted
		Mockito.verify(context.descriptor, Mockito.times(1)).delete();
	}

	@Test
	public void canChangeAddressBookPassword() {
		// Arrange:
		final AddressBookNamePasswordPair pair2 = createPair("n", "p2");
		final AddressBookDescriptor descriptor2 = createDescriptor("n");
		final TestContext context = new TestContext();
		Mockito.when(context.descriptorFactory.openExisting(pair2, FILE_EXTENSION)).thenReturn(descriptor2);

		// Act:
		context.addressBookServices.move(context.pair, pair2);

		// Assert:
		// - the original and target descriptors are both opened as existing
		Mockito.verify(context.descriptorFactory, Mockito.times(1)).openExisting(context.pair, FILE_EXTENSION);
		Mockito.verify(context.descriptorFactory, Mockito.times(1)).openExisting(pair2, FILE_EXTENSION);

		// - the original is loaded
		Mockito.verify(context.repository, Mockito.times(1)).load(context.descriptor);

		// - the target is saved
		final AddressBook updatedAddressBook = context.addressBookServices.get(new AddressBookName("n"));
		Assert.assertThat(updatedAddressBook.getName(), IsEqual.equalTo(new AddressBookName("n")));
		// TODO BR: Fix this
		//Mockito.verify(context.repository, Mockito.times(1)).save(descriptor2, updatedAddressBook);
		Mockito.verify(context.repository, Mockito.times(1)).save(Mockito.any(), Mockito.any());
		Mockito.verify(context.repository, Mockito.times(1)).save(Mockito.any(), Mockito.any());

		// - nothing is deleted
		Mockito.verify(context.descriptor, Mockito.times(0)).delete();
	}

	@Test
	public void canChangeAddressBookNameAndPassword() {
		// Arrange:
		final AddressBookNamePasswordPair pair2 = createPair("n2", "p2");
		final AddressBookDescriptor descriptor2 = createDescriptor("n2");
		final TestContext context = new TestContext();
		Mockito.when(context.descriptorFactory.createNew(pair2, FILE_EXTENSION)).thenReturn(descriptor2);

		// Act:
		context.addressBookServices.move(context.pair, pair2);

		// Assert:
		// - the original is opened and the target is created
		Mockito.verify(context.descriptorFactory, Mockito.times(1)).openExisting(context.pair, FILE_EXTENSION);
		Mockito.verify(context.descriptorFactory, Mockito.times(1)).createNew(pair2, FILE_EXTENSION);

		// - the original is loaded
		Mockito.verify(context.repository, Mockito.times(1)).load(context.descriptor);

		// - the target is saved
		final AddressBook updatedAddressBook = context.addressBookServices.get(new AddressBookName("n2"));
		Assert.assertThat(updatedAddressBook.getName(), IsEqual.equalTo(new AddressBookName("n2")));
		// TODO BR: Fix this
		//Mockito.verify(context.repository, Mockito.times(1)).save(descriptor2, updatedAddressBook);
		Mockito.verify(context.repository, Mockito.times(1)).save(Mockito.any(), Mockito.any());
		Mockito.verify(context.repository, Mockito.times(1)).save(Mockito.any(), Mockito.any());

		// - the original addressBook is deleted
		Mockito.verify(context.descriptor, Mockito.times(1)).delete();
	}

	@Test
	public void canChangeAddressBookNameAndPasswordIfAddressBookIsAlreadyOpen() {
		// Arrange:
		final AddressBookNamePasswordPair pair2 = createPair("n2", "p2");
		final AddressBookDescriptor descriptor2 = createDescriptor("n2");
		final TestContext context = new TestContext();
		Mockito.when(context.descriptorFactory.createNew(pair2, FILE_EXTENSION)).thenReturn(descriptor2);

		// Act:
		context.addressBookServices.open(context.pair);
		context.addressBookServices.move(context.pair, pair2);

		// Assert:
		// - the original is opened and the target is created
		Mockito.verify(context.descriptorFactory, Mockito.times(2)).openExisting(context.pair, FILE_EXTENSION);
		Mockito.verify(context.descriptorFactory, Mockito.times(1)).createNew(pair2, FILE_EXTENSION);

		// - the original is loaded
		Mockito.verify(context.repository, Mockito.times(2)).load(context.descriptor);

		// - the target is saved
		final AddressBook updatedAddressBook = context.addressBookServices.get(new AddressBookName("n2"));
		Assert.assertThat(updatedAddressBook.getName(), IsEqual.equalTo(new AddressBookName("n2")));
		// TODO BR: Fix this
		//Mockito.verify(context.repository, Mockito.times(1)).save(descriptor2, updatedAddressBook);
		Mockito.verify(context.repository, Mockito.times(1)).save(Mockito.any(), Mockito.any());
		Mockito.verify(context.repository, Mockito.times(1)).save(Mockito.any(), Mockito.any());

		// - the original addressBook is deleted
		Mockito.verify(context.descriptor, Mockito.times(1)).delete();
	}

	@Test
	public void renamedAddressBookIsNotAccessibleByOldName() {
		// Arrange:
		final AddressBookNamePasswordPair pair2 = createPair("n2", "p");
		final AddressBookDescriptor descriptor2 = createDescriptor("n2");
		final TestContext context = new TestContext();
		Mockito.when(context.descriptorFactory.createNew(pair2, FILE_EXTENSION)).thenReturn(descriptor2);

		// Act:
		context.addressBookServices.open(context.pair);
		context.addressBookServices.move(context.pair, pair2);

		// Assert:
		Assert.assertThat(context.addressBookServices.get(new AddressBookName("n2")), IsNull.notNullValue());
		ExceptionAssert.assertThrowsNccException(
				v -> context.addressBookServices.get(new AddressBookName("n")),
				NccException.Code.ADDRESS_BOOK_IS_NOT_OPEN);
	}

	@Test
	public void renamedAddressBookPreservesAccountInformation() {
		// Arrange:
		final AddressBookNamePasswordPair pair2 = createPair("n2", "p");
		final AddressBookDescriptor descriptor2 = createDescriptor("n2");
		final List<AccountLabel> acccountLabels = this.createAccountLabels(3);
		final TestContext context = new TestContext();
		acccountLabels.forEach(context.originalAddressBook::addLabel);
		Mockito.when(context.descriptorFactory.createNew(pair2, FILE_EXTENSION)).thenReturn(descriptor2);

		// Act:
		context.addressBookServices.open(context.pair);
		context.addressBookServices.move(context.pair, pair2);

		// Assert:
		final AddressBook updatedAddressBook = context.addressBookServices.get(new AddressBookName("n2"));
		Assert.assertThat(updatedAddressBook.getAccountLabels(), IsEquivalent.equivalentTo(context.originalAddressBook.getAccountLabels()));
	}

	//endregion

	//region copyTo

	@Test
	public void copyToDelegatesToDescriptorFactoryAndUsesReturnedDescriptor() {
		// Arrange:
		final TestContext context = new TestContext();
		final AddressBookNamePasswordPair pair = createPair("n", "p");
		final OutputStream outputStream = Mockito.mock(OutputStream.class);
		Mockito.when(context.descriptor.openRead(StorableEntityReadMode.Raw)).thenReturn(new ByteArrayInputStream("test".getBytes()));

		// Act:
		context.addressBookServices.copyTo(pair, outputStream);

		// Assert:
		Mockito.verify(context.descriptorFactory, Mockito.only()).openExisting(pair, new AddressBookFileExtension());
		Mockito.verify(context.descriptor, Mockito.only()).openRead(StorableEntityReadMode.Raw);
	}

	@Test
	public void copyToCopiesBytesToGivenOutputStream() {
		// Arrange:
		final TestContext context = new TestContext();
		final AddressBookNamePasswordPair pair = createPair("n", "p");
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Mockito.when(context.descriptor.openRead(StorableEntityReadMode.Raw)).thenReturn(new ByteArrayInputStream("test".getBytes()));

		// Act:
		context.addressBookServices.copyTo(pair, outputStream);

		// Assert:
		Assert.assertThat(outputStream.size(), IsEqual.equalTo(4));
		Assert.assertThat(outputStream.toString(), IsEqual.equalTo("test"));
	}

	//endregion

	private List<AccountLabel> createAccountLabels(final int count) {
		final List<AccountLabel> accountLabels = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			accountLabels.add(new AccountLabel(
					Utils.generateRandomAddress(),
					String.format("publicLabel%d", i),
					String.format("privateLabel%d", i)));
		}

		return accountLabels;
	}

	private static AddressBookNamePasswordPair createPair(final String name, final String password) {
		return new AddressBookNamePasswordPair(new AddressBookName(name), new AddressBookPassword(password));
	}

	private static AddressBookDescriptor createDescriptor(final String name) {
		final AddressBookDescriptor descriptor = Mockito.mock(AddressBookDescriptor.class);
		Mockito.when(descriptor.getAddressBookName()).thenReturn(new AddressBookName(name));
		return descriptor;
	}

	private static class TestContext {
		private final AddressBookRepository repository = Mockito.mock(AddressBookRepository.class);
		private final AddressBookDescriptorFactory descriptorFactory = Mockito.mock(AddressBookDescriptorFactory.class);
		private final DefaultAddressBookServices addressBookServices = new DefaultAddressBookServices(this.repository, this.descriptorFactory);
		final AddressBookNamePasswordPair pair = createPair("n", "p");
		final AddressBookDescriptor descriptor = createDescriptor("n");
		final StorableAddressBook originalAddressBook = new MemoryAddressBook(new AddressBookName("n"));

		public TestContext() {
			Mockito.when(this.descriptorFactory.createNew(this.pair, FILE_EXTENSION)).thenReturn(this.descriptor);
			Mockito.when(this.descriptorFactory.openExisting(this.pair, FILE_EXTENSION)).thenReturn(this.descriptor);
			Mockito.when(this.repository.load(this.descriptor)).thenReturn(this.originalAddressBook);
		}
	}
}
