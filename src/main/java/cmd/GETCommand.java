package cmd;

import Message.Message;
import cache.Cache;
import cache.model.DictValue;
import connection.model.Connection;
import org.apache.log4j.Logger;
/**
 * get命令
 */
public class GETCommand implements Command {
    private static final Logger logger = Logger.getLogger(GETCommand.class);
    private CMDType cmdType = CMDType.GET_CMD;
    private String[] keys;
    private Connection connection;

    public GETCommand( String[] keys, Connection connection) {
        this.keys = keys;
        this.connection = connection;
    }

    public Message execute() {
        //调用get方法
        StringBuilder sb = new StringBuilder();
        //VALUE <key> <flags> <bytes>\r\n
        for (String key:keys) {
            DictValue dictValue = Cache.get(key);
            if(dictValue==null)
            {
                continue;
            }
            sb.append("VALUE "+key+" "+dictValue.getFlags()+" "+dictValue.getValue().length()+"\\r\\n"+
            dictValue.getValue()+"\r\n");
        }
        sb.append("END\\r\\n\"");
        logger.info(Thread.currentThread().getName()+"" +
                "get command|"+sb.toString());
        Message message = new Message(sb.toString(),CMDType.GET_CMD,connection);
        return message;
    }
    //public Message getMessage()


    public CMDType getCmdType() {
        return this.cmdType;
    }
}
