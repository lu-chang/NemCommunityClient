package org.nem.ncc.controller.requests;

import net.minidev.json.JSONObject;
import org.hamcrest.core.*;
import org.junit.*;
import org.nem.core.model.Address;
import org.nem.core.model.primitive.Amount;
import org.nem.core.serialization.Deserializer;
import org.nem.ncc.test.Utils;

public class PartialTransferInformationRequestTest {

	@Test
	public void requestCanBeCreated() {
		// Act:
		final PartialTransferInformationRequest request = new PartialTransferInformationRequest(
				Address.fromEncoded("r"),
				Amount.fromMicroNem(7),
				"m",
				false,
				true);

		// Assert:
		Assert.assertThat(request.getRecipientAddress(), IsEqual.equalTo(Address.fromEncoded("r")));
		Assert.assertThat(request.getAmount(), IsEqual.equalTo(Amount.fromMicroNem(7L)));
		Assert.assertThat(request.getMessage(), IsEqual.equalTo("m"));
		Assert.assertThat(request.shouldEncrypt(), IsEqual.equalTo(true));
	}

	@Test
	public void requestCanBeDeserializedWithAllParameters() {
		// Act:
		final PartialTransferInformationRequest request = this.createRequestFromJson("r", 7L, "m", 8);

		// Assert:
		Assert.assertThat(request.getRecipientAddress(), IsEqual.equalTo(Address.fromEncoded("r")));
		Assert.assertThat(request.getAmount(), IsEqual.equalTo(Amount.fromMicroNem(7L)));
		Assert.assertThat(request.getMessage(), IsEqual.equalTo("m"));
		Assert.assertThat(request.shouldEncrypt(), IsEqual.equalTo(true));
	}

	@Test
	public void requestCanBeDeserializedWithAllParametersWithoutMessage() {
		// Act:
		final PartialTransferInformationRequest request = this.createRequestFromJson("r", 7L, null, 0);

		// Assert:
		Assert.assertThat(request.getRecipientAddress(), IsEqual.equalTo(Address.fromEncoded("r")));
		Assert.assertThat(request.getAmount(), IsEqual.equalTo(Amount.fromMicroNem(7L)));
		Assert.assertThat(request.getMessage(), IsNull.nullValue());
		Assert.assertThat(request.shouldEncrypt(), IsEqual.equalTo(false));
	}

	@Test
	public void requestCanBeDeserializedWithoutAnyParameters() {
		// Act:
		final Deserializer deserializer = Utils.createDeserializer(new JSONObject());
		final PartialTransferInformationRequest request = new PartialTransferInformationRequest(deserializer);

		// Assert:
		Assert.assertThat(request.getRecipientAddress(), IsNull.nullValue());
		Assert.assertThat(request.getAmount(), IsNull.nullValue());
		Assert.assertThat(request.getMessage(), IsNull.nullValue());
		Assert.assertThat(request.shouldEncrypt(), IsEqual.equalTo(false));
	}

	private PartialTransferInformationRequest createRequestFromJson(
			final String recipientAddress,
			final Long amount,
			final String message,
			final Integer shouldEncrypt) {
		final JSONObject jsonObject = new JSONObject();
		jsonObject.put("recipient", recipientAddress);
		jsonObject.put("amount", amount);
		jsonObject.put("message", message);
		jsonObject.put("encrypt", shouldEncrypt);
		return new PartialTransferInformationRequest(Utils.createDeserializer(jsonObject));
	}
}