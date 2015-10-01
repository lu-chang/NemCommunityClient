package org.nem.ncc.wallet;

import org.hamcrest.core.IsEqual;
import org.junit.*;
import org.nem.core.serialization.*;
import org.nem.ncc.test.*;

import java.util.*;
import java.util.function.Consumer;

public class MemoryWalletTest extends WalletTest {

	//region createWallet

	@Override
	protected Wallet createWallet(final WalletName name) {
		return new MemoryWallet(name);
	}

	@Override
	protected Wallet createWallet(final WalletName name, final WalletAccount primaryAccount) {
		return new MemoryWallet(name, primaryAccount);
	}

	@Override
	protected Wallet createWallet(final WalletName name, final WalletAccount primaryAccount, final Collection<WalletAccount> otherAccounts) {
		return new MemoryWallet(name, primaryAccount, otherAccounts);
	}

	//endregion

	//region deserialization

	@Test
	public void walletCanBeRoundTripped() {
		// Arrange:
		final WalletAccount account = new WalletAccount();
		final Wallet originalWallet = this.createWallet(new WalletName("bar"), account);
		originalWallet.addOtherAccount(new WalletAccount());
		originalWallet.addOtherAccount(new WalletAccount());
		originalWallet.addOtherAccount(new WalletAccount());

		// Act:
		final Wallet wallet = new MemoryWallet(Utils.roundtripSerializableEntity(originalWallet, null));

		// Assert:
		Assert.assertThat(wallet.getName(), IsEqual.equalTo(new WalletName("bar")));
		Assert.assertThat(wallet.getPrimaryAccount(), IsEqual.equalTo(account));
		Assert.assertThat(wallet.getOtherAccounts(), IsEquivalent.equivalentTo(originalWallet.getOtherAccounts()));
	}

	@Test
	public void walletCannotBeDeserializedWithInvalidAccounts() {
		// Arrange:
		final WalletAccount account = new WalletAccount();

		// Act:
		// Assert: that other accounts are validated as part of deserialization
		assertThrowsWalletException(
				v -> createWalletFromJson("bar", account, Collections.singletonList(account)),
				WalletException.Code.WALLET_ALREADY_CONTAINS_ACCOUNT);
	}

	@Test
	public void walletCannotBeDeserializedWithMissingRequiredParameters() {
		// Arrange:
		final WalletAccount account = new WalletAccount();
		final List<WalletAccount> otherAccounts = Collections.singletonList(new WalletAccount());
		final List<Consumer<Void>> actions = Arrays.asList(
				v -> createWalletFromJson(null, account, otherAccounts),
				v -> createWalletFromJson("bar", null, otherAccounts),
				v -> createWalletFromJson("bar", account, null));

		// Assert:
		for (final Consumer<Void> action : actions) {
			ExceptionAssert.assertThrows(v -> action.accept(null), SerializationException.class);
		}
	}

	private static Wallet createWalletFromJson(
			final String name,
			final WalletAccount primaryAccount,
			final List<WalletAccount> otherAccounts) {
		final JsonSerializer serializer = new JsonSerializer();
		if (null != name) {
			serializer.writeString("wallet", name);
		}

		if (null != primaryAccount) {
			serializer.writeObject("primaryAccount", primaryAccount);
		}

		if (null != otherAccounts) {
			serializer.writeObjectArray("otherAccounts", otherAccounts);
		}

		return new MemoryWallet(Utils.createDeserializer(serializer.getObject()));
	}

	//endregion
}
