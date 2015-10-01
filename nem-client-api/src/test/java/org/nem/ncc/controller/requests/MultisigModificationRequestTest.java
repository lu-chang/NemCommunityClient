package org.nem.ncc.controller.requests;

import net.minidev.json.*;
import org.hamcrest.core.*;
import org.junit.*;
import org.nem.core.model.*;
import org.nem.core.model.primitive.Amount;
import org.nem.core.serialization.*;
import org.nem.ncc.controller.viewmodels.TransactionViewModel;
import org.nem.ncc.test.*;
import org.nem.ncc.wallet.*;

import java.util.*;

public class MultisigModificationRequestTest {

	//region constructor

	@Test
	public void requestCanBeCreated() {
		// Arrange:
		final Address initiator = Utils.generateRandomAddress();
		final List<Address> cosignatoryAddAddresses = Arrays.asList(Utils.generateRandomAddress(), Utils.generateRandomAddress());
		final List<Address> cosignatoryDelAddresses = Arrays.asList(Utils.generateRandomAddress(), Utils.generateRandomAddress());

		// Act:
		final MultisigModificationRequest request = new MultisigModificationRequest(
				new WalletName("wlt"),
				TransactionViewModel.Type.Aggregate_Modification.getValue(),
				new WalletPassword("pwd"),
				initiator,
				null,
				cosignatoryAddAddresses,
				cosignatoryDelAddresses,
				new MultisigMinCosignatoriesModification(5),
				12,
				Amount.fromNem(123),
				Amount.ZERO);

		// Assert:
		Assert.assertThat(request.getWalletName(), IsEqual.equalTo(new WalletName("wlt")));
		Assert.assertThat(request.getPassword(), IsEqual.equalTo(new WalletPassword("pwd")));
		Assert.assertThat(request.getType(), IsEqual.equalTo(TransactionViewModel.Type.Aggregate_Modification.getValue()));
		Assert.assertThat(request.getMultisigAccount(), IsEqual.equalTo(initiator));
		Assert.assertThat(request.getIssuerAddress(), IsNull.nullValue());
		Assert.assertThat(request.getAddedCosignatories(), IsEquivalent.equivalentTo(cosignatoryAddAddresses));
		Assert.assertThat(request.getRemovedCosignatories(), IsEquivalent.equivalentTo(cosignatoryDelAddresses));
		Assert.assertThat(request.getMinCosignatoriesModification().getRelativeChange(), IsEqual.equalTo(5));
		Assert.assertThat(request.getHoursDue(), IsEqual.equalTo(12));
		Assert.assertThat(request.getFee(), IsEqual.equalTo(Amount.fromNem(123)));
		Assert.assertThat(request.getMultisigFee(), IsEqual.equalTo(Amount.ZERO));
	}

	@Test
	public void multisigRequestCanBeCreated() {
		// Arrange:
		final Address initiator = Utils.generateRandomAddress();
		final Address issuer = Utils.generateRandomAddress();
		final List<Address> cosignatoryAddAddresses = Arrays.asList(Utils.generateRandomAddress(), Utils.generateRandomAddress());
		final List<Address> cosignatoryDelAddresses = Arrays.asList(Utils.generateRandomAddress(), Utils.generateRandomAddress());

		// Act:
		final MultisigModificationRequest request = new MultisigModificationRequest(
				new WalletName("wlt"),
				TransactionViewModel.Type.Multisig_Aggregate_Modification.getValue(),
				new WalletPassword("pwd"),
				initiator,
				issuer,
				cosignatoryAddAddresses,
				cosignatoryDelAddresses,
				new MultisigMinCosignatoriesModification(5),
				12,
				Amount.fromNem(123),
				Amount.fromNem(321));

		// Assert:
		Assert.assertThat(request.getWalletName(), IsEqual.equalTo(new WalletName("wlt")));
		Assert.assertThat(request.getPassword(), IsEqual.equalTo(new WalletPassword("pwd")));
		Assert.assertThat(request.getType(), IsEqual.equalTo(TransactionViewModel.Type.Multisig_Aggregate_Modification.getValue()));
		Assert.assertThat(request.getMultisigAccount(), IsEqual.equalTo(initiator));
		Assert.assertThat(request.getIssuerAddress(), IsEqual.equalTo(issuer));
		Assert.assertThat(request.getAddedCosignatories(), IsEquivalent.equivalentTo(cosignatoryAddAddresses));
		Assert.assertThat(request.getRemovedCosignatories(), IsEquivalent.equivalentTo(cosignatoryDelAddresses));
		Assert.assertThat(request.getMinCosignatoriesModification().getRelativeChange(), IsEqual.equalTo(5));
		Assert.assertThat(request.getHoursDue(), IsEqual.equalTo(12));
		Assert.assertThat(request.getFee(), IsEqual.equalTo(Amount.fromNem(123)));
		Assert.assertThat(request.getMultisigFee(), IsEqual.equalTo(Amount.fromNem(321)));
	}

	//endregion

	//region serialization

	@Test
	public void requestCanBeDeserializedWithAllParameters() {
		// Arrange:
		final int type = TransactionViewModel.Type.Aggregate_Modification.getValue();
		final Deserializer deserializer = createDeserializer("w", "p", "s", "i", "a", "d", 5, 12, 123L, 10L, type, true);

		// Act:
		final MultisigModificationRequest request = new MultisigModificationRequest(deserializer);

		// Assert:
		Assert.assertThat(request.getWalletName(), IsEqual.equalTo(new WalletName("w")));
		Assert.assertThat(request.getPassword(), IsEqual.equalTo(new WalletPassword("p")));
		Assert.assertThat(request.getType(), IsEqual.equalTo(type));
		Assert.assertThat(request.getMultisigAccount(), IsEqual.equalTo(Address.fromEncoded("s")));
		Assert.assertThat(request.getIssuerAddress(), IsEqual.equalTo(Address.fromEncoded("i")));
		Assert.assertThat(request.getAddedCosignatories(), IsEquivalent.equivalentTo(Collections.singletonList(Address.fromEncoded("a"))));
		Assert.assertThat(request.getRemovedCosignatories(), IsEquivalent.equivalentTo(Collections.singletonList(Address.fromEncoded("d"))));
		Assert.assertThat(request.getMinCosignatoriesModification().getRelativeChange(), IsEqual.equalTo(5));
		Assert.assertThat(request.getHoursDue(), IsEqual.equalTo(12));
		Assert.assertThat(request.getFee(), IsEqual.equalTo(Amount.fromMicroNem(123)));
		Assert.assertThat(request.getMultisigFee(), IsEqual.equalTo(Amount.fromMicroNem(10)));
	}

	@Test
	public void requestCanBeDeserializedWithEmptyCosignatoryAddressList() {
		// Arrange:
		final int type = TransactionViewModel.Type.Aggregate_Modification.getValue();
		final Deserializer deserializer = createDeserializer("w", "p", "s", "i", "", "", 5, 12, 123L, 10L, type, true);

		// Act:
		final MultisigModificationRequest request = new MultisigModificationRequest(deserializer);

		// Assert:
		Assert.assertThat(request.getWalletName(), IsEqual.equalTo(new WalletName("w")));
		Assert.assertThat(request.getPassword(), IsEqual.equalTo(new WalletPassword("p")));
		Assert.assertThat(request.getType(), IsEqual.equalTo(type));
		Assert.assertThat(request.getMultisigAccount(), IsEqual.equalTo(Address.fromEncoded("s")));
		Assert.assertThat(request.getIssuerAddress(), IsEqual.equalTo(Address.fromEncoded("i")));
		Assert.assertThat(request.getAddedCosignatories(), IsEquivalent.equivalentTo(Collections.emptyList()));
		Assert.assertThat(request.getRemovedCosignatories(), IsEquivalent.equivalentTo(Collections.emptyList()));
		Assert.assertThat(request.getMinCosignatoriesModification().getRelativeChange(), IsEqual.equalTo(5));
		Assert.assertThat(request.getHoursDue(), IsEqual.equalTo(12));
		Assert.assertThat(request.getFee(), IsEqual.equalTo(Amount.fromMicroNem(123)));
		Assert.assertThat(request.getMultisigFee(), IsEqual.equalTo(Amount.fromMicroNem(10)));
	}

	@Test
	public void requestCanBeDeserializedWithoutMinCosignatories() {
		// Arrange:
		final int type = TransactionViewModel.Type.Aggregate_Modification.getValue();
		final Deserializer deserializer = createDeserializer("w", "p", "s", "i", "a", "d", 5, 12, 123L, 10L, type, false);

		// Act:
		final MultisigModificationRequest request = new MultisigModificationRequest(deserializer);

		// Assert:
		Assert.assertThat(request.getWalletName(), IsEqual.equalTo(new WalletName("w")));
		Assert.assertThat(request.getPassword(), IsEqual.equalTo(new WalletPassword("p")));
		Assert.assertThat(request.getType(), IsEqual.equalTo(type));
		Assert.assertThat(request.getMultisigAccount(), IsEqual.equalTo(Address.fromEncoded("s")));
		Assert.assertThat(request.getIssuerAddress(), IsEqual.equalTo(Address.fromEncoded("i")));
		Assert.assertThat(request.getAddedCosignatories(), IsEquivalent.equivalentTo(Collections.singletonList(Address.fromEncoded("a"))));
		Assert.assertThat(request.getRemovedCosignatories(), IsEquivalent.equivalentTo(Collections.singletonList(Address.fromEncoded("d"))));
		Assert.assertThat(request.getMinCosignatoriesModification(), IsNull.nullValue());
		Assert.assertThat(request.getHoursDue(), IsEqual.equalTo(12));
		Assert.assertThat(request.getFee(), IsEqual.equalTo(Amount.fromMicroNem(123)));
		Assert.assertThat(request.getMultisigFee(), IsEqual.equalTo(Amount.fromMicroNem(10)));
	}

	@Test
	public void requestCanBeDeserializedWithZeroMultisigFee() {
		// Arrange:
		final int type = TransactionViewModel.Type.Aggregate_Modification.getValue();
		final Deserializer deserializer = createDeserializer("w", "p", "s", "i", "", "", 5, 12, 123L, 0L, type, true);

		// Act:
		final MultisigModificationRequest request = new MultisigModificationRequest(deserializer);

		// Assert:
		Assert.assertThat(request.getWalletName(), IsEqual.equalTo(new WalletName("w")));
		Assert.assertThat(request.getPassword(), IsEqual.equalTo(new WalletPassword("p")));
		Assert.assertThat(request.getType(), IsEqual.equalTo(type));
		Assert.assertThat(request.getMultisigAccount(), IsEqual.equalTo(Address.fromEncoded("s")));
		Assert.assertThat(request.getIssuerAddress(), IsEqual.equalTo(Address.fromEncoded("i")));
		Assert.assertThat(request.getAddedCosignatories(), IsEquivalent.equivalentTo(Collections.emptyList()));
		Assert.assertThat(request.getRemovedCosignatories(), IsEquivalent.equivalentTo(Collections.emptyList()));
		Assert.assertThat(request.getMinCosignatoriesModification().getRelativeChange(), IsEqual.equalTo(5));
		Assert.assertThat(request.getHoursDue(), IsEqual.equalTo(12));
		Assert.assertThat(request.getFee(), IsEqual.equalTo(Amount.fromMicroNem(123)));
		Assert.assertThat(request.getMultisigFee(), IsEqual.equalTo(Amount.fromMicroNem(0)));
	}

	@Test
	public void requestCanBeDeserializedWithMultisigMultisigType() {
		// Arrange:
		final int type = TransactionViewModel.Type.Multisig_Aggregate_Modification.getValue();
		final Deserializer deserializer = createDeserializer("w", "p", "s", "i", "", "", 5, 12, 123L, 10L, type, true);

		// Act:
		final MultisigModificationRequest request = new MultisigModificationRequest(deserializer);

		// Assert:
		Assert.assertThat(request.getWalletName(), IsEqual.equalTo(new WalletName("w")));
		Assert.assertThat(request.getPassword(), IsEqual.equalTo(new WalletPassword("p")));
		Assert.assertThat(request.getType(), IsEqual.equalTo(type));
		Assert.assertThat(request.getMultisigAccount(), IsEqual.equalTo(Address.fromEncoded("s")));
		Assert.assertThat(request.getIssuerAddress(), IsEqual.equalTo(Address.fromEncoded("i")));
		Assert.assertThat(request.getAddedCosignatories(), IsEquivalent.equivalentTo(Collections.emptyList()));
		Assert.assertThat(request.getRemovedCosignatories(), IsEquivalent.equivalentTo(Collections.emptyList()));
		Assert.assertThat(request.getMinCosignatoriesModification().getRelativeChange(), IsEqual.equalTo(5));
		Assert.assertThat(request.getHoursDue(), IsEqual.equalTo(12));
		Assert.assertThat(request.getFee(), IsEqual.equalTo(Amount.fromMicroNem(123)));
		Assert.assertThat(request.getMultisigFee(), IsEqual.equalTo(Amount.fromMicroNem(10L)));
	}

	@Test
	public void requestCannotBeDeserializedWithMissingRequiredParameters() {
		// Arrange:
		final int type = TransactionViewModel.Type.Aggregate_Modification.getValue();
		final List<Deserializer> deserializers = Arrays.asList(
				createDeserializer(null, "p", "s", "i", "a", "d", 5, 12, 123L, 10L, type, true),
				createDeserializer("w", null, "s", "i", "a", "d", 5, 12, 123L, 10L, type, true),
				createDeserializer("w", "p", null, "i", "a", "d", 5, 12, 123L, 10L, type, true),
				// createDeserializer("w", "p", "s", null, "a", "d", 5, 12, 123L, 10L, type), // issuer is optional
				createDeserializer("w", "p", "s", "i", null, "d", 5, 12, 123L, 10L, type, true),
				createDeserializer("w", "p", "s", "i", "a", null, 5, 12, 123L, 10L, type, true),
				createDeserializer("w", "p", "s", "i", "a", "d", null, 12, 123L, 10L, type, true),
				createDeserializer("w", "p", "s", "i", "a", "d", 5, null, 123L, 10L, type, true),
				createDeserializer("w", "p", "s", "i", "a", "d", 5, 12, null, 10L, type, true),
				createDeserializer("w", "p", "s", "i", "a", "d", 5, 12, 123L, null, type, true),
				createDeserializer("w", "p", "s", "i", "a", "d", 5, 12, 123L, 10L, null, true)
		);

		// Assert:
		for (final Deserializer deserializer : deserializers) {
			ExceptionAssert.assertThrows(v -> new MultisigModificationRequest(deserializer), MissingRequiredPropertyException.class);
		}
	}

	//endregion

	private static Deserializer createDeserializer(
			final String walletName,
			final String walletPassword,
			final String sender,
			final String issuer,
			final String addCosignatory,
			final String delCosignatory,
			final Integer relativeChange,
			final Integer hoursDue,
			final Long fee,
			final Long multisigFee,
			final Integer type,
			final boolean hasMinCosignatories) {
		final JSONObject jsonObject = new JSONObject();
		jsonObject.put("wallet", walletName);
		jsonObject.put("password", walletPassword);
		jsonObject.put("account", sender);
		jsonObject.put("issuer", issuer);

		jsonObject.put("addedCosignatories", createCosignatoryArray(addCosignatory));
		jsonObject.put("removedCosignatories", createCosignatoryArray(delCosignatory));

		if (hasMinCosignatories) {
			final JSONObject minCosignatories = new JSONObject();
			minCosignatories.put("relativeChange", relativeChange);
			jsonObject.put("minCosignatories", minCosignatories);
		}
		jsonObject.put("hoursDue", hoursDue);
		jsonObject.put("fee", fee);
		jsonObject.put("multisigFee", multisigFee);
		jsonObject.put("type", type);
		return new JsonDeserializer(jsonObject, null);
	}

	private static JSONArray createCosignatoryArray(final String cosignatory) {
		final JSONArray cosignatoryArray = null != cosignatory ? new JSONArray() : null;
		if (null != cosignatory && !cosignatory.isEmpty()) {
			final JSONObject address = new JSONObject();
			address.put("address", cosignatory);
			cosignatoryArray.add(address);
		}

		return cosignatoryArray;
	}
}
