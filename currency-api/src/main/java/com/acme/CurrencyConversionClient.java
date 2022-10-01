package com.acme;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "stork://currency-conversion")
public interface CurrencyConversionClient {
	
	@GET
	@Path("converter/from/{from}/to/{to}/quantity/{quantity}")
	@Retry(maxRetries = 10)
	@Timeout(300)
	@CircuitBreaker(requestVolumeThreshold = 50)
	Response convert(@PathParam("from") final String from, 
			@PathParam("to") final String to, 
			@PathParam("quantity") final Integer quantity);
}