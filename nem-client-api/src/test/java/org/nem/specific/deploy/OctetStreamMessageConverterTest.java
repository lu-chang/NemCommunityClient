package org.nem.specific.deploy;

import org.hamcrest.core.IsEqual;
import org.junit.*;
import org.nem.ncc.test.*;
import org.springframework.http.MediaType;

import java.util.List;

public class OctetStreamMessageConverterTest {

	// region supports / canRead / canWrite

	@Test
	public void converterSupportsApplicationOctetStream() {
		// Arrange:
		final MediaType mediaType = new MediaType("application", "octet-stream");
		final OctetStreamMessageConverter mc = createMessageConverter();

		// Act:
		final List<MediaType> mediaTypes = mc.getSupportedMediaTypes();

		// Assert:
		Assert.assertThat(mediaTypes.size(), IsEqual.equalTo(1));
		Assert.assertThat(mediaTypes.get(0), IsEqual.equalTo(mediaType));
	}

	@Test
	public void canReadCompatibleTypes() {
		// Arrange:
		final MediaType supportedType = new MediaType("application", "octet-stream");
		final OctetStreamMessageConverter mc = createMessageConverter();

		// Assert:
		Assert.assertThat(mc.canRead(OctetStream.class, supportedType), IsEqual.equalTo(true));
		Assert.assertThat(mc.canRead(ExtendedOctetStream.class, supportedType), IsEqual.equalTo(true));
	}

	@Test
	public void cannotReadIncompatibleTypes() {
		// Arrange:
		final MediaType supportedType = new MediaType("application", "octet-stream");
		final OctetStreamMessageConverter mc = createMessageConverter();

		// Assert:
		Assert.assertThat(mc.canRead(MediaType.class, supportedType), IsEqual.equalTo(false));
		Assert.assertThat(mc.canRead(Object.class, supportedType), IsEqual.equalTo(false));
	}

	@Test
	public void canWriteCompatibleTypes() {
		// Arrange:
		final MediaType supportedType = new MediaType("application", "octet-stream");
		final OctetStreamMessageConverter mc = createMessageConverter();

		// Assert:
		Assert.assertThat(mc.canWrite(OctetStream.class, supportedType), IsEqual.equalTo(true));
		Assert.assertThat(mc.canWrite(ExtendedOctetStream.class, supportedType), IsEqual.equalTo(true));
	}

	@Test
	public void cannotWriteIncompatibleTypes() {
		// Arrange:
		final MediaType supportedType = new MediaType("application", "octet-stream");
		final OctetStreamMessageConverter mc = createMessageConverter();

		// Assert:
		Assert.assertThat(mc.canWrite(MediaType.class, supportedType), IsEqual.equalTo(false));
		Assert.assertThat(mc.canWrite(Object.class, supportedType), IsEqual.equalTo(false));
	}

	//endregion

	//region read / write

	@Test
	public void readOctetStreamIsCorrectlyCreatedAroundInput() throws Exception {
		// Arrange:
		final OctetStreamMessageConverter mc = createMessageConverter();

		// Act:
		final OctetStream stream = mc.read(
				OctetStream.class,
				new MockHttpInputMessage("this is a test message"));

		// Assert:
		Assert.assertThat(stream.toByteArray(), IsEqual.equalTo("this is a test message".getBytes()));
	}

	@Test
	public void writeOctetStreamIsCorrectlyWrittenAsOutput() throws Exception {
		// Arrange:
		final OctetStreamMessageConverter mc = createMessageConverter();
		final MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();

		// Act::
		mc.write(
				new OctetStream("this is a test message".getBytes()),
				new MediaType("application", "octet-stream"),
				outputMessage);

		// Assert:
		Assert.assertThat(outputMessage.getBodyAsString(), IsEqual.equalTo("this is a test message"));
	}

	//endregion

	private static class ExtendedOctetStream extends OctetStream {

		public ExtendedOctetStream(final byte[] buffer) {
			super(buffer);
		}
	}

	private static OctetStreamMessageConverter createMessageConverter() {
		return new OctetStreamMessageConverter();
	}
}