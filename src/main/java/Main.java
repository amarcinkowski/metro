import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

/**
 * Created by amarcinkowski on 26.02.17.
 */
public class Main {
    public static void main(String[] args) throws IOException {

        MetroLexer lexer = new MetroLexer(new ANTLRFileStream(args[0]));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MetroParser parser = new MetroParser(tokens);
        ParseTree tree = parser.prog();
        ParseTreeWalker walker = new ParseTreeWalker();
        MetroWalker metroWalker = new MetroWalker();
        walker.walk(metroWalker, tree);
        System.out.println(metroWalker.getCommands());
    }

    public static int max(int a, int b) {
        return a > b ? a : b;
    }
}
