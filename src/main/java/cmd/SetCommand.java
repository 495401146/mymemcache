package cmd;

import Message.Message;
import cache.Cache;
import connection.model.Connection;
import org.apache.log4j.Logger;

/**
 * 调用set命令
 */
public class SetCommand implements Command {
    private static final Logger logger = Logger.getLogger(SetCommand.class);
    private String key;
    private String value;
    private String expire;
    private String flags;
    private Connection connection;

    public SetCommand(String key,String value,String expire
            ,String flags,Connection connection) {
        this.key = key;
        this.value = value;
        this.expire = expire;
        this.flags = flags;
        this.connection = connection;
    }
    //跟add命令差不多，但是不需要检测key是否已存在
    public Message execute() {
        StringBuilder sb = new StringBuilder();
        if (!Cache.set(this.key, this.value, this.flags, this.expire)) {
            logger.error(Thread.currentThread().getName()+
                            ":the set is fail");
            Message message = new Message(Response.ERROR_SERVER_SET
                    , CMDType.SET_CMD, connection);
            return message;
        }
        sb.append(Response.CMD_SET_SUCCESS);

        logger.info(Thread.currentThread().getName()+
                ":command:set|key:"+this.key+",value:"+this.value);
        Message message = new Message(sb.toString(),CMDType.SET_CMD,
                connection);
        return message;
    }

    public CMDType getCmdType() {
        return CMDType.SET_CMD;
    }
}
