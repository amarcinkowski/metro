package com.github.amarcinkowski.metro;

import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.*;

@Slf4j
public class Metro {

    private MetroWalker metroWalker;

    private void print() {
        log.info(metroWalker.getCommands().toString());
    }

    private void walk(ParseTree tree) {
        ParseTreeWalker walker = new ParseTreeWalker();
        metroWalker = new MetroWalker();
        walker.walk(metroWalker, tree);
    }

    private void parse(MetroLexer lexer) {
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MetroParser parser = new MetroParser(tokens);
        ParseTree tree = parser.prog();
        walk(tree);
    }

    public void run(File file) throws IOException {
        MetroLexer lexer = new MetroLexer(new ANTLRFileStream(file.getAbsolutePath()));
        parse(lexer);
        print();
    }

}
