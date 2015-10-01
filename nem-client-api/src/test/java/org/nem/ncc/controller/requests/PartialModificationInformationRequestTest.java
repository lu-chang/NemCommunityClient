package org.nem.ncc.controller.requests;

import net.minidev.json.*;
import org.hamcrest.core.IsEqual;
import org.junit.*;
import org.nem.core.model.*;
import org.nem.core.serialization.*;
import org.nem.ncc.test.*;

import java.util.*;

public class PartialModificationInformationRequestTest {

	//region constructor

	@Test
	public void requestCanBeCreated() {
		// Assert:
		assertRequestCanBeCreatedFromArguments(
				Utils.generateRandomAddress(),
				Arrays.asList(Utils.generateRandomAddress(), Utils.generateRandomAddress()),
				new MultisigMinCosignatoriesModification(5));
	}

	@Test
	public void requestCanBeCreatedWithEmptyCosignatoryAddressList() {
		// Assert:
		assertRequestCanBeCreatedFromArguments(
				Utils.generateRandomAddress(),
				Collections.emptyList(),
				new MultisigMinCosignatoriesModification(5));
	}

	@Test
	public void requestCannotBeCreatedWithMissingMultisigAddress() {
		assertRequestCannotBeCreatedFromArguments(
				null,
				Collections.emptyList(),
				new MultisigMinCosignatoriesModification(5));
	}

	@Test
	public void requestCannotBeCreatedWithMissingCosignatoryAddressList() {
		assertRequestCannotBeCreatedFromArguments(
				Utils.generateRandomAddress(),
				null,
				new MultisigMinCosignatoriesModification(5));
	}

	@Test
	public void requestCannotBeCreatedWithMissingMinCosignatoriesModification() {
		assertRequestCannotBeCreatedFromArguments(
				Utils.generateRandomAddress(),
				Collections.emptyList(),
				null);
	}

	//endregion

	//region serialization

	@Test
	public void requestCanBeDeserialized() {
		// Assert:
		assertRequestCanBeDeserialized(
				Utils.generateRandomAddress(),
				Arrays.asList(Utils.generateRandomAddress(), Utils.generateRandomAddress()),
				new MultisigMinCosignatoriesModification(5));
	}

	@Test
	public void requestCanBeDeserializedWithEmptyCosignatoryAddressList() {
		// Assert:
		assertRequestCanBeDeserialized(
				Utils.generateRandomAddress(),
				Collections.emptyList(),
				new MultisigMinCosignatoriesModification(5));
	}

	@Test
	public void requestCannotBeDeserializedWithMissingMultisigAddress() {
		// Assert:
		assertRequestCannotBeDeserialized(
				null,
				Collections.emptyList(),
				new MultisigMinCosignatoriesModification(5));
	}

	@Test
	public void requestCannotBeDeserializedWithMissingCosignatoryAddressList() {
		// Assert:
		assertRequestCannotBeDeserialized(
				Utils.generateRandomAddress(),
				null,
				new MultisigMinCosignatoriesModification(5));
	}

	@Test
	public void requestCannotBeDeserializedWithMissingMinCosignatoriesModification() {
		// Assert:
		assertRequestCannotBeDeserialized(
				Utils.generateRandomAddress(),
				Collections.emptyList(),
				null);
	}

	//endregion

	private static void assertRequestCanBeCreatedFromArguments(
			final Address multisigAddress,
			final List<Address> expectedCosignatoryAddresses,
			final MultisigMinCosignatoriesModification expectedMinCosignatoriesModification) {
		// Act:
		final PartialModificationInformationRequest request = new PartialModificationInformationRequest(
				multisigAddress,
				expectedCosignatoryAddresses,
				expectedMinCosignatoriesModification);

		// Assert:
		Assert.assertThat(request.getMultisigAddress(), IsEqual.equalTo(multisigAddress));
		Assert.assertThat(request.getCosignatoriesAddresses(), IsEquivalent.equivalentTo(expectedCosignatoryAddresses));
		Assert.assertThat(
				request.getMinCosignatoriesModification().getRelativeChange(),
				IsEqual.equalTo(expectedMinCosignatoriesModification.getRelativeChange()));
	}

	private static void assertRequestCannotBeCreatedFromArguments(
			final Address multisigAddress,
			final List<Address> expectedCosignatoryAddresses,
			final MultisigMinCosignatoriesModification expectedMinCosignatoriesModification) {
		// Assert:
		ExceptionAssert.assertThrows(
				v -> new PartialModificationInformationRequest(multisigAddress, expectedCosignatoryAddresses, expectedMinCosignatoriesModification),
				IllegalArgumentException.class);
	}

	private static void assertRequestCanBeDeserialized(
			final Address expectedMultisigAddress,
			final List<Address> expectedCosignatoryAddresses,
			final MultisigMinCosignatoriesModification expectedMinCosignatoriesModification) {
		// Act:
		final Deserializer deserializer = createDeserializer(
				expectedMultisigAddress,
				expectedCosignatoryAddresses,
				expectedMinCosignatoriesModification);
		final PartialModificationInformationRequest request = new PartialModificationInformationRequest(deserializer);

		// Assert:
		Assert.assertThat(request.getMultisigAddress(), IsEqual.equalTo(expectedMultisigAddress));
		Assert.assertThat(request.getCosignatoriesAddresses(), IsEquivalent.equivalentTo(expectedCosignatoryAddresses));
		Assert.assertThat(
				request.getMinCosignatoriesModification().getRelativeChange(),
				IsEqual.equalTo(expectedMinCosignatoriesModification.getRelativeChange()));
	}

	private static void assertRequestCannotBeDeserialized(
			final Address expectedMultisigAddress,
			final List<Address> expectedCosignatoryAddresses,
			final MultisigMinCosignatoriesModification expectedMinCosignatoriesModification) {
		// Act:
		final Deserializer deserializer = createDeserializer(
				expectedMultisigAddress,
				expectedCosignatoryAddresses,
				expectedMinCosignatoriesModification);
		ExceptionAssert.assertThrows(
				v -> new PartialModificationInformationRequest(deserializer),
				MissingRequiredPropertyException.class);
	}

	private static Deserializer createDeserializer(
			final Address multisigAddress,
			final List<Address> cosignatories,
			final MultisigMinCosignatoriesModification minCosignatoriesModification) {
		final JSONObject jsonObject = new JSONObject();
		jsonObject.put("multisig", null == multisigAddress ? null : multisigAddress.toString());
		if (null == cosignatories) {
			jsonObject.put("cosignatories", null);
		} else {
			final JSONArray cosignatoryArray = new JSONArray();
			cosignatories.stream().forEach(cosignatory -> {
				final JSONObject address = new JSONObject();
				address.put("address", cosignatory.toString());
				cosignatoryArray.add(address);
			});
			jsonObject.put("cosignatories", cosignatoryArray);
		}

		if (null == minCosignatoriesModification) {
			jsonObject.put("minCosignatories", null);
		} else {
			final JSONObject minCosignatories = new JSONObject();
			minCosignatories.put("relativeChange", minCosignatoriesModification.getRelativeChange());
			jsonObject.put("minCosignatories", minCosignatories);
		}

		return new JsonDeserializer(jsonObject, null);
	}
}
