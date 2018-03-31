package eventthread;

import connection.model.Connection;
import connection.model.ConnectionState;

import java.io.InputStream;
import java.net.Socket;

public class ConnRead implements Runnable {
    private boolean flag = false;
    Connection connection;

    public ConnRead(Connection connection) {
        this.connection = connection;
    }

    public void run() {

    }


    private int readFromNetwork(Connection connection) {
        System.out.println("开始读客户端命令");
        try {
            Socket socket = connection.getClientSocket();
            if (socket == null) {
                throw new Exception.NullConnectionException("Socket is NULL");
            }
            InputStream in = socket.getInputStream();
            int readChar;
            while ((readChar = in.read()) != -1) {
                connection.getBuilder().append((char) readChar);
                System.out.println((char) readChar);
                if ((char) readChar == '\\') {
                    flag = true;
                    continue;
                }
                if (flag) {
                    if ((char) readChar == 'n') {
                        connection.setConnectionState(ConnectionState.CONN_PARSE_CMD);
                        break;
                    } else {
                        flag = false;
                    }

                }
//                if(builder.length()>1024)
//                    return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ;
    }
}
