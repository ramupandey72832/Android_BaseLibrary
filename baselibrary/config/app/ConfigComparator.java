package com.rohit.baselibrary.config.app;

public class ConfigComparator {

    /**
     * Compares the current config version from ConfigWrapper with the latest version from UpdatedConfig.
     * 
     * @param currentConfig The current configuration wrapper.
     * @param updateResponse The latest update information.
     * @return true if an update is available (latest version is different from current version), false otherwise.
     */
    public boolean isUpdateAvailable(ConfigWrapper currentConfig, UpdatedConfig updateResponse) {
        if (currentConfig == null || updateResponse == null) {
            return false;
        }

        Config config = currentConfig.getConfig();
        UpdateInfo updateInfo = updateResponse.getUpdateCheck();

        if (config == null || updateInfo == null) {
            return false;
        }

        String currentVersion = config.getConfigVersion();
        String latestVersion = updateInfo.getConfigLatestVersion();

        if (currentVersion == null || latestVersion == null) {
            return false;
        }

        // Returns true if versions are different
        return !currentVersion.equals(latestVersion);
    }

    /**
     * Checks if a force update is required based on the UpdateResponse.
     * 
     * @param updateResponse The latest update information.
     * @return true if forceUpdate policy is set to true.
     */
    public boolean isForceUpdateRequired(UpdatedConfig updateResponse) {
        if (updateResponse == null || updateResponse.getPolicy() == null) {
            return false;
        }
        return updateResponse.getPolicy().isForceUpdate();
    }
}
