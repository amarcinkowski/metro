package com.github.amarcinkowski.metro;

import com.github.amarcinkowski.metro.exceptions.DuplicateFunctionException;
import com.github.amarcinkowski.metro.exceptions.MissingFunctionException;
import com.github.amarcinkowski.metro.exceptions.ParamNotSetException;
import com.github.amarcinkowski.metro.exceptions.WrongNumberArgsException;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.*;
import java.util.regex.Pattern;

@Slf4j
public class MetroWalker extends MetroBaseListener {

    private Vector<String> commands = new Vector<>();
    private HashMap<String, ParseTree> functions = new HashMap<>();
    private HashMap<String, String> globals = new HashMap<>();

    public Vector<String> getCommands() {
        return commands;
    }

    public static List<? extends ParseTree> getAncestors(ParseTree t) {
        if ( t.getParent()==null ) {
            return Collections.emptyList();
        }

        List<ParseTree> ancestors = new ArrayList<>();
        t = t.getParent();
        while ( t!=null ) {
            ancestors.add(0, t);
            t = t.getParent();
        }

        return ancestors;
    }

    public static boolean inFunction(ParseTree t) {
        List<? extends ParseTree> ancestors = getAncestors(t);
        log.trace("inFunction " + ancestors);
        for(ParseTree pt : ancestors) {
            if (pt instanceof MetroParser.FunctionContext) {
                log.debug("COMMAND " + t.getText() + " is in FUNC " + pt.getText());
                return true;
            }
        }
        return false;
    }

    private String replaceParamWithGlobal(String param, String command) {
        if (globals.containsKey(param)) {
            log.debug("GLOBAL PARAM " + param + " should be " + globals.get(param));
            command = command.replaceAll("(\\(|,)" + param + "(\\)|,)", "$1" + globals.get(param) + "$2");
        }
        return command;
    }

    private String substituteParamsWithValues(MetroParser.CommandContext ctx, HashMap<String, String> locals) {
        String command = ctx.getText();

        // no null pointer for commands outside functions
        if (locals == null) {
            locals = new HashMap<>();
        }

        // if command has params replace it with functions' go arguments
        int commandsNumberOfParams = (ctx.parameters() != null ? ctx.parameters().parameter().size() : 0);
        for (int i = 0; i < commandsNumberOfParams; i++) {
            String param = ctx.parameters().parameter(i).getText();

            // no value in globals and locals - throw pns
            if (!(globals.containsKey(param) || locals.containsKey(param))) {
                throw new ParamNotSetException("No value for param " + param);
            }

            // local - first
            if (locals.containsKey(param)) {
                log.debug("LOCAL PARAM " + param + " should be " + locals.get(param));
                command = command.replaceAll("(\\(|,)" + param + "(\\)|,)", "$1" + locals.get(param) + "$2");
            }
            // global - second
            command = replaceParamWithGlobal(param, command);
        }
        return command;
    }

    @Override
    public void exitCommand(MetroParser.CommandContext ctx) {
        log.debug("COMMAND " + ctx.getText());
        if (!inFunction(ctx)) {
            log.trace(ctx.getText() + " ANCESTORS " + getAncestors(ctx));

            // command parameters with globals
            String command = substituteParamsWithValues(ctx, null);
            commands.add(command);
        }
    }

    @Override
    public void exitProg(MetroParser.ProgContext ctx) {
        log.trace("MetroWalker EXIT");
    }

    @Override
    public void exitArgument(MetroParser.ArgumentContext ctx) {
        log.trace("ARG " + ctx.getText());
    }

    @Override
    public void exitFunction(MetroParser.FunctionContext ctx) throws RuntimeException {
        log.debug("FUNCTION " + ctx.name.getText());
        if (functions.containsKey(ctx.name.getText())) {
            log.error("Cannot redeclare " + ctx.name.getText() + " - already exists");
            throw new DuplicateFunctionException("Function already exists");
        } else {
        functions.put(ctx.name.getText(), ctx);
    }}

    @Override
    public void exitBlock(MetroParser.BlockContext ctx) {
        log.trace("BLOCK " + ctx.getText());
    }


    @Override
    public void exitGlobalVariables(MetroParser.GlobalVariablesContext ctx) {
        log.info("GLOBAL " + ctx.name.getText() + " => " + ctx.value.getText());
        globals.put(ctx.name.getText(), ctx.value.getText());
    }

    @Override
    public void exitGo(MetroParser.GoContext ctx) {
        log.debug("RUN " + ctx.getText());

        // check if function exists
        if (!functions.containsKey(ctx.name.getText())) {
            log.error("Missing function: " + ctx.name.getText());
            throw new MissingFunctionException("No function by that name");
        }

        // load function ctx definition
        ParseTree function = functions.get(ctx.name.getText());
        MetroParser.FunctionContext functionCtx = (MetroParser.FunctionContext) function.getPayload();

        // get go arguments
        MetroParser.ArgumentsContext arguments = ctx.arguments();
        log.trace("RUN " + functionCtx.getText());

        // get function params
        MetroParser.ParametersContext parametersContext = functionCtx.parameters();

        // number of function params
        int functionNumberOfParams = parametersContext != null ? parametersContext.parameter().size() : 0;
        log.trace("FUNC " + functionCtx.name.getText() + " HAS " + functionNumberOfParams + " PARAMS");

        int goNumberOfArgs = ctx.arguments() != null ? ctx.arguments().argument().size() : 0;
        log.trace("GO HAS " + goNumberOfArgs + " ARGS");

        // TODO mix args and params in Command ??
        // if go has different number of arguments than function params throw exc
        if (functionNumberOfParams != goNumberOfArgs) {
            log.error("Wrong number of args. Is " + goNumberOfArgs + ", should be " +functionNumberOfParams);
            throw new WrongNumberArgsException("wrong number of args");
        }

        // if function has body
        if (functionCtx.block() != null) {
            // iterate over functions' commands
            for (MetroParser.CommandContext commandCtx : functionCtx.block().command()) {

                HashMap<String, String> argValues = new HashMap<>();
                // map params to arg values
                for (int i = 0; i < functionNumberOfParams; i++) {
                    String param = parametersContext.parameter(i).getText();
                    String arg = arguments.argument(i).getText();
                    log.trace("Param " + param + " should be " + arg);
                    argValues.put(param, arg);
                }

                String command = substituteParamsWithValues(commandCtx, argValues);
                commands.add(command);
            }
        }
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        log.trace("RULE " + ctx.getRuleContext().getText());
    }
}

