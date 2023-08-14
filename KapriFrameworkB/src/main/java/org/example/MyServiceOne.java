package org.example;

import FrameWork.Autowired;
import FrameWork.Qualifier;
import FrameWork.Service;

@Service()
public class MyServiceOne {

    private MyServiceTwo myServiceTwo1;
    private MyService3 myService3;
    private ServiceClass4 serviceClass4;

    @Autowired
    public void setServiceClass4(ServiceClass4 serviceClass4) {
        this.serviceClass4 = serviceClass4;
    }

    @Autowired
    @Qualifier(name = "org.example.MyServiceTwo")
    private MyServiceTwo myServiceTwo;

    public MyServiceOne() {
    }

    @Autowired
    public MyServiceOne(MyService3 myService3) {
        this.myService3 = myService3;
    }

    public void call() {
        if (this.serviceClass4!=null) {
            this.serviceClass4.print();
        }

    }
}
