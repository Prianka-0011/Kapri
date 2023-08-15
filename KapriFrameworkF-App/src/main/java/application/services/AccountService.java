package application.services;

import framework.annotations.Service;
import framework.annotations.Value;

@Service
public class AccountService {
    @Value(name = "client")
    String client;

    public void deposit() {
        System.out.println("$50 has been deposited to " + client);
    }
}
