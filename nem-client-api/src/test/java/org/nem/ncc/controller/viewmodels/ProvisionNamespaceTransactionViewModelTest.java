package org.nem.ncc.controller.viewmodels;

import net.minidev.json.JSONObject;
import org.hamcrest.core.IsEqual;
import org.junit.*;
import org.nem.core.crypto.Hash;
import org.nem.core.model.*;
import org.nem.core.model.namespace.*;
import org.nem.core.model.ncc.*;
import org.nem.core.model.primitive.*;
import org.nem.core.serialization.JsonSerializer;
import org.nem.core.time.*;
import org.nem.ncc.test.*;

public class ProvisionNamespaceTransactionViewModelTest {

	//region constructor

	@Test(expected = IllegalArgumentException.class)
	public void cannotCreateViewModelAroundNonProvisionNamespace() {
		// Arrange:
		final Transaction transaction = new MockTransaction();

		// Act:
		map(transaction, Address.fromEncoded("foo"));
	}

	@Test
	public void canCreateViewModelAroundUnconfirmedProvisionNamespace() {
		// Arrange:
		final Context context = new Context(new NamespaceIdPart("root"), null);

		// Act:
		final ProvisionNamespaceTransactionViewModel viewModel = context.map(Address.fromEncoded("foo"));

		// Assert:
		context.assertViewModel(viewModel);

		Assert.assertThat(viewModel.getNamespaceName(), IsEqual.equalTo("root"));
		Assert.assertThat(viewModel.getId(), IsEqual.equalTo(context.transactionHash.getShortId()));
		Assert.assertThat(viewModel.isConfirmed(), IsEqual.equalTo(false));
		Assert.assertThat(viewModel.getConfirmations(), IsEqual.equalTo(0L));
		Assert.assertThat(viewModel.getBlockHeight(), IsEqual.equalTo(0L));
	}

	@Test
	public void canCreateViewModelAroundConfirmedProvisionNamespace() {
		// Arrange:
		final Context context = new Context(new NamespaceIdPart("root"), null);

		// Act:
		final ProvisionNamespaceTransactionViewModel viewModel = context.map(
				Address.fromEncoded("foo"),
				new BlockHeight(12));

		// Assert:
		context.assertViewModel(viewModel);

		Assert.assertThat(viewModel.getNamespaceName(), IsEqual.equalTo("root"));
		Assert.assertThat(viewModel.getId(), IsEqual.equalTo(14L));
		Assert.assertThat(viewModel.isConfirmed(), IsEqual.equalTo(true));
		Assert.assertThat(viewModel.getConfirmations(), IsEqual.equalTo(6L));
		Assert.assertThat(viewModel.getBlockHeight(), IsEqual.equalTo(7L));
	}

	@Test
	public void canCreateViewModelAroundSecondLevelProvisionNamespace() {
		// Arrange:
		final Context context = new Context(new NamespaceIdPart("fizzbuzz"), new NamespaceId("root"));

		// Act:
		final ProvisionNamespaceTransactionViewModel viewModel = context.map(
				Address.fromEncoded("foo"),
				new BlockHeight(12));

		// Assert:
		context.assertViewModel(viewModel);

		Assert.assertThat(viewModel.getNamespaceName(), IsEqual.equalTo("root.fizzbuzz"));
		Assert.assertThat(viewModel.getId(), IsEqual.equalTo(14L));
		Assert.assertThat(viewModel.isConfirmed(), IsEqual.equalTo(true));
		Assert.assertThat(viewModel.getConfirmations(), IsEqual.equalTo(6L));
		Assert.assertThat(viewModel.getBlockHeight(), IsEqual.equalTo(7L));
	}

	//endregion

	//region serialization

	@Test
	public void canSerializeViewModelAroundUnconfirmedTransfer() {
		// Arrange:
		final Context context = new Context(new NamespaceIdPart("root"), null);
		context.transaction.setDeadline(new TimeInstant(222));
		context.recalculateHash();

		// Act:
		final ProvisionNamespaceTransactionViewModel viewModel = context.map(context.rentalFeeSink.getAddress());
		final JSONObject jsonObject = JsonSerializer.serializeToJson(viewModel);

		// Assert:
		Assert.assertThat(jsonObject.size(), IsEqual.equalTo(13));
		Assert.assertThat(jsonObject.get("type"), IsEqual.equalTo(TransactionViewModel.Type.Provision_Namespace.getValue()));
		Assert.assertThat(jsonObject.get("hash"), IsEqual.equalTo(context.transactionHash.toString()));
		Assert.assertThat(jsonObject.get("sender"), IsEqual.equalTo(context.sender.getAddress().toString()));
		Assert.assertThat(jsonObject.get("timeStamp"), IsEqual.equalTo(SystemTimeProvider.getEpochTimeMillis() + 125 * 1000));
		Assert.assertThat(jsonObject.get("fee"), IsEqual.equalTo(23000000L));

		Assert.assertThat(jsonObject.get("rentalFeeSink"), IsEqual.equalTo(context.rentalFeeSink.getAddress().toString()));
		Assert.assertThat(jsonObject.get("amount"), IsEqual.equalTo(576000000L));
		Assert.assertThat(jsonObject.get("namespace"), IsEqual.equalTo("root"));

		Assert.assertThat(jsonObject.get("id"), IsEqual.equalTo(context.transactionHash.getShortId()));
		Assert.assertThat(jsonObject.get("confirmed"), IsEqual.equalTo(0));
		Assert.assertThat(jsonObject.get("confirmations"), IsEqual.equalTo(0L));
		Assert.assertThat(jsonObject.get("blockHeight"), IsEqual.equalTo(0L));
		Assert.assertThat(jsonObject.get("deadline"), IsEqual.equalTo(SystemTimeProvider.getEpochTimeMillis() + 222 * 1000));
	}

	@Test
	public void canSerializeViewModelAroundConfirmedTransfer() {
		// Arrange:
		final Context context = new Context(new NamespaceIdPart("root"), null);
		context.transaction.setDeadline(new TimeInstant(222));
		context.recalculateHash();

		// Act:
		final ProvisionNamespaceTransactionViewModel viewModel = context.map(
				Address.fromEncoded("foo"),
				new BlockHeight(12));
		final JSONObject jsonObject = JsonSerializer.serializeToJson(viewModel);

		// Assert:
		Assert.assertThat(jsonObject.size(), IsEqual.equalTo(13));
		Assert.assertThat(jsonObject.get("type"), IsEqual.equalTo(TransactionViewModel.Type.Provision_Namespace.getValue()));
		Assert.assertThat(jsonObject.get("hash"), IsEqual.equalTo(context.transactionHash.toString()));
		Assert.assertThat(jsonObject.get("sender"), IsEqual.equalTo(context.sender.getAddress().toString()));
		Assert.assertThat(jsonObject.get("timeStamp"), IsEqual.equalTo(SystemTimeProvider.getEpochTimeMillis() + 125 * 1000));
		Assert.assertThat(jsonObject.get("fee"), IsEqual.equalTo(23000000L));

		Assert.assertThat(jsonObject.get("rentalFeeSink"), IsEqual.equalTo(context.rentalFeeSink.getAddress().toString()));
		Assert.assertThat(jsonObject.get("amount"), IsEqual.equalTo(576000000L));
		Assert.assertThat(jsonObject.get("namespace"), IsEqual.equalTo("root"));

		Assert.assertThat(jsonObject.get("confirmed"), IsEqual.equalTo(1));
		Assert.assertThat(jsonObject.get("confirmations"), IsEqual.equalTo(12L - 7L + 1));
		Assert.assertThat(jsonObject.get("blockHeight"), IsEqual.equalTo(7L));
		Assert.assertThat(jsonObject.get("deadline"), IsEqual.equalTo(SystemTimeProvider.getEpochTimeMillis() + 222 * 1000));
	}

	//endregion

	//region confirmed

	@Test
	public void transactionInLastBlockIsConfirmed() {
		// Arrange:
		final Context context = new Context(new NamespaceIdPart("fizzbuzz"), new NamespaceId("root"));

		// Act:
		final ProvisionNamespaceTransactionViewModel viewModel = context.map(
				Address.fromEncoded("foo"),
				new BlockHeight(7));

		// Assert:
		Assert.assertThat(viewModel.isConfirmed(), IsEqual.equalTo(true));
		Assert.assertThat(viewModel.getConfirmations(), IsEqual.equalTo(1L));
		Assert.assertThat(viewModel.getBlockHeight(), IsEqual.equalTo(7L));
		Assert.assertThat(viewModel.getId(), IsEqual.equalTo(14L));
	}

	//endregion

	private static TransactionMetaData createMetaData(final int height, final long id) {
		return new TransactionMetaData(new BlockHeight(height), id, Hash.ZERO);
	}

	private static ProvisionNamespaceTransactionViewModel map(final Transaction transaction, final Address address) {
		return (ProvisionNamespaceTransactionViewModel)TransactionToViewModelMapper.map(transaction, address);
	}

	private static class Context {
		private final Account sender = Utils.generateRandomAccount();
		private final Account rentalFeeSink = Utils.generateRandomAccount();
		private final Transaction transaction;
		private Hash transactionHash;

		public Context(final NamespaceIdPart current, final NamespaceId parent) {
			this.transaction = new ProvisionNamespaceTransaction(
					new TimeInstant(125),
					this.sender,
					this.rentalFeeSink,
					Amount.fromNem(576),
					current,
					parent);
			this.transaction.setFee(Amount.fromNem(23));
			this.recalculateHash();
		}

		public void recalculateHash() {
			this.transactionHash = HashUtils.calculateHash(this.transaction);
		}

		public ProvisionNamespaceTransactionViewModel map(final Address address) {
			return (ProvisionNamespaceTransactionViewModel)TransactionToViewModelMapper.map(this.transaction, address);
		}

		public void assertViewModel(final ProvisionNamespaceTransactionViewModel viewModel) {
			Assert.assertThat(viewModel.getHash(), IsEqual.equalTo(this.transactionHash));
			Assert.assertThat(viewModel.getSigner(), IsEqual.equalTo(this.sender.getAddress()));
			Assert.assertThat(viewModel.getTimeStamp(), IsEqual.equalTo(SystemTimeProvider.getEpochTimeMillis() + 125 * 1000));
			Assert.assertThat(viewModel.getFee(), IsEqual.equalTo(Amount.fromNem(23)));

			Assert.assertThat(viewModel.getRentalFeeSink(), IsEqual.equalTo(this.rentalFeeSink.getAddress()));
			Assert.assertThat(viewModel.getRentalFee(), IsEqual.equalTo(Amount.fromNem(576)));
		}

		public ProvisionNamespaceTransactionViewModel map(final Address address, final BlockHeight blockHeight) {
			return (ProvisionNamespaceTransactionViewModel)TransactionToViewModelMapper.map(
					new TransactionMetaDataPair(this.transaction, createMetaData(7, 14L)),
					address,
					blockHeight);
		}
	}
}