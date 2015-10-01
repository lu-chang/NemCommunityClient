package org.nem.console.commands;

import org.apache.commons.cli.*;
import org.nem.console.models.AliasedKeyPair;
import org.nem.console.utils.*;
import org.nem.core.crypto.*;
import org.nem.core.model.*;
import org.nem.core.time.TimeInstant;
import org.nem.core.utils.HexEncoder;

import java.util.Collection;
import java.util.function.Function;

/**
 * A base class for all transaction commands.
 */
public abstract class TransactionCommand implements Command {
	private final String name;

	/**
	 * Creates a transaction command.
	 *
	 * @param name The command name.
	 */
	protected TransactionCommand(final String name) {
		this.name = name;
	}

	@Override
	public final String name() {
		return this.name;
	}

	@Override
	public final void handle(final CommandLine commandLine) {
		final Collection<AliasedKeyPair> keyPairs = KeyPairsStorage.load(commandLine);

		final String senderAlias = commandLine.getOptionValue("sender");
		final Transaction transaction = this.createTransaction(
				new TimeInstant(Integer.parseInt(commandLine.getOptionValue("time"))),
				this.findByAlias(keyPairs, senderAlias),
				alias -> this.findByAlias(keyPairs, alias),
				commandLine);

		prepareAndSign(transaction);
		TransactionStorage.save(transaction, commandLine);
		System.out.println("Created transaction: ");
		System.out.println(String.format("     type: %d", transaction.getType()));
		System.out.println(String.format("  version: %d", transaction.getVersion()));
		System.out.println(String.format("   sender: %s", transaction.getSigner()));
		System.out.println(String.format("      fee: %s", transaction.getFee()));
	}

	/**
	 * Creates a transaction.
	 *
	 * @param timeStamp The transaction timestamp.
	 * @param sender The transaction sender
	 * @param accountLookup A function that can be used to get an account given an address.
	 * @param commandLine The user-supplied command line.
	 * @return The transaction
	 */
	protected abstract Transaction createTransaction(
			final TimeInstant timeStamp,
			final Account sender,
			final Function<String, Account> accountLookup,
			final CommandLine commandLine);

	private Account findByAlias(final Collection<AliasedKeyPair> keyPairs, final String alias) {
		for (final AliasedKeyPair keyPair : keyPairs) {
			if (keyPair.alias().equals(alias)) {
				return new Account(keyPair.keyPair());
			}
		}

		try {
			final PublicKey publicKey = new PublicKey(HexEncoder.getBytes(alias));
			return new Account(new KeyPair(publicKey));
		} catch (final Exception ex) {
			// ignore for now
			// TODO this is a hack
		}

		final Address address = Address.fromEncoded(alias);
		if (!address.isValid()) {
			throw new IllegalArgumentException(String.format("no known key pair for alias '%s'", alias));
		}

		return new Account(address);
	}

	private static void prepareAndSign(final Transaction transaction) {
		final DefaultTransactionFeeCalculator calculator = new DefaultTransactionFeeCalculator();
		transaction.setFee(calculator.calculateMinimumFee(transaction));
		transaction.setDeadline(transaction.getTimeStamp().addHours(12));
		transaction.sign();
	}

	@Override
	public final Options options() {
		final Options options = new Options();
		OptionsUtils.addReadOptions(options);
		options.addOption("sender", true, "The sender alias");
		options.addOption("time", true, "The soft timestamp");
		options.addOption("output", true, "The output transaction file");
		this.addCustomOptions(options);
		return options;
	}

	/**
	 * Adds custom transaction-specific options to the specified options.
	 *
	 * @param options The options to update.
	 */
	protected abstract void addCustomOptions(Options options);
}
