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
        new Metro().run("src/main/resources/simple.metro");
    }

    public static int max(int a, int b) {
        return a > b ? a : b;
    }
}
