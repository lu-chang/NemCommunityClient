package org.nem.ncc.controller.requests;

import net.minidev.json.JSONObject;
import org.hamcrest.core.IsEqual;
import org.junit.*;
import org.nem.core.crypto.Hash;
import org.nem.core.model.Address;
import org.nem.core.model.primitive.Amount;
import org.nem.core.serialization.*;
import org.nem.ncc.test.*;
import org.nem.ncc.wallet.*;

import java.util.*;

public class MultisigSignatureRequestTest {

	//region constructor

	@Test
	public void requestCanBeCreated() {
		// Arrange:
		final Address senderAddress = Utils.generateRandomAddress();
		final Address multisigAddress = Utils.generateRandomAddress();
		final Hash innerTransactionHash = Utils.generateRandomHash();

		// Act:
		final MultisigSignatureRequest request = new MultisigSignatureRequest(
				new WalletName("wlt"),
				new WalletPassword("pwd"),
				senderAddress,
				multisigAddress,
				innerTransactionHash,
				12,
				Amount.fromNem(123));

		// Assert:
		Assert.assertThat(request.getWalletName(), IsEqual.equalTo(new WalletName("wlt")));
		Assert.assertThat(request.getPassword(), IsEqual.equalTo(new WalletPassword("pwd")));
		Assert.assertThat(request.getSenderAddress(), IsEqual.equalTo(senderAddress));
		Assert.assertThat(request.getMultisigAddress(), IsEqual.equalTo(multisigAddress));
		Assert.assertThat(request.getInnerTransactionHash(), IsEqual.equalTo(innerTransactionHash));
		Assert.assertThat(request.getHoursDue(), IsEqual.equalTo(12));
		Assert.assertThat(request.getFee(), IsEqual.equalTo(Amount.fromNem(123)));
	}

	//endregion

	//region serialization

	@Test
	public void requestCanBeDeserializedWithAllParameters() {
		// Arrange:
		final Hash innerTransactionHash = Utils.generateRandomHash();
		final Deserializer deserializer = createDeserializer("w", "p", "a", "m", innerTransactionHash.toString(), 12, 123L);

		// Act:
		final MultisigSignatureRequest request = new MultisigSignatureRequest(deserializer);

		// Assert:
		Assert.assertThat(request.getWalletName(), IsEqual.equalTo(new WalletName("w")));
		Assert.assertThat(request.getPassword(), IsEqual.equalTo(new WalletPassword("p")));
		Assert.assertThat(request.getSenderAddress(), IsEqual.equalTo(Address.fromEncoded("a")));
		Assert.assertThat(request.getMultisigAddress(), IsEqual.equalTo(Address.fromEncoded("m")));
		Assert.assertThat(request.getInnerTransactionHash(), IsEqual.equalTo(innerTransactionHash));
		Assert.assertThat(request.getHoursDue(), IsEqual.equalTo(12));
		Assert.assertThat(request.getFee(), IsEqual.equalTo(Amount.fromMicroNem(123)));
	}

	@Test
	public void requestCannotBeDeserializedWithMissingRequiredParameters() {
		// Arrange:
		final String hash = Utils.generateRandomHash().toString();
		final List<Deserializer> deserializers = Arrays.asList(
				createDeserializer(null, "p", "a", "m", hash, 12, 123L),
				createDeserializer("w", null, "a", "m", hash, 12, 123L),
				createDeserializer("w", "p", null, "m", hash, 12, 123L),
				createDeserializer("w", "p", "a", null, hash, 12, 123L),
				createDeserializer("w", "p", "a", "m", null, 12, 123L),
				createDeserializer("w", "p", "a", "m", hash, null, 123L),
				createDeserializer("w", "p", "a", "m", hash, 12, null)
		);

		// Assert:
		for (final Deserializer deserializer : deserializers) {
			ExceptionAssert.assertThrows(v -> new MultisigSignatureRequest(deserializer), MissingRequiredPropertyException.class);
		}
	}

	//endregion

	private static Deserializer createDeserializer(
			final String walletName,
			final String walletPassword,
			final String senderAddress,
			final String multisigAddress,
			final String innerTransactionHash,
			final Integer hoursDue,
			final Long fee) {
		final JSONObject jsonObject = new JSONObject();
		jsonObject.put("wallet", walletName);
		jsonObject.put("password", walletPassword);
		jsonObject.put("account", senderAddress);
		jsonObject.put("multisigAddress", multisigAddress);
		final JSONObject innerHash = new JSONObject();
		innerHash.put("data", innerTransactionHash);
		jsonObject.put("innerHash", innerHash);
		jsonObject.put("hoursDue", hoursDue);
		jsonObject.put("fee", fee);
		return new JsonDeserializer(jsonObject, null);
	}
}
