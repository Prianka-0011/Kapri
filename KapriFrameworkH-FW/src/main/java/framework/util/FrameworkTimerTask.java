package framework.util;

import java.lang.reflect.Method;
import java.util.TimerTask;

public class FrameworkTimerTask extends TimerTask {
    private Object serviceObject;
    private Method methodObject;

    public FrameworkTimerTask(Object serviceObject, Method methodObject) {
        this.serviceObject = serviceObject;
        this.methodObject = methodObject;
    }

    @Override
    public void run() {
        try {
            methodObject.invoke(serviceObject);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
