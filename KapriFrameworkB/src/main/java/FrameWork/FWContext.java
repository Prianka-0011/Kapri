package FrameWork;

import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class FWContext {
    private static Map<String, Object> serviceObjectMap = new HashMap<>();
    public void start(Class<?> clazz) {
        try {
            Reflections reflections = new Reflections(clazz.getPackageName());
            Set<Class<?>> customServices = reflections.getTypesAnnotatedWith(Service.class);
            for (Class<?> serviceClass : customServices) {
                serviceObjectMap.put(serviceClass.getName(), (Object)serviceClass.getDeclaredConstructor().newInstance());
            }
            performDI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performDI() {
        try {
            for (Object service: serviceObjectMap.values()) {
             Object returnConstructor = performConstructorInjection(service);
             if (returnConstructor != null)
             {
                 performFieldInjection(returnConstructor);
                 performSetterInjection(returnConstructor);
             } else {
                 performFieldInjection(service);
                 performSetterInjection(service);
             }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void performSetterInjection(Object serviceobject) {
        try {
            Method [] methods = serviceobject.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Autowired.class)) {
                    Class<?>[] paramTypes = method.getParameterTypes();
                    Object [] instanceOfParams = new Object[paramTypes.length];
                    for (int i = 0 ; i<paramTypes.length; i++) {
                        instanceOfParams[i] = getServiceBeanOfType(paramTypes[i].getName());
                    }
                    method.invoke(serviceobject,instanceOfParams);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void performFieldInjection(Object serviceObject) throws IllegalAccessException {
        try {
            for (Field field: serviceObject.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    if (field.isAnnotationPresent(Qualifier.class))
                    {
                        Annotation annotation = field.getAnnotation(Qualifier.class);
                        String className = ((Qualifier)annotation).name();
                        Object instance = getServiceBeanOfType(className);
                        field.setAccessible(true);
                        field.set(serviceObject,instance);
                    }

                }
            }

        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public Object performConstructorInjection(Object object) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        try {
            Constructor [] constructorList = object.getClass().getDeclaredConstructors();
            for (Constructor constructor: constructorList) {
                if (constructor.isAnnotationPresent(Autowired.class)) {
                    Class<?>[] parameterTypes = constructor.getParameterTypes();

                    Object[] parameterInstances = new Object[parameterTypes.length];

                    for (int i = 0; i < parameterTypes.length; i++) {
                        parameterInstances[i] = getServiceBeanOfType(parameterTypes[i].getName());
                    }
                    Object serviceClassInstance = (Object) constructor.newInstance(parameterInstances);
                    serviceObjectMap.put(serviceClassInstance.getClass().getName(), serviceClassInstance);
                    return serviceClassInstance;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public Object getServiceBeanOfType(String  clazzName) {
        Object service = serviceObjectMap.get(clazzName);
        return service;
    }
    public Object getServiceBeanOfTypeByClass(Class interfaceClass) {
        Object service = null;
        try {
            for (Object theClass : serviceObjectMap.values()) {
                //check class without interface
                if (theClass.getClass().getName().equals(interfaceClass.getName()))
                    return theClass;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return service;
    }
}
