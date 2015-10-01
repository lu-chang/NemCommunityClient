package org.nem.ncc.cache;

import org.hamcrest.core.*;
import org.junit.*;
import org.mockito.Mockito;
import org.nem.core.crypto.KeyPair;
import org.nem.core.model.*;
import org.nem.core.model.ncc.*;
import org.nem.core.model.primitive.*;
import org.nem.ncc.services.*;
import org.nem.ncc.test.Utils;
import org.nem.ncc.wallet.WalletAccount;

public class WalletAwareAccountLookupTest {

	//region findByAddress

	@Test
	public void findByAddressReturnsNullForUnknownAccount() {
		// Arrange:
		final TestContext context = new TestContext();
		final Address address = Utils.generateRandomAddress();

		// Act:
		final Account result = context.accountLookup.findByAddress(address);

		// Assert:
		Assert.assertThat(result, IsNull.nullValue());
		Mockito.verify(context.mockAccountLookup, Mockito.times(1)).findByAddress(address);
		Mockito.verify(context.walletServices, Mockito.times(0)).tryFindOpenAccount(address);
	}

	@Test
	public void findByAddressReturnsAccountWithUnknownPrivateKeyAsPassThrough() {
		// Arrange:
		final TestContext context = new TestContext();
		final Address address = Utils.generateRandomAddress();
		final Account account = Mockito.mock(Account.class);
		Mockito.when(context.mockAccountLookup.findByAddress(address)).thenReturn(account);

		// Act:
		final Account result = context.accountLookup.findByAddress(address);

		// Assert:
		Assert.assertThat(result, IsEqual.equalTo(account));
		Mockito.verify(context.mockAccountLookup, Mockito.times(1)).findByAddress(address);
		Mockito.verify(context.walletServices, Mockito.times(1)).tryFindOpenAccount(address);
	}

	@Test
	public void findByAddressReturnsAccountWithKnownPrivateKeyAsShallowCopy() {
		// Arrange:
		final TestContext context = new TestContext();
		final Address address = Utils.generateRandomAddress();
		final WalletAccount walletAccount = new WalletAccount();
		final Account account = Mockito.mock(Account.class);
		Mockito.when(context.mockAccountLookup.findByAddress(address)).thenReturn(account);
		Mockito.when(context.walletServices.tryFindOpenAccount(address)).thenReturn(walletAccount);

		// Act:
		final Account result = context.accountLookup.findByAddress(address);

		// Assert:
		Assert.assertThat(account.hasPrivateKey(), IsEqual.equalTo(false)); // sanity check
		Assert.assertThat(result.hasPrivateKey(), IsEqual.equalTo(true));
		Mockito.verify(context.mockAccountLookup, Mockito.times(1)).findByAddress(address);
		Mockito.verify(context.walletServices, Mockito.times(1)).tryFindOpenAccount(address);
	}

	//endregion

	//region findPairByAddress

	@Test
	public void findPairByAddressReturnsNullForUnknownPair() {
		// Arrange:
		final TestContext context = new TestContext();
		final Address address = Utils.generateRandomAddress();

		// Act:
		final AccountMetaDataPair result = context.accountLookup.findPairByAddress(address);

		// Assert:
		Assert.assertThat(result, IsNull.nullValue());
		Mockito.verify(context.mockAccountLookup, Mockito.times(1)).findPairByAddress(address);
		Mockito.verify(context.walletServices, Mockito.times(0)).tryFindOpenAccount(address);
	}

	@Test
	public void findPairByAddressReturnsPairWithPublicKeyAsPassThrough() {
		// Arrange:
		final TestContext context = new TestContext();
		final Address address = Utils.generateRandomAddressWithPublicKey();
		final AccountMetaDataPair pair = createAccountMetaDataPair(address);
		Mockito.when(context.mockAccountLookup.findPairByAddress(address)).thenReturn(pair);

		// Act:
		final AccountMetaDataPair result = context.accountLookup.findPairByAddress(address);

		// Assert:
		Assert.assertThat(result, IsEqual.equalTo(pair));
		Mockito.verify(context.mockAccountLookup, Mockito.times(1)).findPairByAddress(address);
		Mockito.verify(context.walletServices, Mockito.times(0)).tryFindOpenAccount(address);
	}

	@Test
	public void findPairByAddressReturnsPairWithoutPublicKeyNotInWalletAsPassThrough() {
		// Arrange:
		final TestContext context = new TestContext();
		final Address address = Utils.generateRandomAddress();
		final AccountMetaDataPair pair = createAccountMetaDataPair(address);
		Mockito.when(context.mockAccountLookup.findPairByAddress(address)).thenReturn(pair);

		// Act:
		final AccountMetaDataPair result = context.accountLookup.findPairByAddress(address);

		// Assert:
		Assert.assertThat(result, IsEqual.equalTo(pair));
		Mockito.verify(context.mockAccountLookup, Mockito.times(1)).findPairByAddress(address);
		Mockito.verify(context.walletServices, Mockito.times(1)).tryFindOpenAccount(address);
	}

	@Test
	public void findPairByAddressReturnsPairWithPublicKeyInWalletAsShallowCopy() {
		// Arrange:
		final TestContext context = new TestContext();
		final KeyPair keyPair = new KeyPair();
		final Address address = Address.fromPublicKey(keyPair.getPublicKey());
		final Address addressWithoutPublicKey = Address.fromEncoded(address.getEncoded());
		final AccountMetaDataPair pair = new AccountMetaDataPair(
				new AccountInfo(addressWithoutPublicKey, Amount.fromNem(17), Amount.fromNem(14), new BlockAmount(12), "foo", 1.5),
				new AccountMetaData(AccountStatus.UNLOCKED, AccountRemoteStatus.INACTIVE, null, null));
		Mockito.when(context.mockAccountLookup.findPairByAddress(address)).thenReturn(pair);
		Mockito.when(context.walletServices.tryFindOpenAccount(addressWithoutPublicKey)).thenReturn(new WalletAccount(keyPair.getPrivateKey()));

		// Act:
		final AccountMetaDataPair result = context.accountLookup.findPairByAddress(address);
		final AccountInfo resultInfo = result.getEntity();

		// Assert:
		Assert.assertThat(resultInfo.getAddress().getPublicKey(), IsEqual.equalTo(address.getPublicKey()));
		Assert.assertThat(resultInfo.getKeyPair().getPublicKey(), IsEqual.equalTo(address.getPublicKey()));
		Assert.assertThat(resultInfo.getBalance(), IsEqual.equalTo(Amount.fromNem(17)));
		Assert.assertThat(resultInfo.getVestedBalance(), IsEqual.equalTo(Amount.fromNem(14)));
		Assert.assertThat(resultInfo.getNumHarvestedBlocks(), IsEqual.equalTo(new BlockAmount(12)));
		Assert.assertThat(resultInfo.getLabel(), IsEqual.equalTo("foo"));
		Assert.assertThat(resultInfo.getImportance(), IsEqual.equalTo(1.5));
		Assert.assertThat(result.getMetaData().getStatus(), IsEqual.equalTo(AccountStatus.UNLOCKED));
		Assert.assertThat(result.getMetaData().getRemoteStatus(), IsEqual.equalTo(AccountRemoteStatus.INACTIVE));
		Mockito.verify(context.mockAccountLookup, Mockito.times(1)).findPairByAddress(address);
		Mockito.verify(context.walletServices, Mockito.times(1)).tryFindOpenAccount(address);
	}

	//endregion

	private static AccountMetaDataPair createAccountMetaDataPair(final Address address) {
		return new AccountMetaDataPair(
				Utils.createAccountInfoFromAddress(address),
				new AccountMetaData(AccountStatus.LOCKED, AccountRemoteStatus.INACTIVE, null, null));
	}

	private static class TestContext {
		private final AccountMetaDataPairLookup mockAccountLookup = Mockito.mock(AccountMetaDataPairLookup.class);
		private final WalletServices walletServices = Mockito.mock(WalletServices.class);
		private final AccountMetaDataPairLookup accountLookup = new WalletAwareAccountLookup(
				this.mockAccountLookup,
				this.walletServices);
	}
}