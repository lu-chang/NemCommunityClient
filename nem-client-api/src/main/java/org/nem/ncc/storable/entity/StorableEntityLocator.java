package org.nem.ncc.storable.entity;

import org.nem.core.serialization.ObjectDeserializer;
import org.nem.ncc.storable.entity.storage.StorableEntityDescriptor;

import java.util.List;

/**
 * Interface for locating storable entities.
 */
public interface StorableEntityLocator<
		TEntity extends StorableEntity & ObjectDeserializer<TEntity>,
		TEntityName extends StorableEntityName,
		TEntityFileExtension extends StorableEntityFileExtension,
		TEntityDescriptor extends StorableEntityDescriptor<TEntity, TEntityName, TEntityFileExtension>> {

	/**
	 * Gets all wallet storable entity descriptors.
	 *
	 * @return A list of all storable entity descriptors.
	 */
	List<TEntityDescriptor> getAll();
}
