package org.example;

import FrameWork.Autowired;
import FrameWork.Qualifier;
import FrameWork.Service;

@Service()
public class MyServiceOne {
    @Autowired
    private MyServiceTwo myServiceTwo1;
    @Autowired
    @Qualifier(name = "org.example.MyServiceTwo")
    private MyServiceTwo myServiceTwo;
    public void call() {
        if (this.myServiceTwo!=null) {
            this.myServiceTwo.print();
        }

    }
}
