package org.nem.ncc.controller.requests;

import net.minidev.json.JSONObject;
import org.hamcrest.core.*;
import org.junit.*;
import org.nem.core.crypto.*;
import org.nem.core.model.Address;
import org.nem.core.model.primitive.Amount;
import org.nem.core.serialization.*;
import org.nem.ncc.controller.viewmodels.TransactionViewModel;
import org.nem.ncc.test.*;
import org.nem.ncc.wallet.*;

import java.util.*;
import java.util.function.Consumer;

public class TransferImportanceRequestTest {
	private final Address TEST_ADDRESS = Utils.generateRandomAddress();
	private final Address TEST_MULTISIG_ADDRESS = Utils.generateRandomAddress();
	private final WalletName TEST_WALLET_NAME = new WalletName("wal");
	private final WalletPassword TEST_WALLET_PASS = new WalletPassword("From forth the fatal loins");
	private final int TEST_TYPE = TransactionViewModel.Type.Importance_Transfer.getValue();
	private final int TEST_HOURS_DUE = 8;
	private final Amount TEST_FEE = Amount.fromNem(10);
	private final Amount TEST_MULTISIG_FEE = Amount.fromNem(123);
	private final KeyPair TEST_KEY_PAIR = new KeyPair();

	@Test
	public void requestCanBeCreated() {
		// Act:
		final TransferImportanceRequest request = this.createRequest();

		// Assert:
		Assert.assertThat(request.getAddress(), IsEqual.equalTo(this.TEST_ADDRESS));
		Assert.assertThat(request.getWalletName(), IsEqual.equalTo(this.TEST_WALLET_NAME));
		Assert.assertThat(request.getWalletPassword(), IsEqual.equalTo(this.TEST_WALLET_PASS));
		Assert.assertThat(request.getHoursDue(), IsEqual.equalTo(this.TEST_HOURS_DUE));
	}

	@Test
	public void requestCanBeDeserializedWithoutPublicKey() {
		// Act:
		final TransferImportanceRequest request = this.createRequestFromJson(
				this.TEST_ADDRESS.getEncoded(),
				this.TEST_WALLET_NAME.toString(),
				this.TEST_WALLET_PASS.toString(),
				this.TEST_TYPE,
				this.TEST_HOURS_DUE,
				this.TEST_FEE.getNumMicroNem(),
				null,
				this.TEST_MULTISIG_ADDRESS.toString(),
				this.TEST_MULTISIG_FEE.getNumMicroNem());

		// Assert:
		Assert.assertThat(request.getAddress(), IsEqual.equalTo(this.TEST_ADDRESS));
		Assert.assertThat(request.getWalletName(), IsEqual.equalTo(this.TEST_WALLET_NAME));
		Assert.assertThat(request.getWalletPassword(), IsEqual.equalTo(this.TEST_WALLET_PASS));
		Assert.assertThat(request.getType(), IsEqual.equalTo(this.TEST_TYPE));
		Assert.assertThat(request.getFee(), IsEqual.equalTo(this.TEST_FEE));
		Assert.assertThat(request.getHoursDue(), IsEqual.equalTo(this.TEST_HOURS_DUE));
		Assert.assertThat(request.getPublicKey(), IsNull.nullValue());
		Assert.assertThat(request.getMultisigAddress(), IsEqual.equalTo(this.TEST_MULTISIG_ADDRESS));
		Assert.assertThat(request.getMultisigFee(), IsEqual.equalTo(this.TEST_MULTISIG_FEE));
	}

	@Test
	public void requestCanBeDeserializedWithPublicKey() {
		// Act:
		final TransferImportanceRequest request = this.createRequestFromJson(
				this.TEST_ADDRESS.getEncoded(),
				this.TEST_WALLET_NAME.toString(),
				this.TEST_WALLET_PASS.toString(),
				this.TEST_TYPE,
				this.TEST_HOURS_DUE,
				this.TEST_FEE.getNumMicroNem(),
				this.TEST_KEY_PAIR.getPublicKey(),
				this.TEST_MULTISIG_ADDRESS.toString(),
				this.TEST_MULTISIG_FEE.getNumMicroNem());

		// Assert:
		Assert.assertThat(request.getAddress(), IsEqual.equalTo(this.TEST_ADDRESS));
		Assert.assertThat(request.getWalletName(), IsEqual.equalTo(this.TEST_WALLET_NAME));
		Assert.assertThat(request.getWalletPassword(), IsEqual.equalTo(this.TEST_WALLET_PASS));
		Assert.assertThat(request.getType(), IsEqual.equalTo(this.TEST_TYPE));
		Assert.assertThat(request.getFee(), IsEqual.equalTo(this.TEST_FEE));
		Assert.assertThat(request.getHoursDue(), IsEqual.equalTo(this.TEST_HOURS_DUE));
		Assert.assertThat(request.getPublicKey(), IsEqual.equalTo(this.TEST_KEY_PAIR.getPublicKey()));
		Assert.assertThat(request.getMultisigAddress(), IsEqual.equalTo(this.TEST_MULTISIG_ADDRESS));
		Assert.assertThat(request.getMultisigFee(), IsEqual.equalTo(this.TEST_MULTISIG_FEE));
	}

	@Test
	public void requestCannotBeDeserializedWithMissingParameters() {
		// Arrange:
		final Address address = Utils.generateRandomAddress();
		final List<Consumer<Void>> actions = Arrays.asList(
				v -> this.createRequestFromJson(address.getEncoded(), null, null, 2, 0, 123, null, "", 0),
				v -> this.createRequestFromJson(null, "wal", null, 2, 0, 123, null, "", 0),
				v -> this.createRequestFromJson(address.getEncoded(), "wal", null, 2, 0, 123, null, "", 0));

		// Assert:
		for (final Consumer<Void> action : actions) {
			ExceptionAssert.assertThrows(
					v -> action.accept(null),
					SerializationException.class);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void requestCannotBeCreatedAroundInvalidAccountId() {
		// Act:
		new AccountWalletPasswordRequest(Address.fromEncoded("FOO"), null, null);
	}

	private TransferImportanceRequest createRequest() {
		return new TransferImportanceRequest(
				this.TEST_ADDRESS,
				this.TEST_WALLET_NAME,
				this.TEST_WALLET_PASS,
				this.TEST_MULTISIG_ADDRESS,
				this.TEST_TYPE,
				this.TEST_HOURS_DUE,
				this.TEST_FEE,
				this.TEST_MULTISIG_FEE,
				this.TEST_KEY_PAIR.getPublicKey());
	}

	private TransferImportanceRequest createRequestFromJson(
			final String address,
			final String walletName,
			final String walletPassword,
			final int type,
			final int hoursDue,
			final long fee,
			final PublicKey publicKey,
			final String multisigAddress,
			final long multisigFee) {
		final JSONObject jsonObject = new JSONObject();
		jsonObject.put("account", address);
		jsonObject.put("wallet", walletName);
		jsonObject.put("password", walletPassword);
		jsonObject.put("type", type);
		jsonObject.put("hoursDue", hoursDue);
		jsonObject.put("fee", fee);
		jsonObject.put("multisigAddress", multisigAddress);
		jsonObject.put("multisigFee", multisigFee);

		if (publicKey != null) {
			final JSONObject key = new JSONObject();
			key.put("value", publicKey.toString());
			jsonObject.put("publicKey", key);
		}

		return new TransferImportanceRequest(new JsonDeserializer(jsonObject, null));
	}
}
