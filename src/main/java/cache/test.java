package cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class test {
    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("127.0.0.1",11000);
        OutputStream os = socket.getOutputStream();
        os.write("set username xxx 0 10\\r\\nhechangzhi\\r\\n".getBytes());
        os.write("get username \\n".getBytes());
        socket.getOutputStream().flush();
        InputStream is = socket.getInputStream();
        //os.close();
        //Thread.sleep(3000);
        StringBuilder builder = new StringBuilder();
        while(true)
        {
            builder.append((char) is.read());
            System.out.println(builder);
        }

        //StringBuilder builder = new StringBuilder();


    }

}
