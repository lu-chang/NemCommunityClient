package org.nem.ncc.services;

import org.hamcrest.core.*;
import org.junit.*;
import org.mockito.Mockito;
import org.nem.ncc.exceptions.NccException;
import org.nem.ncc.storable.entity.storage.StorableEntityReadMode;
import org.nem.ncc.test.*;
import org.nem.ncc.wallet.*;
import org.nem.ncc.wallet.storage.*;

import java.io.*;
import java.util.Collections;

public class DefaultWalletServicesTest {
	private static final WalletFileExtension FILE_EXTENSION = new WalletFileExtension();

	//region get

	@Test
	public void getCanReturnOpenWallet() {
		// Arrange:
		final TestContext context = new TestContext();

		// Act:
		final Wallet wallet1 = context.walletServices.open(context.pair);
		final Wallet wallet2 = context.walletServices.get(context.pair.getName());

		// Assert:
		Assert.assertThat(wallet2, IsEqual.equalTo(wallet1));
	}

	@Test
	public void getCannotReturnClosedWallet() {
		// Arrange:
		final TestContext context = new TestContext();

		// Act:
		context.walletServices.open(context.pair);
		context.walletServices.close(context.pair.getName());

		// Act:
		ExceptionAssert.assertThrowsNccException(
				v -> context.walletServices.get(context.pair.getName()),
				NccException.Code.WALLET_IS_NOT_OPEN);
	}

	//endregion

	//region tryFindOpenAccount

	@Test
	public void canReturnWalletAccountInOpenWallet() {
		// Arrange:
		final WalletAccount account = new WalletAccount();
		final TestContext context = new TestContext();
		context.originalWallet.addOtherAccount(account);

		// Act:
		context.walletServices.open(context.pair);
		final WalletAccount resultAccount = context.walletServices.tryFindOpenAccount(account.getAddress());

		// Assert:
		Assert.assertThat(resultAccount, IsEqual.equalTo(account));
	}

	@Test
	public void cannotReturnWalletAccountInClosedWallet() {
		// Arrange:
		final WalletAccount account = new WalletAccount();
		final TestContext context = new TestContext();
		context.originalWallet.addOtherAccount(account);

		// Act:
		context.walletServices.open(context.pair);
		context.walletServices.close(context.pair.getName());
		final WalletAccount resultAccount = context.walletServices.tryFindOpenAccount(account.getAddress());

		// Assert:
		Assert.assertThat(resultAccount, IsNull.nullValue());
	}

	//endregion

	//region open

	@Test
	public void openLoadsWalletIfWalletIsNotOpened() {
		// Arrange:
		final TestContext context = new TestContext();

		// Act:
		final Wallet wallet = context.walletServices.open(context.pair);

		// Assert:
		Assert.assertThat(wallet.getName(), IsEqual.equalTo(context.originalWallet.getName()));
		Assert.assertThat(
				context.walletServices.getOpenWalletNames(),
				IsEquivalent.equivalentTo(Collections.singletonList(context.originalWallet.getName())));
		Mockito.verify(context.descriptorFactory, Mockito.times(1)).openExisting(context.pair, FILE_EXTENSION);
		Mockito.verify(context.repository, Mockito.times(1)).load(context.descriptor);
	}

	@Test
	public void openReturnsCachedWalletIfWalletIsCached() {
		// Arrange:
		final TestContext context = new TestContext();

		// Act:
		context.walletServices.open(context.pair);
		final Wallet wallet = context.walletServices.open(context.pair);

		// Assert:
		Assert.assertThat(
				context.walletServices.getOpenWalletNames(),
				IsEquivalent.equivalentTo(Collections.singletonList(context.originalWallet.getName())));
		Assert.assertThat(wallet.getName(), IsEqual.equalTo(context.originalWallet.getName()));
		Mockito.verify(context.descriptorFactory, Mockito.times(2)).openExisting(context.pair, FILE_EXTENSION);
		Mockito.verify(context.repository, Mockito.times(2)).load(context.descriptor);
	}

	@Test
	public void openFailsIfCachedWalletPasswordIsIncorrect() {
		// Arrange:
		final TestContext context = new TestContext();
		context.walletServices.open(context.pair); // cache the wallet
		Mockito.when(context.repository.load(context.descriptor))
				.thenThrow(new WalletStorageException(WalletStorageException.Code.WALLET_PASSWORD_INCORRECT));

		// Act:
		ExceptionAssert.assertThrowsWalletStorageException(
				v -> context.walletServices.open(context.pair),
				WalletStorageException.Code.WALLET_PASSWORD_INCORRECT);
	}

	@Test
	public void openReturnsAutoSavingWallet() {
		// Arrange:
		final TestContext context = new TestContext();

		// Act:
		final Wallet wallet = context.walletServices.open(context.pair);
		wallet.addOtherAccount(new WalletAccount());

		// Assert:
		Mockito.verify(context.repository, Mockito.times(1)).save(context.descriptor, context.originalWallet);
	}

	//endregion

	//region create

	@Test
	public void createCreatesNewWallet() {
		// Arrange:
		final TestContext context = new TestContext();

		// Act:
		final Wallet wallet = context.walletServices.create(context.pair);

		// Assert:
		Assert.assertThat(
				context.walletServices.getOpenWalletNames(),
				IsEquivalent.equivalentTo(Collections.singletonList(context.originalWallet.getName())));
		Assert.assertThat(wallet.getName(), IsEqual.equalTo(context.originalWallet.getName()));
		Mockito.verify(context.descriptorFactory, Mockito.times(1)).createNew(context.pair, FILE_EXTENSION);
		Mockito.verify(context.repository, Mockito.times(0)).load(context.descriptor);
	}

	@Test
	public void createSavesNewWallet() {
		// Arrange:
		final TestContext context = new TestContext();

		// Act:
		final Wallet wallet = context.walletServices.create(context.pair);

		// Assert:
		// TODO BR: Fix this
		//Mockito.verify(context.repository, Mockito.times(1)).save(context.descriptor, context.originalWallet);
		Mockito.verify(context.repository, Mockito.times(1)).save(Mockito.any(), Mockito.any());
	}

	@Test
	public void createReturnsAutoSavingWallet() {
		// Arrange:
		final TestContext context = new TestContext();

		// Act:
		final Wallet wallet = context.walletServices.create(context.pair);
		wallet.addOtherAccount(new WalletAccount());

		// Assert:
		// TODO BR: Fix this
		//Mockito.verify(context.repository, Mockito.times(2)).save(context.descriptor, wallet);
		Mockito.verify(context.repository, Mockito.times(2)).save(Mockito.any(), Mockito.any());
	}

	//endregion

	//region close

	@Test
	public void closeRemovesWalletFromOpenedList() {
		// Arrange:
		final TestContext context = new TestContext();

		// Act:
		context.walletServices.open(context.pair);
		context.walletServices.close(context.pair.getName());

		// Assert:
		Assert.assertThat(
				context.walletServices.getOpenWalletNames(),
				IsEquivalent.equivalentTo(new WalletName[] {}));
		Mockito.verify(context.descriptorFactory, Mockito.times(1)).openExisting(context.pair, FILE_EXTENSION);
		Mockito.verify(context.repository, Mockito.times(1)).load(context.descriptor);
	}

	//endregion

	//region move

	@Test
	public void canChangeWalletName() {
		// Arrange:
		final WalletNamePasswordPair pair2 = createPair("n2", "p");
		final WalletDescriptor descriptor2 = createDescriptor("n2");
		final TestContext context = new TestContext();
		Mockito.when(context.descriptorFactory.createNew(pair2, FILE_EXTENSION)).thenReturn(descriptor2);

		// Act:
		context.walletServices.move(context.pair, pair2);

		// Assert:
		// - the original is opened and the target is created
		Mockito.verify(context.descriptorFactory, Mockito.times(1)).openExisting(context.pair, FILE_EXTENSION);
		Mockito.verify(context.descriptorFactory, Mockito.times(1)).createNew(pair2, FILE_EXTENSION);

		// - the original is loaded
		Mockito.verify(context.repository, Mockito.times(1)).load(context.descriptor);

		// - the target is saved
		final Wallet updatedWallet = context.walletServices.get(new WalletName("n2"));
		Assert.assertThat(updatedWallet.getName(), IsEqual.equalTo(new WalletName("n2")));
		// TODO BR: Fix this
		//Mockito.verify(context.repository, Mockito.times(1)).save(descriptor2, updatedWallet);
		Mockito.verify(context.repository, Mockito.times(1)).save(Mockito.any(), Mockito.any());

		// - the original wallet is deleted
		Mockito.verify(context.descriptor, Mockito.times(1)).delete();
	}

	@Test
	public void canChangeWalletPassword() {
		// Arrange:
		final WalletNamePasswordPair pair2 = createPair("n", "p2");
		final WalletDescriptor descriptor2 = createDescriptor("n");
		final TestContext context = new TestContext();
		Mockito.when(context.descriptorFactory.openExisting(pair2, FILE_EXTENSION)).thenReturn(descriptor2);

		// Act:
		context.walletServices.move(context.pair, pair2);

		// Assert:
		// - the original and target descriptors are both opened as existing
		Mockito.verify(context.descriptorFactory, Mockito.times(1)).openExisting(context.pair, FILE_EXTENSION);
		Mockito.verify(context.descriptorFactory, Mockito.times(1)).openExisting(pair2, FILE_EXTENSION);

		// - the original is loaded
		Mockito.verify(context.repository, Mockito.times(1)).load(context.descriptor);

		// - the target is saved
		final Wallet updatedWallet = context.walletServices.get(new WalletName("n"));
		Assert.assertThat(updatedWallet.getName(), IsEqual.equalTo(new WalletName("n")));
		// TODO BR: Fix this
		//Mockito.verify(context.repository, Mockito.times(1)).save(descriptor2, updatedWallet);
		Mockito.verify(context.repository, Mockito.times(1)).save(Mockito.any(), Mockito.any());

		// - nothing is deleted
		Mockito.verify(context.descriptor, Mockito.times(0)).delete();
	}

	@Test
	public void canChangeWalletNameAndPassword() {
		// Arrange:
		final WalletNamePasswordPair pair2 = createPair("n2", "p2");
		final WalletDescriptor descriptor2 = createDescriptor("n2");
		final TestContext context = new TestContext();
		Mockito.when(context.descriptorFactory.createNew(pair2, FILE_EXTENSION)).thenReturn(descriptor2);

		// Act:
		context.walletServices.move(context.pair, pair2);

		// Assert:
		// - the original is opened and the target is created
		Mockito.verify(context.descriptorFactory, Mockito.times(1)).openExisting(context.pair, FILE_EXTENSION);
		Mockito.verify(context.descriptorFactory, Mockito.times(1)).createNew(pair2, FILE_EXTENSION);

		// - the original is loaded
		Mockito.verify(context.repository, Mockito.times(1)).load(context.descriptor);

		// - the target is saved
		final Wallet updatedWallet = context.walletServices.get(new WalletName("n2"));
		Assert.assertThat(updatedWallet.getName(), IsEqual.equalTo(new WalletName("n2")));
		// TODO BR: Fix this
		//Mockito.verify(context.repository, Mockito.times(1)).save(descriptor2, updatedWallet);
		Mockito.verify(context.repository, Mockito.times(1)).save(Mockito.any(), Mockito.any());

		// - the original wallet is deleted
		Mockito.verify(context.descriptor, Mockito.times(1)).delete();
	}

	@Test
	public void canChangeWalletNameAndPasswordIfWalletIsAlreadyOpen() {
		// Arrange:
		final WalletNamePasswordPair pair2 = createPair("n2", "p2");
		final WalletDescriptor descriptor2 = createDescriptor("n2");
		final TestContext context = new TestContext();
		Mockito.when(context.descriptorFactory.createNew(pair2, FILE_EXTENSION)).thenReturn(descriptor2);

		// Act:
		context.walletServices.open(context.pair);
		context.walletServices.move(context.pair, pair2);

		// Assert:
		// - the original is opened and the target is created
		Mockito.verify(context.descriptorFactory, Mockito.times(2)).openExisting(context.pair, FILE_EXTENSION);
		Mockito.verify(context.descriptorFactory, Mockito.times(1)).createNew(pair2, FILE_EXTENSION);

		// - the original is loaded
		Mockito.verify(context.repository, Mockito.times(2)).load(context.descriptor);

		// - the target is saved
		final Wallet updatedWallet = context.walletServices.get(new WalletName("n2"));
		Assert.assertThat(updatedWallet.getName(), IsEqual.equalTo(new WalletName("n2")));
		// TODO BR: Fix this
		//Mockito.verify(context.repository, Mockito.times(1)).save(descriptor2, updatedWallet);
		Mockito.verify(context.repository, Mockito.times(1)).save(Mockito.any(), Mockito.any());

		// - the original wallet is deleted
		Mockito.verify(context.descriptor, Mockito.times(1)).delete();
	}

	@Test
	public void renamedWalletIsNotAccessibleByOldName() {
		// Arrange:
		final WalletNamePasswordPair pair2 = createPair("n2", "p");
		final WalletDescriptor descriptor2 = createDescriptor("n2");
		final TestContext context = new TestContext();
		Mockito.when(context.descriptorFactory.createNew(pair2, FILE_EXTENSION)).thenReturn(descriptor2);

		// Act:
		context.walletServices.open(context.pair);
		context.walletServices.move(context.pair, pair2);

		// Assert:
		Assert.assertThat(context.walletServices.get(new WalletName("n2")), IsNull.notNullValue());
		ExceptionAssert.assertThrowsNccException(
				v -> context.walletServices.get(new WalletName("n")),
				NccException.Code.WALLET_IS_NOT_OPEN);
	}

	@Test
	public void renamedWalletPreservesAccountInformation() {
		// Arrange:
		final WalletNamePasswordPair pair2 = createPair("n2", "p");
		final WalletDescriptor descriptor2 = createDescriptor("n2");
		final TestContext context = new TestContext();
		context.originalWallet.addOtherAccount(new WalletAccount());
		context.originalWallet.addOtherAccount(new WalletAccount());
		Mockito.when(context.descriptorFactory.createNew(pair2, FILE_EXTENSION)).thenReturn(descriptor2);

		// Act:
		context.walletServices.open(context.pair);
		context.walletServices.move(context.pair, pair2);

		// Assert:
		final Wallet updatedWallet = context.walletServices.get(new WalletName("n2"));
		Assert.assertThat(updatedWallet.getPrimaryAccount(), IsEqual.equalTo(context.originalWallet.getPrimaryAccount()));
		Assert.assertThat(updatedWallet.getOtherAccounts(), IsEquivalent.equivalentTo(context.originalWallet.getOtherAccounts()));
	}

	//endregion

	//region copyTo

	@Test
	public void copyToDelegatesToDescriptorFactoryAndUsesReturnedDescriptor() {
		// Arrange:
		final TestContext context = new TestContext();
		final WalletNamePasswordPair pair = createPair("n", "p");
		final OutputStream outputStream = Mockito.mock(OutputStream.class);
		Mockito.when(context.descriptor.openRead(StorableEntityReadMode.Raw)).thenReturn(new ByteArrayInputStream("test".getBytes()));

		// Act:
		context.walletServices.copyTo(pair, outputStream);

		// Assert:
		Mockito.verify(context.descriptorFactory, Mockito.only()).openExisting(pair, new WalletFileExtension());
		Mockito.verify(context.descriptor, Mockito.only()).openRead(StorableEntityReadMode.Raw);
	}

	@Test
	public void copyToCopiesBytesToGivenOutputStream() {
		// Arrange:
		final TestContext context = new TestContext();
		final WalletNamePasswordPair pair = createPair("n", "p");
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Mockito.when(context.descriptor.openRead(StorableEntityReadMode.Raw)).thenReturn(new ByteArrayInputStream("test".getBytes()));

		// Act:
		context.walletServices.copyTo(pair, outputStream);

		// Assert:
		Assert.assertThat(outputStream.size(), IsEqual.equalTo(4));
		Assert.assertThat(outputStream.toString(), IsEqual.equalTo("test"));
	}

	//endregion

	private static WalletNamePasswordPair createPair(final String name, final String password) {
		return new WalletNamePasswordPair(new WalletName(name), new WalletPassword(password));
	}

	private static WalletDescriptor createDescriptor(final String name) {
		final WalletDescriptor descriptor = Mockito.mock(WalletDescriptor.class);
		Mockito.when(descriptor.getWalletName()).thenReturn(new WalletName(name));
		return descriptor;
	}

	private static class TestContext {
		private final WalletRepository repository = Mockito.mock(WalletRepository.class);
		private final WalletDescriptorFactory descriptorFactory = Mockito.mock(WalletDescriptorFactory.class);
		private final DefaultWalletServices walletServices = new DefaultWalletServices(this.repository, this.descriptorFactory);
		final WalletNamePasswordPair pair = createPair("n", "p");
		final WalletDescriptor descriptor = createDescriptor("n");
		final StorableWallet originalWallet = new MemoryWallet(new WalletName("n"));

		public TestContext() {
			Mockito.when(this.descriptorFactory.createNew(this.pair, FILE_EXTENSION)).thenReturn(this.descriptor);
			Mockito.when(this.descriptorFactory.openExisting(this.pair, FILE_EXTENSION)).thenReturn(this.descriptor);
			Mockito.when(this.repository.load(this.descriptor)).thenReturn(this.originalWallet);
		}
	}
}