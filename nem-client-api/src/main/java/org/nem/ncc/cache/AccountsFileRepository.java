package org.nem.ncc.cache;

import net.minidev.json.*;
import org.apache.commons.io.IOUtils;
import org.nem.core.model.ncc.AccountInfo;
import org.nem.core.serialization.*;
import org.nem.core.utils.ExceptionUtils;
import org.nem.ncc.exceptions.NccException;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * A JSON-file based accounts repository.
 */
public class AccountsFileRepository implements AccountsRepository {
	private static final Logger LOGGER = Logger.getLogger(AccountsFileRepository.class.getName());

	private final AccountsFileDescriptor descriptor;

	/**
	 * Creates a new accounts file repository.
	 *
	 * @param descriptor The descriptor.
	 */
	public AccountsFileRepository(final AccountsFileDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public void save(final Collection<AccountInfo> accounts) {
		ExceptionUtils.propagateVoid(() -> {
			try (final OutputStream os = this.descriptor.openWrite()) {
				os.write(JsonSerializer.serializeToBytes(new Payload(accounts)));
			}
		}, ex -> new NccException(NccException.Code.ACCOUNT_CACHE_ERROR));
	}

	@Override
	public Collection<AccountInfo> load() {
		final Collection<AccountInfo> accounts = this.tryLoad();
		return null == accounts ? new ArrayList<>() : accounts;
	}

	private Collection<AccountInfo> tryLoad() {
		final String errorPrefix = "unable to load the accounts cache from disk";
		try {
			// if the file doesn't exist yet, openRead will return an empty stream
			try (final InputStream is = this.descriptor.openRead()) {
				final byte[] contents = IOUtils.toByteArray(is);
				if (0 == contents.length) {
					LOGGER.warning(String.format("%s because it is empty", errorPrefix));
					return null;
				}

				final JSONObject jsonObject = (JSONObject)JSONValue.parse(contents);
				final Payload payload = new Payload(new JsonDeserializer(jsonObject, null));
				if (Payload.DEFAULT_VERSION != payload.version) {
					LOGGER.warning(String.format("%s because it has an unsupported version: %d", errorPrefix, payload.version));
					return null;
				}

				return payload.accounts;
			}
		} catch (final IOException | SerializationException ex) {
			LOGGER.warning(String.format("%s: %s", errorPrefix, ex));
			return null;
		}
	}

	private static class Payload implements SerializableEntity {
		private static final int DEFAULT_VERSION = 1;

		private final int version;
		private final Collection<AccountInfo> accounts;

		private Payload(final Collection<AccountInfo> accounts) {
			this.version = DEFAULT_VERSION;
			this.accounts = accounts;
		}

		private Payload(final Deserializer deserializer) {
			this.version = deserializer.readInt("version");
			this.accounts = deserializer.readObjectArray("accounts", AccountInfo::new);
		}

		@Override
		public void serialize(final Serializer serializer) {
			serializer.writeInt("version", this.version);
			serializer.writeObjectArray("accounts", this.accounts);
		}
	}
}
