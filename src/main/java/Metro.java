import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.*;

/**
 * Created by amarcinkowski on 26.02.17.
 */
@Slf4j
public class Metro {

    public void run(String file) throws IOException {
        InputStream is = new FileInputStream(file);
        Reader r = new InputStreamReader(is, "UTF-8"); // e.g., euc-jp or utf-8
        MetroLexer lexer = new MetroLexer(new ANTLRFileStream(file));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MetroParser parser = new MetroParser(tokens);
        ParseTree tree = parser.prog();
        ParseTreeWalker walker = new ParseTreeWalker();
        MetroWalker metroWalker = new MetroWalker();
        walker.walk(metroWalker, tree);
        log.info(metroWalker.getCommands().toString());
    }

}
