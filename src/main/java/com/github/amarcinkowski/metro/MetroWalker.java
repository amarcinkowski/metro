package com.github.amarcinkowski.metro;

import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.*;

@Slf4j
public class MetroWalker extends MetroBaseListener {

    private Vector<String> commands = new Vector<>();
    private HashMap<String, ParseTree> functions = new HashMap<>();

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
        for(ParseTree pt : ancestors) {
            if (pt.getText().startsWith("function ")) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void exitCommand(MetroParser.CommandContext ctx) {
        log.trace("COMMAND " + ctx.getText());
        if (!inFunction(ctx)) {
            commands.add(ctx.getText());
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
            // TODO test
            log.error("Function already exists");
            throw new RuntimeException("Function already exists");
        } else {
        functions.put(ctx.name.getText(), ctx);
    }}

    @Override
    public void exitBlock(MetroParser.BlockContext ctx) {
        log.trace("BLOCK " + ctx.getText());
    }

    @Override
    public void exitGo(MetroParser.GoContext ctx) {
        log.debug("GO " + ctx.getText());
        if (!functions.containsKey(ctx.name.getText())) {
            log.error("Missing function");
            throw new RuntimeException("No function by that name");
        }
        ParseTree function = functions.get(ctx.name.getText());
        MetroParser.FunctionContext functionCtx = (MetroParser.FunctionContext) function.getPayload();
        log.trace("RUN " + functionCtx.getText());
        for (MetroParser.CommandContext commandCtx:functionCtx.block().command()) {
            commands.add(commandCtx.getText());
        }
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        log.trace("RULE " + ctx.getRuleContext().getText());
    }
}

