package com.echophase.slplugins;

import org.springsource.loaded.LoadtimeInstrumentationPlugin;
import org.springsource.loaded.ReloadEventProcessorPlugin;

import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public abstract class StaticCacheClearingPlugin implements ReloadEventProcessorPlugin, LoadtimeInstrumentationPlugin {

    private final Map<ClassLoader, Boolean> loaders =
            Collections.synchronizedMap(new WeakHashMap<ClassLoader, Boolean>());
    private final String cacheClassName;
    private final String slashedCacheClassName;

    protected StaticCacheClearingPlugin(String cacheClassName) {
        this.cacheClassName = cacheClassName;
        this.slashedCacheClassName = this.cacheClassName.replace('.', '/');
    }

    @Override
    public boolean shouldRerunStaticInitializer(String typename, Class<?> clazz, String encodedTimestamp) {
        return false;
    }

    @Override
    public void reloadEvent(String typename, Class<?> clazz, String encodedTimestamp) {
        if (loaders.containsKey(clazz.getClassLoader())) {
            try {
                Class<?> cacheClass = clazz.getClassLoader().loadClass(cacheClassName);
                clearStaticCache(cacheClass, clazz);
            } catch (Exception e) {
                System.err.println("Failed to clear cache: " + e.getMessage());
                e.printStackTrace(System.err);
            }
        }
    }

    protected abstract void clearStaticCache(Class<?> cacheClass, Class<?> classReloaded) throws Exception;

    @Override
    public boolean accept(String slashedTypeName, ClassLoader classLoader, ProtectionDomain protectionDomain, byte[] bytes) {
        if (slashedTypeName.equals(slashedCacheClassName)) {
            loaders.put(classLoader, Boolean.TRUE);
        }
        return false;
    }

    @Override
    public byte[] modify(String slashedClassName, ClassLoader classLoader, byte[] bytes) {
        return bytes;
    }
}
