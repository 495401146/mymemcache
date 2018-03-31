package cmd;

import cache.Cache;

public class WriteCommand implements Command {
    private String key;
    private String value;

    public WriteCommand(String key,String value) {
        this.key = key;
        this.value = value;
    }

    public String execute() {
        Cache.set(this.key,this.value);
        System.out.println("设置键值，key:"+this.key+",value:"+this.value);
        return null;
    }

    public CMDType getCmdType() {
        return CMDType.SET_CMD;
    }
}
