package id.blacklabs.vertx.consul;

import id.blacklabs.vertx.consul.common.Common;
import id.blacklabs.vertx.consul.verticle.ConsulVerticle;
import id.blacklabs.vertx.consul.verticle.HttpServerVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.SharedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MainVerticle extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

    private final Set<String> deployedVerticle = new HashSet<>();

    @Override
    public void start() throws Exception {
        String env = config().getString("active.profile", null);//fill it with default value

        if (env == null) {
            logger.error("Run with {}", "run id.blacklabs.vertx.consul.MainVerticle -conf src/main/resources/env/local/config.json");

            stop();

            throw new RuntimeException();
        }

        retrieveConfig().getConfig(asyncResult -> {
            if (!Collections.disjoint(vertx.deploymentIDs(), deployedVerticle)) {
                logger.warn("The verticle already deployed");

                return;
            }

            if (asyncResult.succeeded()) {
                config().mergeIn(asyncResult.result());

                SharedData sharedData = vertx.sharedData();
                sharedData.getLocalMap(Common.LOCAL_MAP_CONFIG).put(Common.LOCAL_MAP_ENV, config().encode());

                vertx.deployVerticle(new HttpServerVerticle(), deployedHttpResult -> {
                    if (deployedHttpResult.succeeded()) {
                        deployedVerticle.add(deployedHttpResult.result());
                    }
                });

                vertx.deployVerticle(new ConsulVerticle(), deployedConsulResult -> {
                    if (!deployedConsulResult.succeeded()) {
                        logger.error("Failed deploy consul verticle {}", deployedConsulResult.cause().getMessage());
                    }
                });
            } else {
                logger.error("retrieving configuration form consul failed [{}]", asyncResult.cause().getMessage());
            }
        });
    }

    private ConfigRetriever retrieveConfig() {
        ConfigStoreOptions consulConfig = new ConfigStoreOptions()
            .setType("consul")
            .setConfig(new JsonObject()
                .put("host", config().getValue("consul.host"))
                .put("port", 8500)
                .put("prefix", config().getValue("consul.prefix"))
                .put("raw-data", false)
            );

        ConfigRetrieverOptions retrieverOptions = new ConfigRetrieverOptions()
            .addStore(consulConfig);

        ConfigRetriever retriever = ConfigRetriever.create(vertx, retrieverOptions);
        retriever.listen(configChange -> { //Handle configuration change
            logger.info("listening config retreiver [{}]", configChange.toJson());

            config().mergeIn(configChange.getNewConfiguration()); //do nothing
        });

        return retriever;
    }

}
