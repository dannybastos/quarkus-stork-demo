package org.acme;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri="stork://currency-service")
public interface CurrencyServiceClient {

	@GET
	@Path("currency-service/from/{from}/to/{to}")
	@Retry(maxRetries = 10)
	@Timeout(300)
	@CircuitBreaker(requestVolumeThreshold = 50)
	CurrencyConversionBean convert(@PathParam("from") final String from, @PathParam("to") String to);
}
