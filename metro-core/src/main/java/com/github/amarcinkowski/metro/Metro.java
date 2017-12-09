package com.github.amarcinkowski.metro;

import com.github.amarcinkowski.metro.exceptions.MissingMetroSourceException;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import javax.swing.*;
import java.io.*;
import java.util.Arrays;

@Slf4j
public class Metro {

    private MetroWalker metroWalker;
    private MetroParser parser;
    private ParseTree tree;

    private void print() {
        log.info(metroWalker.getCommands().toString());
    }

    public void show() {
        JFrame frame = new JFrame("Antlr AST");
        JPanel panel = new JPanel();
        TreeViewer viewer = new TreeViewer(Arrays.asList(
                parser.getRuleNames()),tree);
        panel.add(viewer);
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,300);
        frame.setVisible(true);
    }

    private void walk(ParseTree tree) {
        ParseTreeWalker walker = new ParseTreeWalker();
        metroWalker = new MetroWalker();
        walker.walk(metroWalker, tree);
    }

    private void parse(MetroLexer lexer) {
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        parser = new MetroParser(tokens);
        tree = parser.prog();
        walk(tree);
    }

    public void run(File file) throws MissingMetroSourceException {
        ANTLRFileStream stream;
        try {
            stream = new ANTLRFileStream(file.getAbsolutePath());
        } catch (IOException e) {
            log.info("Missing metro source file " + file.getName() + " at " + file.getAbsolutePath());
            throw new MissingMetroSourceException("Missing file " + file.getName());
        }
        MetroLexer lexer = new MetroLexer(stream);
        parse(lexer);
        print();
        if (Math.random() > 1) {
            show();
        }
    }

}
