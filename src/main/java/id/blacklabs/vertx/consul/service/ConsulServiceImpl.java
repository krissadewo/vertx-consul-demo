package id.blacklabs.vertx.consul.service;

import id.blacklabs.vertx.consul.common.Environment;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.consul.CheckOptions;
import io.vertx.ext.consul.ConsulClient;
import io.vertx.ext.consul.ServiceOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author krissadewo
 * @date 4/22/21 9:39 PM
 */
public class ConsulServiceImpl implements ConsulService {

    private final ConsulClient client;

    private final Logger logger = LoggerFactory.getLogger(ConsulService.class);

    private final Environment env;

    public ConsulServiceImpl(Vertx vertx, Environment env) {
        this.client = ConsulClient.create(vertx);
        this.env = env;
    }

    @Override
    public void registerService(Handler<AsyncResult<Void>> resultHandler) {
        CheckOptions httpCheckOptions = new CheckOptions()
            .setServiceId(env.getServiceName())
            .setName("HTTP")
            .setHttp("http://localhost:8888/health")
            .setInterval("10s");

        ServiceOptions serviceOptions = new ServiceOptions()
            .setName(env.getServiceName())
            .setId(env.getServiceName())
            .setTags(Arrays.asList("tag1", "tag2"))
            .setCheckOptions(httpCheckOptions)
            .setAddress(env.getConsulHost());

        client.registerService(serviceOptions, event -> {
            if (event.succeeded()) {
                logger.info("{} successfully registered", env.getServiceName());

                resultHandler.handle(Future.succeededFuture(event.result()));
            } else {
                logger.error(event.cause().getMessage());

                resultHandler.handle(Future.failedFuture(event.cause()));
            }
        });
    }

    @Override
    public void deregisterService(Handler<AsyncResult<Void>> resultHandler) {
        client.deregisterService(env.getServiceName(), event -> {
            if (event.succeeded()) {
                logger.info("{} successfully deregistered", env.getServiceName());

                resultHandler.handle(Future.succeededFuture(event.result()));
            } else {
                logger.error(event.cause().getMessage());

                resultHandler.handle(Future.failedFuture(event.cause()));
            }
        });
    }

    @Override
    public void close() {
    }
}
