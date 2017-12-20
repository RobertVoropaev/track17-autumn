package ru.track.prefork;

public class RunClientCopy {
    public static void main(String[] args) throws Exception{
        ThreadClient client = new ThreadClient("127.0.0.1", 9999);
    }
}