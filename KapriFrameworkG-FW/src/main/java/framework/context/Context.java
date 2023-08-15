package framework.context;

import framework.annotations.*;
import framework.annotations.EventListener;
import framework.util.ConfigFileReader;
import framework.util.FrameworkTimerTask;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Context {
    private static Map<String, Object> serviceObjects = new HashMap<>();
    EventContext eventContext = new EventContext();
    public void start(Class<?> clazz) {
        try {
            Reflections reflections = new Reflections(clazz.getPackageName());
            Set<Class<?>> customServices = reflections.getTypesAnnotatedWith(Service.class);
            for (Class<?> service: customServices) {
                serviceObjects.put(service.getName(), service.getDeclaredConstructor().newInstance());
            }
            serviceObjects.put("publisher", new EventPublisher(eventContext));
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
                    performListenerMethods(returnConstructor);
                    performScheduling(returnConstructor);
                } else {
                    performFieldInjection(service);
                    performSetterInjection(service);
                    performValueInjection(service);
                    performListenerMethods(service);
                    performScheduling(service);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void performListenerMethods(Object serviceObject)
    {
        Method[] methods = serviceObject.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(EventListener.class)) {
                //found event listener method
                //EventListenerMethod eventListenerMethod = new EventListenerMethod(serviceObject, method);
                Class<?>[] parameters = method.getParameterTypes();
                Class parameterClass = parameters[0];
                eventContext.addEventListeners(parameterClass.getName(), serviceObject, method);
            }
        }
    }
    private void performScheduling(Object serviceObject) throws IllegalAccessException {
        try {
            Method [] methods = serviceObject.getClass().getDeclaredMethods();
            for (Method method: methods) {
                if (method.isAnnotationPresent(Scheduled.class)) {
                    Annotation methodAnnotation = method.getAnnotation(Scheduled.class);
                    int rate = ((Scheduled) methodAnnotation).fixedRate();
                    String corn = ((Scheduled)methodAnnotation).corn();
                    Timer timer =new Timer();
                    if (rate>0) {
                        timer.scheduleAtFixedRate(new FrameworkTimerTask(serviceObject,method),0,rate);
                    }
                    if (corn!="") {
                     int cornRate = generateCornRate(corn);
                        timer.scheduleAtFixedRate(new FrameworkTimerTask(serviceObject,method),0,cornRate);

                    }
                }
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private int generateCornRate(String corn) {
        String [] splitCorn = corn.split(" ");
        int minute = Integer.parseInt(splitCorn[0]);
        int second = Integer.parseInt(splitCorn[1]);
        int milliSecond = ((minute*60)+second)*1000;
        return milliSecond;
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
