package ru.track.prefork;

public class RunServer {
    public static void main(String[] args) throws Exception{
        ThreadServer server = new ThreadServer(9999);
    }

}
