package org.nem.specific.deploy;

import org.nem.deploy.*;
import org.nem.ncc.web.servlet.*;
import org.nem.specific.deploy.appconfig.NccAppConfig;

import javax.servlet.http.HttpServlet;

/**
 * Class for supplying addition NCC configuration information.
 */
public class NccConfigurationPolicy implements NemConfigurationPolicy {

	@Override
	public Class getAppConfigClass() {
		return NccAppConfig.class;
	}

	@Override
	public Class getWebAppInitializerClass() {
		return NccWebAppInitializer.class;
	}

	@Override
	public Class<? extends HttpServlet> getJarFileServletClass() {
		return JarFileServlet.class;
	}

	@Override
	public Class<? extends HttpServlet> getDefaultServletClass() {
		return NccDefaultServlet.class;
	}

	@Override
	public CommonConfiguration loadConfig(final String[] args) {
		return new CommonConfiguration();
	}
}
