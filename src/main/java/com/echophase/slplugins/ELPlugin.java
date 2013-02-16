package com.echophase.slplugins;


import java.lang.reflect.Field;
import java.util.Map;

public class ELPlugin extends StaticCacheClearingPlugin {

    protected ELPlugin() {
        super("javax.el.BeanELResolver");
    }

    @Override
    protected void clearStaticCache(Class<?> cacheClass, Class<?> classReloaded) throws Exception {
        Field properties = cacheClass.getDeclaredField("properties");
        properties.setAccessible(true);
        Map<?, ?> cache = (Map<?, ?>) properties.get(null);
        cache.clear();
    }
}
