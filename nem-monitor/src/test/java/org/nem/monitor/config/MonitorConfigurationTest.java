package org.nem.monitor.config;

import org.hamcrest.core.IsEqual;
import org.junit.*;
import org.nem.core.test.*;

import java.nio.file.Paths;
import java.util.*;

public class MonitorConfigurationTest {
	private final static List<String> REQUIRED_PROPERTY_NAMES = Arrays.asList(
			"nis.uri",
			"ncc.uri");

	private final static List<String> OPTIONAL_PROPERTY_NAMES = Arrays.asList(
			"nem.folder",
			"nis.vmOptions",
			"nis.jnlpUrl",
			"nis.isMonitored",
			"nis.shouldAutoBoot",
			"ncc.vmOptions",
			"ncc.jnlpUrl",
			"ncc.isMonitored",
			"ncc.shouldAutoBoot");

	//region basic construction

	@Test
	public void canReadDefaultConfigurationFromResources() {
		// Act:
		final MonitorConfiguration config = new MonitorConfiguration();

		// Assert:
		assertDefaultRequiredConfiguration(config);
		assertDefaultOptionalConfiguration(config);
	}

	@Test
	public void canReadCustomConfiguration() {
		// Arrange:
		final Properties properties = getCustomProperties();

		// Act:
		final MonitorConfiguration config = new MonitorConfiguration(properties);

		// Assert
		assertCustomRequiredConfiguration(config);
		assertCustomOptionalConfiguration(config);
	}

	@Test
	public void canReadCustomConfigurationWithoutOptionalProperties() {
		// Arrange:
		final Properties properties = getCustomProperties();
		OPTIONAL_PROPERTY_NAMES.forEach(properties::remove);

		// Act:
		final MonitorConfiguration config = new MonitorConfiguration(properties);

		// Assert
		assertCustomRequiredConfiguration(config);
		assertDefaultOptionalConfiguration(config);
	}

	private static void assertDefaultRequiredConfiguration(final MonitorConfiguration config) {
		// Assert:
		Assert.assertThat(
				config.getNisConfiguration().getUri(),
				IsEqual.equalTo(Paths.get(System.getProperty("user.home"), "nem", "package", "nis").toString()));
		Assert.assertThat(
				config.getNccConfiguration().getUri(),
				IsEqual.equalTo(Paths.get(System.getProperty("user.home"), "nem", "package", "ncc").toString()));
	}

	private static void assertDefaultOptionalConfiguration(final MonitorConfiguration config) {
		// Assert:
		Assert.assertThat(
				config.getNemFolder(),
				IsEqual.equalTo(Paths.get(System.getProperty("user.home"), "nem").toString()));

		final NodeConfiguration nisConfig = config.getNisConfiguration();
		Assert.assertThat(nisConfig.getVmOptions(), IsEqual.equalTo("-Xms512M -Xmx1G"));
		Assert.assertThat(nisConfig.getJnlpUrl(), IsEqual.equalTo(""));
		Assert.assertThat(nisConfig.isMonitored(), IsEqual.equalTo(true));
		Assert.assertThat(nisConfig.shouldAutoBoot(), IsEqual.equalTo(true));

		final NodeConfiguration nccConfig = config.getNccConfiguration();
		Assert.assertThat(nccConfig.getVmOptions(), IsEqual.equalTo(""));
		Assert.assertThat(nccConfig.getJnlpUrl(), IsEqual.equalTo(""));
		Assert.assertThat(nccConfig.isMonitored(), IsEqual.equalTo(true));
		Assert.assertThat(nccConfig.shouldAutoBoot(), IsEqual.equalTo(true));
	}

	private static void assertCustomRequiredConfiguration(final MonitorConfiguration config) {
		// Assert:
		Assert.assertThat(config.getNisConfiguration().getUri(), IsEqual.equalTo("nis-local-uri"));
		Assert.assertThat(config.getNccConfiguration().getUri(), IsEqual.equalTo("ncc-local-uri"));
	}

	private static void assertCustomOptionalConfiguration(final MonitorConfiguration config) {
		// Assert:
		Assert.assertThat(config.getNemFolder(), IsEqual.equalTo("folder"));

		final NodeConfiguration nisConfig = config.getNisConfiguration();
		Assert.assertThat(nisConfig.getVmOptions(), IsEqual.equalTo("-XNis"));
		Assert.assertThat(nisConfig.getJnlpUrl(), IsEqual.equalTo("nis-remote.jnlp"));
		Assert.assertThat(nisConfig.isMonitored(), IsEqual.equalTo(true));
		Assert.assertThat(nisConfig.shouldAutoBoot(), IsEqual.equalTo(false));

		final NodeConfiguration nccConfig = config.getNccConfiguration();
		Assert.assertThat(nccConfig.getVmOptions(), IsEqual.equalTo("-XNcc"));
		Assert.assertThat(nccConfig.getJnlpUrl(), IsEqual.equalTo("ncc-remote.jnlp"));
		Assert.assertThat(nccConfig.isMonitored(), IsEqual.equalTo(false));
		Assert.assertThat(nccConfig.shouldAutoBoot(), IsEqual.equalTo(true));
	}

	//endregion

	//region property required status

	@Test
	public void requiredPropertiesAreDetectedCorrectly() {
		// Arrange:
		final MockNemProperties properties = new MockNemProperties(getCustomProperties());

		// Act:
		new MonitorConfiguration(properties);

		// Assert:
		Assert.assertThat(properties.getRequiredPropertyNames(), IsEquivalent.equivalentTo(REQUIRED_PROPERTY_NAMES));
	}

	@Test
	public void optionalPropertiesAreDetectedCorrectly() {
		// Arrange:
		final MockNemProperties properties = new MockNemProperties(getCustomProperties());

		// Act:
		new MonitorConfiguration(properties);

		// Assert:
		Assert.assertThat(properties.getOptionalPropertyNames(), IsEquivalent.equivalentTo(OPTIONAL_PROPERTY_NAMES));
	}

	//endregion

	private static Properties getCustomProperties() {
		final Properties properties = new Properties();
		properties.setProperty("nem.folder", "folder");

		properties.setProperty("nis.uri", "nis-local-uri");
		properties.setProperty("nis.vmOptions", "-XNis");
		properties.setProperty("nis.jnlpUrl", "nis-remote.jnlp");
		properties.setProperty("nis.isMonitored", "true");
		properties.setProperty("nis.shouldAutoBoot", "false");

		properties.setProperty("ncc.uri", "ncc-local-uri");
		properties.setProperty("ncc.vmOptions", "-XNcc");
		properties.setProperty("ncc.jnlpUrl", "ncc-remote.jnlp");
		properties.setProperty("ncc.isMonitored", "false");
		properties.setProperty("ncc.shouldAutoBoot", "true");
		return properties;
	}
}