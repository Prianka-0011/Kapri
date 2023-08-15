package application;

import application.interfaces.DbService;
import application.services.AccountService;
import framework.SpringFramework;
import framework.annotations.Autowired;
import framework.annotations.Profile;

public class Application implements Runnable {

    @Autowired
    AccountService accountService;

    @Autowired
    DbService dbService;

    public static void main(String[] args) {
        SpringFramework.run(Application.class);
    }

    @Override
    public void run() {
        accountService.deposit();
        dbService.connect();
    }
}