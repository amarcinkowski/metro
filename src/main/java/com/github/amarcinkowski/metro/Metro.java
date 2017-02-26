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

    public void run(File file) throws IOException {
        MetroLexer lexer = new MetroLexer(new ANTLRFileStream(file.getAbsolutePath()));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MetroParser parser = new MetroParser(tokens);
        ParseTree tree = parser.prog();
        ParseTreeWalker walker = new ParseTreeWalker();
        metroWalker = new MetroWalker();
        walker.walk(metroWalker, tree);
        log.info(metroWalker.getCommands().toString());
    }

}
