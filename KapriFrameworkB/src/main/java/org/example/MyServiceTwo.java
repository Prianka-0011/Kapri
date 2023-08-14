package org.example;

import FrameWork.Service;

@Service(name = "two")
public class MyServiceTwo {
    public void print() {
        System.out.println("Service is working fine");
    }
}
