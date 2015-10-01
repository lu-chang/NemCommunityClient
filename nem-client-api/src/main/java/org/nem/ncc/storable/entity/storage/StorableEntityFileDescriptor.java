package org.nem.ncc.storable.entity.storage;

import org.eclipse.jetty.util.UrlEncoded;
import org.nem.core.serialization.*;
import org.nem.core.utils.ExceptionUtils;
import org.nem.ncc.storable.entity.*;

import java.io.*;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * A StorableEntityDescriptor implementation that references files stored on disk.
 */
public abstract class StorableEntityFileDescriptor<
		TEntity extends StorableEntity & ObjectDeserializer<TEntity>,
		TEntityName extends StorableEntityName,
		TEntityFileExtension extends StorableEntityFileExtension>
		implements StorableEntityDescriptor<TEntity, TEntityName, TEntityFileExtension> {
	private final static Logger LOGGER = Logger.getLogger(StorableEntityFileDescriptor.class.getName());

	private final File file;
	private final TEntityName name;
	private final TEntityFileExtension fileExtension;
	private final ObjectDeserializer<TEntity> deserializer;

	/**
	 * Creates a new storable entity file descriptor around a file.
	 *
	 * @param file The storable entity location.
	 * @param deserializer The entity deserializer.
	 * @param nameActivator The entity name activator.
	 * @param fileExtensionActivator The entity file extension activator.
	 */
	public StorableEntityFileDescriptor(
			final File file,
			final ObjectDeserializer<TEntity> deserializer,
			final Function<String, TEntityName> nameActivator,
			final Function<String, TEntityFileExtension> fileExtensionActivator) {
		this.file = file;
		this.deserializer = deserializer;
		if (file.isDirectory()) {
			throw this.getException(StorableEntityStorageException.Code.STORABLE_ENTITY_CANNOT_BE_DIRECTORY.value(), null);
		}

		final String fileName = this.file.getName();
		final String entityName = fileName.substring(0, fileName.length() - StorableEntityFileExtension.EXTENSION_LENGTH);
		this.name = nameActivator.apply(UrlEncoded.decodeString(entityName, 0, entityName.length(), UrlEncoded.ENCODING));
		final String entityFileExtension = fileName.substring(fileName.length() - StorableEntityFileExtension.EXTENSION_LENGTH, fileName.length());
		this.fileExtension = fileExtensionActivator.apply(entityFileExtension);
	}

	/**
	 * Gets the storable entity location.
	 *
	 * @return The storable entity location.
	 */
	public String getStorableEntityLocation() {
		return this.file.getAbsolutePath();
	}

	@Override
	public TEntityName getName() {
		return this.name;
	}

	@Override
	public TEntityFileExtension getFileExtension() {
		return this.fileExtension;
	}

	@Override
	public ObjectDeserializer<TEntity> getDeserializer() {
		return this.deserializer;
	}

	@Override
	public InputStream openRead() {
		LOGGER.info(String.format("Opening storable entity for reading located at <%s>", this.getStorableEntityLocation()));
		if (!this.file.exists()) {
			throw this.getException(StorableEntityStorageException.Code.STORABLE_ENTITY_DOES_NOT_EXIST.value(), null);
		}

		return ExceptionUtils.propagate(
				() -> new FileInputStream(this.getStorableEntityLocation()),
				ex -> this.getException(StorableEntityStorageException.Code.STORABLE_ENTITY_COULD_NOT_BE_READ.value(), ex));
	}

	@Override
	public InputStream openRead(final StorableEntityReadMode mode) {
		// TODO 20150526 J-B: i think in this case it would make more sense for raw and decode to return the same stream
		// > (since there is no encoding the raw stream is already decoded)
		if (!StorableEntityReadMode.Raw.equals(mode)) {
			throw new IllegalArgumentException("file descriptor cannot decode the stream");
		}

		return this.openRead();
	}

	@Override
	public OutputStream openWrite() {
		LOGGER.info(String.format("Opening storable entity for writing located at <%s>", this.getStorableEntityLocation()));
		return ExceptionUtils.propagate(
				() -> new FileOutputStream(this.getStorableEntityLocation()),
				ex -> this.getException(StorableEntityStorageException.Code.STORABLE_ENTITY_COULD_NOT_BE_SAVED.value(), ex));
	}

	@Override
	public void delete() {
		LOGGER.info(String.format("Deleting storable entity <%s> located at <%s>.", this.name, this.getStorableEntityLocation()));
		if (!this.file.delete()) {
			throw this.getException(StorableEntityStorageException.Code.STORABLE_ENTITY_COULD_NOT_BE_DELETED.value(), null);
		}
	}

	@Override
	public void serialize(final Serializer serializer) {
		StorableEntityName.writeTo(serializer, this.name.getLabel(), this.name);
		serializer.writeString("location", this.getStorableEntityLocation());
	}

	protected abstract StorableEntityStorageException getException(final int value, final Exception ex);
}
