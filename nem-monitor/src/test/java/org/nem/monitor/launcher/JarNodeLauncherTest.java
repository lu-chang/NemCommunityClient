package org.nem.monitor.launcher;

import org.hamcrest.core.IsEqual;
import org.junit.*;
import org.mockito.*;
import org.nem.monitor.config.NodeConfiguration;
import org.nem.monitor.node.NemNodeType;

import java.io.*;
import java.util.*;

public class JarNodeLauncherTest {

	@Test
	public void canLaunchNisNodeWithNoOptions() throws IOException {
		// Arrange:
		final TestContext testContext = new TestContext("", "-XNccOptions");

		// Act:
		testContext.launch(NemNodeType.NIS);

		// Assert:
		testContext.assertLaunched(Collections.emptyList(), "nis-jar-path");
	}

	@Test
	public void canLaunchNisNodeWithOptions() throws IOException {
		// Arrange:
		final TestContext testContext = new TestContext("-Xms512M -Xmx1G -XNisOptions", "");

		// Act:
		testContext.launch(NemNodeType.NIS);

		// Assert:
		testContext.assertLaunched(Arrays.asList("-Xms512M", "-Xmx1G", "-XNisOptions"), "nis-jar-path");
	}

	@Test
	public void canLaunchNccNodeWithNoOptions() throws IOException {
		// Arrange:
		final TestContext testContext = new TestContext("-XNisOptions", "");

		// Act:
		testContext.launch(NemNodeType.NCC);

		// Assert:
		testContext.assertLaunched(Collections.emptyList(), "ncc-jar-path");
	}

	@Test
	public void canLaunchNccNodeWithOptions() throws IOException {
		// Arrange:
		final TestContext testContext = new TestContext("", "-XNccOptions -Xms512M -Xmx1G");

		// Act:
		testContext.launch(NemNodeType.NCC);

		// Assert:
		testContext.assertLaunched(Arrays.asList("-XNccOptions", "-Xms512M", "-Xmx1G"), "ncc-jar-path");
	}

	private static class TestContext {
		private final JavaProcessLauncher processLauncher = Mockito.mock(JavaProcessLauncher.class);
		private final NodeLauncher launcher;

		public TestContext(final String nisVmOptions, final String nccVmOptions) {
			this.launcher = new JarNodeLauncher(
					this.processLauncher,
					createNodeConfiguration("nis-jar-path", nisVmOptions),
					createNodeConfiguration("ncc-jar-path", nccVmOptions));
		}

		private static NodeConfiguration createNodeConfiguration(final String uri, final String vmOptions) {
			return new NodeConfiguration(uri, vmOptions, "", false, false);
		}

		public void launch(final NemNodeType nodeType) {
			this.launcher.launch(nodeType);
		}

		public void assertLaunched(
				final List<String> expectedAdditionalArguments,
				final String expectedWorkingDirectory) throws IOException {
			@SuppressWarnings("unchecked")
			final ArgumentCaptor<List<String>> argumentsCaptor = ArgumentCaptor.forClass((Class)List.class);
			final ArgumentCaptor<File> workingDirectoryCaptor = ArgumentCaptor.forClass(File.class);
			Mockito.verify(this.processLauncher, Mockito.only())
					.launch(argumentsCaptor.capture(), workingDirectoryCaptor.capture());

			// Assert:
			final List<String> expectedArguments = new ArrayList<>(expectedAdditionalArguments);
			expectedArguments.addAll(Arrays.asList(
					"-cp",
					"." + File.pathSeparator + "./*" + File.pathSeparator + "../libs/*",
					"org.nem.deploy.CommonStarter"));
			Assert.assertThat(argumentsCaptor.getValue(), IsEqual.equalTo(expectedArguments));
			Assert.assertThat(workingDirectoryCaptor.getValue().toString(), IsEqual.equalTo(expectedWorkingDirectory));
		}
	}
}