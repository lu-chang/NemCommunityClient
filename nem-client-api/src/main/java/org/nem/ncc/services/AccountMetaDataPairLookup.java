package org.nem.ncc.services;

import org.nem.core.model.Address;
import org.nem.core.model.ncc.AccountMetaDataPair;
import org.nem.core.serialization.SimpleAccountLookup;

/**
 * An interface for looking up account metadata pairs.
 */
public interface AccountMetaDataPairLookup extends SimpleAccountLookup {

	/**
	 * Looks up a metadata pair by its id.
	 *
	 * @param id The account id.
	 * @return The account with the specified id.
	 */
	AccountMetaDataPair findPairByAddress(final Address id);

	/**
	 * Force refresh of account inside lookup object.
	 *
	 * @param address The account id.
	 */
	void refreshAccount(final Address address);
}