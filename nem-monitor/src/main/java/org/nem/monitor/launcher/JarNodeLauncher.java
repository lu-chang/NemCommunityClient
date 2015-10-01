package org.nem.monitor.launcher;

import org.nem.core.utils.*;
import org.nem.monitor.config.NodeConfiguration;
import org.nem.monitor.node.NemNodeType;

import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A JAR based node launcher.
 */
public class JarNodeLauncher implements NodeLauncher {
	private final JavaProcessLauncher launcher;
	private final NodeConfiguration nisConfig;
	private final NodeConfiguration nccConfig;

	/**
	 * Creates a new node launcher.
	 *
	 * @param launcher The launcher for launching a java process.
	 * @param nisConfig The NIS node configuration.
	 * @param nccConfig The NCC node configuration.
	 */
	public JarNodeLauncher(
			final JavaProcessLauncher launcher,
			final NodeConfiguration nisConfig,
			final NodeConfiguration nccConfig) {
		this.launcher = launcher;
		this.nisConfig = nisConfig;
		this.nccConfig = nccConfig;
	}

	@Override
	public void launch(final NemNodeType nodeType) {
		final NodeConfiguration config = this.getNodeConfig(nodeType);
		final List<String> arguments = new ArrayList<>();
		arguments.addAll(Arrays.stream(config.getVmOptions().split(" "))
				.filter(arg -> !StringUtils.isNullOrWhitespace(arg))
				.collect(Collectors.toList()));

		final Path directory = Paths.get(config.getUri());
		arguments.addAll(Arrays.asList(
				"-cp",
				Arrays.asList(".", "./*", "../libs/*").stream().collect(Collectors.joining(File.pathSeparator)),
				"org.nem.deploy.CommonStarter"));
		ExceptionUtils.propagateVoid(() -> this.launcher.launch(arguments, directory.toFile()));
	}

	private NodeConfiguration getNodeConfig(final NemNodeType nodeType) {
		switch (nodeType) {
			case NIS:
				return this.nisConfig;

			case NCC:
			default:
				return this.nccConfig;
		}
	}
}