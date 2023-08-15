package application.services;

import framework.annotations.*;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class AccountService {
    @Autowired
    EventPublisher eventPublisher;
    @Value(name = "client")
    String client;


    public void deposit() {
        System.out.println("$50 has been deposited to " + client);
        try {
            eventPublisher.publish(new AccountChangeEvent());
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        //this.welcome();
    }


    @Scheduled(fixedRate = 5000)
    public void welcome() {
        Date date = Calendar.getInstance().getTime();
        DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.DEFAULT);
        String currenttime = timeFormatter.format(date);
        System.out.println("This task runs at " + currenttime);
    }

    @Scheduled(corn = "8 0")
    public void welcome2() {
        Date date = Calendar.getInstance().getTime();
        DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.DEFAULT);
        String currenttime = timeFormatter.format(date);
        System.out.println("This cron task runs at " + currenttime);
    }
}
