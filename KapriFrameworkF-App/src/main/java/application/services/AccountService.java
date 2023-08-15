package application.services;

import framework.annotations.Scheduled;
import framework.annotations.Service;
import framework.annotations.Value;

@Service
public class AccountService {
    @Value(name = "client")
    String client;

    @Scheduled(fixedRate = 500000)
    public void deposit() {
        System.out.println("$50 has been deposited to " + client);
    }
}
