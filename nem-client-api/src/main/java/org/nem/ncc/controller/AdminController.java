package org.nem.ncc.controller;

import org.nem.core.model.NemStatus;
import org.nem.core.model.ncc.NemRequestResult;
import org.nem.core.serialization.SerializableEntity;
import org.nem.deploy.CommonStarter;
import org.nem.ncc.cache.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller dealing with administrative functions.
 */
@RestController
public class AdminController {
	private final AccountsFileRepository repository;
	private final NccAccountCache accountCache;
	private final CommonStarter commonStarter;

	@Autowired(required = true)
	public AdminController(
			final AccountsFileRepository repository,
			final NccAccountCache accountCache,
			final CommonStarter commonStarter) {
		this.repository = repository;
		this.accountCache = accountCache;
		this.commonStarter = commonStarter;
	}

	/**
	 * Simple heartbeat api that can be pinged to test the online status of NCC.
	 *
	 * @return An entity representing a successful result.
	 */
	@RequestMapping(value = "/heartbeat", method = RequestMethod.GET)
	public SerializableEntity heartbeat() {
		return serializer -> serializer.writeString("status", "ok");
	}

	/**
	 * Stops the current NCC server. Afterwards it has to be started via WebStart again (or just running {@link org.nem.ncc.NccMain})
	 */
	@RequestMapping(value = "/shutdown", method = RequestMethod.GET)
	public void shutdown() {
		this.flushAccountsCache();
		this.commonStarter.stopServerAsync();
	}

	/**
	 * Gets the NCC status.
	 *
	 * @return The NIS request result.
	 */
	@RequestMapping(value = "/status", method = RequestMethod.GET)
	public NemRequestResult status() {
		return new NemRequestResult(NemRequestResult.TYPE_STATUS, NemStatus.RUNNING.getValue(), "status");
	}

	/**
	 * Flushes (saves to disk) the account cache.
	 */
	private void flushAccountsCache() {
		this.repository.save(this.accountCache.getAccounts());
	}
}
