package com.github.amarcinkowski.metro.command;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Vector;
import java.util.stream.Collectors;

@Slf4j
public class PrintCommand extends Command {

    public PrintCommand(Vector<String> args, HashMap<String, String> typedArgs) {
        super(args, typedArgs);
    }

    @Override
    public String execute() {
        String args = getArgs().stream().collect(Collectors.joining(" % "));
        log.trace("EXEC print # " + args);
        return args;
    }
}
