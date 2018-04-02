package eventthread;

import Message.Message;
import cmd.*;
import config.Config;
import connection.model.Connection;
import connection.model.ConnectionState;
import eventthread.model.ParseCmdState;
import org.apache.log4j.Logger;
import utils.ValidateUtils;

import java.io.IOException;
import java.security.Timestamp;

/**
 * 解析命令的线程，当读状态成功了之后转入此状态，此线程比较复杂，因为其既要
 * 负责解析，又要处理read状态时没读完的情况
 *
 */
public class ConnParseCmd implements Runnable {
    private static final Logger logger = Logger.getLogger(ConnParseCmd.class);
    private Connection connection;

    public ConnParseCmd(Connection connection)
    {
        logger.info(Thread.currentThread().getName()+":create a parse cmd thread");
        this.connection = connection;
    }
    public void run() {
        assert connection!=null;
        StringBuilder builder = connection.getBuilder();
        int state = tryReadCommand(connection);
        //如果解析失败，进入close状态
        if(ParseCmdState.FAIL==state)
        {
            logger.error(Thread.currentThread().getName()+":read bytes is not normally");
            connection.setConnectionState(ConnectionState.CONN_CLOSING);
        }
        //讲连接放回连接阻塞队列
        try {
            connection.getConnections().put(connection);
        } catch (InterruptedException e) {
            logger.error(Thread.currentThread().getName()+"put connection is fail");
            logger.error(e.getMessage());
            connection = null;
        }
    }



    //进行命令解析
    private int tryReadCommand(Connection connection) {
        //System.out.println("进入命令解析");
        assert connection != null;
        StringBuilder builder = connection.getBuilder();
        //当此时builder中无数据时，转入read状态
        if(builder.length()==0)
        {
            connection.setConnectionState(ConnectionState.CONN_READ);
            return ParseCmdState.SUCCESS;
        }
        int point = builder.indexOf("\\n");
        if(point!=-1)
        {
            logger.info(Thread.currentThread().getName()+":have //n,continue execute command");
            StringBuilder builder1 = null;
            //如果有/r,因为windows与linux换行符不一样，所有只有/n或者/r/n的情况都得考虑到
            if(point>1&&builder.charAt(point-1)=='r'&&builder.charAt(point-2)=='\\')
            {
                point-=2;
                builder1 = new StringBuilder(builder.substring(0,point));
                connection.setBuilder(new StringBuilder(builder.substring(point+4)));
            }
            else{
                builder1 = new StringBuilder(builder.substring(0,point));
                connection.setBuilder(new StringBuilder(builder.substring(point+2)));
            }
            //截取/n或者/r/n之前的数据进行解析
            dispatcherCommand(builder1);
        }else{
            //当builder中没有\n，并且其数据已经大于指定的字节数（一般为1024个字节）
            if(connection.getBuilder().length()> Config.EXCEPTED_BYTES)
            {
                return ParseCmdState.FAIL;
            }
            logger.info(Thread.currentThread().getName()+":not have \\n,continue read");
            //当没有读到\n,继续进行读取
            connection.setConnectionState(ConnectionState.CONN_READ);
            return ParseCmdState.SUCCESS;
        }

        return ParseCmdState.SUCCESS;

    }
   //根据不同的命令调用不同的函数处理
    private void dispatcherCommand(StringBuilder builder) {
        assert builder.length()!=0;
        //System.out.println(builder.toString());
        //进行切片和trim处理
        String[] commandParams = tokenizerText(builder);
        logger.info(Thread.currentThread().getName()+":the command is legal");
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
           set(commandParams,builder,CMDType.SET_CMD);

        }
        else if("add".equals(commandParams[0]))
        {
            set(commandParams,builder,CMDType.ADD_CDM);
        }
        else if("end".equals(commandParams[0]))
        {
            end(commandParams);
        }
        else{
            //当读到的第一个词是当前没有定义过的命令，则回写错误原因
            logger.error(Thread.currentThread().getName()+":the command is not legal");
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
        if(ValidateUtils.isNumeric(times)&&Integer.valueOf(times)>0)
        {
            return true;
        }
        return false;

    }

    //调用cmd执行函数，执行回写，因为每个命令都要回写，所以抽象出来
    public void writeToClient(Command cmd,Connection connection)
    {
        //获取到执行之后的反馈信息，将状态转换为写状态
        Message message = cmd.execute();
        connection.setMessage(message);
        connection.setConnectionState(ConnectionState.CONN_WRITE);
    }


    //delete处理函数
    public void delete(String[] commandParams)
    {
        logger.info(Thread.currentThread().getName()+":start parse delete command");
        //当delete函数不符合定义
        if(commandParams.length!=3)
        {
            logger.info(Thread.currentThread().getName()+":delete params num is not match");
            Command cmd = new ErrorCommand(connection,Response.ERROR_CLIENT_DELETE_PARAMS_NUM_NOEXCEPTED);
            writeToClient(cmd,connection);
            return;
        }
        String key = commandParams[1];
        String time = commandParams[2];
        //进行参数校验
        if(!validataDeleteParam(time))
        {
            logger.info(Thread.currentThread().getName()+":delete params is not match");
            Command cmd = new ErrorCommand(connection,Response.ERROR_CLIENT_DELETE_PARAMS_NOT_VERIFY);
            writeToClient(cmd,connection);
            return;
        }
        logger.info(Thread.currentThread().getName()+":start parse is successfully");
        //调用对应的命令进行执行
        Command cmd = new DeleteCommand(connection,key,time);
        writeToClient(cmd,connection);
    }

    //get处理函数
    public void get(String[] commandParams)
    {
        logger.info(Thread.currentThread().getName()+":start parse get command");
        //判断参数个数
        if(commandParams.length==1)
        {
            logger.info(Thread.currentThread().getName()+":get params num is not match");
            Command command = new ErrorCommand(connection,Response.ERROR_CLIENT_GET_NO_PARAMS);
            writeToClient(command,connection);
            return;
        }
        String[] getParams = new String[commandParams.length-1];
        for(int i = 0;i<getParams.length;i++)
        {
            getParams[i] = commandParams[i+1];
        }
        logger.info(Thread.currentThread().getName()+":get command is successfully");
        //执行get命令
        Command cmd = new GETCommand(getParams,connection);
        writeToClient(cmd,connection);
    }

    //set处理函数，set有点不一样，因为有两行，所以我采取了先读取字节数
    //然后根据字节数判断后续状态，知道读到所有数据，memcache的实现是先
    //根据字节数开辟一个item空间，在等待写入，要新添一种状态，我就没有这么做
    public void set(String[] commandParams,StringBuilder builder,CMDType cmdType)
    {
        logger.info(Thread.currentThread().getName()+":start parse set command");
        if(commandParams.length!=5)
        {
            logger.error(Thread.currentThread().getName()+":set or add params num is not match");
            Command cmd = new ErrorCommand(connection,Response.ERROR_CLIENT_SET_PARAMS_NUM_NOEXCEPTED);
            writeToClient(cmd,connection);
            return;
        }
        int bytes = Integer.valueOf(commandParams[4]);
        //判断此时读取的字节是否够value的，不够则回读状态
        if(connection.getBuilder().length()<bytes+4)
        {
            connection.setBuilder(builder.append("\\r\\n").append(connection.getBuilder()));
            connection.setConnectionState(ConnectionState.CONN_READ);
        }
        else{
            int point = connection.getBuilder().indexOf("\\n");
            //有一种可能是我已经到了字节数的大小但是没读到\n，这种返回错误
            //并将当前builder截取一部分
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
            String expire = commandParams[3];
            String flags = commandParams[2];
            //验证参数
            if(!validataSetParam(key,flags,expire))
            {
                logger.error(Thread.currentThread().getName()+":set or add params is not match");
                Command cmd = new ErrorCommand(connection,Response.ERROR_CLIENT_SET_PARAMS_NOT_VERIFY);
                writeToClient(cmd,connection);
                return;
            }
            //set和add都调用此函数，因为这两个命令传入参数
            else{
                logger.info(Thread.currentThread().getName()+"set or add command is parse successfully");
                Command cmd;
                if(CMDType.SET_CMD.equals(cmdType))
                {
                    cmd = new SetCommand(key,value,expire,flags,connection);
                }
                else{
                    cmd = new AddCommand(key,value,expire,flags,connection);
                }
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
