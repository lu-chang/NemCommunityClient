package org.nem.ncc.wallet;

import org.hamcrest.core.IsEqual;
import org.junit.*;
import org.mockito.Mockito;
import org.nem.ncc.test.ExceptionAssert;
import org.nem.ncc.wallet.storage.*;

import java.util.*;

public class AutoFallbackRepositoryTest {

	//region construction

	@Test
	public void cannotBeCreatedAroundZeroRepositories() {
		// Assert:
		ExceptionAssert.assertThrows(v -> new AutoFallbackRepository(null), IllegalArgumentException.class);
		ExceptionAssert.assertThrows(v -> new AutoFallbackRepository(Collections.emptyList()), IllegalArgumentException.class);
	}

	@Test
	public void canCreatedAroundSingleChildRepository() {
		// Act:
		final WalletRepository childRepository1 = Mockito.mock(WalletRepository.class);
		new AutoFallbackRepository(Collections.singletonList(childRepository1));
	}

	@Test
	public void canCreatedAroundMultipleChildRepositories() {
		// Act:
		final WalletRepository childRepository1 = Mockito.mock(WalletRepository.class);
		final WalletRepository childRepository2 = Mockito.mock(WalletRepository.class);
		new AutoFallbackRepository(Arrays.asList(childRepository1, childRepository2));
	}

	//endregion

	//region save

	@Test
	public void saveDelegatesToFirstChildRepository() {
		// Arrange:
		final WalletRepository childRepository1 = Mockito.mock(WalletRepository.class);
		final WalletRepository childRepository2 = Mockito.mock(WalletRepository.class);
		final WalletRepository repository = new AutoFallbackRepository(Arrays.asList(childRepository1, childRepository2));

		final StorableWallet wallet = Mockito.mock(StorableWallet.class);
		final WalletDescriptor descriptor = Mockito.mock(WalletDescriptor.class);

		// Act:
		repository.save(descriptor, wallet);

		// Assert:
		Mockito.verify(childRepository1, Mockito.times(1)).save(descriptor, wallet);
		Mockito.verify(childRepository2, Mockito.times(0)).save(descriptor, wallet);
	}

	@Test
	public void saveDoesNotDelegateToSubsequentChildRepositories() {
		// Arrange:
		final WalletRepository childRepository1 = Mockito.mock(WalletRepository.class);
		final WalletRepository childRepository2 = Mockito.mock(WalletRepository.class);
		final WalletRepository repository = new AutoFallbackRepository(Arrays.asList(childRepository1, childRepository2));

		final StorableWallet wallet = Mockito.mock(StorableWallet.class);
		final WalletDescriptor descriptor = Mockito.mock(WalletDescriptor.class);

		Mockito.doThrow(new WalletStorageException(WalletStorageException.Code.WALLET_COULD_NOT_BE_SAVED))
				.when(childRepository1).save(descriptor, wallet);

		// Act:
		ExceptionAssert.assertThrowsWalletStorageException(
				v -> repository.save(descriptor, wallet),
				WalletStorageException.Code.WALLET_COULD_NOT_BE_SAVED);

		// Assert:
		Mockito.verify(childRepository1, Mockito.times(1)).save(descriptor, wallet);
		Mockito.verify(childRepository2, Mockito.times(0)).save(descriptor, wallet);
	}

	//endregion

	//region load

	@Test
	public void loadDelegatesToFirstChildRepository() {
		// Arrange:
		final WalletRepository childRepository1 = Mockito.mock(WalletRepository.class);
		final WalletRepository childRepository2 = Mockito.mock(WalletRepository.class);
		final WalletRepository repository = new AutoFallbackRepository(Arrays.asList(childRepository1, childRepository2));

		final StorableWallet wallet = Mockito.mock(StorableWallet.class);
		final WalletDescriptor descriptor = Mockito.mock(WalletDescriptor.class);

		Mockito.when(childRepository1.load(descriptor)).thenReturn(wallet);

		// Act:
		final Wallet result = repository.load(descriptor);

		// Assert:
		Assert.assertThat(result, IsEqual.equalTo(wallet));
		Mockito.verify(childRepository1, Mockito.times(1)).load(descriptor);
		Mockito.verify(childRepository2, Mockito.times(0)).load(descriptor);
	}

	@Test
	public void loadDelegatesToNextChildRepositoryIfFirstChildRepositoryFails() {
		// Arrange:
		final WalletRepository childRepository1 = Mockito.mock(WalletRepository.class);
		final WalletRepository childRepository2 = Mockito.mock(WalletRepository.class);
		final WalletRepository repository = new AutoFallbackRepository(Arrays.asList(childRepository1, childRepository2));

		final StorableWallet wallet = Mockito.mock(StorableWallet.class);
		final WalletDescriptor descriptor = Mockito.mock(WalletDescriptor.class);

		Mockito.doThrow(new WalletStorageException(WalletStorageException.Code.WALLET_COULD_NOT_BE_READ))
				.when(childRepository1).load(descriptor);
		Mockito.when(childRepository2.load(descriptor)).thenReturn(wallet);

		// Act:
		final Wallet result = repository.load(descriptor);

		// Assert:
		Assert.assertThat(result, IsEqual.equalTo(wallet));
		Mockito.verify(childRepository1, Mockito.times(1)).load(descriptor);
		Mockito.verify(childRepository2, Mockito.times(1)).load(descriptor);
	}

	@Test
	public void loadPropagatesFirstExceptionIfAllChildRepositoriesFail() {
		// Arrange:
		final WalletRepository childRepository1 = Mockito.mock(WalletRepository.class);
		final WalletRepository childRepository2 = Mockito.mock(WalletRepository.class);
		final WalletRepository repository = new AutoFallbackRepository(Arrays.asList(childRepository1, childRepository2));

		final WalletDescriptor descriptor = Mockito.mock(WalletDescriptor.class);

		Mockito.doThrow(new WalletStorageException(WalletStorageException.Code.WALLET_PASSWORD_INCORRECT))
				.when(childRepository1).load(descriptor);
		Mockito.doThrow(new WalletStorageException(WalletStorageException.Code.WALLET_COULD_NOT_BE_READ))
				.when(childRepository2).load(descriptor);

		// Act:
		ExceptionAssert.assertThrowsWalletStorageException(
				v -> repository.load(descriptor),
				WalletStorageException.Code.WALLET_PASSWORD_INCORRECT);

		// Assert:
		Mockito.verify(childRepository1, Mockito.times(1)).load(descriptor);
		Mockito.verify(childRepository2, Mockito.times(1)).load(descriptor);
	}

	//endregion
}