package ru.track.prefork;


import java.io.*;

import java.util.Scanner;

public class ThreadClient {
    private Client client;

    private Scanner scanner;

    public ThreadClient(String host, int port) throws IOException{
        client = new Client(host, port);
        scanner = new Scanner(System.in);

        Thread thread = new Thread(new ClientReader());
        thread.start();


        String msg = scanner.nextLine();
        while(!msg.equals("exit")){
            client.writeString(msg);
            msg = scanner.nextLine();
        }
        client.writeString(msg);
        client.close();
    }

    private class ClientReader implements Runnable{
        @Override
        public void run() {
            try {
                while (true) {
                    System.out.println(client.readString());
                }
            } catch (Exception ex) {}
        }
    }
}
