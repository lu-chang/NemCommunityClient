package org.nem.ncc.connector;

import org.nem.core.connect.HttpPostRequest;
import org.nem.core.connect.client.NisApiId;
import org.nem.core.node.NodeEndpoint;
import org.nem.core.serialization.Deserializer;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * A simple NIS connector that allows communication with a single node.
 */
public interface PrimaryNisConnector {

	/**
	 * Gets a value indicating whether or not this a NIS instance is accessible.
	 *
	 * @return true if a NIS instance is accessible
	 */
	boolean isConnected();

	/**
	 * Gets a response from the specified NIS relative url path.
	 *
	 * @param apiId The api to call.
	 * @param query The get query string or null.
	 * @return The result.
	 */
	Deserializer get(final NisApiId apiId, final String query);

	/**
	 * Forwards a synchronous request to the primary NIS node.
	 *
	 * @param request The request to forward.
	 * @return The result.
	 */
	<T> T forward(final Function<NodeEndpoint, CompletableFuture<T>> request);

	/**
	 * Forwards an asynchronous request to the primary NIS node.
	 *
	 * @param request The request to forward.
	 * @return The result.
	 */
	<T> CompletableFuture<T> forwardAsync(final Function<NodeEndpoint, CompletableFuture<T>> request);

	/**
	 * Posts a request to the specified NIS relative url path.
	 *
	 * @param apiId The api to call.
	 * @param postRequest The request data.
	 * @return The result.
	 */
	Deserializer post(final NisApiId apiId, final HttpPostRequest postRequest);

	/**
	 * Posts a request to the specified NIS relative url path.
	 *
	 * @param apiId The api to call.
	 * @param postRequest The request data.
	 */
	void voidPost(final NisApiId apiId, final HttpPostRequest postRequest);
}
