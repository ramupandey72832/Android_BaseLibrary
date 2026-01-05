package com.rohit.baselibrary.config.app;

public class UpdateInfo {
    private String configLatestVersion;
    private String lastUpdated;
    private String configUrl;

    public String getConfigLatestVersion() {
        return configLatestVersion;
    }

    public void setConfigLatestVersion(String configLatestVersion) {
        this.configLatestVersion = configLatestVersion;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getConfigUrl() {
        return configUrl;
    }

    public void setConfigUrl(String configUrl) {
        this.configUrl = configUrl;
    }
}