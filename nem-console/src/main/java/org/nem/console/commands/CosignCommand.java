package org.nem.console.commands;

import org.apache.commons.cli.*;
import org.nem.core.crypto.Hash;
import org.nem.core.model.*;
import org.nem.core.model.primitive.Amount;
import org.nem.core.time.TimeInstant;

import java.util.function.Function;

/**
 * A command for generating a multisig signature.
 */
public class CosignCommand extends TransactionCommand {

	/**
	 * Creates a cosign command.
	 */
	public CosignCommand() {
		super("cosign");
	}

	@Override
	protected Transaction createTransaction(
			final TimeInstant timeStamp,
			final Account sender,
			final Function<String, Account> accountLookup,
			final CommandLine commandLine) {
		final Hash hash = Hash.fromHexString(commandLine.getOptionValue("hash"));
		final Account multisig = accountLookup.apply(commandLine.getOptionValue("multisig"));
		System.out.println(String.format("cosigning with %s for multisig %s (%s)", sender, multisig, hash));
		return new MultisigSignatureTransaction(
				timeStamp,
				sender,
				multisig,
				hash);
	}

	@Override
	protected void addCustomOptions(final Options options) {
		options.addOption("multisig", true, "The multisig account");
		options.addOption("hash", true, "The hash of the transaction to sign");
	}
}
