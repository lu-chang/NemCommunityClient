package org.nem.ncc.services;

import org.nem.ncc.addressbook.*;

import java.io.OutputStream;

/**
 * A set of services for interacting with address books.
 */
public interface AddressBookServices {

	/**
	 * Retrieves the specified address book.
	 *
	 * @param name The name of the address book.
	 * @return The address book.
	 */
	AddressBook get(final AddressBookName name);

	/**
	 * Opens and returns the specified address book.
	 *
	 * @param pair The address book name and password pair.
	 * @return The address book.
	 */
	AddressBook open(final AddressBookNamePasswordPair pair);

	/**
	 * Creates and returns the specified address book.
	 *
	 * @param pair The address book name and password pair.
	 * @return The address book.
	 */
	AddressBook create(final AddressBookNamePasswordPair pair);

	/**
	 * Closes the specified address book.
	 *
	 * @param name The name of the address book.
	 */
	void close(final AddressBookName name);

	/**
	 * Renames the specified address book.
	 *
	 * @param originalPair The original address book name and password pair.
	 * @param desiredPair The desired address book name and password pair.
	 */
	void move(final AddressBookNamePasswordPair originalPair, final AddressBookNamePasswordPair desiredPair);

	/**
	 * Writes the address book file contents to the given output stream.
	 *
	 * @param pair The address book name and password pair.
	 * @param outputStream The output stream to write into.
	 */
	void copyTo(final AddressBookNamePasswordPair pair, final OutputStream outputStream);
}
