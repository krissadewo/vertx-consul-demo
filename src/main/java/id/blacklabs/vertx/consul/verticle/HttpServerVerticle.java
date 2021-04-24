package id.blacklabs.vertx.consul.verticle;

import id.blacklabs.vertx.consul.MainVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author krissadewo
 * @date 4/22/21 3:28 PM
 */
public class HttpServerVerticle extends ApplicationVerticle {

    private final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        super.start(startPromise);

        HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx);
        healthCheckHandler.register("http-service", future -> future.complete(Status.OK()));

        Router router = Router.router(vertx);
        router.get("/").handler(req -> {
            req.response()
                .putHeader("content-type", "application/json")
                .end(Json.encode(env));
        });

        router.get("/health*").handler(healthCheckHandler);
        router.get("/ping*").handler(healthCheckHandler);
        router.put("/check*").handler(healthCheckHandler);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8888, http -> {
                if (http.succeeded()) {
                    logger.debug("HTTP server started on port 8888");
                }
            });
    }

}
