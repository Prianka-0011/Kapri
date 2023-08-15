package application.services;

import framework.annotations.Service;

@Service(name = "two")
public class ServiceTwo {
    public void print() {
        System.out.println("Service is working fine");
    }
}
