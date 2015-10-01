package org.nem.monitor.config;

import org.nem.core.model.NemProperties;
import org.nem.core.utils.ExceptionUtils;

import java.io.InputStream;
import java.util.Properties;

/**
 * Class responsible for loading all monitor configuration settings.
 */
public class MonitorConfiguration {
	private final String nemFolder;
	private final NodeConfiguration nisNodeConfig;
	private final NodeConfiguration nccNodeConfig;

	/**
	 * Creates a new configuration object from the default properties.
	 */
	public MonitorConfiguration() {
		this(loadDefaultProperties());
	}

	private static Properties loadDefaultProperties() {
		return ExceptionUtils.propagate(() -> {
			try (final InputStream inputStream = MonitorConfiguration.class.getClassLoader().getResourceAsStream("config.properties")) {
				final Properties properties = new Properties();
				properties.load(inputStream);
				return properties;
			}
		}, IllegalStateException::new);
	}

	/**
	 * Creates a new configuration object around the specified properties.
	 *
	 * @param properties The specified properties.
	 */
	public MonitorConfiguration(final Properties properties) {
		this(new NemProperties(properties));
	}

	/**
	 * Creates a new configuration object around the specified properties.
	 *
	 * @param properties The specified properties.
	 */
	public MonitorConfiguration(final NemProperties properties) {
		// use '/' as the path separator in the default value in order to match the value in the resources file
		// otherwise, the default value (from resources) and the default value (in code) will not match on all OSs
		this.nemFolder = expandPath(properties.getOptionalString("nem.folder", "%h/nem"));

		this.nisNodeConfig = parseNodeConfiguration(properties, "nis", "-Xms512M -Xmx1G");
		this.nccNodeConfig = parseNodeConfiguration(properties, "ncc", "");
	}

	private static NodeConfiguration parseNodeConfiguration(
			final NemProperties properties,
			final String prefix,
			final String defaultVmOptions) {
		return new NodeConfiguration(
				expandPath(properties.getString(prefix + ".uri")),
				properties.getOptionalString(prefix + ".vmOptions", defaultVmOptions),
				properties.getOptionalString(prefix + ".jnlpUrl", ""),
				properties.getOptionalBoolean(prefix + ".isMonitored", true),
				properties.getOptionalBoolean(prefix + ".shouldAutoBoot", true));
	}

	private static String expandPath(final String path) {
		return path
				.replace("/", System.getProperty("file.separator"))
				.replace("%h", getDefaultFolder());
	}

	private static String getDefaultFolder() {
		return System.getProperty("user.home");
	}

	/**
	 * Gets the path to the folder where database and log files should be located.
	 *
	 * @return The path to the folder.
	 */
	public String getNemFolder() {
		return this.nemFolder;
	}

	/**
	 * Gets the NIS node configuration.
	 *
	 * @return The NIS node configuration.
	 */
	public NodeConfiguration getNisConfiguration() {
		return this.nisNodeConfig;
	}

	/**
	 * Gets the NCC node configuration.
	 *
	 * @return The NCC node configuration.
	 */
	public NodeConfiguration getNccConfiguration() {
		return this.nccNodeConfig;
	}
}
