package org.acme;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Optional;


@Path("/currency-service")
public class CurrencyServiceController {

    @Inject
    ExchangeValueRepository exchangeValueRepository;

    @ConfigProperty(name = "quarkus.http.port")
    int port;

    @GET
    @Path("/from/{from}/to/{to}")
    public Response retrieveExchangeValue(@PathParam("from") final String from,
                                          @PathParam("to") final String to) {
        Optional<ExchangeValue> exchangeValue = exchangeValueRepository.findFromAndTo(from, to);
        if (exchangeValue.isPresent()) {
            exchangeValue.get().setPort(port);
        	return Response.ok(exchangeValue.get()).build();        	
        } else {
        	return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
