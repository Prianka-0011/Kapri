package framework.context;

import com.sun.security.auth.login.ConfigFile;
import framework.annotations.Autowired;
import framework.annotations.Service;
import framework.annotations.Value;
import framework.util.ConfigFileReader;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Context {
    private static Map<String, Object> serviceObjects = new HashMap<>();

    public void start(Class<?> clazz) {
        try {
            Reflections reflections = new Reflections(clazz.getPackageName());
            Set<Class<?>> customServices = reflections.getTypesAnnotatedWith(Service.class);
            for (Class<?> service: customServices) {
                serviceObjects.put(service.getName(), service.getDeclaredConstructor().newInstance());
            }
            performDependencyInjection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performDependencyInjection() {
        try {
            for (Object service : serviceObjects.values()) {
                performFieldInjection(service);
                performValueInjection(service);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performFieldInjection(Object service) throws IllegalAccessException {
        try {
            for (Field field: service.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Class<?> fieldType = field.getType();
                    Object instance = getServiceBeanByType(fieldType.getName());
                    field.setAccessible(true);
                    field.set(service, instance);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performValueInjection(Object service) throws IllegalAccessException {
        Properties properties = ConfigFileReader.getConfigProperties();
        try {
            for (Field field: service.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Value.class)) {
                    String fieldName = field.getAnnotation(Value.class).name();
                    field.setAccessible(true);
                    field.set(service, properties.getProperty(fieldName));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object getServiceBeanByType(String name) {
        return serviceObjects.get(name);
    }

    public Object getServiceBeanByClass(Class clazz) {
        try {
            if (serviceObjects.containsKey(clazz.getName())) {
                return serviceObjects.get(clazz.getName());
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
