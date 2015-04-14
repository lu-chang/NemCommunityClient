package org.nem.specific.deploy;

import org.hamcrest.core.IsEqual;
import org.junit.*;
import org.nem.ncc.test.Utils;

public class OctetStreamTest {

	@Test
	public void canCreateOctetStream() {
		// Arrange:
		final byte[] bytes = Utils.generateRandomBytes(254);

		// Act:
		final OctetStream stream = new OctetStream(bytes);

		// Assert:
		Assert.assertThat(stream.toByteArray().length, IsEqual.equalTo(254));
		Assert.assertThat(stream.toByteArray(), IsEqual.equalTo(bytes));
	}
}