package com.acme;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.vertx.ext.consul.ConsulClientOptions;
import io.vertx.ext.consul.ServiceOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.consul.ConsulClient;

@ApplicationScoped
public class CurrencyApiRegistration {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@ConfigProperty(name = "consul.host")
	String consulHost;
	@ConfigProperty(name = "consul.port")
	int consulPort;
	@ConfigProperty(name = "environment")
	String env;

	@ConfigProperty(name = "quarkus.application.name")
	String appName;

	@ConfigProperty(name = "quarkus.http.port")
	String appPort;

	private String instanceId;
	private ConsulClient consulClient;

	public void init(@Observes StartupEvent ev, Vertx vertx) {
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.schedule(() -> {
			consulClient = ConsulClient.create(vertx,
					new ConsulClientOptions().setHost(consulHost).setPort(consulPort));
			
			log.info("registering service: {}, port:{}", appName, appPort);
			instanceId = appName + "-" + System.currentTimeMillis();
			consulClient.registerServiceAndAwait(new ServiceOptions()
					.setPort(Integer.parseInt(appPort))
					.setAddress(env)
					.setName(appName)
					.setId(instanceId));
		}, 5, TimeUnit.SECONDS);
	}

	
	public void stop(@Observes ShutdownEvent ev, Vertx vertx) {
		consulClient.deregisterServiceAndAwait(instanceId);
		log.info("Instance de-registered: id={}", appName);
	}
}
