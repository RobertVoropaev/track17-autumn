package ru.track.prefork;

import java.io.IOException;

public class EchoServer {
    private Server server;
    private String stopWord = "exit";

    EchoServer(int port) throws IOException{
        server = new Server(port);

        server.accept();

        String msg = server.readString();
        while(!stopWord.equals(msg)){
            server.writeString(msg);
            msg = server.readString();
        }

        server.close();
    }
}
