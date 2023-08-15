package application.services;

import application.interfaces.DbService;
import framework.annotations.Profile;
import framework.annotations.Service;

@Service
@Profile("test")
public class TestDbService implements DbService {
    @Override
    public void connect() {
        System.out.println("Connected to Production");
    }
}
