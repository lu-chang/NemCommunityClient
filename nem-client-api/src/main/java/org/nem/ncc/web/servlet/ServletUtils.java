package org.nem.ncc.web.servlet;

import org.nem.deploy.CommonConfiguration;
import org.nem.ncc.NccConfiguration;

/**
 * Helper class containing servlet utility functions.
 */
class ServletUtils {

	/**
	 * Gets the NCC configuration.
	 * This is a workaround because injecting with Spring didn't work.
	 *
	 * @return The NCC configuration.
	 */
	public static NccConfiguration getNccConfiguration() {
		return new NccConfiguration(new CommonConfiguration());
	}
}
