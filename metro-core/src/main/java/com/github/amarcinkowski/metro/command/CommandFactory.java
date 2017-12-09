package com.github.amarcinkowski.metro.command;

public class CommandFactory {

    public final static int PACKAGE_NAME_INDEX=8;

    public Class<Command> getCommand(String name) throws ClassNotFoundException{
        String className = name.substring(0,1).toUpperCase() + name.substring(1) + "Command";
        String packageName = this.getClass().getPackage().toString().substring(PACKAGE_NAME_INDEX);
        return (Class<Command>) Class.forName(packageName + "." + className);
    }

}
