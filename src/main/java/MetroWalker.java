import java.util.Vector;

/**
 * Created by amarcinkowski on 26.02.17.
 */
public class MetroWalker extends MetroBaseListener {

    private Vector<String> commands = new Vector<String>();

    public Vector<String> getCommands() {
        return commands;
    }

    @Override
    public void exitCommand(MetroParser.CommandContext ctx) {
        commands.add(ctx.getText());
    }
}

