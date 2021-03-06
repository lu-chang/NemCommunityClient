package org.nem.console;

import org.apache.commons.cli.*;
import org.nem.console.commands.*;
import org.nem.core.model.NetworkInfos;

public class Main {
	private static final Command[] COMMANDS = new Command[] {
			new GenerateCommand(),
			new ReEncryptCommand(),

			new DumpContentsCommand(),
			new ImportanceTransferCommand(),
			new TransferCommand()
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
	public static void main(final String[] args) throws ParseException {
		NetworkInfos.setDefault(NetworkInfos.getMainNetworkInfo());
		// RunDebugScenario();
		mainImpl(args);
	}

	private static void mainImpl(final String[] args) throws ParseException {
		if (0 == args.length) {
			OutputUsage();
			return;
		}

		final String mode = args[0].toLowerCase();
		for (final Command command : COMMANDS) {
			if (!command.name().equals(mode)) {
				continue;
			}

			final CommandLineParser parser = new PosixParser();
			final CommandLine commandLine = parser.parse(command.options(), args);
			command.handle(commandLine);
			return;
		}

		OutputUsage();
	}

	private static void RunDebugScenario() throws ParseException {
		mainImpl(new String[] { "generate", "--pass=foo bar", "--prefixes=NA,NA2,NB,NC", "--output=sec_orig.dat" });
		mainImpl(new String[] { "reencrypt", "--input=sec_orig.dat", "--pass=foo bar", "--newPass=bar foo", "--output=sec.dat" });
		mainImpl(new String[] { "dump", "--pass=bar foo", "--input=sec.dat", "--showPrivate=true", "--filter=NA" });
		mainImpl(new String[] { "transfer", "--pass=bar foo", "--input=sec.dat", "--output=transfer.dat", "--time=4590033", "--sender=NA", "--recipient=NB", "--amount=1000000" });
		mainImpl(new String[] { "importance", "--pass=bar foo", "--input=sec.dat", "--output=importance.dat", "--time=4590033", "--sender=NA", "--remote=NC" });
	}

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