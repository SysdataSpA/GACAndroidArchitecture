package sysdata.it.androidarchitecture;

/**
 * Created by Andrea Guitto on 18/01/2018.
 */

public interface BaseModuleConfiguration {
    void onClassProviderSetup(DependencyManager.ConfigurationDependencyLoader loader);
    void onClassProviderDiscard(DependencyManager.ConfigurationDependencyLoader loader);
}
