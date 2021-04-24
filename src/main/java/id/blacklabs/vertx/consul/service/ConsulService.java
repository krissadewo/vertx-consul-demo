package id.blacklabs.vertx.consul.service;

import id.blacklabs.vertx.consul.common.Environment;
import io.vertx.codegen.annotations.ProxyClose;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * @author krissadewo
 * @date 4/22/21 9:17 PM
 */
@ProxyGen
@VertxGen
public interface ConsulService {

    static ConsulService create(Vertx vertx, Environment environment) {
        return new ConsulServiceImpl(vertx, environment);
    }

    static ConsulService createProxy(Vertx vertx, String address) {
        return new ConsulServiceVertxEBProxy(vertx, address);
    }

    void registerService(Handler<AsyncResult<Void>> resultHandler);

    void deregisterService(Handler<AsyncResult<Void>> resultHandler);

    @ProxyClose
    void close();
}
