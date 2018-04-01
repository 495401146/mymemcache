package eventthread;

import Message.Message;
import connection.model.Connection;
import connection.model.ConnectionState;
import eventthread.model.ReadState;
import eventthread.model.WriteToClientState;
import sun.misc.ClassFileTransformer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ConnWrite implements Runnable {
    private Connection connection;

    public ConnWrite(Connection connection)
    {
        System.out.println("创建了一个回写的线程");
        this.connection = connection;
    }
    public void run() {
        assert connection!=null;

        if(writeToClient())
        {
            System.out.println("又开始解析了");
            connection.setConnectionState(ConnectionState.CONN_PARSE_CMD);
        }
        try {
            connection.getConnections().put(connection);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean writeToClient()
    {
        Socket socket = connection.getClientSocket();
        OutputStream os = null;
        try {
            os = socket.getOutputStream();
            Message message = connection.getMessage();
            os.write(message.getMsg().getBytes());
            //os.flush();
        } catch (Exception e) {
            connection.setConnectionState(ConnectionState.CONN_CLOSING);
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
