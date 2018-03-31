package cmd;

import cache.Cache;

public class GETCommand implements Command {
    private CMDType cmdType = CMDType.GET_CMD;
    private String[] keys;

    public GETCommand(String[] keys) {
        this.keys = keys;
    }

    public String execute() {
        //调用get方法
        StringBuilder sb = new StringBuilder();
        for (String key:keys) {
            String value = Cache.getValue(key);
            sb.append(value+",");
        }
        System.out.println(sb.toString());
        return new String(sb.substring(0,sb.length()-1));
    }


    public CMDType getCmdType() {
        return this.cmdType;
    }
}
