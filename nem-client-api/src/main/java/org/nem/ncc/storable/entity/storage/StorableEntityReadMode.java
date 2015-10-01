package org.nem.ncc.storable.entity.storage;

/**
 * Enumeration of read modes for a storable entity.
 */
public enum StorableEntityReadMode {
	/**
	 * Open the entity for reading of the raw (encoded) stream.
	 */
	Raw,

	/**
	 * Open the entity for reading of the decoded stream.
	 */
	Decode
}
