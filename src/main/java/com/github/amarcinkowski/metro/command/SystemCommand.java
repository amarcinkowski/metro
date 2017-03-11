package com.github.amarcinkowski.metro.command;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Vector;
import java.util.stream.Collectors;

@Slf4j
public class SystemCommand extends Command {

    public SystemCommand(Vector<String> args, HashMap<String, String> typedArgs) {
        super(args, typedArgs);
    }

    @Override
    public void execute() {
        log.debug("EXEC system: " + getTypedArgs().get("command") + " arg: " + getTypedArgs().get("commandArg"));
    }
}
