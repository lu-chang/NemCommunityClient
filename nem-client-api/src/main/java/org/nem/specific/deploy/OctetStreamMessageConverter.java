package org.nem.specific.deploy;

import org.apache.commons.io.IOUtils;
import org.nem.core.utils.ExceptionUtils;
import org.springframework.http.*;
import org.springframework.http.converter.*;

import java.io.IOException;

/**
 * An HttpMessageConverter that supports handling OctetStream.
 */
public class OctetStreamMessageConverter extends AbstractHttpMessageConverter<OctetStream> {

	/**
	 * Creates a new http message converter.
	 */
	public OctetStreamMessageConverter() {
		super(new MediaType("application", "octet-stream"));
	}

	@Override
	protected boolean supports(final Class<?> aClass) {
		return OctetStream.class.isAssignableFrom(aClass);
	}

	@Override
	protected OctetStream readInternal(final Class<? extends OctetStream> clazz, final HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		final byte[] bytes = ExceptionUtils.propagate(() -> IOUtils.toByteArray(inputMessage.getBody()));
		return new OctetStream(bytes);
	}

	@Override
	protected void writeInternal(final OctetStream octetStream, final HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
		httpOutputMessage.getBody().write(octetStream.toByteArray());
	}
}
