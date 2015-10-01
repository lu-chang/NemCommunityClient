package org.nem.ncc.addressbook.storage;

import org.nem.ncc.addressbook.*;
import org.nem.ncc.storable.entity.storage.StorableEntityDescriptor;

/**
 * An interface that describes an address book.
 */
public interface AddressBookDescriptor extends StorableEntityDescriptor<StorableAddressBook, AddressBookName, AddressBookFileExtension> {

	/**
	 * Gets the address book name.
	 *
	 * @return The address book name.
	 */
	AddressBookName getAddressBookName();

	/**
	 * Gets the address book's location.
	 *
	 * @return The address book's location.
	 */
	String getAddressBookLocation();
}
