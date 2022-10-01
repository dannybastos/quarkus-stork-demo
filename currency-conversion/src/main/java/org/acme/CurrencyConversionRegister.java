package org.acme;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.eclipse.microprofile.config.ConfigProvider;
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
public class CurrencyConversionRegister {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final String QUARKUS_HTTP_PORT = "quarkus.http.port";

	@ConfigProperty(name="consul.host")
	String consulHost;
	@ConfigProperty(name="consul.port")
	int consulPort;
	@ConfigProperty(name="quarkus.application.name")
	String appName;
	@ConfigProperty(name="environment")
	String env;
	@ConfigProperty(name="quarkus.application.version")
	private String appVersion;

	private String instanceId;
	
	private ConsulClient consulClient;

	public void init(@Observes StartupEvent ev, Vertx vertx) {
		ScheduledExecutorService executorService = Executors
				.newSingleThreadScheduledExecutor();
		executorService.schedule(() -> {
			consulClient = ConsulClient.create(vertx,
					new ConsulClientOptions().setHost(consulHost).setPort(consulPort));
			String appPort = ConfigProvider.getConfig().getValue(QUARKUS_HTTP_PORT, String.class);
			log.info("registering service: {}, port:{}", appName, appPort);
			Map<String, String> meta = new HashMap<>();
			meta.put("version", appVersion);
			instanceId = appName + "-" + System.currentTimeMillis();
			consulClient.registerServiceAndAwait(
					new ServiceOptions()
							.setName(appName)
							.setMeta(meta)
							.setAddress(env)
							.setPort(Integer.parseInt(appPort))
							.setId(instanceId));
		}, 5, TimeUnit.SECONDS);
	}

	void onStop(@Observes ShutdownEvent ev, Vertx vertx) {
		consulClient.deregisterServiceAndAwait(instanceId);
		log.info("Instance de-registered: id={}", appName);
	}	
}
