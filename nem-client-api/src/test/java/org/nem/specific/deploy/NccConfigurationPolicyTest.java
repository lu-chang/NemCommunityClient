package org.nem.specific.deploy;

import org.hamcrest.core.*;
import org.junit.*;
import org.nem.deploy.CommonConfiguration;
import org.nem.specific.deploy.appconfig.NccAppConfig;
import org.nem.ncc.web.servlet.*;

public class NccConfigurationPolicyTest {
	//region get class

	@Test
	public void canGetNisAppConfigClass() {
		// Arrange:
		final NccConfigurationPolicy policy = createPolicy();

		// Assert:
		Assert.assertThat(policy.getAppConfigClass(), IsEqual.equalTo(NccAppConfig.class));
	}

	@Test
	public void canGetNisWebAppInitializerClass() {
		// Arrange:
		final NccConfigurationPolicy policy = createPolicy();

		// Assert:
		Assert.assertThat(policy.getWebAppInitializerClass(), IsEqual.equalTo(NccWebAppInitializer.class));
	}

	@Test
	public void canGetJarFileServletClass() {
		// Arrange:
		final NccConfigurationPolicy policy = createPolicy();

		// Assert:
		Assert.assertThat(policy.getJarFileServletClass(), IsEqual.equalTo(JarFileServlet.class));
	}

	@Test
	public void canGetDefaultServletClass() {
		// Arrange:
		final NccConfigurationPolicy policy = createPolicy();

		// Assert:
		Assert.assertThat(policy.getDefaultServletClass(), IsEqual.equalTo(NccDefaultServlet.class));
	}

	//endregion

	@Test
	public void loadConfigReturnsValidConfiguration() {
		// Arrange:
		final NccConfigurationPolicy policy = createPolicy();

		// Act:
		final CommonConfiguration configuration = policy.loadConfig(new String[] {});

		// Assert:
		Assert.assertThat(configuration, IsNull.notNullValue());
	}

	private static NccConfigurationPolicy createPolicy() {
		return new NccConfigurationPolicy();
	}
}
