
import java.io.*;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class Antlr implements Analyser {
	private boolean debug;
	private String name;
	private Reader reader;
	private int errors;
	private Node tree;
	Antlr() { this(false); }
	Antlr(boolean deb) {
		reader = new InputStreamReader(System.in);
		debug = deb;
	}
	Antlr(String in, boolean deb) throws IOException {
		debug = deb;
		reader = new FileReader(in);
		name = in;
	}
	public boolean parse() {
	    System.err.println("Antlr3 parsing...");
	    try {
		simplesLexer lexer = new simplesLexer(
					new ANTLRReaderStream(reader));
		simplesParser tokenParser = new simplesParser(
					new CommonTokenStream(lexer));
		tree = tokenParser.start();
		reader.close();
	    } catch (Exception e) {
		System.err.println(e); // debug
	    	errors = 1; // where is the error counter?
		return false;
	    }
	    return true;
	}
	public int errors() { return errors; }
	public void accept(Compiler c) { tree.accept(c); }
	public String filename() { return name; }
}

