package org.example;

import FrameWork.Autowired;
import FrameWork.Service;

@Service()
public class MyServiceOne {
    private MyServiceTwo myServiceTwo1;
    @Autowired
    private MyServiceTwo myServiceTwo;
    public void call() {
        if (this.myServiceTwo!=null) {
            this.myServiceTwo.print();
        }

    }
}
