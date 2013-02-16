package com.echophase.slplugins;


import org.springsource.loaded.ReloadEventProcessorPlugin;

import javax.sound.midi.SysexMessage;
import java.lang.reflect.Field;
import java.util.Map;

public class ELPlugin implements ReloadEventProcessorPlugin {

    @Override
    public boolean shouldRerunStaticInitializer(String typename, Class<?> clazz, String encodedTimestamp) {
        return false;
    }

    @Override
    public void reloadEvent(String typename, Class<?> clazz, String encodedTimestamp) {
        Class<?> beanELResolver;
        try {
            beanELResolver = clazz.getClassLoader().loadClass("javax.el.BeanELResolver");
        } catch (ClassNotFoundException e) {
            return;
        }

        try {
            Field propertiesField = beanELResolver.getDeclaredField("properties");
            propertiesField.setAccessible(true);
            Map<?, ?> cache = (Map<?, ?>) propertiesField.get(null);
            cache.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
