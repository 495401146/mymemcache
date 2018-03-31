package server.main;

import cmd.Command;
import cmd.GETCommand;
import cmd.WriteCommand;
import connection.model.Connection;
import Exception.NullConnectionException;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionHandler implements Runnable {
    private BlockingQueue<Connection> connections;
    private boolean stop = false;
    private boolean flag = false;
    public ConnectionHandler(BlockingQueue<Connection> connections)
    {
        this.connections = connections;
    }
    private AtomicInteger failureNum = new AtomicInteger(0);


    public void run() {
        System.out.println("启动了一个处理连接请求类");
        Connection connection = null;
        try {
            connection = connections.take();
            System.out.println("获取到了一个连接，进行处理");
            Socket socket = connection.getClientSocket();
            if (socket == null) {
                throw new NullConnectionException("Socket is NULL");
            }
            InputStream in = socket.getInputStream();
            int readChar;
            while((readChar=in.read())!=-1&&!stop)
            {
                connection.getBuilder().append((char)readChar);
                System.out.println((char)readChar);
                if((char)readChar=='\\')
                {
                    flag = true;
                    continue;
                }
                if(flag)
                {
                    if((char)readChar=='n')
                    {
                        //System.out.println(builder.toString());
                        if(!tryReadCommand(connection))
                        {
                            run();
                        }
                    }
                    else
                    {
                        flag = false;
                    }

                }
//                if(builder.length()>1024)
//                    return;
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean tryReadCommand(Connection connection) {
        System.out.println("进入命令解析");
        assert connection != null;
        assert connection.getBuilder()!=null;
        StringBuilder builder = connection.getBuilder();
        System.out.println("进入命令解析的builder:"+builder.toString());
        if(builder.length()==0)
        {
            return false;
        }
        int point = builder.indexOf("\\n");
        if(point!=-1)
        {
            System.out.println("有n,开始解析");
            System.out.println("n的位置："+point);
            System.out.println("bu"+builder.charAt(point));
            if(point>1&&builder.charAt(point-1)=='r'&&builder.charAt(point-2)=='\\')
            {
                System.out.println("有r");
                point-=2;
            }
            StringBuilder builder1 = new StringBuilder(builder.substring(0,point));
            connection.setBuilder(builder1);
            dispatcherCommand(connection);
            System.out.println("执行完命令分发");
            connection.getBuilder().delete(0,connection.getBuilder().length());
            System.out.println("清空后的builder："+connection.getBuilder());
        }else{
            return false;
        }
        return true;


    }

    private void dispatcherCommand(Connection connection) {
        System.out.println(connection.getBuilder().toString());
        String[] commandParams = tokenizerText(connection.getBuilder());

        if(commandParams.length<1)
        {

        }
        if("get".equals(commandParams[0]))
        {
            String[] getParams = new String[commandParams.length-1];
            for(int i = 0;i<getParams.length;i++)
            {
                getParams[i] = commandParams[i+1];
            }
            GETCommand cmd = new GETCommand(getParams);
            cmd.execute();
        }
        if("set".equals(commandParams[0]))
        {
            String key = commandParams[1];
            String value = commandParams[2];
            WriteCommand cmd = new WriteCommand(key,value);
            cmd.execute();
        }
    }


    private String[] tokenizerText(StringBuilder builder)
    {
        String commandBytes = new String(builder);
        String[] commandParams = commandBytes.split(" ");
        for(int i = 0;i<commandParams.length;i++)
        {
            commandParams[i].trim();
        }
        return commandParams;
    }


}
