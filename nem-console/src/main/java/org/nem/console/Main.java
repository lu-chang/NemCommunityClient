package org.nem.console;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.ArrayUtils;
import org.nem.console.commands.*;
import org.nem.core.model.NetworkInfos;
import org.nem.core.utils.StringEncoder;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.*;

public class Main {
	private static final Command[] COMMANDS = new Command[] {
			new GenerateCommand(),
			new ReEncryptCommand(),

			new DumpContentsCommand(),
			new ImportanceTransferCommand(),
			new TransferCommand(),
			new CosignCommand()
	};

	// TODO 20150407 J-J add tests for the command package
	// TODO 20150407 J-J add secure password entry

	// TODO: few notes: I think it'd be nicer to split generator into generator and vanitygen
	// generator itself, would only generate random key(s) and associate it with some alias(es) (i.e. cosig1,cosig2,cosig3... etc)
	// vanitygen would generate only a single address, but the one that .contains() searched string (and associate it with some alias (i.e. gimreMain)
	//
	// next step is ofc aggregate transaction, where I can pass --time=... --multisig=gimreMain --cosignatories=cosig1,cosig2,cosig3 as cosignatories
	// and multisig-transfer, where I can pass --time=... --multisig=gimreMain --cosignatory=cosig1 --recipient=xxxxx --amount=xxxxx
	// (or maybe even allow --cosignatories and then it would be capable of generating multisig tx along with appropriate signatures)
	public static void main(final String[] args) throws ParseException, IOException {
		NetworkInfos.setDefault(NetworkInfos.getMainNetworkInfo());
		// RunDebugScenario();
		handle(args);
	}

	private static void handle(final String[] args) throws ParseException, IOException {
		final CommandLineParser parser = new PosixParser();
		final Options options = mergeAllOptions();
		options.addOption("secure", false, "prompt for password");
		options.addOption("file", true, "file containing commands to execute");
//		options.addOption("pass", true, "The cold wallet password");
//		options.addOption("numHashes", true, "The number of password hashes");

		// TODO 20150409 support propagation of all parameters downstream

		CommandLine commandLine = parser.parse(options, args);
		if (commandLine.hasOption("secure")) {
			final String password = new String(System.console().readPassword("Password: "));
			final String numHashes = new String(System.console().readPassword("  Hashes: "));
			final String[] newArgs = ArrayUtils.addAll(
					args,
					"--pass=" + password,
					"--numHashes=" + numHashes);
			commandLine = parser.parse(options, newArgs);
		}

		final String password = commandLine.getOptionValue("pass");
		final String numHashes = commandLine.getOptionValue("numHashes");
		final String input = commandLine.getOptionValue("input");
		final List<String[]> commandArgumentsList = new ArrayList<>();
		if (commandLine.hasOption("file")) {
			final File commandFile = new File(commandLine.getOptionValue("file"));
			try (final Stream<String> lines = Files.lines(commandFile.toPath())) {
				commandArgumentsList.addAll(lines.map(l -> l.split(" ")).collect(Collectors.toList()));
			}
		} else {
			commandArgumentsList.add(args);
		}

		for (final String[] commandArguments : commandArgumentsList) {
			final String[] newArgs = ArrayUtils.addAll(
					commandArguments,
					"--input=" + input,
					"--pass=" + password,
					"--numHashes=" + numHashes);
			if (!processCommand(newArgs)) {
				OutputUsage();
			}

			System.out.println();
		}
	}

	private static boolean processCommand(final String[] args) throws ParseException, IOException {
		if (args.length < 1) {
			return false;
		}

		final String mode = args[0].toLowerCase();
		for (final Command command : COMMANDS) {
			if (!command.name().equals(mode)) {
				continue;
			}

			final CommandLineParser parser = new PosixParser();
			final Options options = command.options();
			final CommandLine commandLine = parser.parse(options, args);
			command.handle(commandLine);
			return true;
		}

		return false;
	}


	private static Options mergeAllOptions() {
		final Options options = new Options();
		for (final Command command : COMMANDS) {
			for (final Object object : command.options().getOptions()) {
				final Option option = (Option)object;
				if (!options.hasOption(option.getArgName())) {
					options.addOption(option);
				}
			}
		}

		return options;
	}


//	private static void RunDebugScenario() throws ParseException {
//		mainImpl(new String[] { "generate", "--pass=foo bar", "--prefixes=NA,NA2,NB,NC", "--output=sec_orig.dat" });
//		mainImpl(new String[] { "reencrypt", "--input=sec_orig.dat", "--pass=foo bar", "--newPass=bar foo", "--output=sec.dat" });
//		mainImpl(new String[] { "dump", "--pass=bar foo", "--input=sec.dat", "--showPrivate=true", "--filter=NA" });
//		mainImpl(new String[] { "transfer", "--pass=bar foo", "--input=sec.dat", "--output=transfer.dat", "--time=4590033", "--sender=NA", "--recipient=NB", "--amount=1000000" });
//		mainImpl(new String[] { "importance", "--pass=bar foo", "--input=sec.dat", "--output=importance.dat", "--time=4590033", "--sender=NA", "--remote=NC" });
//	}

	private static void OutputUsage(final Command command) {
		System.out.println(String.format("*** %s ***", command.name()));
		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(command.name(), command.options());
	}

	private static void OutputUsage() {
		for (final Command command : COMMANDS) {
			OutputUsage(command);
		}
	}
}