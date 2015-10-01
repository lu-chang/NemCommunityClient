package org.nem.ncc.storable.entity;

import org.nem.core.serialization.ObjectDeserializer;
import org.nem.ncc.storable.entity.storage.StorableEntityDescriptor;

/**
 * Repository class for loading and saving storable entities.
 */
public interface StorableEntityRepository<
		TEntity extends StorableEntity & ObjectDeserializer<TEntity>,
		TEntityName extends StorableEntityName,
		TEntityFileExtension extends StorableEntityFileExtension,
		TEntityDescriptor extends StorableEntityDescriptor<TEntity, TEntityName, TEntityFileExtension>> {

	/**
	 * Saves the storable entity to the specified descriptor.
	 *
	 * @param descriptor The output descriptor.
	 * @param storableEntity The storable entity.
	 */
	void save(final TEntityDescriptor descriptor, final TEntity storableEntity);

	/**
	 * Loads a storable entity from the specified descriptor.
	 *
	 * @param descriptor The input descriptor.
	 * @return The storable entity.
	 */
	TEntity load(final TEntityDescriptor descriptor);
}
