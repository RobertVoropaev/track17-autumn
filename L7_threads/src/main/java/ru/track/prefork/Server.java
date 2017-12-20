package ru.track.prefork;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    private Socket socket;

    private InputStream in;
    private OutputStream out;

    private DataInputStream din;
    private DataOutputStream dout;

    public Server(ServerSocket serverSocket) throws IOException{
        this.serverSocket = serverSocket;
    }

    public Server(int port) throws IOException{
        this(new ServerSocket(port));
    }

    public void accept() throws IOException{
        socket = serverSocket.accept();
        setStreams();
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
        closeConnection();
        serverSocket.close();
    }

    public void closeConnection() throws IOException{
        din.close();
        dout.close();
        in.close();
        out.close();
        socket.close();
    }
}
