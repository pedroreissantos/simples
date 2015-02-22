
import java.io.*;
import java_cup.runtime.*;

public class Cup implements Analyser {
	private parser cup;
	private boolean debug;
	private String name;
	Cup () { this(false); }
	Cup (boolean debug) {
		cup = new parser(new lexer(System.in));
		this.debug = debug;
	}
	Cup (String in, boolean debug) throws IOException {
		cup = new parser(new lexer(new java.io.FileInputStream(in)));
		this.debug = debug;
		name = in;
	}
	public boolean parse() {
	  Symbol tree = null;
	  try {
	    if (debug)
	      tree = cup.debug_parse();
	    else
	      tree = cup.parse();
	  } catch (Exception e) { return false; }
	  return true;
	}
	public int errors() { return cup.errors(); }
	public void accept(Compiler c) { cup.syntax().accept(c); }
	public String filename() { return name; }
}
