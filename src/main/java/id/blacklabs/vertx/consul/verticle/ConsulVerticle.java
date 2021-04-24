package id.blacklabs.vertx.consul.verticle;

import id.blacklabs.vertx.consul.MainVerticle;
import id.blacklabs.vertx.consul.service.ConsulService;
import id.blacklabs.vertx.consul.service.ConsulServiceImpl;
import io.vertx.core.Promise;
import io.vertx.serviceproxy.ServiceBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author krissadewo
 * @date 4/23/21 9:43 AM
 */
public class ConsulVerticle extends ApplicationVerticle {

    private final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

    private ConsulService consulServiceProxy;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        super.start(startPromise);

        consulServiceProxy = ConsulService.createProxy(vertx, "consul.service");
        consulServiceProxy.registerService(registerResult -> {
            if (!registerResult.succeeded()) {
                logger.error(registerResult.cause().getMessage());
            }
        });
    }

    @Override
    void registerService() {
        new ServiceBinder(vertx)
            .setAddress("consul.service")
            .register(ConsulService.class, new ConsulServiceImpl(vertx, env))
            .exceptionHandler(event -> {
                logger.error(event.getCause().getMessage());
            });
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        consulServiceProxy.deregisterService(stopPromise);
    }
}
