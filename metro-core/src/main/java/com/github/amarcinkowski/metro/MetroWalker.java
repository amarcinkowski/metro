package com.github.amarcinkowski.metro;

import com.github.amarcinkowski.metro.command.Command;
import com.github.amarcinkowski.metro.command.CommandFactory;
import com.github.amarcinkowski.metro.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;

@Slf4j
public class MetroWalker extends MetroBaseListener {

    private Vector<String> commands = new Vector<>();
    private HashMap<String, ParseTree> functions = new HashMap<>();
    private HashMap<String, String> globals = new HashMap<>();
    private Vector<Command> commandsFifo = new Vector<>();
    private CommandFactory commandFactory = new CommandFactory();

    public Vector<String> getCommands() {
        return commands;
    }

    public Vector<Command> getCommandsFifo() {
        return commandsFifo;
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

    private void addCommand(MetroParser.CommandContext commandCtx, HashMap<String,String> args) {
        String commandName = commandCtx.name.getText();
        try {
            Class commandClass = commandFactory.getCommand(commandName);
            Params argValues = substituteParamsWithValues(commandCtx, args);
            log.trace(">>> ARG VALUES: " + argValues);
            commands.add(commandCtxToString(commandCtx,argValues.getParams()));
            Constructor constructor = commandClass.getConstructor(Vector.class,HashMap.class);
            Command commandObject = (Command) constructor.newInstance(argValues.getParams(), argValues.getParamTypes());
            commandsFifo.add(commandObject);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            log.error("Missing command " + e.getMessage());
            throw new MissingCommandException("Missing command " + commandName);
        }
    }

    private String commandCtxToString(MetroParser.CommandContext ctx, Vector<String> argValues) {
        String command = ctx.getText();
        if (ctx.commandParameters() != null) {
            for(int i = 0; i < ctx.commandParameters().commandParameter().size(); i++) {
                String param = ctx.commandParameters().commandParameter(i).getText();
                command = command.replaceAll("(\\(|,)" + param + "(\\)|,)", "$1" + Matcher.quoteReplacement(argValues.get(i)) + "$2");
            }
        }
        log.trace("COMMAND toString " + command);
        return command;
    }

    private Params substituteParamsWithValues(MetroParser.CommandContext ctx, HashMap<String, String> locals) {

        // no null pointer for commands outside functions
        if (locals == null) {
            locals = new HashMap<>();
        }

        // check if all params has possible values
        int commandsNumberOfParams = (ctx.commandParameters() != null ? ctx.commandParameters().commandParameter().size() : 0);
        Params params = new Params();
        for (int i = 0; i < commandsNumberOfParams; i++) {
            String param = ctx.commandParameters().commandParameter(i).ID().getText();

            // no value in globals and locals - throw pns
            if (!(globals.containsKey(param) || locals.containsKey(param))) {
                log.error("Param value not set: " + param);
                throw new ParamNotSetException("No value for param " + param);
            }

            params.add(ctx.commandParameters().commandParameter(i));
        }

        // substitue with values
        for(int i =2; i < ctx.getChildCount() - 1; i++) {
            int index = i - 2;
            log.debug(ctx.getChild(i).getText() + " > " + ctx.getChild(i).getClass().getSimpleName());
            if (ctx.getChild(i).getClass().equals(MetroParser.CommandParametersContext.class)) {
                int paramIndex = 0;
                for(MetroParser.CommandParameterContext commandParameterContext : ctx.commandParameters().commandParameter()) {
                    int currentParamIndex = index + paramIndex++;
                    String param = params.get(currentParamIndex);
                    // local - first
                    if (locals.containsKey(param)) {
                        log.debug("LOCAL PARAM " + param + " -> " + locals.get(param));
                        params.replace(currentParamIndex, locals.get(param));
                    } else
                        // global - second
                        if (globals.containsKey(param)) {
                            log.debug("GLOBAL PARAM " + param + " -> " + globals.get(param));
                            params.replace(currentParamIndex, globals.get(param));
                        }
                }
            } else if (ctx.getChild(i).getClass().equals(MetroParser.ArgumentsContext.class)) {
                log.trace("ARGUMENT CONTEXT");
                params.add(index,ctx.getChild(i).getText());
            }
        }
        return params;
    }

    @Override
    public void exitCommand(MetroParser.CommandContext ctx) {
        log.debug("COMMAND " + ctx.getText());
        if (!inFunction(ctx)) {
            log.trace(ctx.getText() + " ANCESTORS " + getAncestors(ctx));

            // command parameters with globals
            if (ctx.arguments() != null && ctx.arguments().argument() != null) {
                log.trace("Command has " + ctx.arguments().argument().size() + " arguments");
            }
            addCommand(ctx,null);
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


    private void checkDuplicate(String name) {
        if (functions.containsKey(name)) {
            log.error("Cannot redeclare " + name + " - already exists");
            throw new DuplicateFunctionException("Function already exists");
        }
    }

    private void checkUniqueParams(MetroParser.ParametersContext paramsCtx) {
        if (paramsCtx != null) {
            Vector<String> params = new Vector<>();
            paramsCtx.parameter().forEach(s -> params.add(s.getText()));
            if (params.stream().distinct().count() != params.size()) {
                log.error("Should have parameters with unique names " + params.toString());
                throw new NonUniqueParamsException("Should have parameters with unique names");
            }
        }
    }

    @Override
    public void exitFunction(MetroParser.FunctionContext ctx) throws RuntimeException {
        log.debug("FUNCTION " + ctx.name.getText());
        checkDuplicate(ctx.name.getText());
        checkUniqueParams(ctx.parameters());
        functions.put(ctx.name.getText(), ctx);
    }

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
                    String param = parametersContext.parameter(i).ID().getText();
                    String arg = arguments.argument(i).getText();
                    log.trace("Param " + param + " should be " + arg);
                    argValues.put(param, arg);
                }

                addCommand(commandCtx,argValues);
            }
        }
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        log.trace("RULE " + ctx.getRuleContext().getText());
    }
}

