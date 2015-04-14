package org.nem.ncc.controller;

import net.minidev.json.JSONObject;
import org.hamcrest.core.IsEqual;
import org.junit.*;
import org.mockito.Mockito;
import org.nem.deploy.CommonStarter;
import org.nem.core.model.NemStatus;
import org.nem.core.model.ncc.NemRequestResult;
import org.nem.core.serialization.*;
import org.nem.ncc.cache.*;

import java.util.ArrayList;

public class AdminControllerTest {

	@Test
	public void heartbeatReturnsSuccessfulResult() {
		// Arrange:
		final AdminController controller = new AdminController(
				Mockito.mock(AccountsFileRepository.class),
				Mockito.mock(NccAccountCache.class),
				Mockito.mock(CommonStarter.class));

		// Act:
		final SerializableEntity entity = controller.heartbeat();
		final JSONObject jsonObject = JsonSerializer.serializeToJson(entity);

		// Assert:
		Assert.assertThat(jsonObject.size(), IsEqual.equalTo(1));
		Assert.assertThat(jsonObject.get("status"), IsEqual.equalTo("ok"));
	}

	@Test
	public void shutdownDelegatesToRepositoryAndNccMain() throws InterruptedException {
		// Arrange:
		final AccountsFileRepository repository = Mockito.mock(AccountsFileRepository.class);
		final NccAccountCache cache = Mockito.mock(NccAccountCache.class);
		final CommonStarter commonStarter = Mockito.mock(CommonStarter.class);
		Mockito.when(cache.getAccounts()).thenReturn(new ArrayList<>());

		// Act:
		final AdminController controller = new AdminController(repository, cache, commonStarter);
		controller.shutdown();
		Thread.sleep(500);

		// Assert:
		Mockito.verify(commonStarter, Mockito.times(1)).stopServerAsync();
		Mockito.verify(repository, Mockito.times(1)).save(cache.getAccounts());
	}

	@Test
	public void statusReturnsSuccessfulResult() {
		// Arrange:
		final AdminController controller = new AdminController(
				Mockito.mock(AccountsFileRepository.class),
				Mockito.mock(NccAccountCache.class),
				Mockito.mock(CommonStarter.class));

		// Act:
		final SerializableEntity entity = controller.status();
		final JSONObject jsonObject = JsonSerializer.serializeToJson(entity);

		// Assert:
		Assert.assertThat(jsonObject.size(), IsEqual.equalTo(3));
		Assert.assertThat(jsonObject.get("type"), IsEqual.equalTo(NemRequestResult.TYPE_STATUS));
		Assert.assertThat(jsonObject.get("code"), IsEqual.equalTo(NemStatus.RUNNING.getValue()));
		Assert.assertThat(jsonObject.get("message"), IsEqual.equalTo("status"));
	}
}
