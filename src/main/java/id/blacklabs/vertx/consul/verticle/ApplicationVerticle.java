package id.blacklabs.vertx.consul.verticle;

import id.blacklabs.vertx.consul.common.Common;
import id.blacklabs.vertx.consul.common.Environment;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;

/**
 * @author krissadewo
 * @date 4/23/21 8:25 PM
 */
public abstract class ApplicationVerticle extends AbstractVerticle {

    protected Environment env;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        super.start(startPromise);

        env = Json.decodeValue((String) vertx.sharedData()
            .getLocalMap(Common.LOCAL_MAP_CONFIG)
            .get(Common.LOCAL_MAP_ENV), Environment.class);

        registerService();
    }

    void registerService() {
    }
}
