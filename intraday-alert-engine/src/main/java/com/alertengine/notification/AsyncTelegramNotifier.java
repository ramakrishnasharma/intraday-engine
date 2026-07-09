package com.alertengine.notification;

import java.util.concurrent.*;

public class AsyncTelegramNotifier {

    private BlockingQueue<String> queue =
            new LinkedBlockingQueue<>();

    private TelegramNotifier notifier;

    public AsyncTelegramNotifier(TelegramNotifier notifier){

        this.notifier = notifier;

        startWorker();
    }

    public void send(String msg){

        queue.offer(msg);
    }

    private void startWorker(){

        Thread t = new Thread(() -> {

            while(true){

                try{

                    String msg = queue.take();

                    notifier.send(msg);

                }catch(Exception e){

                    e.printStackTrace();
                }
            }

        });

        t.setDaemon(true);
        t.start();
    }
}