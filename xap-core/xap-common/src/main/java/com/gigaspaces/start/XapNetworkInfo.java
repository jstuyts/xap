package com.gigaspaces.start;

import org.jini.rio.boot.BootUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Niv Ingberg
 * @since 12.1
 */
public class XapNetworkInfo {
    private static volatile XapNetworkInfo instance;

    private final String hostId;
    private final InetAddress host;
    private final InetAddress publicHost;
    private String publicHostId;
    private boolean publicHostConfigured;
    private final String kubernetesServiceHost;

    private final String kubernetesClusterId;

    private final boolean kubernetesServiceConfigured;
    public static XapNetworkInfo getInstance() {
        XapNetworkInfo snapshot = instance;
        if (snapshot != null)
            return snapshot;
        synchronized (XapNetworkInfo.class) {
            if (instance == null)
                instance = new XapNetworkInfo();
            return instance;
        }
    }

    private XapNetworkInfo() {
        try {
            this.hostId = BootUtil.getHostAddress();
            this.host = InetAddress.getByName(hostId);

            //apply public host if configured, otherwise same as bind address
            publicHostId = System.getenv("XAP_PUBLIC_HOST");
            if (publicHostId == null) {
                publicHostId = hostId;
                publicHost = host;
            } else {
                publicHostConfigured = true;
                publicHost = InetAddress.getByName(publicHostId);
            }

        } catch (UnknownHostException e) {
            throw new IllegalStateException("Failed to get network information", e);
        }

        this.kubernetesServiceHost = System.getenv("XAP_KUBERNETES_HOST");
        this.kubernetesClusterId = System.getenv("KUBERNETES_CLUSTER_ID");
        this.kubernetesServiceConfigured = validateString(kubernetesServiceHost) && validateString(kubernetesClusterId);
    }

    public String getHostId() {
        return hostId;
    }

    public InetAddress getHost() {
        return host;
    }


    public String getPublicHostId() {
        return publicHostId;
    }

    public InetAddress getPublicHost() {
        return publicHost;
    }


    public boolean isPublicHostConfigured(){
        return publicHostConfigured;
    }

    public String getKubernetesServiceHost() {
        return kubernetesServiceHost;
    }

    public String getKubernetesClusterId() {
        return kubernetesClusterId;
    }

    public boolean isKubernetesServiceConfigured() {
        return kubernetesServiceConfigured;
    }

    private boolean validateString(String s){
        return s != null && !s.isEmpty() && !s.equals("null");
    }
}
