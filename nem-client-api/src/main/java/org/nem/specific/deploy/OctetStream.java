package org.nem.specific.deploy;

/**
 * Represents an octet stream.
 */
public class OctetStream {
	private final byte[] buffer;

	/**
	 * Creates an octet stream.
	 *
	 * @param buffer The raw byte array.
	 */
	public OctetStream(final byte[] buffer) {
		this.buffer = buffer;
	}

	/**
	 * Gets the underlying byte array.
	 *
	 * @return The underlying byte array.
	 */
	public byte[] toByteArray() {
		return this.buffer;
	}
}
