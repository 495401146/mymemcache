package server;

import config.Config;
import connection.model.Connection;
import connection.model.ConnectionState;
import jobthread.FlushThread;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
//主线程，一切初始化操作在这里进行
public class Server {
    private static final Logger logger = Logger.getLogger(Server.class);
    //管理所有connection的阻塞队列，主要保证线程分发的安全性
    private static BlockingQueue<Connection> connections = new LinkedBlockingQueue<Connection>();
    public static void main(String[] args)
    {
        try {
            //开启dispatcher线程，分发事件，用singleThreadPool可靠性高
            ExecutorService dispatcherService = Executors.newSingleThreadExecutor();
            dispatcherService.execute(new Dispatcher(connections));
            //开启后台缓存定时刷新线程
            ExecutorService flushService = Executors.newSingleThreadExecutor();
            flushService.execute(new FlushThread());

            ServerSocket serverSocket = new ServerSocket(Config.port);

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
            logger.error(e.getMessage());
            logger.error(Thread.currentThread().getName()+":socket network is exception");
        }
    }
    //根据新进入的socket创建新的connection并放入阻塞队列
    private static void addConnection(Socket socket) {
        Connection connection = new Connection(socket, ConnectionState.CONN_WAITING,connections);
        try {
            connections.put(connection);
            logger.info(Thread.currentThread().getName()+":the connection is established");
        } catch (InterruptedException e) {
            logger.error(Thread.currentThread().getName()+":the connection is not put");
        }
    }
}
