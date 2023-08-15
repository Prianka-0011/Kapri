package application.services;

import framework.annotations.EventListener;
import framework.annotations.Service;

import java.text.MessageFormat;

@Service
public class EventListnerA {
    @EventListener
    public void listen(AccountChangeEvent accountChangeEvent){
        System.out.println(MessageFormat.format("BalanceChangeListenerA:  {0}", accountChangeEvent)); }
}
