package framework.context;

import com.sun.security.auth.login.ConfigFile;
import framework.annotations.Autowired;
import framework.annotations.Service;
import framework.annotations.Value;
import framework.util.ConfigFileReader;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
            for (Object service: serviceObjects.values()) {
                Object returnConstructor = performConstructorInjection(service);
                if (returnConstructor != null)
                {
                    performFieldInjection(returnConstructor);
                    performSetterInjection(returnConstructor);
                    performValueInjection(returnConstructor);
                } else {
                    performFieldInjection(service);
                    performSetterInjection(service);
                    performValueInjection(service);
                }
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

    public void performSetterInjection(Object serviceobject) {
        try {
            Method[] methods = serviceobject.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Autowired.class)) {
                    Class<?>[] paramTypes = method.getParameterTypes();
                    Object [] instanceOfParams = new Object[paramTypes.length];
                    for (int i = 0 ; i<paramTypes.length; i++) {
                        instanceOfParams[i] = getServiceBeanByType(paramTypes[i].getName());
                    }
                    method.invoke(serviceobject,instanceOfParams);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object performConstructorInjection(Object object) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        try {
            Constructor[] constructorList = object.getClass().getDeclaredConstructors();
            for (Constructor constructor: constructorList) {
                if (constructor.isAnnotationPresent(Autowired.class)) {
                    Class<?>[] parameterTypes = constructor.getParameterTypes();

                    Object[] parameterInstances = new Object[parameterTypes.length];

                    for (int i = 0; i < parameterTypes.length; i++) {
                        parameterInstances[i] = getServiceBeanByType(parameterTypes[i].getName());
                    }
                    Object serviceClassInstance = (Object) constructor.newInstance(parameterInstances);
                    serviceObjects.put(serviceClassInstance.getClass().getName(), serviceClassInstance);
                    return serviceClassInstance;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
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
