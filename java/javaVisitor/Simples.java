import java.io.*;

public class Simples {
	private Compiler comp;

	public static void main(String args[]) throws IOException {
		Simples lang = new Simples(args);
		if (lang.parse())
			lang.generate();
	}
	Simples(String args[]) throws IOException {
		String in = null, out = null;
		boolean syndeb = false, tree = false, interp = false;
		boolean jasmin = false, msil = false, sexp = false;
		boolean debug = false, cup = false, antlr = false;
		int files = 0;
		Analyser parser;

		for (int i = 0; i < args.length; i++)
			if (args[i].charAt(0) == '-') {
				if (args[i].equals("-syntax")) syndeb = true;
				if (args[i].equals("-tree")) tree = true;
				if (args[i].equals("-sexp")) sexp = true;
				if (args[i].equals("-interp")) interp = true;
				if (args[i].equals("-msil")) msil = true;
				if (args[i].equals("-jasmin")) jasmin = true;
			        if (args[i].equals("-debug")) debug = true;
			        if (args[i].equals("-cup")) cup = true;
			        if (args[i].equals("-antlr")) antlr = true;
			}
			else {
				if (++files == 1) in = args[i];
				if (files == 2) out = args[i];
				if (files > 2)
					System.err.println(args[i] + ": too many arguments.");
			}

		if (in == null) {
			System.err.println("Reading from stdin ...");
			if (antlr) parser = new Antlr(syndeb);
			else if (cup) parser = new Cup(syndeb);
			else parser = new Byaccj(syndeb);
		} else {
			if (antlr) parser = new Antlr(in, syndeb);
			else if (cup) parser = new Cup(in, syndeb);
			else parser = new Byaccj(in, syndeb);
		}

		if (jasmin) {
			if (out == null) comp = new Jasmin(parser);
			else comp = new Jasmin(parser, out);
		} else if (msil) {
			if (out == null) comp = new Msil(parser);
			else comp = new Msil(parser, out);
		} else if (sexp) {
			if (out == null) comp = new Sexp(parser);
			else comp = new Sexp(parser, out);
		} else if (tree) {
			if (out == null) comp = new Tree(parser);
			else comp = new Tree(parser, out);
		} else if (interp) comp = new Interp(parser);
		else comp = new Via(parser, out, debug);
	}
	public boolean parse() { return comp.parse(); }
	public boolean generate() { return comp.generate(); }
}
