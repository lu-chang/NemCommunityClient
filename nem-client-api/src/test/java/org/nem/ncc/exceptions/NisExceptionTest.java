package org.nem.ncc.exceptions;

import org.hamcrest.core.IsEqual;
import org.junit.*;
import org.nem.core.connect.ErrorResponse;
import org.nem.core.time.TimeInstant;

import java.util.*;
import java.util.regex.Pattern;

public class NisExceptionTest {

	@Test
	public void nisErrorResponseMessageIsCorrectlyMappedToNisExceptionCode() {
		// Arrange:
		final Map<String, ValueBasedEnum> nisErrorMap = new HashMap<String, ValueBasedEnum>() {
			{
				// GENERAL
				this.put("NIS_ILLEGAL_STATE_ALREADY_BOOTED", NisException.Code.NODE_ALREADY_BOOTED);
				this.put("NIS_ILLEGAL_STATE_LOADING_CHAIN", NisException.Code.NODE_LOADING_DB);
				this.put("NIS_ILLEGAL_STATE_NOT_BOOTED", NisException.Code.NODE_NOT_BOOTED);

				// HARVESTING RELATED
				// "FAILURE_HARVESTING_BLOCKED",
				this.put("FAILURE_SERVER_LIMIT", NisException.Code.HARVESTING_LIMIT_HIT);
				this.put("FAILURE_HARVESTING_INELIGIBLE", NisException.Code.HARVESTING_INELIGIBLE);

				// nis transaction validation errors
				final Pattern name = Pattern.compile("^TRANSACTION_REJECTED_");
				for (final NisValidationException e : NisValidationException.values()) {
					final String nisName = name.matcher(e.toString()).replaceFirst("FAILURE_");
					this.put(nisName, e);
				}
			}
		};

		for (final Map.Entry<String, ValueBasedEnum> pair : nisErrorMap.entrySet()) {
			// Act:
			final ErrorResponse response = new ErrorResponse(TimeInstant.ZERO, pair.getKey(), 0);
			final NisException exception = new NisException(response);

			// Assert:
			Assert.assertThat(exception.getCode(), IsEqual.equalTo(pair.getValue()));
		}
	}
}