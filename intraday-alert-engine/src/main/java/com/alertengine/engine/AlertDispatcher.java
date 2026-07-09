package com.alertengine.engine;

import com.alertengine.notification.AsyncTelegramNotifier;

import java.util.concurrent.*;

public class AlertDispatcher {

    private AsyncTelegramNotifier notifier;

    public AlertDispatcher(AsyncTelegramNotifier notifier) {
        this.notifier = notifier;
    }

    private BlockingQueue<String> alerts =
            new LinkedBlockingQueue<>();

    public void push(String msg){

        alerts.offer(msg);
    }

    public void startWorker(){

        Thread t = new Thread(() -> {

            while(!Thread.currentThread().isInterrupted()){

                try{

                    String msg = alerts.take();

                    System.out.println(msg);

                    notifier.send(msg);

                }catch(Exception e){

                    e.printStackTrace();
                }
            }

        });

        t.setName("AlertDispatcher");
        t.setDaemon(true);
        t.start();
    }
}