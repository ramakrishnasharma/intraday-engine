package com.alertengine.notification;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class TelegramNotifier {

    private String token;
    private String chatId;

    public TelegramNotifier(String token, String chatId){
        this.token = token;
        this.chatId = chatId;
    }

    public void send(String message){

        try{
            System.out.println(message);

            String urlString =
                    "https://api.telegram.org/bot" + token +
                    "/sendMessage?chat_id=" + chatId +
                    "&text=" + URLEncoder.encode(message, "UTF-8");

            URL url = new URL(urlString);

            HttpURLConnection conn =
                    (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            if(responseCode != 200){

                System.out.println("Telegram send failed: " + responseCode);
            }

        }catch(Exception e){

            e.printStackTrace();
        }
    }
}