package id.blacklabs.vertx.consul.common;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * @author krissadewo
 * @date 4/23/21 6:36 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@DataObject(generateConverter = true)
public class Environment {

    @JsonProperty("active.profile")
    private String activeProfile;

    @JsonProperty("service.name")
    private String serviceName;

    @JsonProperty("service.port")
    private String servicePort;

    @JsonProperty("consul.host")
    private String consulHost;

    @JsonProperty("consul.prefix")
    private String consulPrefix;

    private Service service;

    public Environment() {
    }

    public Environment(JsonObject json) {
        EnvironmentConverter.fromJson(json, this);
    }

    public JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }

    public Service getService() {
        return service;
    }

    public String getActiveProfile() {
        return activeProfile;
    }

    public void setActiveProfile(String activeProfile) {
        this.activeProfile = activeProfile;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServicePort() {
        return servicePort;
    }

    public void setServicePort(String servicePort) {
        this.servicePort = servicePort;
    }

    public String getConsulHost() {
        return consulHost;
    }

    public void setConsulHost(String consulHost) {
        this.consulHost = consulHost;
    }

    public String getConsulPrefix() {
        return consulPrefix;
    }

    public void setConsulPrefix(String consulPrefix) {
        this.consulPrefix = consulPrefix;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public static class Service {

        @JsonProperty("name")
        private String name;

        @JsonProperty("version")
        private String version;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
