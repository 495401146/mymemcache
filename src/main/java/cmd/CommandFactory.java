package cmd;

public class CommandFactory {
    private CommandFactory() {}
    private volatile CommandFactory commandFactory;

    public CommandFactory newInstance()
    {
        if(commandFactory==null)
        {
            synchronized (CommandFactory.class)
            {
                if(commandFactory==null)
                {
                    commandFactory = new CommandFactory();
                }
            }
        }
        return commandFactory;
    }



}
