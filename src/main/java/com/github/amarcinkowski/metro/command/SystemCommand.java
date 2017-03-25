package com.github.amarcinkowski.metro.command;

import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SystemCommand extends Command {

    public SystemCommand(Vector<String> args, HashMap<String, String> typedArgs) {
        super(args, typedArgs);
    }

    private String[] getCommand() {
        String command = getTypedArgs().get("command");
        String commandArgs = getTypedArgs().get("commandArgs");
        log.debug("EXEC system: " + command + " arg: " + commandArgs);
        Splitter splitter = Splitter.on('|').omitEmptyStrings().trimResults();
        List<String> commandLineList = new ArrayList<>();
        commandLineList.add(command);
        try {
            commandLineList.addAll(splitter.splitToList(commandArgs));
        } catch (Exception e) {
        }
        log.trace("Command " + commandLineList);
        return commandLineList.toArray(new String[]{});
    }

    @Override
    public String execute() {
        String timeoutString = getTypedArgs().get("timeout");
        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(getCommand());
            Integer timeout = Integer.parseInt(timeoutString);
            if (timeout == 0) {
                return "NO-OUTPUT";
            }
            if (!p.waitFor(timeout, TimeUnit.SECONDS)) {
                //timeout - kill the process.
                p.destroy(); // consider using destroyForcibly instead
            } else {
//            p.waitFor();
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(p.getInputStream()));

                String line = "";
                while ((line = reader.readLine()) != null) {
                    System.out.println("LINE" + line);
                    output.append(line + "\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.debug("EXEC system output: " + output.toString());
        return output.toString();
    }
}
