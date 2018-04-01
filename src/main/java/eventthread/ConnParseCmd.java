package eventthread;

import Message.Message;
import cmd.*;
import connection.model.Connection;
import connection.model.ConnectionState;
import eventthread.model.ParseCmdState;
import utils.ValidateUtils;

import java.security.Timestamp;

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
        try {
            connection.getConnections().put(connection);
        } catch (InterruptedException e) {
            connection.setConnectionState(ConnectionState.CONN_CLOSING);
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
            connection.setConnectionState(ConnectionState.CONN_READ);
            return ParseCmdState.SUCCESS;
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
            dispatcherCommand(builder1);
            //System.out.println("剩余的的builder："+connection.getBuilder());
        }else{
            if(connection.getBuilder().length()>1024)
            {
                return ParseCmdState.FAIL;
            }
            connection.setConnectionState(ConnectionState.CONN_READ);
            return ParseCmdState.SUCCESS;
        }
        return ParseCmdState.SUCCESS;


    }
    //如果成功，在dispatchercommand中设置连接状态
    private void dispatcherCommand(StringBuilder builder) {
        assert builder.length()!=0;
        System.out.println(builder.toString());
        String[] commandParams = tokenizerText(builder);
        //根据第一个单词进入具体命令处理
        if("delete".equals(commandParams[0]))
        {
            delete(commandParams);
        }
        else if("get".equals(commandParams[0]))
        {
            get(commandParams);
        }
        else if("set".equals(commandParams[0]))
        {
           set(commandParams,builder);

        }
        else if("end".equals(commandParams[0]))
        {
            end(commandParams);
        }
        else{
            Command cmd = new ErrorCommand(connection,Response.ERROR_NO_EXISTS_CMD);
            writeToClient(cmd,connection);
            return;
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

    //检验set的参数，包括键长度，flags是否为16位无符号整数，expire是否为时间戳
    public boolean validataSetParam(String key,String flags,String expire)
    {
        if(ValidateUtils.isNumeric(flags)&&ValidateUtils.isNumeric(expire)
                &&key.length()<200&&Integer.valueOf(expire)>=0)
        {
            return true;
        }
        return false;

    }

    //检验delete的参数，包括times是否为整数
    public boolean validataDeleteParam(String times)
    {
        if(ValidateUtils.isNumeric(times))
        {
            return true;
        }
        return false;

    }

    //调用cmd执行函数，执行会写
    public void writeToClient(Command cmd,Connection connection)
    {
        Message message = cmd.execute();
        connection.setMessage(message);
        connection.setConnectionState(ConnectionState.CONN_WRITE);
    }


    //delete处理函数
    public void delete(String[] commandParams)
    {
        if(commandParams.length!=3)
        {
            Command cmd = new ErrorCommand(connection,Response.ERROR_CLIENT_DELETE_PARAMS_NUM_NOEXCEPTED);
            writeToClient(cmd,connection);
            return;
        }
        String key = commandParams[1];
        String time = commandParams[2];
        if(!validataDeleteParam(time))
        {
            Command cmd = new ErrorCommand(connection,Response.ERROR_CLIENT_DELETE_PARAMS_NOT_VERIFY);
            writeToClient(cmd,connection);
            return;
        }
        Command cmd = new DeleteCommand(connection,key,time);
        writeToClient(cmd,connection);
    }

    //get处理函数
    public void get(String[] commandParams)
    {
        if(commandParams.length==1)
        {
            Command command = new ErrorCommand(connection,Response.ERROR_CLIENT_GET_NO_PARAMS);
            writeToClient(command,connection);
            return;
        }
        String[] getParams = new String[commandParams.length-1];
        for(int i = 0;i<getParams.length;i++)
        {
            getParams[i] = commandParams[i+1];
        }
        Command cmd = new GETCommand(getParams,connection);
        writeToClient(cmd,connection);
    }

    //set处理函数
    public void set(String[] commandParams,StringBuilder builder)
    {
        System.out.println("进入set命令");
        if(commandParams.length!=5)
        {
            Command cmd = new ErrorCommand(connection,Response.ERROR_CLIENT_SET_PARAMS_NUM_NOEXCEPTED);
            writeToClient(cmd,connection);
            return;
        }
        int bytes = Integer.valueOf(commandParams[4]);
        //判断此时读取的字节是否够value的
        if(connection.getBuilder().length()<bytes+4)
        {
            connection.setBuilder(builder.append("\\r\\n").append(connection.getBuilder()));
            connection.setConnectionState(ConnectionState.CONN_READ);
        }
        else{
            System.out.println("现在还剩:"+connection.getBuilder().toString());
            int point = connection.getBuilder().indexOf("\\n");
            if(point==-1)
            {
                Command cmd = new ErrorCommand(connection,Response.ERROR_CLIENT_SET_BYTES_NOT_MATCH_VALUE);
                connection.setBuilder(new StringBuilder(connection.getBuilder().substring(bytes)));
                writeToClient(cmd,connection);
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
            if(!validataSetParam(key,flags,expire))
            {
                Command cmd = new ErrorCommand(connection,Response.ERROR_CLIENT_SET_PARAMS_NOT_VERIFY);
                writeToClient(cmd,connection);
                return;
            }
            else{
                Command cmd = new WriteCommand(key,value,expire,flags,connection);
                writeToClient(cmd,connection);
                return;
            }
        }

    }
    //end处理函数
    public void end(String[] commandParams)
    {
        if(commandParams.length>1)
        {
            Command cmd = new ErrorCommand(connection,Response.ERROR_CLIENT_END_PARAMS_NOT_MATCH);
            writeToClient(cmd,connection);
            return;
        }
        connection.setConnectionState(ConnectionState.CONN_CLOSING);
    }


}
