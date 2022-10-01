package org.acme;

import java.math.BigDecimal;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/converter")
public class CurrencyConversionController {
	
    @RestClient
    CurrencyServiceClient currencyServiceClient;

	@GET
    @Path("from/{from}/to/{to}/quantity/{quantity}")
    public Response convertCurrency(@PathParam("from") final String from,
                                    @PathParam("to") final String to,
                                    @PathParam("quantity") final BigDecimal quantity) {

		CurrencyConversionBean currencyConversionBean = currencyServiceClient.convert(from, to);
		currencyConversionBean.setQuantity(quantity);
		currencyConversionBean.setTotalCalculatedAmount(quantity.multiply(currencyConversionBean.getConversionMultiple()));
        return Response.ok(currencyConversionBean).build();
    }
}