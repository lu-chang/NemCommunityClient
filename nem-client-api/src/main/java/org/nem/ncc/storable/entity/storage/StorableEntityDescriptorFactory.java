package org.nem.ncc.storable.entity.storage;

import org.nem.ncc.storable.entity.*;

/**
 * Factory for creating storable entity descriptors.
 */
public interface StorableEntityDescriptorFactory<
		TEntityNamePasswordPair extends StorableEntityNamePasswordPair,
		TEntityFileExtension extends StorableEntityFileExtension,
		TEntityDescriptor extends StorableEntityDescriptor> {

	/**
	 * Creates a storable entity descriptor corresponding to a new storable entity.
	 *
	 * @param pair The storable entity name and password.
	 * @param fileExtension The storable entity file extension.
	 * @return The descriptor.
	 */
	TEntityDescriptor createNew(final TEntityNamePasswordPair pair, TEntityFileExtension fileExtension);

	/**
	 * Opens a storable entity descriptor corresponding to en existing storable entity.
	 *
	 * @param pair The storable entity name and password.
	 * @param fileExtension The storable entity file extension.
	 * @return The descriptor.
	 */
	TEntityDescriptor openExisting(final TEntityNamePasswordPair pair, TEntityFileExtension fileExtension);
}
