package application.services;

import framework.annotations.Autowired;
import framework.annotations.Qualifier;
import framework.annotations.Service;

@Service
public class ServiceOne {

    private ServiceThree serviceThree;
    private ServiceFour serviceFour;

    @Autowired
    public void setServiceClass4(ServiceFour serviceFour) {
        this.serviceFour = serviceFour;
    }

    @Autowired
    @Qualifier(name = "application.services.ServiceTwo")
    private ServiceTwo myServiceTwo;

    public ServiceOne() {
    }

    @Autowired
    public ServiceOne(ServiceThree serviceThree) {
        this.serviceThree = serviceThree;
    }

    public void call() {
        if (this.serviceThree != null) {
            this.serviceThree.print();
        }
    }
}
