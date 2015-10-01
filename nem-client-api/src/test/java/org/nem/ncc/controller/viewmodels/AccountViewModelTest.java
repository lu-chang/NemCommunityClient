package org.nem.ncc.controller.viewmodels;

import net.minidev.json.*;
import org.hamcrest.core.*;
import org.junit.*;
import org.nem.core.crypto.PublicKey;
import org.nem.core.model.*;
import org.nem.core.model.ncc.*;
import org.nem.core.model.primitive.*;
import org.nem.core.serialization.JsonSerializer;
import org.nem.ncc.test.*;

import java.util.List;
import java.util.stream.*;

public class AccountViewModelTest {

	@Test
	public void viewModelCanBeCreatedAroundExplicitParameters() {
		// Arrange:
		final AccountInfo account = createAccountInfo(null, Address.fromEncoded("xyz"));
		final List<AccountInfo> multisigAccounts = createAccountInfos();
		final List<AccountInfo> cosignatories = createAccountInfos();
		final PublicKey remotePublicKey = Utils.generateRandomPublicKey();

		// Act:
		final AccountViewModel viewModel = createAccountViewModel(
				account,
				multisigAccounts,
				cosignatories,
				remotePublicKey);

		// Assert:
		Assert.assertThat(viewModel.getAddress(), IsEqual.equalTo(Address.fromEncoded("xyz")));
		Assert.assertThat(viewModel.getRemoteStatus(), IsEqual.equalTo(AccountRemoteStatus.INACTIVE));
		Assert.assertThat(viewModel.getBalance(), IsEqual.equalTo(Amount.fromNem(271)));
		Assert.assertThat(viewModel.getVestedBalance(), IsEqual.equalTo(Amount.fromNem(234)));
		Assert.assertThat(viewModel.getHarvestedBlocks(), IsEqual.equalTo(new BlockAmount(3)));
		Assert.assertThat(viewModel.getImportance(), IsEqual.equalTo(3.7));
		Assert.assertThat(viewModel.getPublicKey(), IsNull.nullValue());
		Assert.assertThat(viewModel.getStatus(), IsEqual.equalTo(AccountStatus.LOCKED));
		Assert.assertThat(viewModel.getMultisigAccounts(), IsEquivalent.equivalentTo(multisigAccounts));
		Assert.assertThat(viewModel.getCosignatories(), IsEquivalent.equivalentTo(cosignatories));
		Assert.assertThat(viewModel.getRemotePublicKey(), IsEqual.equalTo(remotePublicKey));
	}

	@Test
	public void viewModelCanBeCreatedAroundAccountWithPublicKey() {
		// Arrange:
		final Address address = Utils.generateRandomAddressWithPublicKey();
		final AccountInfo account = createAccountInfo(null, address);

		// Act:
		final AccountViewModel viewModel = createAccountViewModel(
				account,
				createAccountInfos(),
				createAccountInfos(),
				null);

		// Assert:
		Assert.assertThat(viewModel.getPublicKey(), IsNull.notNullValue());
		Assert.assertThat(viewModel.getPublicKey(), IsEqual.equalTo(address.getPublicKey()));
	}

	@Test
	public void viewModelCanBeCreatedAroundAccountWithoutRemotePublicKey() {
		// Arrange:
		final AccountInfo account = createAccountInfo(null, Utils.generateRandomAddress());

		// Act:
		final AccountViewModel viewModel = createAccountViewModel(
				account,
				createAccountInfos(),
				createAccountInfos(),
				null);

		// Assert:
		Assert.assertThat(viewModel.getRemotePublicKey(), IsNull.nullValue());
	}

	@Test
	public void viewModelCanBeCreatedAroundAccountMetaDataPair() {
		// Arrange:
		final AccountInfo account = createAccountInfo(null, Address.fromEncoded("xyz"));
		final List<AccountInfo> multisigAccounts = createAccountInfos();
		final List<AccountInfo> cosignatories = createAccountInfos();
		final AccountMetaDataPair pair = new AccountMetaDataPair(
				account,
				new AccountMetaData(AccountStatus.LOCKED, AccountRemoteStatus.INACTIVE, multisigAccounts, cosignatories));

		// Act:
		final AccountViewModel viewModel = new AccountViewModel(pair, null);

		// Assert:
		Assert.assertThat(viewModel.getAddress(), IsEqual.equalTo(Address.fromEncoded("xyz")));
		Assert.assertThat(viewModel.getRemoteStatus(), IsEqual.equalTo(AccountRemoteStatus.INACTIVE));
		Assert.assertThat(viewModel.getBalance(), IsEqual.equalTo(Amount.fromNem(271)));
		Assert.assertThat(viewModel.getVestedBalance(), IsEqual.equalTo(Amount.fromNem(234)));
		Assert.assertThat(viewModel.getHarvestedBlocks(), IsEqual.equalTo(new BlockAmount(3)));
		Assert.assertThat(viewModel.getImportance(), IsEqual.equalTo(3.7));
		Assert.assertThat(viewModel.getPublicKey(), IsNull.nullValue());
		Assert.assertThat(viewModel.getStatus(), IsEqual.equalTo(AccountStatus.LOCKED));
		Assert.assertThat(viewModel.getMultisigAccounts(), IsEquivalent.equivalentTo(multisigAccounts));
		Assert.assertThat(viewModel.getCosignatories(), IsEquivalent.equivalentTo(cosignatories));
	}

	@Test
	public void viewModelCanBeSerializedWithBothMultisigAccountsAndCosignatories() {
		// Arrange:
		assertCanBeSerialized(createAccountInfos(), createAccountInfos(), 10);
	}

	@Test
	public void viewModelCanBeSerializedWithCosignatoriesButNotMultisigAccounts() {
		// Arrange:
		assertCanBeSerialized(null, createAccountInfos(), 9);
	}

	@Test
	public void viewModelCanBeSerializedWithMultisigAccountsButNotCosignatories() {
		// Arrange:
		assertCanBeSerialized(createAccountInfos(), null, 9);
	}

	@Test
	public void viewModelCanBeSerializedWithNeitherMultisigAccountsNorCosignatories() {
		// Arrange:
		assertCanBeSerialized(null, null, 8);
	}

	@Test
	public void viewModelCanBeSerializedWithRemotePublicKey() {
		// Arrange:
		assertCanBeSerialized(null, null, Utils.generateRandomPublicKey(), 10);
	}

	private static void assertCanBeSerialized(
			final List<AccountInfo> multisigAccounts,
			final List<AccountInfo> cosignatoryAccounts,
			final int expectedSize) {
		assertCanBeSerialized(multisigAccounts, cosignatoryAccounts, null, expectedSize);
	}

	private static void assertCanBeSerialized(
			final List<AccountInfo> multisigAccounts,
			final List<AccountInfo> cosignatoryAccounts,
			final PublicKey remotePublicKey,
			final int expectedSize) {
		// Arrange:
		final Address address = Utils.generateRandomAddressWithPublicKey();
		final AccountInfo account = createAccountInfo(null, address);

		final AccountViewModel viewModel = createAccountViewModel(account, multisigAccounts, cosignatoryAccounts, remotePublicKey);

		// Act:
		final JSONObject jsonObject = JsonSerializer.serializeToJson(viewModel);

		// Assert:
		Assert.assertThat(jsonObject.size(), IsEqual.equalTo(expectedSize));
		Assert.assertThat(jsonObject.get("address"), IsEqual.equalTo(account.getAddress().getEncoded()));
		Assert.assertThat(jsonObject.get("remoteStatus"), IsEqual.equalTo("INACTIVE"));
		Assert.assertThat(jsonObject.get("publicKey"), IsEqual.equalTo(address.getPublicKey().toString()));
		Assert.assertThat(jsonObject.get("balance"), IsEqual.equalTo(271000000L));
		Assert.assertThat(jsonObject.get("vestedBalance"), IsEqual.equalTo(234000000L));
		Assert.assertThat(jsonObject.get("importance"), IsEqual.equalTo(3.7));
		Assert.assertThat(jsonObject.get("harvestedBlocks"), IsEqual.equalTo(3L));
		Assert.assertThat(jsonObject.get("status"), IsEqual.equalTo("LOCKED"));
		assertAccountsMatch((JSONArray)jsonObject.get("multisigAccounts"), multisigAccounts);
		assertAccountsMatch((JSONArray)jsonObject.get("cosignatories"), cosignatoryAccounts);

		if (null != remotePublicKey) {
			Assert.assertThat(jsonObject.get("remotePublicKey"), IsEqual.equalTo(remotePublicKey.toString()));
			Assert.assertThat(jsonObject.get("remoteAddress"), IsEqual.equalTo(Address.fromPublicKey(remotePublicKey).toString()));
		}
	}

	private static void assertAccountsMatch(final JSONArray jsonAccounts, final List<AccountInfo> originalAccounts) {
		if (null == originalAccounts) {
			Assert.assertThat(jsonAccounts, IsNull.nullValue());
			return;
		}

		Assert.assertThat(jsonAccounts.size(), IsEqual.equalTo(originalAccounts.size()));
		for (int i = 0; i < jsonAccounts.size(); ++i) {
			assertAccountInfoMatches((JSONObject)jsonAccounts.get(i), originalAccounts.get(i));
		}
	}

	private static void assertAccountInfoMatches(final JSONObject jsonAccountInfo, final AccountInfo originalAccountInfo) {
		Assert.assertThat(jsonAccountInfo.size(), IsEqual.equalTo(8));
		Assert.assertThat(jsonAccountInfo.get("address"), IsEqual.equalTo(originalAccountInfo.getAddress().getEncoded()));
		Assert.assertThat(jsonAccountInfo.get("publicKey"), IsNull.nullValue());
		Assert.assertThat(jsonAccountInfo.get("balance"), IsEqual.equalTo(originalAccountInfo.getBalance().getNumMicroNem()));
		Assert.assertThat(jsonAccountInfo.get("vestedBalance"), IsEqual.equalTo(originalAccountInfo.getVestedBalance().getNumMicroNem()));
		Assert.assertThat(jsonAccountInfo.get("importance"), IsEqual.equalTo(originalAccountInfo.getImportance()));
		Assert.assertThat(jsonAccountInfo.get("harvestedBlocks"), IsEqual.equalTo(originalAccountInfo.getNumHarvestedBlocks().getRaw()));
		Assert.assertThat(jsonAccountInfo.get("label"), IsEqual.equalTo(originalAccountInfo.getLabel()));
		Assert.assertThat(jsonAccountInfo.get("multisigInfo"), IsEqual.equalTo(new JSONObject())); // this is not set in any of the dependent accounts
	}

	private static AccountViewModel createAccountViewModel(
			final AccountInfo account,
			final List<AccountInfo> multisigAccounts,
			final List<AccountInfo> cosignatories,
			final PublicKey remotePublicKey) {
		return new AccountViewModel(
				account,
				AccountStatus.LOCKED,
				AccountRemoteStatus.INACTIVE,
				multisigAccounts,
				cosignatories,
				remotePublicKey);
	}

	private static AccountInfo createAccountInfo(final String label, final Address address) {
		return new AccountInfo(
				address,
				Amount.fromNem(271),
				Amount.fromNem(234),
				new BlockAmount(3),
				label,
				3.7);
	}

	private static List<AccountInfo> createAccountInfos() {
		return IntStream.range(0, 3)
				.mapToObj(i -> createAccountInfo(String.format("multisig%d", i + 1), Utils.generateRandomAddress()))
				.collect(Collectors.toList());
	}
}