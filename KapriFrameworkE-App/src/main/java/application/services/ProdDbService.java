package application.services;

import application.interfaces.DbService;
import framework.annotations.Profile;
import framework.annotations.Service;

@Service
@Profile("production")
public class ProdDbService implements DbService {

    @Override
    public void connect() {
        System.out.println("Connected to Production");
    }
}
