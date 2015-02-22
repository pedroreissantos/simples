
import java.io.*;

public class Byaccj implements Analyser {
	private Bparser parser;
	private String name;
	Byaccj() { this(false); }
	Byaccj(boolean debug) {
		parser = new Bparser(debug);
	}
	Byaccj(String in, boolean debug) throws IOException {
		parser = new Bparser(new FileReader(in), debug);
		name = in;
	}
	public boolean parse() { return parser.yyparse() == 0; }
	public int errors() { return parser.errors(); }
	public void accept(Compiler c) { parser.syntax().accept(c); }
	public String filename() { return name; }
}
