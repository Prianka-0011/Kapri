package org.example;

import FrameWork.FWContext;
import FrameWork.Service;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class Application {
    public static void main(String[] args) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        FWContext context = new FWContext();
        context.start(Application.class);
        MyServiceOne myServiceOne = (MyServiceOne) context.getServiceBeanOfTypeByClass(MyServiceOne.class);
        if (myServiceOne != null) {
            myServiceOne.call();
        }
    }
}