package eventthread;

import connection.model.Connection;
import connection.model.ConnectionState;
import eventthread.model.ReadState;

import java.io.InputStream;
import java.net.Socket;

public class ConnRead implements Runnable {
    private boolean flag = false;
    private Connection connection;

    public ConnRead(Connection connection) {
        System.out.println("创建一个读客户端线程");
        this.connection = connection;
    }

    public void run() {
        System.out.println("等待读取");
        assert connection!=null;
        int state = readFromNetwork(connection);
        if(ReadState.READ_NO_DATA_RECEIVED==state)
        {
            connection.setConnectionState(ConnectionState.CONN_WAITING);
        }
        else if(ReadState.READ_DATA_RECEIVED==state){
            connection.setConnectionState(ConnectionState.CONN_PARSE_CMD);
        }
        else{
            connection.setConnectionState(ConnectionState.CONN_CLOSING);
        }
        try {
            connection.getConnections().put(connection);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private int readFromNetwork(Connection connection) {
        assert connection!=null;
        //System.out.println("开始读客户端命令");
        try {
            Socket socket = connection.getClientSocket();
            if (socket == null) {
                //throw new Exception.NullConnectionException("Socket is NULL");
            }
            InputStream in = socket.getInputStream();
            if(in.available()==0)
            {
                return ReadState.READ_NO_DATA_RECEIVED;
            }
            byte[] buf = new byte[3];
            int len;
            if((len = in.read(buf)) != -1) {
                String str = new String(buf,0,len,"utf8");
                connection.getBuilder().append(str);
            }
            //System.out.println("正在读的builder："+connection.getBuilder().toString());
        } catch (Exception e) {
            return ReadState.READ_ERROR;
            //e.printStackTrace();
        }
        //System.out.println("aaa");
        return ReadState.READ_DATA_RECEIVED;
    }
}
