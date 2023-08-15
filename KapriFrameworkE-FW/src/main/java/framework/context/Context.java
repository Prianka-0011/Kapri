package framework.context;

import framework.annotations.Autowired;
import framework.annotations.Profile;
import framework.annotations.Service;
import framework.annotations.Value;
import framework.util.ConfigFileReader;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Context {
    private static Map<String, Object> serviceObjects = new HashMap<>();
    private String activeProfile;

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
        Properties properties = ConfigFileReader.getConfigProperties();
        activeProfile = properties.getProperty("profile.active");
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
                    Object instance = getServiceBeanByType(fieldType);
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
                        instanceOfParams[i] = getServiceBeanByType(paramTypes[i]);
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
                        parameterInstances[i] = getServiceBeanByType(parameterTypes[i]);
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

    public Object getServiceBeanByType(Class<?> interfaceClass) {
        List<Object> objectList = new ArrayList<Object>();
        try {
            for (Object theServiceClass : serviceObjects.values()) {
                Class<?>[] interfaces = theServiceClass.getClass().getInterfaces();
                for (Class<?> theInterface : interfaces) {
                    if (theInterface.getName().contentEquals(interfaceClass.getName()))
                        objectList.add(theServiceClass);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (objectList.size() == 1) return objectList.get(0);
        if (objectList.size() > 1) {
            for (Object theObject : objectList) {
                String profilevalue = theObject.getClass().getAnnotation(Profile.class).value();
                if (profilevalue.contentEquals(activeProfile)) {
                    return theObject;
                }
            }
        }
        try {
            for (Object theClass : serviceObjects.values()) {
                if (theClass.getClass().getName().equals(interfaceClass.getName())) {
                    return theClass;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isTypeMatch(Object service, String typeName) {
        Class<?>[] interfaces = service.getClass().getInterfaces();
        for (Class<?> intf : interfaces) {
            if (intf.getName().equals(typeName)) {
                return true;
            }
        }
        return false;
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

    public void setActiveProfile(String activeProfile) {
        this.activeProfile = activeProfile;
    }
    private boolean isProfileActive(String profileValues) {
        if (activeProfile == null || activeProfile.isEmpty()) {
            return true;
        }
        return profileValues.equals(activeProfile);
    }
}
