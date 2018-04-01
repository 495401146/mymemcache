package eventthread;

import Message.Message;
import cmd.DeleteCommand;
import cmd.GETCommand;
import cmd.WriteCommand;
import connection.model.Connection;
import connection.model.ConnectionState;
import eventthread.model.ParseCmdState;

public class ConnParseCmd implements Runnable {
    private Connection connection;

    public ConnParseCmd(Connection connection)
    {
        System.out.println("创建了一个解析线程");
        this.connection = connection;
    }
    public void run() {
        assert connection!=null;
        StringBuilder builder = connection.getBuilder();
        int state = tryReadCommand(connection);
        if(ParseCmdState.FAIL==state)
        {
            connection.setConnectionState(ConnectionState.CONN_CLOSING);
        }
        else if(ParseCmdState.SUCCESS==state){
            connection.setConnectionState(ConnectionState.CONN_WRITE);

        }
        else{
            connection.setConnectionState(ConnectionState.CONN_READ);
        }
        try {
            connection.getConnections().put(connection);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




    private int tryReadCommand(Connection connection) {
        //System.out.println("进入命令解析");
        assert connection != null;
        StringBuilder builder = connection.getBuilder();
        System.out.println("进入命令解析的builder:"+builder.toString());
        if(builder.length()==0)
        {
            return ParseCmdState.NEW_CMD;
        }
        int point = builder.indexOf("\\n");
        if(point!=-1)
        {
            //System.out.println("有n,开始解析");
            //System.out.println("n的位置："+point);
            StringBuilder builder1 = null;
            if(point>1&&builder.charAt(point-1)=='r'&&builder.charAt(point-2)=='\\')
            {
                System.out.println("有r");
                point-=2;
                builder1 = new StringBuilder(builder.substring(0,point));
                connection.setBuilder(new StringBuilder(builder.substring(point+4)));
            }
            else{
                builder1 = new StringBuilder(builder.substring(0,point));
                connection.setBuilder(new StringBuilder(builder.substring(point+2)));
            }

            if(!dispatcherCommand(builder1))
            {
                return ParseCmdState.NEW_CMD;
            }
            //System.out.println("剩余的的builder："+connection.getBuilder());
        }else{
            if(connection.getBuilder().length()>1024)
            {
                return ParseCmdState.FAIL;
            }
            return ParseCmdState.NEW_CMD;
        }
        return ParseCmdState.SUCCESS;


    }

    private boolean dispatcherCommand(StringBuilder builder) {
        System.out.println(builder.toString());
        String[] commandParams = tokenizerText(builder);

        if(commandParams.length<1)
        {

        }
        if("delete".equals(commandParams[0]))
        {
            String key = commandParams[1];
            String time = commandParams[1];
            DeleteCommand cmd = new DeleteCommand(connection,key,time);
            Message message = cmd.execute();
            connection.setMessage(message);
            connection.setConnectionState(ConnectionState.CONN_WRITE);
        }
        else if("get".equals(commandParams[0]))
        {
            String[] getParams = new String[commandParams.length-1];
            for(int i = 0;i<getParams.length;i++)
            {
                getParams[i] = commandParams[i+1];
            }
            GETCommand cmd = new GETCommand(getParams,connection);
            Message message = cmd.execute();
            connection.setMessage(message);
            connection.setConnectionState(ConnectionState.CONN_WRITE);
        }
        else if("set".equals(commandParams[0]))
        {
            System.out.println("进入set命令");
            int bytes = Integer.valueOf(commandParams[4]);
            //判断此时读取的字节是否够value的
            if(connection.getBuilder().length()<bytes+4)
            {
                connection.setBuilder(builder.append("\\r\\n").append(connection.getBuilder()));
                connection.setConnectionState(ConnectionState.CONN_READ);
                return false;
            }
            else{
                System.out.println("现在还剩:"+connection.getBuilder().toString());
                int point = connection.getBuilder().indexOf("\\n");
                if(point==-1)
                {
                    connection.setConnectionState(ConnectionState.CONN_CLOSING);
                    return true;
                }
                if(point>1&&connection.getBuilder().charAt(point-1)=='r'&&connection.getBuilder().charAt(point-2)=='\\')
                {
                    point-=2;
                }
                String value = connection.getBuilder().substring(0,point).trim();
                connection.setBuilder(new StringBuilder(connection.getBuilder().substring(point+4)));
                String key = commandParams[1];
                String expire = commandParams[2];
                String flags = commandParams[3];
                WriteCommand cmd = new WriteCommand(key,value,expire,flags,connection);
                Message message = cmd.execute();
                connection.setMessage(message);
                connection.setConnectionState(ConnectionState.CONN_WRITE);
            }

        }
        else{
            System.out.println("不支持此操作");
        }
        return true;
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
