package server;


import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class CommandTest {
    @Test
    public void connectAndCommand() throws IOException, InterruptedException {
        Socket socket = new Socket("127.0.0.1",12000);
        OutputStream os = socket.getOutputStream();
        os.write("set username 2323131 10 10\\r\\nhechangzhi\\r\\n".getBytes());
        os.write("get username \\n".getBytes());
        socket.getOutputStream().flush();
        InputStream is = socket.getInputStream();

        //os.close();
//        while(true)
//        {
//            builder.append((char) is.read());
//            System.out.println(builder);
//        }

        //StringBuilder builder = new StringBuilder();

    }
}
