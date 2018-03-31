package server.main;

import connection.model.Connection;
import connection.model.ConnectionState;
import eventthread.ConnRead;
import eventthread.ConnWaiting;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Dispatcher implements Runnable{
    private BlockingQueue<Connection> connections;
    private boolean stop = false;
    public static ExecutorService executor = Executors.newFixedThreadPool(10);
    public Dispatcher(BlockingQueue<Connection> connections)
    {
        this.connections = connections;
    }
    public void run() {
        while(!stop)
        {
            try {
                Connection connection = connections.take();
                ConnectionState state = connection.getConnectionState();
                if(ConnectionState.CONN_WAITING.equals(state))
                {
                    executor.execute(new ConnWaiting(connection));
                }
                else if(ConnectionState.CONN_READ.equals(state))
                {
                    executor.execute(new ConnRead(connection));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
