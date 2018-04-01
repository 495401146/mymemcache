package eventthread;

import connection.model.Connection;

import java.io.IOException;

public class ConnClose implements Runnable {
    private Connection connection;

    public ConnClose(Connection connection)
    {
        System.out.println("创建了一个关闭连接线程");
        this.connection = connection;
    }

    public void run() {
        assert connection !=null;
        connection.getConnections().remove(connection);
        try {
            connection.getClientSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection = null;
    }
}
