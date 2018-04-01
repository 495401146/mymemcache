package server.main;

import config.Config;
import connection.model.Connection;
import connection.model.ConnectionState;
import jobthread.FlushThread;
import sun.nio.ch.ThreadPool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.*;

public class Server {
    private static BlockingQueue<Connection> connections = new LinkedBlockingQueue<Connection>();
    public static ExecutorService executor = Executors.newFixedThreadPool(Config.WRITE_THREAD_NUM);
    public static void main(String[] args)
    {
        try {
            Thread dispatcherThread = new Thread(new Dispatcher(connections));
            dispatcherThread.start();
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(new FlushThread());
            ServerSocket serverSocket = new ServerSocket(11000);

            while(true)
            {
                Socket socket = serverSocket.accept();
                if(connections.size()>=Config.MAX_CONNECTION_NUM)
                {
                    continue;
                }
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
            System.out.println("一个连接被放入队列");
            connections.put(connection);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
