import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12000);
        while(true)
        {
            Socket socket = serverSocket.accept();
            int readChars;
            InputStream is = socket.getInputStream();
            System.out.println("读取数据");
            while((readChars=is.read())!=-1)
            {
                System.out.println(is.read());
            }
        }
    }
}
