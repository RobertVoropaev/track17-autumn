package ru.track.prefork;

import java.io.*;

import java.net.InetAddress;
import java.net.Socket;

public class Client {
    private Socket socket;

    private InputStream in;
    private OutputStream out;

    private DataInputStream din;
    private DataOutputStream dout;

    public Client(Socket socket) throws IOException{
        this.socket = socket;
        setStreams();
    }

    public Client(String host, int port) throws IOException{
        this(new Socket(InetAddress.getByName(host), port));
    }

    private void setStreams() throws IOException{
        in = socket.getInputStream();
        out = socket.getOutputStream();
        din = new DataInputStream(in);
        dout = new DataOutputStream(out);
    }

    public String readString() throws IOException{
        return din.readUTF();
    }

    public void writeString(String msg) throws IOException{
        dout.writeUTF(msg);
    }

    public void close() throws IOException{
        din.close();
        dout.close();
        in.close();
        out.close();
        socket.close();
    }
}
