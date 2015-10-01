package org.nem.ncc.controller.requests;

import net.minidev.json.JSONObject;
import org.hamcrest.core.*;
import org.junit.*;
import org.nem.core.model.Address;
import org.nem.core.serialization.*;
import org.nem.ncc.test.Utils;

public class PartialSignatureInformationRequestTest {

	//region constructor

	@Test
	public void requestCanBeCreated() {
		// Assert:
		assertRequestCanBeCreatedFromArguments(Utils.generateRandomAddress(), Utils.generateRandomAddress());
	}

	@Test
	public void requestCanBeCreatedWithoutMultisigAddress() {
		// Assert:
		assertRequestCanBeCreatedFromArguments(null, Utils.generateRandomAddress());
	}

	@Test
	public void requestCanBeCreatedWithoutCosignatoryAddress() {
		// Assert:
		assertRequestCanBeCreatedFromArguments(Utils.generateRandomAddress(), null);
	}

	@Test
	public void requestCanBeCreatedWithNeitherMultisigAddressNorCosignatoryAddress() {
		// Assert:
		assertRequestCanBeCreatedFromArguments(null, null);
	}

	private static void assertRequestCanBeCreatedFromArguments(final Address multisigAddress, final Address cosignatoryAddress) {
		// Act:
		final PartialSignatureInformationRequest request = new PartialSignatureInformationRequest(multisigAddress, cosignatoryAddress);

		// Assert:
		Assert.assertThat(request.getMultisigAddress(), null == multisigAddress ? IsNull.nullValue() : IsEqual.equalTo(multisigAddress));
		Assert.assertThat(request.getCosignatoryAddress(), null == cosignatoryAddress ? IsNull.nullValue() : IsEqual.equalTo(cosignatoryAddress));
	}

	//endregion

	//region serialization

	@Test
	public void requestCanBeDeserialized() {
		// Assert:
		assertRequestCanBeDeserialized(Utils.generateRandomAddress(), Utils.generateRandomAddress());
	}

	@Test
	public void requestCanBeDeserializedWithoutMultisigAddress() {
		// Assert:
		assertRequestCanBeDeserialized(null, Utils.generateRandomAddress());
	}

	@Test
	public void requestCanBeDeserializedWithoutCosignatoryAddress() {
		// Assert:
		assertRequestCanBeDeserialized(Utils.generateRandomAddress(), null);
	}

	@Test
	public void requestCanBeDeserializedWithNeitherMultisigAddressNorCosignatoryAddress() {
		// Assert:
		assertRequestCanBeDeserialized(null, null);
	}

	//endregion

	private static void assertRequestCanBeDeserialized(final Address expectedMultisigAddress, final Address expectedCosignatoryAddress) {
		// Act:
		final Deserializer deserializer = createDeserializer(expectedMultisigAddress, expectedCosignatoryAddress);
		final PartialSignatureInformationRequest request = new PartialSignatureInformationRequest(deserializer);

		// Assert:
		Assert.assertThat(request.getMultisigAddress(), null == expectedMultisigAddress ? IsNull.nullValue() : IsEqual.equalTo(expectedMultisigAddress));
		Assert.assertThat(
				request.getCosignatoryAddress(),
				null == expectedCosignatoryAddress ? IsNull.nullValue() : IsEqual.equalTo(expectedCosignatoryAddress));
	}

	private static Deserializer createDeserializer(
			final Address multisigAddress,
			final Address cosignatoryAddress) {
		final JSONObject jsonObject = new JSONObject();
		if (null != multisigAddress) {
			jsonObject.put("multisig", multisigAddress.toString());
		}

		if (null != cosignatoryAddress) {
			jsonObject.put("cosignatory", cosignatoryAddress.toString());
		}

		return new JsonDeserializer(jsonObject, null);
	}
}
