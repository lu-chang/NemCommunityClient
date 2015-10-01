package org.nem.ncc.controller;

import net.minidev.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.hamcrest.core.*;
import org.junit.*;
import org.mockito.Mockito;
import org.nem.core.utils.ExceptionUtils;
import org.nem.ncc.addressbook.*;
import org.nem.ncc.controller.requests.WalletNamePasswordBag;
import org.nem.ncc.controller.viewmodels.WalletViewModel;
import org.nem.ncc.services.*;
import org.nem.ncc.test.Utils;
import org.nem.ncc.wallet.*;
import org.nem.specific.deploy.OctetStream;

import java.io.*;
import java.util.Arrays;
import java.util.zip.*;

public class WalletControllerTest {

	//region create / open / info / close

	@Test
	public void createDelegatesToWalletServicesAndMapperAndAddressBookServices() {
		// Arrange:
		final WalletNamePasswordPair request = new WalletNamePasswordPair(new WalletName("wal"), new WalletPassword("pwd"));
		final AddressBookNamePasswordPair pair = new AddressBookNamePasswordPair(new AddressBookName("wal"), new AddressBookPassword("pwd"));
		final TestContext context = new TestContext();
		Mockito.when(context.walletServices.create(request)).thenReturn(context.wallet);
		Mockito.when(context.walletMapper.toViewModel(context.wallet)).thenReturn(context.walletViewModel);
		Mockito.when(context.wallet.getPrimaryAccount()).thenReturn(context.primaryAccount);
		Mockito.when(context.addressBookServices.create(pair)).thenReturn(context.addressBook);

		// Act:
		final WalletViewModel result = context.controller.create(request);

		// Assert:
		Assert.assertThat(result, IsEqual.equalTo(context.walletViewModel));
		Mockito.verify(context.walletServices, Mockito.times(1)).create(request);
		Mockito.verify(context.walletMapper, Mockito.times(1)).toViewModel(context.wallet);
	}

	@Test
	public void createCallsCreateAddressBookWithRequestParametersAndAddsPrimaryAccountWithEmptyLabel() {
		// Arrange:
		final WalletNamePasswordPair request = new WalletNamePasswordPair(new WalletName("wal"), new WalletPassword("pwd"));
		final AddressBookNamePasswordPair pair = new AddressBookNamePasswordPair(new AddressBookName("wal"), new AddressBookPassword("pwd"));
		final TestContext context = new TestContext();
		Mockito.when(context.walletServices.create(request)).thenReturn(context.wallet);
		Mockito.when(context.wallet.getPrimaryAccount()).thenReturn(context.primaryAccount);
		Mockito.when(context.addressBookServices.create(pair)).thenReturn(context.addressBook);

		// Act:
		context.controller.create(request);

		// Assert:
		Mockito.verify(context.addressBookServices, Mockito.only()).create(Mockito.eq(pair));
		Mockito.verify(context.addressBook, Mockito.only()).addLabel(Mockito.eq(new AccountLabel(context.primaryAccount.getAddress(), "", "")));
	}

	@Test
	public void openDelegatesToWalletServicesAndMapperAndAddressBook() {
		// Arrange:
		final WalletNamePasswordPair request = new WalletNamePasswordPair(new WalletName("wal"), new WalletPassword("pwd"));
		final AddressBookNamePasswordPair pair = new AddressBookNamePasswordPair(new AddressBookName("wal"), new AddressBookPassword("pwd"));
		final TestContext context = new TestContext();
		Mockito.when(context.walletServices.open(request)).thenReturn(context.wallet);
		Mockito.when(context.walletMapper.toViewModel(context.wallet)).thenReturn(context.walletViewModel);

		// Act:
		final WalletViewModel result = context.controller.open(request);

		// Assert:
		Assert.assertThat(result, IsEqual.equalTo(context.walletViewModel));
		Mockito.verify(context.walletServices, Mockito.only()).open(request);
		Mockito.verify(context.walletMapper, Mockito.only()).toViewModel(context.wallet);
		Mockito.verify(context.addressBookServices, Mockito.only()).open(Mockito.eq(pair));
	}

	@Test
	public void infoDelegatesToWalletServicesAndMapper() {
		// Arrange:
		final WalletName request = new WalletName("wal");
		final Wallet wallet = Mockito.mock(Wallet.class);
		final WalletViewModel walletViewModel = Mockito.mock(WalletViewModel.class);
		final TestContext context = new TestContext();
		Mockito.when(context.walletServices.get(request)).thenReturn(wallet);
		Mockito.when(context.walletMapper.toViewModel(wallet)).thenReturn(walletViewModel);

		// Act:
		final WalletViewModel result = context.controller.info(request);

		// Assert:
		Assert.assertThat(result, IsEqual.equalTo(walletViewModel));
		Mockito.verify(context.walletServices, Mockito.times(1)).get(request);
		Mockito.verify(context.walletMapper, Mockito.times(1)).toViewModel(wallet);
	}

	@Test
	public void closeDelegatesToServices() {
		// Arrange:
		final WalletName request = new WalletName("wal");
		final TestContext context = new TestContext();

		// Act:
		context.controller.close(request);

		// Assert:
		Mockito.verify(context.walletServices, Mockito.times(1)).close(request);
		Mockito.verify(context.addressBookServices, Mockito.times(1)).close(Mockito.eq(new AddressBookName(request.toString())));
	}

	//endregion

	//region changePassword / changeWalletName

	@Test
	public void changePasswordDelegatesToServices() {
		// Arrange:
		final JSONObject jsonObject = new JSONObject();
		jsonObject.put("wallet", "w1");
		jsonObject.put("password", "p1");
		jsonObject.put("newPassword", "p2");
		final WalletNamePasswordBag bag = new WalletNamePasswordBag(Utils.createDeserializer(jsonObject));
		final TestContext context = new TestContext();

		// Act:
		context.controller.changePassword(bag);

		// Assert:
		Mockito.verify(context.walletServices, Mockito.only())
				.move(new WalletNamePasswordPair("w1", "p1"), new WalletNamePasswordPair("w1", "p2"));
		Mockito.verify(context.addressBookServices, Mockito.only())
				.move(new AddressBookNamePasswordPair("w1", "p1"), new AddressBookNamePasswordPair("w1", "p2"));
	}

	@Test
	public void changeNameDelegatesToWalletServices() {
		// Arrange:
		final JSONObject jsonObject = new JSONObject();
		jsonObject.put("wallet", "w1");
		jsonObject.put("password", "p1");
		jsonObject.put("newName", "w2");
		final WalletNamePasswordBag bag = new WalletNamePasswordBag(Utils.createDeserializer(jsonObject));
		final TestContext context = new TestContext();

		// Act:
		context.controller.changeName(bag);

		// Assert:
		Mockito.verify(context.walletServices, Mockito.only())
				.move(new WalletNamePasswordPair("w1", "p1"), new WalletNamePasswordPair("w2", "p1"));
		Mockito.verify(context.addressBookServices, Mockito.only())
				.move(new AddressBookNamePasswordPair("w1", "p1"), new AddressBookNamePasswordPair("w2", "p1"));
	}

	//endregion

	//region export

	@Test
	public void exportWalletDelegatesToServices() {
		// Arrange:
		final WalletName request = new WalletName("wal");
		final TestContext context = new TestContext();

		// Act:
		context.controller.exportWallet(request);

		// Assert:
		Mockito.verify(context.walletServices, Mockito.only()).copyTo(Mockito.any(), Mockito.any());
		Mockito.verify(context.addressBookServices, Mockito.only()).copyTo(Mockito.any(), Mockito.any());
	}

	@Test
	public void exportWalletReturnsExpectedOctetStream() {
		// Arrange:
		final WalletName request = new WalletName("wal");
		final TestContext context = new TestContext();

		Mockito.doAnswer(invocationOnMock -> {
			final OutputStream outputStream = (OutputStream)invocationOnMock.getArguments()[1];
			return ExceptionUtils.propagate(() -> {
				IOUtils.copy(new ByteArrayInputStream("wallet".getBytes()), outputStream);
				return null;
			});
		}).when(context.walletServices).copyTo(Mockito.any(), Mockito.any());

		Mockito.doAnswer(invocationOnMock -> {
			final OutputStream outputStream = (OutputStream)invocationOnMock.getArguments()[1];
			return ExceptionUtils.propagate(() -> {
				IOUtils.copy(new ByteArrayInputStream("addressBook".getBytes()), outputStream);
				return null;
			});
		}).when(context.addressBookServices).copyTo(Mockito.any(), Mockito.any());

		// Act:
		final OctetStream octetStream = context.controller.exportWallet(request);
		final InputStream inputStream = new ByteArrayInputStream(octetStream.toByteArray());
		final ZipInputStream zipInputStream = new ZipInputStream(inputStream);
		final ZipEntry[] entries = new ZipEntry[3];
		final String[] contents = new String[2];
		ExceptionUtils.propagateVoid(() -> {
			entries[0] = zipInputStream.getNextEntry();
			contents[0] = this.readString(zipInputStream);
			entries[1] = zipInputStream.getNextEntry();
			contents[1] = this.readString(zipInputStream);
			entries[2] = zipInputStream.getNextEntry();
		});

		// Assert:
		Assert.assertThat(entries[0].toString(), IsEqual.equalTo("wal.wlt"));
		Assert.assertThat(contents[0], IsEqual.equalTo("wallet"));
		Assert.assertThat(entries[1].toString(), IsEqual.equalTo("wal.adb"));
		Assert.assertThat(contents[1], IsEqual.equalTo("addressBook"));
		Assert.assertThat(entries[2], IsNull.nullValue());
	}

	@Test
	public void exportWalletProperlyEncodesWalletFileNames() {
		// Arrange:
		final WalletName request = new WalletName("ßomething in the 真 way");
		final TestContext context = new TestContext();

		Mockito.doAnswer(invocationOnMock -> {
			final OutputStream outputStream = (OutputStream)invocationOnMock.getArguments()[1];
			return ExceptionUtils.propagate(() -> {
				IOUtils.copy(new ByteArrayInputStream("wallet".getBytes()), outputStream);
				return null;
			});
		}).when(context.walletServices).copyTo(Mockito.any(), Mockito.any());

		Mockito.doAnswer(invocationOnMock -> {
			final OutputStream outputStream = (OutputStream)invocationOnMock.getArguments()[1];
			return ExceptionUtils.propagate(() -> {
				IOUtils.copy(new ByteArrayInputStream("addressBook".getBytes()), outputStream);
				return null;
			});
		}).when(context.addressBookServices).copyTo(Mockito.any(), Mockito.any());

		// Act:
		final OctetStream octetStream = context.controller.exportWallet(request);
		final InputStream inputStream = new ByteArrayInputStream(octetStream.toByteArray());
		final ZipInputStream zipInputStream = new ZipInputStream(inputStream);
		final ZipEntry[] entries = new ZipEntry[3];
		final String[] contents = new String[2];
		ExceptionUtils.propagateVoid(() -> {
			entries[0] = zipInputStream.getNextEntry();
			contents[0] = this.readString(zipInputStream);
			entries[1] = zipInputStream.getNextEntry();
			contents[1] = this.readString(zipInputStream);
			entries[2] = zipInputStream.getNextEntry();
		});

		// Assert:
		Assert.assertThat(entries[0].toString(), IsEqual.equalTo("%C3%9Fomething+in+the+%E7%9C%9F+way.wlt"));
		Assert.assertThat(contents[0], IsEqual.equalTo("wallet"));
		Assert.assertThat(entries[1].toString(), IsEqual.equalTo("%C3%9Fomething+in+the+%E7%9C%9F+way.adb"));
		Assert.assertThat(contents[1], IsEqual.equalTo("addressBook"));
		Assert.assertThat(entries[2], IsNull.nullValue());
	}

	private String readString(final ZipInputStream zipInputStream) {
		return ExceptionUtils.propagate(() -> {
			byte[] bytesIn = new byte[12];
			int length = zipInputStream.read(bytesIn, 0, 12);
			return new String(Arrays.copyOf(bytesIn, length));
		});
	}

	//endregion

	private static class TestContext {
		private final WalletAccount primaryAccount = new WalletAccount();
		private final Wallet wallet = Mockito.mock(Wallet.class);
		private final WalletViewModel walletViewModel = Mockito.mock(WalletViewModel.class);
		private final WalletServices walletServices = Mockito.mock(WalletServices.class);
		private final WalletMapper walletMapper = Mockito.mock(WalletMapper.class);
		private final AddressBookServices addressBookServices = Mockito.mock(AddressBookServices.class);
		private final AddressBook addressBook = Mockito.mock(AddressBook.class);
		private final WalletController controller = new WalletController(
				this.walletServices,
				this.walletMapper,
				this.addressBookServices);
	}
}