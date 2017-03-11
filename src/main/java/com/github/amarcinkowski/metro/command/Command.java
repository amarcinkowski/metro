package com.github.amarcinkowski.metro.command;


import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Vector;

@Slf4j
@EqualsAndHashCode()
@ToString
public abstract class Command {

    private Vector<String> args = new Vector<>();
    private HashMap<String,String> typedArgs = new HashMap<>();

    public Vector<String> getArgs() {
        return args;
    }

    public HashMap<String, String> getTypedArgs() {
        return typedArgs;
    }

    public Command(Vector<String> args, HashMap<String,String> typedArgs) {
        this.args = args;
        this.typedArgs=typedArgs;
    }

    public void execute() {
        throw new RuntimeException("missing implementation");
    }

}
