package org.nem.ncc.services;

import org.hamcrest.core.*;
import org.junit.*;
import org.mockito.Mockito;
import org.nem.core.crypto.*;
import org.nem.core.model.*;
import org.nem.core.model.ncc.*;
import org.nem.ncc.controller.viewmodels.AccountViewModel;
import org.nem.ncc.test.Utils;
import org.nem.ncc.wallet.WalletAccount;

public class AccountMapperTest {

	@Test
	public void addressCanBeMappedToViewModel() {
		// Arrange:
		final Address address = Address.fromEncoded("sigma");

		final AccountMetaDataPairLookup accountLookup = Mockito.mock(AccountMetaDataPairLookup.class);
		Mockito.when(accountLookup.findPairByAddress(address))
				.thenReturn(createMetaDataPair(address));

		// Act:
		final AccountMapper mapper = new AccountMapper(accountLookup);
		final AccountViewModel viewModel = mapper.toViewModel(address);

		// Assert:
		Assert.assertThat(viewModel.getAddress(), IsEqual.equalTo(address));
		Assert.assertThat(viewModel.getStatus(), IsEqual.equalTo(AccountStatus.LOCKED));
		Assert.assertThat(viewModel.getRemotePublicKey(), IsNull.nullValue());
	}

	@Test
	public void walletAccountCanBeMappedToViewModel() {
		// Arrange:
		final WalletAccount account = new WalletAccount();
		final PublicKey remotePublicKey = new KeyPair(account.getRemoteHarvestingPrivateKey()).getPublicKey();

		final AccountMetaDataPairLookup accountLookup = Mockito.mock(AccountMetaDataPairLookup.class);
		Mockito.when(accountLookup.findPairByAddress(account.getAddress()))
				.thenReturn(createMetaDataPair(account.getAddress()));

		// Act:
		final AccountMapper mapper = new AccountMapper(accountLookup);
		final AccountViewModel viewModel = mapper.toViewModel(account);

		// Assert:
		Assert.assertThat(viewModel.getAddress(), IsEqual.equalTo(account.getAddress()));
		Assert.assertThat(viewModel.getStatus(), IsEqual.equalTo(AccountStatus.LOCKED));
		Assert.assertThat(viewModel.getRemotePublicKey(), IsEqual.equalTo(remotePublicKey));
	}

	private static AccountMetaDataPair createMetaDataPair(final Address address) {
		return new AccountMetaDataPair(
				Utils.createAccountInfoFromAddress(address),
				new AccountMetaData(AccountStatus.LOCKED, AccountRemoteStatus.INACTIVE, null, null));
	}
}