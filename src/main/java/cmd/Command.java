package cmd;

public interface Command {
    public String execute();
    public CMDType getCmdType();
}
