package org.nem.ncc.exceptions;

import org.nem.core.connect.ErrorResponse;
import org.nem.core.model.ncc.NemRequestResult;

import java.util.*;
import java.util.regex.Pattern;

/**
 * A NIS-originating exception.
 */
public class NisException extends NccException {
	/**
	 * General NIS error codes.
	 */
	public enum Code implements ValueBasedEnum {
		/**
		 * An unexpected NIS error occurred.
		 */
		UNEXPECTED_ERROR(306),

		/**
		 * The NIS node is not booted.
		 */
		NODE_NOT_BOOTED(600),

		/**
		 * The NIS node is already booted.
		 */
		NODE_ALREADY_BOOTED(601),

		/**
		 * The NIS node is loading db.
		 */
		NODE_LOADING_DB(602),

		/**
		 * Maximum number of allowed harvesters was reached.
		 */
		HARVESTING_LIMIT_HIT(699),

		/**
		 * The account is ineligible for foraging.
		 */
		HARVESTING_INELIGIBLE(700);

		private final int value;

		Code(final int value) {
			this.value = value;
		}

		@Override
		public int value() {
			return this.value;
		}

		// note: commented errors will never be shown in NCC UI
		private static final Map<String, ValueBasedEnum> NIS_ERROR_MAP = new HashMap<String, ValueBasedEnum>() {
			{
				// ILLEGAL STATE
				this.put("NIS_ILLEGAL_STATE_ALREADY_BOOTED", NODE_ALREADY_BOOTED);
				this.put("NIS_ILLEGAL_STATE_LOADING_CHAIN", NODE_LOADING_DB);
				this.put("NIS_ILLEGAL_STATE_NOT_BOOTED", NODE_NOT_BOOTED);

				// HARVESTING RELATED
				// "FAILURE_HARVESTING_BLOCKED",
				this.put("FAILURE_SERVER_LIMIT", HARVESTING_LIMIT_HIT);
				this.put("FAILURE_HARVESTING_INELIGIBLE", HARVESTING_INELIGIBLE);

				// nis transaction validation errors
				final Pattern name = Pattern.compile("^TRANSACTION_REJECTED_");
				for (final NisValidationException e : NisValidationException.values()) {
					final String nisName = name.matcher(e.toString()).replaceFirst("FAILURE_");
					this.put(nisName, e);
				}
			}
		};

		/**
		 * Gets the NIS code corresponding to a NIS message.
		 */
		public static ValueBasedEnum fromMessage(final String message) {
			return NIS_ERROR_MAP.getOrDefault(message, UNEXPECTED_ERROR);
		}
	}

	/**
	 * Creates a new NIS exception.
	 *
	 * @param errorResponse The NIS error response.
	 */
	public NisException(final ErrorResponse errorResponse) {
		super(Code.fromMessage(errorResponse.getMessage()));
	}

	/**
	 * Creates a new NIS exception.
	 *
	 * @param result The result of a request to the NIS server.
	 */
	public NisException(final NemRequestResult result) {
		super(Code.fromMessage(result.getMessage()));
	}
}
