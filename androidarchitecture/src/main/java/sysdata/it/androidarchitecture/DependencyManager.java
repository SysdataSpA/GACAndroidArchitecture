package sysdata.it.androidarchitecture;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Andrea Guitto on 18/01/2018.
 */

public final class DependencyManager {

    private static final ConfigurationDependencyLoader dependencyLoader = new ConfigurationDependencyLoader();

    /**
     * This method will load kits
     */
    public static void loadElements() {
        dependencyLoader.loadAllElements();
    }

    /**
     * This method will registerProvider kits
     * @param kitConfiguration
     */
    public static void addModule(BaseModuleConfiguration kitConfiguration) {
        dependencyLoader.addModule(kitConfiguration);
    }

    /**
     * This method will registerProvider kits
     * @param kitConfiguration
     */
    public static void removeModule(BaseModuleConfiguration kitConfiguration) {
        dependencyLoader.removeModule(kitConfiguration);
    }


    /**
     *
     * @param clazz
     * @param customClass
     * @return
     */
    public static <T>T provideObject(Class<T> clazz, String customClass) {
        return provideObject(clazz, customClass, null, null, null);
    }

    /**
     *
     * @param clazz
     * @param customClass
     * @param bundle
     * @return
     */
    public static <T>T provideObject(Class<T> clazz, String customClass, Bundle bundle) {
        return provideObject(clazz, customClass, bundle, null, null);
    }

    /**
     *
     * @param clazz
     * @param customClass
     * @param bundle
     * @param group
     * @return
     */
    public static <T>T provideObject(Class<T> clazz, String customClass, Bundle bundle, String group) {
        return provideObject(clazz, customClass, bundle, group, null);
    }

    /**
     *
     * @param clazz
     * @param customClass
     * @param bundle
     * @param group
     * @param context
     * @return
     */
    public static <T>T provideObject(Class<T> clazz, String customClass, Bundle bundle, String group, Context context) {
        Object returnValue = dependencyLoader.provideObject(customClass, bundle, group, context);

        if(returnValue != null && clazz.isAssignableFrom(returnValue.getClass())){
            return (T) returnValue;
        }
        return null;
    }

    /**
     *
     */
    private static final class ComparableClassProviderWrapper implements Comparable<ComparableClassProviderWrapper>{
        private BaseClassProvider provider;
        int priority;
        private String componentGroup;
        public ComparableClassProviderWrapper(BaseClassProvider provider, int priority, String componentGroup) {
            this.provider = provider;
            this.priority = priority;
            this.componentGroup = componentGroup;
        }

        @Override
        public int compareTo(@NonNull ComparableClassProviderWrapper toObject) {
            if(toObject == null) return 0;
            return priority > toObject.priority ? 1 : priority < toObject.priority ? -1 : 0;
        }
    }

    /**
     *
     */
    public static final class ConfigurationDependencyLoader {

        private List<BaseModuleConfiguration> configurations;
        private Map<String, PriorityQueue<ComparableClassProviderWrapper>> objectProvidersMap = new ConcurrentHashMap();

        /**
         *
         * @param kitConfiguration
         */
        public void addModule(BaseModuleConfiguration kitConfiguration) {
            if(configurations == null){
                configurations = new ArrayList<>();
            }

            configurations.add(kitConfiguration);
        }

        /**
         *
         * @param kitConfiguration
         */
        public void removeModule(BaseModuleConfiguration kitConfiguration) {
            if(configurations != null) {
                kitConfiguration.onClassProviderDiscard(dependencyLoader);
                configurations.remove(kitConfiguration);
            }
        }

        /**
         *
         */
        public void loadAllElements() {
            if(configurations != null){
                for (int i = 0; i < configurations.size(); i++) {
                    BaseModuleConfiguration baseModuleConfiguration = configurations.get(i);
                    baseModuleConfiguration.onClassProviderSetup(this);
                }
            }
        }

        /**
         *
         */
        public void discardAllElements() {
            if(configurations != null){
                for (int i = 0; i < configurations.size(); i++) {
                    BaseModuleConfiguration baseModuleConfiguration = configurations.get(i);
                    baseModuleConfiguration.onClassProviderDiscard(this);
                }
            }
        }

        /**
         *
         * @param key
         * @param provider
         */
        public void registerProvider(String key, BaseClassProvider provider, int priority, String componentGroup) {

            PriorityQueue<ComparableClassProviderWrapper> baseClassProviders = objectProvidersMap.get(key);

            if(baseClassProviders == null) {
                baseClassProviders = new PriorityQueue<>();
            }

            boolean findOne = findProvider(key, provider);
            if(findOne) {
                unregisterProvider(key, provider);
            }

            baseClassProviders.add(new ComparableClassProviderWrapper(provider, priority, componentGroup));

            // setup new list
            objectProvidersMap.put(key, baseClassProviders);
        }

        /**
         *
         * @param key
         * @return
         */
        private boolean findProvider(String key, BaseClassProvider provider) {
            PriorityQueue<ComparableClassProviderWrapper> baseClassProviders = objectProvidersMap.get(key);

            if(baseClassProviders == null) {
                baseClassProviders = new PriorityQueue<>();
            }

            boolean find = false;
            for (ComparableClassProviderWrapper baseClassProvider : baseClassProviders) {
                if(baseClassProvider.provider.equals(provider)){
                    find = true;
                    break;
                }
            }

            return find;
        }

        /**
         *
         * @param key
         * @param provider
         */
        public void unregisterProvider(String key, BaseClassProvider provider) {
            if(objectProvidersMap != null) {
                PriorityQueue<ComparableClassProviderWrapper> baseClassProviders = objectProvidersMap.get(key);
                if (baseClassProviders != null && baseClassProviders.size() > 0) {
                    baseClassProviders.remove(provider);
                }
            }
        }

        /**
         *
         * @param key
         * @return
         */
        public Object provideObject(String key) {
            return provideObject(key, null);
        }

        /**
         *
         * @param key
         * @param bundle
         * @return
         */
        public Object provideObject(String key, Bundle bundle) {
            return provideObject(key, bundle, "default");
        }

        /**
         *
         * @param key
         * @param bundle
         * @return
         */
        public Object provideObject(String key, Bundle bundle, String group) {
            return provideObject(key, bundle, group, null);
        }

        /**
         *
         * @param key
         * @param bundle
         * @return
         */
        public Object provideObject(String key, Bundle bundle, String group, Context context) {
            String groupNormalized = group;

            if(TextUtils.isEmpty(group)){
                groupNormalized = "default";
            }

            if(objectProvidersMap != null) {
                PriorityQueue<ComparableClassProviderWrapper> baseClassProviders = objectProvidersMap.get(key);
                if (baseClassProviders != null && baseClassProviders.size() > 0) {
                    for (ComparableClassProviderWrapper baseClassProvider : baseClassProviders) {
                        if(baseClassProvider.componentGroup.equals(groupNormalized)) {
                            return baseClassProvider.provider.provideObject(bundle, context);
                        }
                    }
                }
            }
            return null;
        }
    }
}
