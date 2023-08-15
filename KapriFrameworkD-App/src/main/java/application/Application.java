package application;

import application.services.AccountService;
import application.services.ServiceOne;
import framework.context.Context;

public class Application {
    public static void main(String[] args) {
        Context context = new Context();
        context.start(Application.class);
        ServiceOne serviceOne = (ServiceOne) context.getServiceBeanByClass(ServiceOne.class);
        AccountService accountService = (AccountService) context.getServiceBeanByClass(AccountService.class);
        if (serviceOne != null) {
            serviceOne.call();
        }
        if (accountService != null) {
            accountService.deposit();
        }
    }
}