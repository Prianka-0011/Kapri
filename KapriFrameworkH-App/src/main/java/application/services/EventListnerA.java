package application.services;

import framework.annotations.EventListener;
import framework.annotations.Service;

@Service
public class EventListnerA {
    @EventListener
    public void listen(AccountChangeEvent accountChangeEvent){
        System.out.println("AccountChangeListenerA: "+accountChangeEvent);
    }
}
