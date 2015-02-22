import java.io.*;

public class Tree extends Compiler {
	private Analyser parser;
	private PrintWriter out;
	private Tabid<Integer> vars;
	private int pos = 1;
	private int step = 2;

	Tree() throws IOException  { this(new Byaccj()); } // read from stdin
	Tree(String in) throws IOException { this(in, false); }
	Tree(String in, boolean debug) throws IOException {
		this(new Byaccj(in, debug));
	}
	Tree(Analyser an) throws IOException {
		parser = an;
		out = new PrintWriter(new PrintWriter(System.out), true);
		vars = new Tabid<Integer>();
	}
	Tree(String in, String out) throws IOException { this(in, out, false); }
	Tree(String in, String out, boolean debug) throws IOException {
		this(new Byaccj(in, debug), out);
	}
	Tree(Analyser an, String outfile) throws IOException {
		parser = an;
		out = new PrintWriter(new FileWriter(outfile), true);
		vars = new Tabid<Integer>();
	}
	Tree(String args[]) throws IOException {
		String in = null, outfile = null;
		boolean lexdeb = false, syndeb = false, cup = false;
		int files = 0;

		for (int i = 0; i < args.length; i++)
			if (args[i].charAt(0) == '-') {
			        if (args[i].equals("-debug")) syndeb = true;
			        if (args[i].equals("-cup")) cup = true;
			        if (args[i].equals("-indent") && args.length > i+1)
					step = Integer.parseInt(args[++i]);
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
		Tree tree = new Tree(args);
		if (tree.parse())
			tree.generate();
	}

	public void indent() { for (int i = 0; i < pos * step; i++) out.print(" "); }
	public void step(int s) { step = s; }
	public boolean parse() { return parser.parse(); }
	public boolean generate() {
		if (parser.errors() > 0) return false;
		out.println("program # generated by Tree");
		indent();
		parser.accept(this);
		out.println(""); // to remove indent
		out.println("end");
		return true;
	}
	public void node(Node n) {}
	public void nodeInteger(NodeInteger n) {
		out.print(" " + n.integer()); }
	public void nodeReal(NodeReal n) {} // No reals
	public void nodeString(NodeString n) {
		out.print(" \"" + n.text() + "\"");
	}
	public void nodeData(NodeData n) { } // No opaque data literals
	public void nodeUnary(NodeUnary n) { } // Use uminus operator
	public void nodeBinary(NodeBinary n) { } // Use specific operators
	public void nodeTernary(NodeTernary n) { } // Use specific operators
	public void nodeList(NodeList n) {
	  	for (int i = 0; i < n.size(); i++) {
	    		n.elementAt(i).accept(this);
			out.println(";");
			indent();
		}
	}
	public void nodeVariable(NodeVariable n) {
		if (vars.containsKey(n.text())) {
			out.print(" " + n.text());
		} else System.err.println(n.text() + ": variable not found");
	}
	public void nodeWhile(NodeWhile n) {
		out.print("while (");
		n.first().accept(this);
		out.println(" ) {");
		pos++; indent();
		n.second().accept(this);
		out.println("}");
		pos--; indent();
	}
	public void nodeIf(NodeIf n) {
		out.print("if (");
		n.first().accept(this);
		out.println(" ) {");
		pos++; indent();
		n.second().accept(this);
		out.println("}");
		pos--; indent();
	}
	public void nodeIfelse(NodeIfelse n) {
		out.print("if (");
		n.first().accept(this);
		out.println(" ) {");
		pos++; indent();
		n.second().accept(this);
		out.println("else {");
		pos++; indent();
		n.third().accept(this);
		out.println("}");
		pos--; indent();
	}
	public void nodeRead(NodeRead n) {
		String name = ((NodeString)n.first()).text();
		if (vars.containsKey(name)) {
			out.print("read " + name + ";");
		} else System.err.println(name + ": variable not found");
	}
	public void nodePrint(NodePrint n) {
		out.print("print");
		n.first().accept(this);
		out.print(";");
	}
	public void nodeAssign(NodeAssign n) {
		String name = ((NodeString)n.first()).text();
		if (!vars.containsKey(name)) // variable not found ?
			vars.put(name, new Integer(0));	// create the var
		out.print(name + " =");
		n.second().accept(this); // determine the new value
	}
	public void nodeUminus(NodeUminus n) {
		out.print(" -");		// make the 2-compliment
		n.first().accept(this);		// determine the new value
	}
	public void nodeAdd(NodeAdd n) {
		n.first().accept(this);
		out.print(" +");
		n.second().accept(this);
	}
	public void nodeSub(NodeSub n) {
		n.first().accept(this);
		out.print(" -");
		n.second().accept(this);
	}
	public void nodeMul(NodeMul n) {
		n.first().accept(this);
		out.print(" *");
		n.second().accept(this);
	}
	public void nodeDiv(NodeDiv n) {
		n.first().accept(this);
		out.print(" /");
		n.second().accept(this);
	}
	public void nodeMod(NodeMod n) {
		n.first().accept(this);
		out.print(" %");
		n.second().accept(this);
	}
	public void nodeEq(NodeEq n) {
		n.first().accept(this);
		out.print(" ==");
		n.second().accept(this);
	}
	public void nodeNe(NodeNe n) {
		n.first().accept(this);
		out.print(" !=");
		n.second().accept(this);
	}
	public void nodeGe(NodeGe n) {
		n.first().accept(this);
		out.print(" >=");
		n.second().accept(this);
	}
	public void nodeGt(NodeGt n) {
		n.first().accept(this);
		out.print(" >");
		n.second().accept(this);
	}
	public void nodeLe(NodeLe n) {
		n.first().accept(this);
		out.print(" <=");
		n.second().accept(this);
	}
	public void nodeLt(NodeLt n) {
		n.first().accept(this);
		out.print(" <");
		n.second().accept(this);
	}
}
