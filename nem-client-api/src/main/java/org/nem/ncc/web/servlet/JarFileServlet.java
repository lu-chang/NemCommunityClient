package org.nem.ncc.web.servlet;

import org.eclipse.jetty.http.HttpContent;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.util.resource.Resource;
import org.nem.ncc.NccConfiguration;

import javax.servlet.http.*;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.*;

public class JarFileServlet extends DefaultServlet {
	private static final Logger LOGGER = Logger.getLogger(JarFileServlet.class.getName());

	private final NccConfiguration configuration = ServletUtils.getNccConfiguration();

	/**
	 * get Resource to serve. Map a path to a resource. The default implementation calls HttpContext.getResource but derived servlets may provide their own
	 * mapping.
	 *
	 * @param pathInContext The path to find a resource for.
	 * @return The resource to serve.
	 */
	@Override
	public Resource getResource(final String pathInContext) {
		final String contextStr = String.format("%s%s", this.configuration.getWebContext(), "/");
		if (contextStr.length() > pathInContext.length()) {
			LOGGER.severe(String.format("Resource not found: <%s>, mapping to welcome page.", pathInContext));
			return null;
		}

		final String resourceStr = pathInContext.substring(contextStr.length());
		if (resourceStr.isEmpty()) {
			return null;
		}

		final String relativeResource = "web/" + resourceStr;
		final ClassLoader classLoader = this.getClass().getClassLoader();
		URL url = classLoader.getResource(relativeResource);
		if (url == null) {
			url = classLoader.getResource(resourceStr);
			if (url == null) {
				LOGGER.severe(String.format("Resource not found: <%s>, mapping to welcome page.", resourceStr));
				return null;
			}
		}

		final Resource r = Resource.newResource(url);
		if (!r.exists()) {
			LOGGER.log(Level.SEVERE, "Resource does not exist", url);
		}

		return r;
	}

	/* ------------------------------------------------------------ */
	/*
	 * Set the no cache pragma and proxy settings. Never cache anything.
	 * 
	 * @see org.eclipse.jetty.servlet.DefaultServlet#sendData(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, boolean,
	 * org.eclipse.jetty.util.resource.Resource, org.eclipse.jetty.http.HttpContent, java.util.Enumeration)
	 */
	@Override
	protected void sendData(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final boolean include,
			final Resource resource,
			final HttpContent content,
			final Enumeration<String> reqRanges) throws IOException {

		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		response.setDateHeader("Expires", 0); // Proxies.
		super.sendData(request, response, include, resource, content, reqRanges);
	}
}