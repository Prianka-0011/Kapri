package application;

import application.services.AccountService;
import framework.SpringFramework;
import framework.annotations.Autowired;

public class Application implements Runnable {

    @Autowired
    AccountService accountService;
    
    public static void main(String[] args) {
        SpringFramework.run(Application.class);
    }

    @Override
    public void run() {
        accountService.deposit();
    }
}