package org.nem.specific.deploy.appconfig;

import org.nem.core.connect.*;
import org.nem.core.connect.client.AsyncNisConnector;
import org.nem.core.metadata.ApplicationMetaData;
import org.nem.core.model.NetworkInfos;
import org.nem.core.time.TimeProvider;
import org.nem.deploy.*;
import org.nem.ncc.*;
import org.nem.ncc.addressbook.*;
import org.nem.ncc.addressbook.storage.SecureAddressBookDescriptorFactory;
import org.nem.ncc.cache.*;
import org.nem.ncc.connector.*;
import org.nem.ncc.model.graph.GraphViewModelFactory;
import org.nem.ncc.services.*;
import org.nem.ncc.time.synchronization.NccTimeSynchronizer;
import org.nem.ncc.wallet.*;
import org.nem.ncc.wallet.storage.SecureWalletDescriptorFactory;
import org.nem.specific.deploy.NccConfigurationPolicy;
import org.springframework.context.annotation.*;

import java.io.File;
import java.util.Collections;

/**
 * Class supplying Spring beans.
 */
@Configuration
public class NccAppConfig {
	private DefaultAsyncNisConnector connector;

	@Bean
	public AsyncNisConnector cloudConnector() {
		// work around a dependency loop
		// AsyncNisConnector -> accountCache -> accountServices -> PrimaryNisConnector -> AsyncNisConnector
		// not sure if there is a better way to deal with it
		if (null != this.connector) {
			return this.connector;
		}

		this.connector = new DefaultAsyncNisConnector(this.httpMethodClient());
		this.connector.setAccountLookup(this.accountLookup());
		return this.connector;
	}

	@Bean
	public PrimaryNisConnector primaryNisConnector() {
		return new DefaultNisConnector(
				() -> this.configuration().getNisEndpoint(),
				this.cloudConnector());
	}

	private HttpMethodClient<ErrorResponseDeserializerUnion> httpMethodClient() {
		final int CONNECTION_TIMEOUT = 10000;
		final int SOCKET_TIMEOUT = 10000;
		final int REQUEST_TIMEOUT = 20000;
		return new HttpMethodClient<>(CONNECTION_TIMEOUT, SOCKET_TIMEOUT, REQUEST_TIMEOUT);
	}

	@Bean
	public NccMain nccMain() {
		return new NccMain(this.nccConfiguration(), this.nccScheduler());
	}

	@Bean
	public CommonStarter commonStarter() {
		return CommonStarter.INSTANCE;
	}

	@Bean
	public AccountServices accountServices() {
		return new AccountServices(this.primaryNisConnector());
	}

	@Bean
	public ChainServices chainServices() {
		return new ChainServices(this.cloudConnector(), this.networkServices());
	}

	@Bean
	public NetworkServices networkServices() {
		return new NetworkServices(this.cloudConnector());
	}

	@Bean
	public NodeServices nodeServices() {
		return new NodeServices(this.cloudConnector());
	}

	@Bean
	public NccScheduler nccScheduler() {
		final NccScheduler scheduler = new NccScheduler(this.timeProvider());
		scheduler.addTimeSynchronizationTask(new NccTimeSynchronizer(this.timeSynchronizationServices(), this.timeProvider(), this.primaryNisConnector()));
		scheduler.addAccountCacheUpdateTask(this.accountCache(), this.accountsFileRepository());
		return scheduler;
	}

	public TimeSynchronizationServices timeSynchronizationServices() {
		return new TimeSynchronizationServices(this.cloudConnector());
	}

	@Bean
	public GraphViewModelFactory graphViewModelFactory() {
		return new GraphViewModelFactory();
	}

	@Bean
	public ApplicationMetaData applicationMetaData() {
		return CommonStarter.META_DATA;
	}

	@Bean
	public TimeProvider timeProvider() {
		return CommonStarter.TIME_PROVIDER;
	}

	@Bean
	public TransactionMapper transactionMapper() {
		return new TransactionMapper(
				this.walletServices(),
				this.accountLookup(),
				this.timeProvider());
	}

	@Bean
	public VersionServices versionServices() {
		final int CONNECTION_TIMEOUT = 2000;
		final int SOCKET_TIMEOUT = 2000;
		final int REQUEST_TIMEOUT = 4000;
		return new VersionServices(
				new VersionProvider(new HttpMethodClient<>(CONNECTION_TIMEOUT, SOCKET_TIMEOUT, REQUEST_TIMEOUT)),
				this.timeProvider()
		);
	}

	@Bean
	public AddressBookLocator addressBookLocator() {
		return new AddressBookFileLocator(this.getNemFolder());
	}

	@Bean
	public AddressBookMapper addressBookMapper() {
		return new AddressBookMapper();
	}

	@Bean
	public WalletLocator walletLocator() {
		return new WalletFileLocator(this.getNemFolder());
	}

	@Bean
	public WalletMapper walletMapper() {
		return new WalletMapper(this.accountMapper(), this.timeProvider());
	}

	@Bean
	public AccountMapper accountMapper() {
		return new AccountMapper(this.accountLookup());
	}

	@Bean
	public NccAccountCache accountCache() {
		final int refreshInSeconds = 60;
		final NccAccountCache accountCache = new NccAccountCache(this.accountServices(),
				this.timeProvider(),
				refreshInSeconds);
		accountCache.seedAccounts(this.accountsFileRepository().load());
		return accountCache;
	}

	@Bean
	@Primary
	public AccountMetaDataPairLookup accountLookup() {
		return new WalletAwareAccountLookup(this.accountCache(), this.walletServices());
	}

	@Bean
	public AccountsFileRepository accountsFileRepository() {
		final File file = new File(this.getNemFolder(), String.format("accounts_cache_%s.json", this.nccConfiguration().getNetworkName()));
		final AccountsFileDescriptor descriptor = new AccountsFileDescriptor(file);
		return new AccountsFileRepository(descriptor);
	}

	@Bean
	public WalletServices walletServices() {
		final WalletRepository walletRepository = new AutoFallbackRepository(Collections.singletonList(new BinaryWalletRepository()));

		return new DefaultWalletServices(
				walletRepository,
				new SecureWalletDescriptorFactory(this.getNemFolder()));
	}

	@Bean
	public AddressBookServices addressBookServices() {
		final AddressBookRepository addressBookRepository = new BinaryAddressBookRepository();

		return new DefaultAddressBookServices(
				addressBookRepository,
				new SecureAddressBookDescriptorFactory(this.getNemFolder()));
	}

	@Bean
	public NemConfigurationPolicy configurationPolicy() {
		return new NccConfigurationPolicy();
	}

	private File getNemFolder() {
		return new File(this.configuration().getNemFolder());
	}

	@Bean
	public org.nem.ncc.model.Configuration configuration() {
		return this.nccConfiguration().getConfiguration();
	}

	@Bean
	public CommonConfiguration commonConfiguration() {
		final CommonConfiguration commonConfiguration = new CommonConfiguration();
		NetworkInfos.setDefault(commonConfiguration.getNetworkInfo());
		return commonConfiguration;
	}

	@Bean
	public NccConfiguration nccConfiguration() {
		return new NccConfiguration(this.commonConfiguration());
	}
}
