package application.services;

import framework.annotations.Autowired;
import framework.annotations.Service;

@Service
public class ServiceOne {
    @Autowired
    private ServiceTwo serviceTwo;
    public void call() {
        serviceTwo.print();
    }
}
