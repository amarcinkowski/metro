import lombok.extern.slf4j.Slf4j;

import java.util.Vector;

/**
 * Created by amarcinkowski on 26.02.17.
 */
@Slf4j
public class MetroWalker extends MetroBaseListener {

    private Vector<String> commands = new Vector<String>();

    public Vector<String> getCommands() {
        return commands;
    }

    @Override
    public void exitCommand(MetroParser.CommandContext ctx) {
        log.debug("Adding command " + ctx.getText());
        commands.add(ctx.getText());
    }
}

