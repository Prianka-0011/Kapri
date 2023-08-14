package org.example;

import FrameWork.FWContext;
import FrameWork.Service;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Application {
    public static void main(String[] args) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
       FWContext context = new FWContext();
       context.start(Application.class);
        MyServiceOne myServiceOne =(MyServiceOne) context.getServiceBeanOfTypeByClass(MyServiceOne.class);
        if (myServiceOne!=null) {
            myServiceOne.call();
        }
    }
}