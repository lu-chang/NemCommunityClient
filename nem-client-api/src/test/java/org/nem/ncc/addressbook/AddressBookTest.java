package org.nem.ncc.addressbook;

import net.minidev.json.*;
import org.hamcrest.core.IsEqual;
import org.junit.*;
import org.nem.core.model.Address;
import org.nem.core.serialization.JsonSerializer;
import org.nem.ncc.test.*;

import java.util.*;
import java.util.function.Consumer;

public abstract class AddressBookTest {

	/**
	 * Creates a new address book with the specified name.
	 *
	 * @param name The address book name.
	 */
	protected abstract AddressBook createAddressBook(final AddressBookName name);

	/**
	 * Creates a new address book with the specified name and account labels.
	 *
	 * @param name The address book name.
	 * @param accountLabels The account labels.
	 */
	protected abstract AddressBook createAddressBook(final AddressBookName name, final Collection<AccountLabel> accountLabels);

	//region creation

	@Test
	public void addressBookCanBeCreatedAroundName() {
		// Act:
		final AddressBook addressBook = this.createAddressBook(new AddressBookName("bar"));

		// Assert:
		Assert.assertThat(addressBook.getName(), IsEqual.equalTo(new AddressBookName("bar")));
		Assert.assertThat(addressBook.size(), IsEqual.equalTo(0));
	}

	@Test
	public void addressBookCanBeCreatedAroundNameAndAccountsLabels() {
		// Act:
		final List<AccountLabel> accountLabels = this.createAccountLabels(3);
		final AddressBook addressBook = this.createAddressBook(new AddressBookName("bar"), accountLabels);

		// Assert:
		Assert.assertThat(addressBook.getName(), IsEqual.equalTo(new AddressBookName("bar")));
		Assert.assertThat(addressBook.getAccountLabels(), IsEquivalent.equivalentTo(accountLabels));
	}

	@Test
	public void addressBookCannotBeCreatedAroundInvalidAccountsLabels() {
		// Act:
		final List<AccountLabel> accountLabels = this.createAccountLabels(3);
		accountLabels.add(accountLabels.get(0));

		// Assert:
		assertThrowsAddressBookException(
				v -> this.createAddressBook(new AddressBookName("bar"), accountLabels),
				AddressBookException.Code.ADDRESS_BOOK_ALREADY_CONTAINS_ADDRESS);
	}

	@Test
	public void addressBookConstructorShallowCopiesSuppliedAccountLabelCollection() {
		// Act:
		final List<AccountLabel> accountLabels = this.createAccountLabels(3);
		final AddressBook addressBook = this.createAddressBook(new AddressBookName("bar"), accountLabels);
		accountLabels.add(accountLabels.get(0));

		// Assert:
		Assert.assertThat(addressBook.getAccountLabels().size(), IsEqual.equalTo(3));
		Assert.assertThat(accountLabels.size(), IsEqual.equalTo(4));
	}

	@Test
	public void addressBookCannotBeCreatedAroundNullParameters() {
		// Arrange:
		final List<Consumer<Void>> consumers = Arrays.asList(
				v -> this.createAddressBook(null),
				v -> this.createAddressBook(null, new ArrayList<>()),
				v -> this.createAddressBook(new AddressBookName("bar"), null));

		// Act + Assert:
		for (final Consumer<Void> consumer : consumers) {
			ExceptionAssert.assertThrows(consumer, IllegalArgumentException.class);
		}
	}

	//endregion

	//region getName

	@Test
	public void getNameReturnsAddressBookName() {
		// Arrange:
		final AddressBook addressBook = this.createAddressBook(new AddressBookName("bar"));

		// Assert:
		Assert.assertThat(addressBook.getName(), IsEqual.equalTo(new AddressBookName("bar")));
	}

	//endregion

	//region size

	@Test
	public void sizeReturnsAddressBookSize() {
		// Arrange:
		final List<AccountLabel> accountLabels = this.createAccountLabels(13);
		final AddressBook addressBook = this.createAddressBook(new AddressBookName("bar"), accountLabels);

		// Assert:
		Assert.assertThat(addressBook.size(), IsEqual.equalTo(13));
	}

	//endregion

	//region addLabel / getAccountLabels

	@Test
	public void nullLabelCannotBeAddedToAddressBook() {
		// Arrange:
		final AddressBook addressBook = this.createAddressBook(new AddressBookName("bar"));

		// Assert:
		ExceptionAssert.assertThrows(v -> addressBook.addLabel(null), IllegalArgumentException.class);
	}

	@Test
	public void existingLabelCannotBeAddedToAddressBook() {
		// Arrange:
		final List<AccountLabel> accountLabels = this.createAccountLabels(3);
		final AddressBook addressBook = this.createAddressBook(new AddressBookName("bar"), accountLabels);

		// Assert:
		ExceptionAssert.assertThrows(v -> addressBook.addLabel(accountLabels.get(0)), AddressBookException.class);
	}

	@Test
	public void newLabelsCanBeAddedToAddressBook() {
		// Arrange:
		final List<AccountLabel> accountLabels = this.createAccountLabels(3);
		final AddressBook addressBook = this.createAddressBook(new AddressBookName("bar"));

		// Act:
		accountLabels.forEach(addressBook::addLabel);

		// Assert:
		Assert.assertThat(addressBook.getAccountLabels().size(), IsEqual.equalTo(3));
		Assert.assertThat(addressBook.getAccountLabels(), IsEquivalent.equivalentTo(accountLabels));
	}

	@Test
	public void newLabelCannotBeAddedToAddressBookIfAddressIsInvalid() {
		// Arrange:
		final List<AccountLabel> accountLabels = Arrays.asList(
				new AccountLabel(Address.fromEncoded(Utils.generateRandomAccount().getAddress().getEncoded() + "badness"), "pub", "priv"), // too long
				new AccountLabel(Address.fromEncoded(""), "pub", "priv"), // empty
				new AccountLabel(Address.fromEncoded("QALICEBMLRCCYHAYBKUVAPLB2DMZIBZFFGIMJHSC"), "pub", "priv")); // invalid network
		final AddressBook addressBook = this.createAddressBook(new AddressBookName("bar"));

		// Assert:
		accountLabels.forEach(a -> assertThrowsAddressBookException(v -> addressBook.addLabel(a), AddressBookException.Code.ADDRESS_NOT_VALID));
	}

	@Test
	public void otherAccountsCannotBeModifiedDirectly() {
		// Act:
		final List<AccountLabel> accountLabels = this.createAccountLabels(3);
		final AddressBook addressBook = this.createAddressBook(new AddressBookName("bar"));
		accountLabels.forEach(l -> addressBook.getAccountLabels().add(l));

		// Assert:
		Assert.assertThat(addressBook.getAccountLabels().size(), IsEqual.equalTo(0));
	}

	//endregion

	//region removeLabel

	@Test
	public void canRemoveLabelInAddressBook() {
		// Arrange:
		final List<AccountLabel> accountLabels = this.createAccountLabels(3);
		final AddressBook addressBook = this.createAddressBook(new AddressBookName("bar"), accountLabels);

		// Act:
		addressBook.removeLabel(accountLabels.get(0).getAddress());
		addressBook.removeLabel(accountLabels.get(1).getAddress());

		// Assert:
		Assert.assertThat(addressBook.getAccountLabels().size(), IsEqual.equalTo(1));
		Assert.assertThat(addressBook.getAccountLabels(), IsEquivalent.equivalentTo(Collections.singletonList(accountLabels.get(2))));
	}

	@Test
	public void cannotRemoveLabelNotInAddressBook() {
		// Arrange:
		final List<AccountLabel> accountLabels = this.createAccountLabels(3);
		final AddressBook addressBook = this.createAddressBook(new AddressBookName("bar"), accountLabels);

		// Act + Assert:
		assertThrowsAddressBookException(
				v -> addressBook.removeLabel(Utils.generateRandomAddress()),
				AddressBookException.Code.ADDRESS_NOT_IN_ADDRESS_BOOK);
	}

	//endregion

	//region getLabel

	@Test
	public void canGetLabelInAddressBook() {
		// Arrange:
		final List<AccountLabel> accountLabels = this.createAccountLabels(3);
		final AddressBook addressBook = this.createAddressBook(new AddressBookName("bar"), accountLabels);

		// Act:
		final AccountLabel accountLabel = addressBook.getLabel(accountLabels.get(1).getAddress());

		// Assert:
		Assert.assertThat(accountLabel, IsEqual.equalTo(accountLabels.get(1)));
	}

	@Test
	public void cannotGetLabelNotInAddressBook() {
		// Arrange:
		final List<AccountLabel> accountLabels = this.createAccountLabels(3);
		final AddressBook addressBook = this.createAddressBook(new AddressBookName("bar"), accountLabels);

		// Act + Assert:
		assertThrowsAddressBookException(
				v -> addressBook.getLabel(Utils.generateRandomAddress()),
				AddressBookException.Code.ADDRESS_NOT_IN_ADDRESS_BOOK);
	}

	//endregion

	//region setLabel

	@Test
	public void canSetLabelInAddressBook() {
		// Arrange:
		final AddressBook addressBook = this.createAddressBook(new AddressBookName("bar"));
		final Address address = Utils.generateRandomAddress();
		addressBook.addLabel(new AccountLabel(address, "foo", "bar"));

		// Act:
		addressBook.setLabel(address, "foobar", "foobaz");

		// Assert:
		Assert.assertThat(addressBook.getLabel(address).getPublicLabel(), IsEqual.equalTo("foobar"));
		Assert.assertThat(addressBook.getLabel(address).getPrivateLabel(), IsEqual.equalTo("foobaz"));
	}

	@Test
	public void cannotSetLabelNotInAddressBook() {
		// Arrange:
		final List<AccountLabel> accountLabels = this.createAccountLabels(3);
		final AddressBook addressBook = this.createAddressBook(new AddressBookName("bar"), accountLabels);

		// Act + Assert:
		assertThrowsAddressBookException(
				v -> addressBook.setLabel(Utils.generateRandomAddress(), "foo", "bar"),
				AddressBookException.Code.ADDRESS_NOT_IN_ADDRESS_BOOK);
	}

	//endregion

	// region labelExists

	@Test
	public void labelExistsReturnTrueIfLabelExistsInAddressBook() {
		// Arrange:
		final AddressBook addressBook = this.createAddressBook(new AddressBookName("bar"));
		final Address address = Utils.generateRandomAddress();
		addressBook.addLabel(new AccountLabel(address, "foo", "bar"));

		// Assert:
		Assert.assertThat(addressBook.contains(address), IsEqual.equalTo(true));
	}

	@Test
	public void labelExistsReturnFalseIfLabelDoesNotExistInAddressBook() {
		// Arrange:
		final AddressBook addressBook = this.createAddressBook(new AddressBookName("bar"));
		final Address address = Utils.generateRandomAddress();

		// Assert:
		Assert.assertThat(addressBook.contains(address), IsEqual.equalTo(false));
	}

	// endregion

	//region serialization

	@Test
	public void addressBookCanBeSerialized() {
		// Arrange:
		final List<AccountLabel> accountLabels = this.createAccountLabels(3);
		final AddressBook addressBook = this.createAddressBook(new AddressBookName("bar"), accountLabels);

		// Act:
		final JSONObject jsonObject = JsonSerializer.serializeToJson(addressBook);

		// Assert:
		Assert.assertThat(jsonObject.size(), IsEqual.equalTo(2));
		Assert.assertThat(jsonObject.get("addressBook"), IsEqual.equalTo("bar"));
		Assert.assertThat(Utils.accountLabelsFromJson((JSONArray)jsonObject.get("accountLabels")), IsEquivalent.equivalentTo(accountLabels));
	}

	//endregion

	protected List<AccountLabel> createAccountLabels(final int count) {
		final List<AccountLabel> accountLabels = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			accountLabels.add(new AccountLabel(
					Utils.generateRandomAddress(),
					String.format("publicLabel%d", i),
					String.format("privateLabel%d", i)));
		}

		return accountLabels;
	}

	/**
	 * Asserts the specified action throws a AddressBookException with the expected code.
	 *
	 * @param consumer The action.
	 * @param code The expected code.
	 */
	protected static void assertThrowsAddressBookException(
			final Consumer<Void> consumer,
			final AddressBookException.Code code) {
		ExceptionAssert.assertThrows(
				consumer,
				AddressBookException.class,
				ex -> Assert.assertThat(ex.getCode(), IsEqual.equalTo(code)));
	}
}
