package com.github.amarcinkowski.metro.command;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Vector;
import java.util.stream.Collectors;

@Slf4j
public class PauseCommand extends Command {

    public PauseCommand(Vector<String> args, HashMap<String, String> typedArgs) {
        super(args, typedArgs);
    }

    @Override
    public String execute() {
        String args = getArgs().stream().collect(Collectors. joining(" % "));
        long start = System.currentTimeMillis();
        try {
            Thread.sleep(Integer.parseInt(args));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("EXEC pause # " + args);
        return "Paused for " + (System.currentTimeMillis() - start);
    }
}
