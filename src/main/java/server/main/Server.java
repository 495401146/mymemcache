package server.main;

import connection.model.Connection;
import connection.model.ConnectionState;
import sun.nio.ch.ThreadPool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.*;

public class Server {
    private static BlockingQueue<Connection> connections = new LinkedBlockingQueue<Connection>();
    public static ExecutorService executor = Executors.newFixedThreadPool(10);
    public static void main(String[] args)
    {
        try {
            for(int i = 0;i<10;i++)
            {
                executor.execute(new ConnectionHandler(connections));
            }

            ServerSocket serverSocket = new ServerSocket(11000);

            while(true)
            {
                Socket socket = serverSocket.accept();
                //加入连接队列
                addConnection(socket);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addConnection(Socket socket) {
        Connection connection = new Connection(socket, ConnectionState.CONN_WAITING,connections);
        try {
            connections.put(connection);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
