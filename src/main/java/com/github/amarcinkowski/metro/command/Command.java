package com.github.amarcinkowski.metro.command;


import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Vector;

@Slf4j
@EqualsAndHashCode()
@ToString
public abstract class Command {

    private Vector<String> args = new Vector<>();

    public Vector<String> getArgs() {
        return args;
    }

    public Command(Vector<String> args) {
        this.args = args;
    }

    public void execute() {
        throw new RuntimeException("missing implementation");
    }

}
