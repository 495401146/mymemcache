package eventthread;

import connection.model.Connection;
import connection.model.ConnectionState;

import java.io.IOException;
import java.net.Socket;

public class ConnWaiting implements Runnable {
    private Connection connection;

    public ConnWaiting(Connection connection)
    {
        System.out.println("创建了一个等待的连接");
        this.connection = connection;
    }
    public void run() {
        System.out.println("等待执行");
        Socket socket = connection.getClientSocket();
        while(true)
        {
            try {
                if(socket.getInputStream().available()>0)
                {
                    connection.setConnectionState(ConnectionState.CONN_READ);
                    break;
                }
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            connection.getConnections().put(connection);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
