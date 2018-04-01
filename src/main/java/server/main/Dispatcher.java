package server.main;

import connection.model.Connection;
import connection.model.ConnectionState;
import eventthread.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Dispatcher implements Runnable{
    private BlockingQueue<Connection> connections;
    private boolean stop = false;
    private int instanceNum = 0;
    public static ExecutorService executor = Executors.newFixedThreadPool(10);
    public Dispatcher(BlockingQueue<Connection> connections)
    {
        System.out.println("创建了一个分发线程");
        this.connections = connections;
    }
    public void run() {
        while(!stop)
        {
            try {
                Connection connection = connections.take();
                //System.out.println("连接池为空了么："+connections.isEmpty());
                ConnectionState state = connection.getConnectionState();
                if(ConnectionState.CONN_WAITING.equals(state))
                {
                    executor.execute(new ConnWaiting(connection));
                }
                else if(ConnectionState.CONN_READ.equals(state))
                {
                    executor.execute(new ConnRead(connection));
                }
                else if(ConnectionState.CONN_PARSE_CMD.equals(state))
                {
                    executor.execute(new ConnParseCmd(connection));
                }
                else if(ConnectionState.CONN_WRITE.equals(state))
                {
                    executor.execute(new ConnWrite(connection));
                }
                else if(ConnectionState.CONN_CLOSING.equals(state))
                {
                    executor.execute(new ConnClose(connection));
                }
                //connections.put(connection);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
