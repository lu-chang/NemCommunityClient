package org.nem.ncc.controller;

import org.nem.core.serialization.*;
import org.nem.core.utils.ExceptionUtils;
import org.nem.specific.deploy.OctetStream;
import org.nem.ncc.addressbook.*;
import org.nem.ncc.controller.requests.WalletNamePasswordBag;
import org.nem.ncc.controller.viewmodels.WalletViewModel;
import org.nem.ncc.services.*;
import org.nem.ncc.wallet.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.zip.*;

/**
 * Handles requests related to the REST resource "wallet".
 */
@RestController
public class WalletController {
	private final WalletServices walletServices;
	private final WalletMapper walletMapper;

	// TODO 20150228 J-B: doesn't really matter, but i probably would have done this as a WalletServices decorator
	// > if we ever consolidate the implementations of the two controllers, we would probably need to do it that way
	// TODO 20150228 J-B: actually, with these changes, i guess we can prune the AddressBookController?
	private final AddressBookServices addressBookServices;

	/**
	 * Creates a new wallet controller.
	 *
	 * @param walletServices The wallet services.
	 * @param walletMapper The wallet mapper.
	 * @param addressBookServices The address book services.
	 */
	@Autowired(required = true)
	public WalletController(
			final WalletServices walletServices,
			final WalletMapper walletMapper,
			final AddressBookServices addressBookServices) {
		this.walletServices = walletServices;
		this.walletMapper = walletMapper;
		this.addressBookServices = addressBookServices;
	}

	//region create / open / info / close

	/**
	 * Creates a new wallet with one account in it. The wallet is stored
	 * encrypted in the configured wallet directory. The name of the wallet file
	 * is the encoded (URL encoded) version of the wallet name. The provided
	 * password is used to encrypt the wallet.
	 * The request also creates the corresponding address book and adds the wallet's primary account to it.
	 *
	 * @param pair The wallet name and password pair.
	 * @return A view of the created wallet.
	 */
	@RequestMapping(value = "/wallet/create", method = RequestMethod.POST)
	public WalletViewModel create(@RequestBody final WalletNamePasswordPair pair) {
		final Wallet wallet = this.walletServices.create(pair);
		final AddressBook addressBook = this.createAddressBook(pair);
		addressBook.addLabel(new AccountLabel(wallet.getPrimaryAccount().getAddress(), "", ""));
		return this.walletMapper.toViewModel(wallet);
	}

	/**
	 * Open a wallet. The wallet is defined by the wallet name. The wallet has
	 * to be located in the wallet storage location. The password must match the
	 * password of the given wallet. For opening a wallet at a different
	 * location the function importWallet has to be used.
	 *
	 * @param pair The wallet name and password pair.
	 * @return A view of the opened wallet.
	 */
	@RequestMapping(value = "/wallet/open", method = RequestMethod.POST)
	public WalletViewModel open(@RequestBody final WalletNamePasswordPair pair) {
		this.openAddressBook(pair);
		final Wallet wallet = this.walletServices.open(pair);
		return this.walletMapper.toViewModel(wallet);
	}

	/**
	 * Returns information about a wallet that is already open.
	 *
	 * @param name The wallet name.
	 * @return A view of the wallet.
	 */
	@RequestMapping(value = "/wallet/info", method = RequestMethod.POST)
	public WalletViewModel info(@RequestBody final WalletName name) {
		final Wallet wallet = this.walletServices.get(name);
		return this.walletMapper.toViewModel(wallet);
	}

	/**
	 * Exports a wallet as a zip file.
	 *
	 * @param name The wallet name.
	 * @return The raw bytes.
	 */
	@RequestMapping(value = "/wallet/export", method = RequestMethod.POST)
	public OctetStream exportWallet(@RequestBody final WalletName name) {
		// TODO 20150312 J-G: a test would be nice, but i'm not expecting it ;)
		final Wallet wallet = this.walletServices.get(name);
		final AddressBook addressBook = this.addressBookServices.get(new AddressBookName(name.toString()));

		return ExceptionUtils.propagate(() -> {
			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			try (final ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
				addToZip(zipOutputStream, name, ".wlt", wallet);
				addToZip(zipOutputStream, name, ".adb", addressBook);
			}

			return new OctetStream(byteArrayOutputStream.toByteArray());
		});
	}

	private static void addToZip(
			final ZipOutputStream zipOutputStream,
			final WalletName name,
			final String extension,
			final SerializableEntity entity) throws IOException {
		final ZipEntry zipEntry = new ZipEntry(name.toString() + extension);

		final byte[] entityBytes = BinarySerializer.serializeToBytes(entity);
		zipOutputStream.putNextEntry(zipEntry);
		zipOutputStream.write(entityBytes);
		zipOutputStream.closeEntry();
	}

	/**
	 * Closes a wallet by removing it from the list of opened wallets.
	 *
	 * @param name The wallet name.
	 */
	@RequestMapping(value = "/wallet/close", method = RequestMethod.POST)
	public void close(@RequestBody final WalletName name) {
		this.closeAddressBook(name);
		this.walletServices.close(name);
	}

	//endregion

	//region changePassword / changeName

	/**
	 * Changes the password of a wallet.
	 *
	 * @param bag The request parameters.
	 */
	@RequestMapping(value = "/wallet/password/change", method = RequestMethod.POST)
	public void changePassword(@RequestBody final WalletNamePasswordBag bag) {
		this.changeAddressBookPassword(bag);
		this.walletServices.move(bag, new WalletNamePasswordPair(bag.getName(), bag.getNewPassword()));
	}

	/**
	 * Changes the password of a wallet.
	 *
	 * @param bag The request parameters.
	 */
	@RequestMapping(value = "/wallet/name/change", method = RequestMethod.POST)
	public void changeName(@RequestBody final WalletNamePasswordBag bag) {
		this.changeAddressBookName(bag);
		this.walletServices.move(bag, new WalletNamePasswordPair(bag.getNewName(), bag.getPassword()));
	}

	//endregion

	private AddressBook createAddressBook(final WalletNamePasswordPair pair) {
		return this.addressBookServices.create(this.createAddressNamePasswordPair(pair));
	}

	private AddressBook openAddressBook(final WalletNamePasswordPair pair) {
		return this.addressBookServices.open(this.createAddressNamePasswordPair(pair));
	}

	private void closeAddressBook(final WalletName name) {
		this.addressBookServices.close(new AddressBookName(name.toString()));
	}

	private void changeAddressBookName(final WalletNamePasswordBag bag) {
		this.addressBookServices.move(
				this.createAddressNamePasswordPair(bag),
				new AddressBookNamePasswordPair(bag.getNewName().toString(), bag.getPassword().toString()));
	}

	private void changeAddressBookPassword(final WalletNamePasswordBag bag) {
		this.addressBookServices.move(
				this.createAddressNamePasswordPair(bag),
				new AddressBookNamePasswordPair(bag.getName().toString(), bag.getNewPassword().toString()));
	}

	private AddressBookNamePasswordPair createAddressNamePasswordPair(final WalletNamePasswordPair pair) {
		return new AddressBookNamePasswordPair(pair.getName().toString(), pair.getPassword().toString());
	}
}
