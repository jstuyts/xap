package com.gigaspaces.start.manager;

import com.gigaspaces.CommonSystemProperties;
import com.gigaspaces.internal.utils.GsEnv;

import java.util.Map;
import java.util.Properties;

public class XapManagerConfig {
    // List of servers.
    // Each server has a host, and a map of component-to-port
    private final String host;
    private final Properties properties;

    private static final String DEFAULT_REST = GsEnv.property(CommonSystemProperties.MANAGER_REST_PORT).get("8090");
    private static final boolean SSL_ENABLED = Boolean.getBoolean(CommonSystemProperties.MANAGER_REST_SSL_ENABLED);

    public XapManagerConfig(String host) {
        this(host, new Properties());
    }

    public XapManagerConfig(String host, Properties properties) {
        this.host = host;
        this.properties = properties;
    }

    public static XapManagerConfig parse(String s) {
        final String[] tokens = s.split(";");
        final String host = tokens[0];
        final Properties properties = new Properties();
        for (int i=1 ; i < tokens.length ; i++) {
            String token = tokens[i];
            int pos = token.indexOf('=');
            if (pos == -1)
                throw new IllegalArgumentException("Invalid manager config '" + s + "' - element '" + token + "' does not contain '='");
            String key = token.substring(0, pos);
            String value = token.substring(pos+1);
            properties.setProperty(key, value);
        }
        return new XapManagerConfig(host, properties);
    }

    public String getHost() {
        return host;
    }

    public Properties getProperties() {
        return properties;
    }

    public String getZookeeper() {
        return properties.getProperty("zookeeper");
    }

    public String getLookupService() {
        return properties.getProperty("lus");
    }

    public String getAdminRest() {
        return properties.getProperty("rest", DEFAULT_REST);
    }

    public String getAdminRestUrl() {
        return getAdminRestUrl(getAdminRest());
    }

    public String getAdminRestUrl(String port) {
        return (SSL_ENABLED ? "https" : "http") + "://" + getHost() + ":" + port;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    @Override
    public String toString() {
        String result = host;
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            result += ";" + entry.getKey() + "=" + entry.getValue();
        }

        return result;
    }
}
