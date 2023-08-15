package application;

import application.services.AccountService;
import application.services.ServiceOne;
import framework.annotations.Autowired;
import framework.annotations.Service;
import framework.context.Context;

public class Application implements Runnable {

//    @Autowired
//    AccountService accountService;

//    public static void main(String[] args) {
//        Context context = new Context();
//        context.start(Application.class);
//        ServiceOne serviceOne = (ServiceOne) context.getServiceBeanByClass(ServiceOne.class);
//        AccountService accountService = (AccountService) context.getServiceBeanByClass(AccountService.class);
//        if (serviceOne != null) {
//            serviceOne.call();
//        }
//        if (accountService != null) {
//            accountService.deposit();
//        }
//    }

    public static void main(String[] args) {
        SpringFramework.run(Application.class);
    }

    @Override
    public void run() {
        accountService.deposit();
    }
}