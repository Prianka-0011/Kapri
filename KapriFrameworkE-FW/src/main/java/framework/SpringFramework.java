package framework;

import framework.annotations.Autowired;
import framework.context.Context;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;

public class SpringFramework {
    public static void run(Class<?> applicationClass) {
        Context context = new Context();
//        context.setActiveProfile(activeProfile);
        context.start(applicationClass);
        try {
            Object applicationObject = (Object) applicationClass.getDeclaredConstructor().newInstance();
            for (Field field : applicationObject.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Class<?> fieldType = field.getType();
                    Object instance = context.getServiceBeanByType(fieldType);
                    field.setAccessible(true);
                    field.set(applicationObject, instance);
                }
            }
            if (applicationObject instanceof Runnable) {
                ((Runnable) applicationObject).run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}