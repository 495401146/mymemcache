package cache;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class test {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1",11000);
        OutputStream os = socket.getOutputStream();
        os.write("set username zhuli\\r\\n".getBytes());
        os.write("get username \\n".getBytes());
    }

}
