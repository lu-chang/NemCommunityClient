package org.nem.ncc.controller.viewmodels;

import net.minidev.json.JSONObject;
import org.hamcrest.core.*;
import org.junit.*;
import org.nem.core.crypto.Hash;
import org.nem.core.model.*;
import org.nem.core.model.mosaic.*;
import org.nem.core.model.namespace.NamespaceId;
import org.nem.core.model.ncc.*;
import org.nem.core.model.primitive.*;
import org.nem.core.serialization.JsonSerializer;
import org.nem.core.time.*;
import org.nem.ncc.test.*;

import java.util.Arrays;

public class MosaicCreationTransactionViewModelTest {
	private final static String MOSAIC_NAME = "paddle";
	private final static NamespaceId MOSAIC_NS = new NamespaceId("nem.games.pong");
	private final static String MOSAIC_DESC = "Paddles for pong game";

	// region constructor
	@Test(expected = IllegalArgumentException.class)
	public void cannotCreateViewModelAroundNonMosaicCreation() {
		// Arrange:
		final Transaction transaction = new MockTransaction();

		// Act:
		map(transaction, Address.fromEncoded("foo"));
	}

	@Test
	public void canCreateViewModelAroundUnconfirmedMosaicCreation() {
		// Arrange:
		final MosaicProperties mosaicProperties = Context.createDefaultProperties();
		final Context context = new Context(MOSAIC_NAME, MOSAIC_NS, MOSAIC_DESC, mosaicProperties);

		// Act:
		final MosaicCreationTransactionViewModel viewModel = context.map(Address.fromEncoded("foo"));

		// Assert:
		context.assertViewModel(viewModel);

		Assert.assertThat(viewModel.getMosaicName(), IsEqual.equalTo(MOSAIC_NAME));
		Assert.assertThat(viewModel.getNamespaceName(), IsEqual.equalTo(MOSAIC_NS.toString()));
		Assert.assertThat(viewModel.getDescription(), IsEqual.equalTo(MOSAIC_DESC));
		Assert.assertThat(viewModel.getMosaicProperties(), IsEqual.equalTo(mosaicProperties));

		Assert.assertThat(viewModel.getId(), IsEqual.equalTo(context.transactionHash.getShortId()));
		Assert.assertThat(viewModel.isConfirmed(), IsEqual.equalTo(false));
		Assert.assertThat(viewModel.getConfirmations(), IsEqual.equalTo(0L));
		Assert.assertThat(viewModel.getBlockHeight(), IsEqual.equalTo(0L));
	}

	@Test
	public void canCreateViewModelAroundConfirmedMosaicCreation() {
		// Arrange:
		final MosaicProperties mosaicProperties = Context.createDefaultProperties();
		final Context context = new Context(MOSAIC_NAME, MOSAIC_NS, MOSAIC_DESC, mosaicProperties);

		// Act:
		final MosaicCreationTransactionViewModel viewModel = context.map(
				Address.fromEncoded("foo"),
				new BlockHeight(123)
		);

		// Assert:
		context.assertViewModel(viewModel);

		Assert.assertThat(viewModel.getMosaicName(), IsEqual.equalTo(MOSAIC_NAME));
		Assert.assertThat(viewModel.getNamespaceName(), IsEqual.equalTo(MOSAIC_NS.toString()));
		Assert.assertThat(viewModel.getDescription(), IsEqual.equalTo(MOSAIC_DESC));
		Assert.assertThat(viewModel.getMosaicProperties(), IsEqual.equalTo(mosaicProperties));

		Assert.assertThat(viewModel.getId(), IsEqual.equalTo(33L));
		Assert.assertThat(viewModel.isConfirmed(), IsEqual.equalTo(true));
		Assert.assertThat(viewModel.getConfirmations(), IsEqual.equalTo(123L - 9L + 1));
		Assert.assertThat(viewModel.getBlockHeight(), IsEqual.equalTo(9L));
	}
	//endregion

	//region serialization
	@Test
	public void canSerializeViewModelAroundUnconfirmedTransfer() {
		// Arrange:
		final MosaicProperties mosaicProperties = Context.createDefaultProperties();
		final Context context = new Context(MOSAIC_NAME, MOSAIC_NS, MOSAIC_DESC, mosaicProperties);
		context.transaction.setDeadline(new TimeInstant(222));
		context.recalculateHash();

		// Act:
		final MosaicCreationTransactionViewModel viewModel = context.map(context.sender.getAddress());
		final JSONObject jsonObject = JsonSerializer.serializeToJson(viewModel);

		// Assert:
		Assert.assertThat(jsonObject.size(), IsEqual.equalTo(14));
		Assert.assertThat(jsonObject.get("type"), IsEqual.equalTo(TransactionViewModel.Type.Mosaic_Creation.getValue()));
		Assert.assertThat(jsonObject.get("hash"), IsEqual.equalTo(context.transactionHash.toString()));
		Assert.assertThat(jsonObject.get("sender"), IsEqual.equalTo(context.sender.getAddress().toString()));
		Assert.assertThat(jsonObject.get("timeStamp"), IsEqual.equalTo(SystemTimeProvider.getEpochTimeMillis() + 125 * 1000));
		Assert.assertThat(jsonObject.get("fee"), IsEqual.equalTo(23000000L));

		Assert.assertThat(jsonObject.get("description"), IsEqual.equalTo(MOSAIC_DESC));
		Assert.assertThat(jsonObject.get("mosaicName"), IsEqual.equalTo(MOSAIC_NAME));
		Assert.assertThat(jsonObject.get("namespaceName"), IsEqual.equalTo(MOSAIC_NS.toString()));
		Assert.assertThat(jsonObject.get("properties"), IsNot.not(IsNull.nullValue()));

		Assert.assertThat(jsonObject.get("id"), IsEqual.equalTo(context.transactionHash.getShortId()));
		Assert.assertThat(jsonObject.get("confirmed"), IsEqual.equalTo(0));
		Assert.assertThat(jsonObject.get("confirmations"), IsEqual.equalTo(0L));
		Assert.assertThat(jsonObject.get("blockHeight"), IsEqual.equalTo(0L));
		Assert.assertThat(jsonObject.get("deadline"), IsEqual.equalTo(SystemTimeProvider.getEpochTimeMillis() + 222 * 1000));
	}

	@Test
	public void canSerializeViewModelAroundConfirmedTransfer() {
		// Arrange:
		final MosaicProperties mosaicProperties = Context.createDefaultProperties();
		final Context context = new Context(MOSAIC_NAME, MOSAIC_NS, MOSAIC_DESC, mosaicProperties);
		context.transaction.setDeadline(new TimeInstant(222));
		context.recalculateHash();

		// Act:
		final MosaicCreationTransactionViewModel viewModel = context.map(
				context.sender.getAddress(),
				new BlockHeight(123)
		);
		final JSONObject jsonObject = JsonSerializer.serializeToJson(viewModel);

		// Assert:
		Assert.assertThat(jsonObject.size(), IsEqual.equalTo(14));
		Assert.assertThat(jsonObject.get("type"), IsEqual.equalTo(TransactionViewModel.Type.Mosaic_Creation.getValue()));
		Assert.assertThat(jsonObject.get("hash"), IsEqual.equalTo(context.transactionHash.toString()));
		Assert.assertThat(jsonObject.get("sender"), IsEqual.equalTo(context.sender.getAddress().toString()));
		Assert.assertThat(jsonObject.get("timeStamp"), IsEqual.equalTo(SystemTimeProvider.getEpochTimeMillis() + 125 * 1000));
		Assert.assertThat(jsonObject.get("fee"), IsEqual.equalTo(23000000L));

		Assert.assertThat(jsonObject.get("description"), IsEqual.equalTo(MOSAIC_DESC));
		Assert.assertThat(jsonObject.get("mosaicName"), IsEqual.equalTo(MOSAIC_NAME));
		Assert.assertThat(jsonObject.get("namespaceName"), IsEqual.equalTo(MOSAIC_NS.toString()));
		Assert.assertThat(jsonObject.get("properties"), IsNot.not(IsNull.nullValue()));

		Assert.assertThat(jsonObject.get("id"), IsEqual.equalTo(33L));
		Assert.assertThat(jsonObject.get("confirmed"), IsEqual.equalTo(1));
		Assert.assertThat(jsonObject.get("confirmations"), IsEqual.equalTo(123L - 9L + 1));
		Assert.assertThat(jsonObject.get("blockHeight"), IsEqual.equalTo(9L));
		Assert.assertThat(jsonObject.get("deadline"), IsEqual.equalTo(SystemTimeProvider.getEpochTimeMillis() + 222 * 1000));
	}
	//endregion

	private static MosaicCreationTransactionViewModel map(final Transaction transaction, final Address address) {
		return (MosaicCreationTransactionViewModel)TransactionToViewModelMapper.map(transaction, address);
	}

	private static class Context {
		final Account sender = Utils.generateRandomAccount();
		final Transaction transaction;
		final MosaicDefinition mosaicDefinition;
		Hash transactionHash;

		public static MosaicProperties createDefaultProperties() {
			return new DefaultMosaicProperties(Arrays.asList(
					new NemProperty("divisibility", "2"),
					new NemProperty("initialSupply", "1234"),
					new NemProperty("supplyMutable", "true"),
					new NemProperty("transferable", "true")
			));
		}

		Context(final String mosaicName, final NamespaceId parent, final String description, final MosaicProperties mosaicProperties) {
			this.mosaicDefinition = new MosaicDefinition(
					this.sender,
					new MosaicId(parent, mosaicName),
					new MosaicDescriptor(description),
					mosaicProperties,
					null);
			this.transaction = new MosaicDefinitionCreationTransaction(
					new TimeInstant(125),
					this.sender,
					this.mosaicDefinition,
					MosaicConstants.MOSAIC_CREATION_FEE_SINK,
					Amount.fromNem(12345));
			this.transaction.setFee(Amount.fromNem(23));
			this.recalculateHash();
		}

		public void recalculateHash() {
			this.transactionHash = HashUtils.calculateHash(this.transaction);
		}

		public MosaicCreationTransactionViewModel map(final Address address) {
			return (MosaicCreationTransactionViewModel)TransactionToViewModelMapper.map(this.transaction, address);
		}

		public void assertViewModel(final MosaicCreationTransactionViewModel viewModel) {
			Assert.assertThat(viewModel.getHash(), IsEqual.equalTo(this.transactionHash));
			Assert.assertThat(viewModel.getSigner(), IsEqual.equalTo(this.sender.getAddress()));
			Assert.assertThat(viewModel.getTimeStamp(), IsEqual.equalTo(SystemTimeProvider.getEpochTimeMillis() + 125 * 1000));
			Assert.assertThat(viewModel.getFee(), IsEqual.equalTo(Amount.fromNem(23)));
		}

		private static TransactionMetaData createMetaData(final int height, final long id) {
			return new TransactionMetaData(new BlockHeight(height), id, Hash.ZERO);
		}

		public MosaicCreationTransactionViewModel map(final Address address, final BlockHeight blockHeight) {
			return (MosaicCreationTransactionViewModel)TransactionToViewModelMapper.map(
					new TransactionMetaDataPair(this.transaction, createMetaData(9, 33L)),
					address,
					blockHeight);
		}
	}
}