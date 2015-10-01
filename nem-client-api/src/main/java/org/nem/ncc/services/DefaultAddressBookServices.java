package org.nem.ncc.services;

import org.apache.commons.io.IOUtils;
import org.nem.core.utils.ExceptionUtils;
import org.nem.ncc.addressbook.*;
import org.nem.ncc.addressbook.storage.*;
import org.nem.ncc.exceptions.NccException;
import org.nem.ncc.storable.entity.storage.StorableEntityReadMode;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

// TODO 20150115 J-B: can you share some of the logic between this class and walletservices?
// > maybe some of the controller logic too (not sure)?
// TODO 20150116 BR -> J: you mean by letting this class extend a generic version too?
// TODO 20150118 J-B: 'you mean by letting this class extend a generic version too' - yea, the logic looks quite similar

/**
 * Implements default address book services
 */
public class DefaultAddressBookServices implements AddressBookServices {
	private final AddressBookRepository repository;
	private final AddressBookDescriptorFactory descriptorFactory;
	private final Map<AddressBookName, AddressBook> addressBooks = new ConcurrentHashMap<>();

	/**
	 * Creates a new default address book services.
	 *
	 * @param repository The repository.
	 * @param descriptorFactory The descriptor factory.
	 */
	public DefaultAddressBookServices(
			final AddressBookRepository repository,
			final AddressBookDescriptorFactory descriptorFactory) {
		this.repository = repository;
		this.descriptorFactory = descriptorFactory;
	}

	/**
	 * Gets the names of all open address books.
	 *
	 * @return The names of all open address books.
	 */
	public List<AddressBookName> getOpenAddressBookNames() {
		return this.addressBooks.keySet().stream().collect(Collectors.toList());
	}

	@Override
	public AddressBook get(final AddressBookName name) {
		final AddressBook addressBook = this.addressBooks.get(name);
		if (null == addressBook) {
			throw new NccException(NccException.Code.ADDRESS_BOOK_IS_NOT_OPEN);
		}

		return addressBook;
	}

	@Override
	public AddressBook open(final AddressBookNamePasswordPair pair) {
		final AddressBook addressBook = this.addressBooks.getOrDefault(pair.getName(), null);
		final AddressBookDescriptor descriptor = this.descriptorFactory.openExisting(pair, new AddressBookFileExtension());
		if (null != addressBook) {
			// ensure that the address book can be loaded; this also serves as a password check
			this.repository.load(descriptor);
			return addressBook;
		}

		return this.wrapAddressBook(this.repository.load(descriptor), descriptor);
	}

	@Override
	public AddressBook create(final AddressBookNamePasswordPair pair) {
		final AddressBookDescriptor descriptor = this.descriptorFactory.createNew(pair, new AddressBookFileExtension());
		final AutoSavingAddressBook addressBook = this.wrapAddressBook(new MemoryAddressBook(pair.getName()), descriptor);
		addressBook.save();
		return addressBook;
	}

	@Override
	public void close(final AddressBookName name) {
		this.addressBooks.remove(name);
	}

	private AutoSavingAddressBook wrapAddressBook(final StorableAddressBook addressBook, final AddressBookDescriptor descriptor) {
		final AutoSavingAddressBook autoSavingAddressBook = new AutoSavingAddressBook(addressBook, descriptor, this.repository);
		this.addressBooks.put(descriptor.getAddressBookName(), autoSavingAddressBook);
		return autoSavingAddressBook;
	}

	@Override
	public void move(final AddressBookNamePasswordPair originalPair, final AddressBookNamePasswordPair desiredPair) {
		final boolean hasNameChange = !originalPair.getName().equals(desiredPair.getName());
		final AddressBookDescriptor newAddressBookDescriptor = hasNameChange
				? this.descriptorFactory.createNew(desiredPair, new AddressBookFileExtension())
				: this.descriptorFactory.openExisting(desiredPair, new AddressBookFileExtension());

		// be sure to reload the address book here so that an auto-saved address book is not wrapped
		final AddressBookDescriptor originalAddressBookDescriptor = this.descriptorFactory.openExisting(originalPair, new AddressBookFileExtension());
		final AddressBook originalAddressBook = this.repository.load(originalAddressBookDescriptor);
		final AutoSavingAddressBook addressBook = this.wrapAddressBook(
				new MemoryAddressBook(desiredPair.getName(), originalAddressBook.getAccountLabels()),
				newAddressBookDescriptor);
		addressBook.save();

		if (hasNameChange) {
			this.addressBooks.remove(originalPair.getName());
			originalAddressBookDescriptor.delete();
		}
	}

	@Override
	public void copyTo(final AddressBookNamePasswordPair pair, final OutputStream outputStream) {
		final AddressBookDescriptor descriptor = this.descriptorFactory.openExisting(pair, new AddressBookFileExtension());

		ExceptionUtils.propagateVoid(() -> {
			try (InputStream inputStream = descriptor.openRead(StorableEntityReadMode.Raw)) {
				IOUtils.copy(inputStream, outputStream);
			}
		});
	}
}
