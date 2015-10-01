package org.nem.ncc.controller.requests;

import net.minidev.json.JSONObject;
import org.hamcrest.core.*;
import org.junit.*;
import org.nem.core.model.Address;
import org.nem.core.model.primitive.Amount;
import org.nem.core.serialization.*;
import org.nem.ncc.controller.viewmodels.TransactionViewModel;
import org.nem.ncc.test.ExceptionAssert;
import org.nem.ncc.wallet.*;

import java.util.*;
import java.util.function.Consumer;

public class TransferSendRequestTest {

	@Test
	public void requestCanBeCreated() {
		// Act:
		final TransferSendRequest request = new TransferSendRequest(
				new WalletName("w"),
				Address.fromEncoded("m"),
				Address.fromEncoded("a"),
				Address.fromEncoded("r"),
				Amount.fromMicroNem(7),
				"m",
				true,
				true,
				5,
				new WalletPassword("p"),
				Amount.fromMicroNem(2),
				Amount.ZERO,
				TransactionViewModel.Type.Transfer.getValue(),
				1234);

		// Assert:
		Assert.assertThat(request.getWalletName(), IsEqual.equalTo(new WalletName("w")));
		Assert.assertThat(request.getMultisigAddress(), IsEqual.equalTo(Address.fromEncoded("m")));
		Assert.assertThat(request.getSenderAddress(), IsEqual.equalTo(Address.fromEncoded("a")));
		Assert.assertThat(request.getRecipientAddress(), IsEqual.equalTo(Address.fromEncoded("r")));
		Assert.assertThat(request.getAmount(), IsEqual.equalTo(Amount.fromMicroNem(7L)));
		Assert.assertThat(request.getMessage(), IsEqual.equalTo("m"));
		Assert.assertThat(request.isHexMessage(), IsEqual.equalTo(true));
		Assert.assertThat(request.shouldEncrypt(), IsEqual.equalTo(true));
		Assert.assertThat(request.getHoursDue(), IsEqual.equalTo(5));
		Assert.assertThat(request.getPassword(), IsEqual.equalTo(new WalletPassword("p")));
		Assert.assertThat(request.getFee(), IsEqual.equalTo(Amount.fromMicroNem(2L)));
		Assert.assertThat(request.getMultisigFee(), IsEqual.equalTo(Amount.ZERO));
		Assert.assertThat(request.getType(), IsEqual.equalTo(TransactionViewModel.Type.Transfer.getValue()));
		Assert.assertThat(request.getVersion(), IsEqual.equalTo(1234));
	}

	@Test
	public void requestCanBeDeserializedWithAllParameters() {
		// Act:
		final TransferSendRequest request = this.createRequestFromJson("w", "a", "r", 7L, "m", 6, 3, 5, "p", 2L, 3L, 20, 1234);

		// Assert:
		Assert.assertThat(request.getWalletName(), IsEqual.equalTo(new WalletName("w")));
		Assert.assertThat(request.getSenderAddress(), IsEqual.equalTo(Address.fromEncoded("a")));
		Assert.assertThat(request.getRecipientAddress(), IsEqual.equalTo(Address.fromEncoded("r")));
		Assert.assertThat(request.getAmount(), IsEqual.equalTo(Amount.fromMicroNem(7L)));
		Assert.assertThat(request.getMessage(), IsEqual.equalTo("m"));
		Assert.assertThat(request.isHexMessage(), IsEqual.equalTo(true));
		Assert.assertThat(request.shouldEncrypt(), IsEqual.equalTo(true));
		Assert.assertThat(request.getHoursDue(), IsEqual.equalTo(5));
		Assert.assertThat(request.getPassword(), IsEqual.equalTo(new WalletPassword("p")));
		Assert.assertThat(request.getFee(), IsEqual.equalTo(Amount.fromMicroNem(2L)));
		Assert.assertThat(request.getMultisigFee(), IsEqual.equalTo(Amount.fromMicroNem(3L)));
		Assert.assertThat(request.getType(), IsEqual.equalTo(TransactionViewModel.Type.Transfer.getValue()));
		Assert.assertThat(request.getVersion(), IsEqual.equalTo(1234));
	}

	@Test
	public void requestCanBeDeserializedWithoutMessage() {
		// Act:
		final TransferSendRequest request = this.createRequestFromJson("w", "a", "r", 7L, null, 0, 0, 5, "p", 2L, 3L, 1, 1);

		// Assert:
		Assert.assertThat(request.getMessage(), IsNull.nullValue());
		Assert.assertThat(request.isHexMessage(), IsEqual.equalTo(false));
		Assert.assertThat(request.shouldEncrypt(), IsEqual.equalTo(false));
	}

	@Test
	public void requestCannotBeDeserializedWithMissingRequiredParameters() {
		// Arrange:
		final List<Consumer<Void>> actions = Arrays.asList(
				v -> this.createRequestFromJson(null, "a", "r", 7L, "m", 1, 3, 5, "p", 2L, 3L, 1, 1),
				v -> this.createRequestFromJson("w", null, "r", 7L, "m", 1, 3, 5, "p", 2L, 3L, 1, 1),
				v -> this.createRequestFromJson("w", "a", null, 7L, "m", 1, 3, 5, "p", 2L, 3L, 1, 1),
				v -> this.createRequestFromJson("w", "a", "r", null, "m", 1, 3, 5, "p", 2L, 3L, 1, 1),
				v -> this.createRequestFromJson("w", "a", "r", 7L, "m", null, 3, 5, "p", 2L, 3L, 1, 1),
				v -> this.createRequestFromJson("w", "a", "r", 7L, "m", 1, null, 5, "p", 2L, 3L, 1, 1),
				v -> this.createRequestFromJson("w", "a", "r", 7L, "m", 1, 3, null, "p", 2L, 3L, 1, 1),
				v -> this.createRequestFromJson("w", "a", "r", 7L, "m", 1, 3, 5, null, 2L, 3L, 1, 1),
				v -> this.createRequestFromJson("w", "a", "r", 7L, "m", 1, 3, 5, "p", null, 3L, 1, 1),
				v -> this.createRequestFromJson("w", "a", "r", 7L, "m", 1, 3, 5, "p", 2L, null, 1, 1),
				v -> this.createRequestFromJson("w", "a", "r", 7L, "m", 1, 3, 5, "p", 2L, 3L, null, 1),
				v -> this.createRequestFromJson("w", "a", "r", 7L, "m", 1, 3, 5, "p", 2L, 3L, 1, null));

		// Assert:
		for (final Consumer<Void> action : actions) {
			ExceptionAssert.assertThrows(v -> action.accept(null), SerializationException.class);
		}
	}

	private TransferSendRequest createRequestFromJson(
			final String walletName,
			final String accountId,
			final String recipientId,
			final Long amount,
			final String message,
			final Integer isHexMessage,
			final Integer shouldEncrypt,
			final Integer hoursDue,
			final String password,
			final Long fee,
			final Long multisigFee,
			final Integer type,
			final Integer version) {
		final JSONObject jsonObject = new JSONObject();
		jsonObject.put("wallet", walletName);
		jsonObject.put("account", accountId);
		jsonObject.put("recipient", recipientId);
		jsonObject.put("amount", amount);
		jsonObject.put("message", message);
		jsonObject.put("hexMessage", isHexMessage);
		jsonObject.put("encrypt", shouldEncrypt);
		jsonObject.put("hoursDue", hoursDue);
		jsonObject.put("password", password);
		jsonObject.put("fee", fee);
		jsonObject.put("multisigFee", multisigFee);
		jsonObject.put("type", type);
		jsonObject.put("version", version);
		return new TransferSendRequest(new JsonDeserializer(jsonObject, null));
	}
}