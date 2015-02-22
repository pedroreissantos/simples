import java.io.*;

public class Sexp extends Compiler {
	private Analyser parser;
	private PrintWriter out;
	private Tabid<Integer> vars;
	private int pos = 0;
	private int col = 0;
	private int max = 78;
	private int step = 2;

	Sexp() throws IOException  { this(new Byaccj()); } // read from stdin
	Sexp(String in) throws IOException { this(in, false); }
	Sexp(String in, boolean debug) throws IOException {
		this(new Byaccj(in, debug));
	}
	Sexp(Analyser an) throws IOException {
		parser = an;
		out = new PrintWriter(new PrintWriter(System.out), true);
		vars = new Tabid<Integer>();
	}
	Sexp(String in, String out) throws IOException { this(in, out, false); }
	Sexp(String in, String out, boolean debug) throws IOException {
		this(new Byaccj(in, debug), out);
	}
	Sexp(Analyser an, String outfile) throws IOException {
		parser = an;
		out = new PrintWriter(new FileWriter(outfile), true);
		vars = new Tabid<Integer>();
	}
	Sexp(String args[]) throws IOException {
		String in = null, outfile = null;
		boolean lexdeb = false, syndeb = false, cup = false;
		int files = 0;

		for (int i = 0; i < args.length; i++)
			if (args[i].charAt(0) == '-') {
			        if (args[i].equals("-debug")) syndeb = true;
			        if (args[i].equals("-cup")) cup = true;
			        if (args[i].equals("-indent") && args.length > i+1)
					step = Integer.parseInt(args[++i]);
			        if (args[i].equals("-col") && args.length > i+1)
					max = Integer.parseInt(args[++i]);
			}
			else {
				if (++files == 1) in = args[i];
				if (files == 2) outfile = args[i];
				if (files > 2)
					System.err.println(args[i] + ": too many arguments.");
			}

		if (in == null) {
			System.err.println("Reading from stdin ...");
			if (cup) parser = new Cup(syndeb);
			else parser = new Byaccj(syndeb);
		} else {
			if (cup) parser = new Cup(in, syndeb);
			else parser = new Byaccj(in, syndeb);
		}

		if (outfile == null)
			out = new PrintWriter(new PrintWriter(System.out), true);
		else
			out = new PrintWriter(new FileWriter(outfile), true);

		vars = new Tabid<Integer>();
	}

	public static void main(String args[]) throws IOException {
		Sexp tree = new Sexp(args);
		if (tree.parse())
			tree.generate();
	}

	public void maxcol(int mcol) { max = mcol; }
	public void step(int s) { step = s; }
	public void print(String s) {
		col += s.length();
		if (col > max && (max > 0 || s.charAt(0) == '(') ) {
		  out.println("");
		  for (int i = 0; i < pos * step; i++) out.print(" ");
		  col = pos * step;
		}
		out.print(s);
	}
	public boolean parse() { return parser.parse(); }
	public boolean generate() {
		if (parser.errors() > 0) return false;
		print("(program "); pos++;
		parser.accept(this);
		out.println(")"); pos--;
		return true;
	}
	public void node(Node n) {}
	public void nodeInteger(NodeInteger n) {
		print(" " + n.integer()); }
	public void nodeReal(NodeReal n) {} // No reals
	public void nodeString(NodeString n) {
		print(" \"" + n.text() + "\"");
	}
	public void nodeData(NodeData n) { } // No opaque data literals
	public void nodeUnary(NodeUnary n) { } // Use uminus operator
	public void nodeBinary(NodeBinary n) { } // Use specific operators
	public void nodeTernary(NodeTernary n) { } // Use specific operators
	public void nodeList(NodeList n) {
		print("( "); pos++;
	  	for (int i = 0; i < n.size(); i++) {
	    		n.elementAt(i).accept(this);
		}
		print(")"); pos--;
	}
	public void nodeVariable(NodeVariable n) {
		if (vars.containsKey(n.text())) {
			print(" " + n.text());
		} else System.err.println(n.text() + ": variable not found");
	}
	public void nodeWhile(NodeWhile n) {
		print("( while "); pos++;
		n.first().accept(this);
		n.second().accept(this);
		print(")"); pos--;
	}
	public void nodeIf(NodeIf n) {
		print("( if "); pos++;
		n.first().accept(this);
		n.second().accept(this);
		print(")"); pos--;
	}
	public void nodeIfelse(NodeIfelse n) {
		print("(ifelse "); pos++;
		n.first().accept(this);
		n.second().accept(this);
		n.third().accept(this);
		print(")"); pos--;
	}
	public void nodeRead(NodeRead n) {
		String name = ((NodeString)n.first()).text();
		if (vars.containsKey(name)) {
			print("( read " + name + ")");
		} else System.err.println(name + ": variable not found");
	}
	public void nodePrint(NodePrint n) {
		print("( print "); pos++;
		n.first().accept(this);
		print(")"); pos--;
	}
	public void nodeAssign(NodeAssign n) {
		String name = ((NodeString)n.first()).text();
		if (!vars.containsKey(name)) // variable not found ?
			vars.put(name, new Integer(0));	// create the var
		print("( = " + name); pos++;
		n.second().accept(this);
		print(")"); pos--;
	}
	public void nodeUminus(NodeUminus n) {
		print("( -"); pos++;
		n.first().accept(this);
		print(")"); pos--;
	}
	public void nodeAdd(NodeAdd n) {
		print("( +"); pos++;
		n.first().accept(this);
		n.second().accept(this);
		print(")"); pos--;
	}
	public void nodeSub(NodeSub n) {
		print("( -"); pos++;
		n.first().accept(this);
		n.second().accept(this);
		print(")"); pos--;
	}
	public void nodeMul(NodeMul n) {
		print("( *"); pos++;
		n.first().accept(this);
		n.second().accept(this);
		print(")"); pos--;
	}
	public void nodeDiv(NodeDiv n) {
		print("( /"); pos++;
		n.first().accept(this);
		n.second().accept(this);
		print(")"); pos--;
	}
	public void nodeMod(NodeMod n) {
		print("( %"); pos++;
		n.first().accept(this);
		n.second().accept(this);
		print(")"); pos--;
	}
	public void nodeEq(NodeEq n) {
		print("( =="); pos++;
		n.first().accept(this);
		n.second().accept(this);
		print(")"); pos--;
	}
	public void nodeNe(NodeNe n) {
		print("( !="); pos++;
		n.first().accept(this);
		n.second().accept(this);
		print(")"); pos--;
	}
	public void nodeGe(NodeGe n) {
		print("( >="); pos++;
		n.first().accept(this);
		n.second().accept(this);
		print(")"); pos--;
	}
	public void nodeGt(NodeGt n) {
		print("( >"); pos++;
		n.first().accept(this);
		n.second().accept(this);
		print(")"); pos--;
	}
	public void nodeLe(NodeLe n) {
		print("( <="); pos++;
		n.first().accept(this);
		n.second().accept(this);
		print(")"); pos--;
	}
	public void nodeLt(NodeLt n) {
		print("( <"); pos++;
		n.first().accept(this);
		n.second().accept(this);
		print(")"); pos--;
	}
}
