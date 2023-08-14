package FrameWork;

import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public class FWContext {
    private static Map<String, Object> serviceObjectMap = new HashMap<>();

    public void start(Class<?> clazz) {
        try {
            Reflections reflections = new Reflections(clazz.getPackageName());
            Set<Class<?>> customServices = reflections.getTypesAnnotatedWith(Service.class);
            for (Class<?> serviceClass : customServices) {
                serviceObjectMap.put(serviceClass.getName(), (Object) serviceClass.getDeclaredConstructor().newInstance());
            }
            performDI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performDI() {
        try {
            for (Object service: serviceObjectMap.values()) {
                performFieldInjection(service);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void performFieldInjection(Object serviceObject) throws IllegalAccessException {
        try {
            for (Field field: serviceObject.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Class<?> theFieldType = field.getType();
                    Object instance = getServiceBeanOfType(theFieldType.getName());
                    field.setAccessible(true);
                    field.set(serviceObject, instance);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public Object getServiceBeanOfType(String  clazzName) {
        Object service = serviceObjectMap.get(clazzName);
        System.out.println(service.getClass().getName());
        return service;
    }

    public Object getServiceBeanOfTypeByClass(Class interfaceClass) {
        Object service = null;
        try {
            for (Object theClass : serviceObjectMap.values()) {
                if (theClass.getClass().getName().equals(interfaceClass.getName())) {
                    return theClass;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return service;
    }

}
