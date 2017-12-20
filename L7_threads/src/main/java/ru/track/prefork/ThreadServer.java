package ru.track.prefork;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class ThreadServer {
    private ServerSocket serverSocket;

    private AtomicInteger counter;

    private Map<Integer, Worker> workers;

    private Logger log = Logger.getLogger(ThreadSocket.class.getName());

    public ThreadServer(int port) throws Exception{
        serverSocket = new ServerSocket(port);

        log.info("Server is created on port: " + port);

        counter = new AtomicInteger(0);

        workers = new ConcurrentHashMap<Integer, Worker>();

        Thread thread = new Thread(new ThreadServerReader());
        thread.start();

        Scanner scanner = new Scanner(System.in);

        String command = scanner.nextLine();
        while(!command.equals("stop")){
            switch (command){
                case "list": list();
                    break;
                case "drop":
                    int id = Integer.parseInt(scanner.nextLine());
                    closeID(id);
                    break;
                default:
                    System.out.println("Uncorrect command!");
            }
            command = scanner.nextLine();
        }

        serverSocket.close();
        workers.clear();
    }

    private void list(){
        for(Map.Entry<Integer, Worker> entry: workers.entrySet()){
            System.out.println(getClientName(entry.getKey(),
                    entry.getValue().getSocket().getInetAddress().getHostAddress(),
                    entry.getValue().getSocket().getPort()));
        }
    }

    private class ThreadServerReader implements Runnable{
        @Override
        public void run() {
            while(true){
                try {
                    Socket socket = serverSocket.accept();
                    Integer id = counter.getAndIncrement();

                    Worker worker = new Worker(socket);
                    workers.put(id, worker);

                    String clientName = getClientName(id, socket.getInetAddress().getHostAddress(), socket.getPort());
                    log.info("Connect " + clientName);

                    Thread thread = new Thread(new ThreadSocket(id));
                    thread.setName(clientName);
                    thread.start();
                } catch (Exception ex){}
            }
        }
    }

    private String getClientName(int id, String host, int port){
        return String.format("Client[%s]@%s:%s", id, host, port);
    }

    private class ThreadSocket implements Runnable{
        private int id;

        public ThreadSocket(int id){
            this.id = id;
        }

        @Override
        public void run() {
            try {
                DataInputStream din = workers.get(id).getDin();

                String msg = din.readUTF();

                log.info("Read <" + msg + "> from " + Thread.currentThread().getName());

                while(!msg.equals("exit")){
                    for(Worker worker: workers.values()){
                        DataOutputStream dout = worker.getDout();
                        log.info("Write " + msg + " in "
                                + getClientName(id,
                                worker.getSocket().getInetAddress().getHostAddress(),
                                worker.getSocket().getPort()));
                        dout.writeUTF(Thread.currentThread().getName() + ">" + msg);
                    }
                    msg = din.readUTF();
                }

                closeID(id);
            } catch (Exception ex){}
        }
    }

    private void closeID(int id) throws Exception{
        workers.get(id).close();
        workers.remove(id);
        log.info(Thread.currentThread().getName() + " is removed");
    }

    private class Worker{
        private Socket socket;
        private InputStream in;
        private OutputStream out;
        private DataInputStream din;
        private DataOutputStream dout;

        public Worker(Socket socket) throws IOException{
            this.socket = socket;
            in = socket.getInputStream();
            out = socket.getOutputStream();
            din = new DataInputStream(in);
            dout = new DataOutputStream(out);
        }

        public Socket getSocket() {
            return socket;
        }

        public InputStream getIn() {
            return in;
        }

        public OutputStream getOut() {
            return out;
        }

        public DataInputStream getDin() {
            return din;
        }

        public DataOutputStream getDout() {
            return dout;
        }

        public void close() throws Exception{
            socket.close();
            in.close();
            out.close();
            din.close();
            dout.close();
        }
    }
}
