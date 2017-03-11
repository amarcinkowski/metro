package com.github.amarcinkowski.metro.command;

import lombok.extern.slf4j.Slf4j;

import java.util.Vector;
import java.util.stream.Collectors;

@Slf4j
public class PrintCommand extends Command {

    public PrintCommand(Vector<String> args) {
        super(args);
    }

    @Override
    public void execute() {
        log.trace("EXEC print # " + getArgs().stream().collect(Collectors.joining(" + ")));
    }
}
