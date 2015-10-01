package org.nem.monitor;

import org.nem.core.connect.*;
import org.nem.core.utils.LockFile;
import org.nem.deploy.LoggingBootstrapper;
import org.nem.monitor.config.*;
import org.nem.monitor.launcher.*;
import org.nem.monitor.node.*;
import org.nem.monitor.ux.TrayIconBuilder;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.logging.Logger;

//import javax.jnlp.*;

/**
 * The nem monitor program.
 */
public class NemMonitor {
	private static final Logger LOGGER = Logger.getLogger(NemMonitor.class.getName());
	private static Closeable fileLockHandle;

	static {
		// initialize logging before anything is logged; otherwise not all
		// settings will take effect
		LoggingBootstrapper.bootstrap(new MonitorConfiguration().getNemFolder());
	}

	/**
	 * The main entry point.
	 *
	 * @param args The command line arguments.
	 */
	public static void main(final String[] args) {
		final MonitorConfiguration config = new MonitorConfiguration();
		final String nemFolder = config.getNemFolder();
		if (!canStart(nemFolder)) {
			return;
		}

		final HttpMethodClient<ErrorResponseDeserializerUnion> httpClient = createHttpMethodClient();
		logVersionInformation(new VersionProvider(httpClient));

		SwingUtilities.invokeLater(() -> {
			LOGGER.info("setting up system tray");

			// fail if the system tray is not supported
			if (!SystemTray.isSupported()) {
				throw new SystemTrayException("SystemTray is not supported");
			}

			final SystemTray tray = SystemTray.getSystemTray();
			final NodeLauncher launcher = createJarLauncher(config);

			final TrayIconBuilder builder = new TrayIconBuilder(httpClient, launcher, new WebBrowser());
			addMenuItems(builder, new NisNodePolicy(nemFolder), config.getNisConfiguration());
			addMenuItems(builder, new NccNodePolicy(nemFolder), config.getNccConfiguration());
			builder.addExitMenuItem(tray);
			builder.addExitAndShutdownMenuItem(tray);

			try {
				tray.add(builder.create());
			} catch (final AWTException e) {
				throw new SystemTrayException("Unable to add icon to system tray", e);
			}
		});
	}

	private static void addMenuItems(
			final TrayIconBuilder builder,
			final NemNodePolicy nodePolicy,
			final NodeConfiguration nodeConfig) {
		if (!nodeConfig.isMonitored()) {
			return;
		}

		final Consumer<?> launchAction = builder.addStatusMenuItems(nodePolicy);
		builder.addSeparator();

		if (nodeConfig.shouldAutoBoot()) {
			launchAction.accept(null);
		}
	}

	private static NodeLauncher createWebStartLauncher(final MonitorConfiguration config) {
		final WebStartLauncher webStartLauncher = new WebStartLauncher(config.getNemFolder());
		return new WebStartNodeLauncher(
				webStartLauncher,
				config.getNisConfiguration().getJnlpUrl(),
				config.getNccConfiguration().getJnlpUrl());
	}

	private static NodeLauncher createJarLauncher(final MonitorConfiguration config) {
		return new JarNodeLauncher(
				new JavaProcessLauncher(),
				config.getNisConfiguration(),
				config.getNccConfiguration());
	}

	private static void logVersionInformation(final VersionProvider versionProvider) {
		LOGGER.info(String.format("Local version: '%s'", versionProvider.getLocalVersion()));
		LOGGER.info(String.format("Latest version: '%s'", versionProvider.getLatestVersion()));
	}

	private static HttpMethodClient<ErrorResponseDeserializerUnion> createHttpMethodClient() {
		final int CONNECTION_TIMEOUT = 2000;
		final int SOCKET_TIMEOUT = 2000;
		final int REQUEST_TIMEOUT = 4000;
		return new HttpMethodClient<>(CONNECTION_TIMEOUT, SOCKET_TIMEOUT, REQUEST_TIMEOUT);
	}

	private static boolean canStart(final String nemFolder) {
		final File lockFile = Paths.get(nemFolder, "mon.lock").toFile();
		LOGGER.info(String.format("Acquiring exclusive lock to lock file: %s", lockFile));
		fileLockHandle = LockFile.tryAcquireLock(lockFile);
		if (null == fileLockHandle) {
			LOGGER.severe("Could not acquire exclusive lock to lock file");
			return false;
		}

		return true;
	}

	/*
	public static boolean isStartedViaWebStart() {
		try {
			ServiceManager.lookup("javax.jnlp.DownloadService2");
			LOGGER.info("NEM monitor was started via web start.");
			return true;
		} catch (final UnavailableServiceException e) {
			return false;
		}
	}
	*/
}