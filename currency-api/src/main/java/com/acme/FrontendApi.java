package com.acme;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/api")
public class FrontendApi {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@RestClient
	CurrencyConversionClient currencyConversionClient;
	
	@GET
	@Path("/convert/from/{from}/to/{to}/amount/{amount}")
	@Produces(MediaType.APPLICATION_JSON)	
	public Response convert(@PathParam("from") final String from, 
			@PathParam("to") final String to, 
			@PathParam("amount") final Integer amount) {
		logger.debug("convert from: {}, to: {}, amount: {}", from, to, amount);
		return currencyConversionClient.convert(from, to, amount);
	}

}
