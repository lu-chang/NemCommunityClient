package org.nem.ncc.storable.entity.storage;

import org.nem.core.serialization.*;
import org.nem.ncc.storable.entity.*;

import java.io.*;

/**
 * An interface that describes a storable entity.
 */
public interface StorableEntityDescriptor<
		TEntity extends StorableEntity & ObjectDeserializer<TEntity>,
		TEntityName extends StorableEntityName,
		TEntityFileExtension extends StorableEntityFileExtension>
		extends SerializableEntity {

	/**
	 * Gets the entity's name.
	 *
	 * @return The entity's name.
	 */
	TEntityName getName();

	/**
	 * Gets the entity's file extension.
	 *
	 * @return The entity's file extension.
	 */
	TEntityFileExtension getFileExtension();

	/**
	 * Gets the entity deserializer.
	 * This is necessary because the repository needs to be able to deserialize the entity.
	 *
	 * @return The entity deserializer.
	 */
	ObjectDeserializer<TEntity> getDeserializer();

	/**
	 * Opens a read stream that can be used to read the contents of the referenced storable entity.
	 *
	 * @return The input stream.
	 */
	InputStream openRead();

	/**
	 * Opens a read stream that can be used to read the contents of the referenced storable entity.
	 *
	 * @param mode The read mode.
	 * @return The input stream.
	 */
	InputStream openRead(final StorableEntityReadMode mode);

	/**
	 * Opens a write stream that can be used to modify the contents of the referenced storable entity.
	 *
	 * @return The output stream.
	 */
	OutputStream openWrite();

	/**
	 * Deletes the referenced storable entity.
	 */
	void delete();
}
