package cmd;

import Message.Message;
import Message.MessageType;
import cache.Cache;
import connection.model.Connection;
import org.apache.log4j.Logger;

/**
 * add命令对应函数
 */
public class AddCommand implements Command {
    private static final Logger logger = Logger.getLogger(AddCommand.class);
    private String key;
    private String value;
    private String expire;
    private String flags;
    private Connection connection;

    public AddCommand(String key,String value,String expire
            ,String flags,Connection connection) {
        this.key = key;
        this.value = value;
        this.expire = expire;
        this.flags = flags;
        this.connection = connection;
    }

    public Message execute() {
        StringBuilder sb = new StringBuilder();
        //此时有key存在，则返回已存在
        if(Cache.get(this.key)!=null)
        {
            logger.error(Thread.currentThread().getName()+":the key is exists");
            sb.append(Response.CMD_SET_EXISTS);
        }
        //此时无key，则根据插入成功或者失败返回结果
        else{
            if(!Cache.set(this.key,this.value,this.flags,this.expire))
            {
                logger.error(Thread.currentThread().getName()+
                        ":add is fail");
                Message message = new Message(Response.ERROR_SERVER_SET
                ,CMDType.SET_CMD,connection);
                return message;
            }
            sb.append(Response.CMD_SET_SUCCESS);
        }
        logger.info(Thread.currentThread().getName()+":command:add|key:"+this.key+",value:"+this.value);
        Message message = new Message(sb.toString(),CMDType.SET_CMD,
                                 connection);
        return message;
    }

    public CMDType getCmdType() {
        return CMDType.SET_CMD;
    }
}
