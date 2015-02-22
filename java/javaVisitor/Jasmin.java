import java.io.*;

public class Jasmin extends Compiler {
	private Analyser parser;
	private PrintWriter out;
	private Tabid<Integer> vars;
	private int lblno;
	private int maxstack = 100;
	private int locals = 100;
	private int pos = 0;

	Jasmin() throws IOException  { this(new Byaccj()); } // read from stdin
	Jasmin(String in) throws IOException { this(in, false); }
	Jasmin(String in, boolean debug) throws IOException {
		this(new Byaccj(in, debug));
	}
	Jasmin(Analyser an) throws IOException {
		parser = an;
		out = new PrintWriter(new PrintWriter(System.out), true);
		vars = new Tabid<Integer>();
	}
	Jasmin(String in, String out) throws IOException { this(in, out, false); }
	Jasmin(String in, String out, boolean debug) throws IOException {
		this(new Byaccj(in, debug), out);
	}
	Jasmin(Analyser an, String outfile) throws IOException {
		parser = an;
		out = new PrintWriter(new FileWriter(outfile), true);
		vars = new Tabid<Integer>();
	}
	Jasmin(String args[]) throws IOException {
		String in = null, outfile = null;
		boolean lexdeb = false, syndeb = false, cup = false;
		int files = 0;

		for (int i = 0; i < args.length; i++)
			if (args[i].charAt(0) == '-') {
			        if (args[i].equals("-debug")) syndeb = true;
			        if (args[i].equals("-cup")) cup = true;
			        if (args[i].equals("-stack") && args.length > i+1)
					maxstack = Integer.parseInt(args[++i]);
			        if (args[i].equals("-locals") && args.length > i+1)
					locals = Integer.parseInt(args[++i]);
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
		Jasmin jasmin = new Jasmin(args);
		if (jasmin.parse())
			jasmin.generate();
	}

	public void stack(int max) { maxstack = max; }
	public void local(int max) { locals = max; }
	private String mklbl() { return "_L"+ ++lblno; }
	private void cprint(String cond) {
		String lbl1 = mklbl(), lbl2 = mklbl();
		out.println("  " + cond + " " + lbl2 + ":");
		out.println("  iconst_0");
		out.println("  goto " + lbl2);
		out.println(lbl1 + ":");
		out.println("  iconst_1");
		out.println(lbl2 + ":");
	}
	public boolean parse() { return parser.parse(); }
	public boolean generate() {
		if (parser.errors() > 0) return false;
		String outclass = "out";
		if (parser.filename() != null) outclass = parser.filename();
		out.println(".source " + outclass);
		out.println(".class public " + outclass);
		out.println(".super java/lang/Object");
		out.println(".method public static main([Ljava/lang/String;)V");
		out.println(".limit stack " + maxstack);
		out.println(".limit locals" + locals);
		parser.accept(this);
		out.println("  return");
		out.println(".end method");
		return true;
	}
	public void node(Node n) {}
	public void nodeInteger(NodeInteger n) {
		if (n.integer() >= 0 && n.integer() < 6)
			out.println("  iconst_" + n.integer());
		else if (n.integer() == -1)
			out.println("  iconst_m1");
		else if (n.integer() >= -128 && n.integer() < 128)
			out.println("  bipush " + n.integer());
		else	out.println("  ldc " + n.integer());
	}
	public void nodeReal(NodeReal n) {} // No reals
	public void nodeString(NodeString n) {
		out.println("  getstatic java/lang/System/out Ljava/io/PrintStream;");
		out.println("  ldc \"" + n.text() + "\"");
		out.println("  invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V");
	}
	public void nodeData(NodeData n) { } // No opaque data literals
	public void nodeUnary(NodeUnary n) { } // Use uminus operator
	public void nodeBinary(NodeBinary n) { } // Use specific operators
	public void nodeTernary(NodeTernary n) { } // Use specific operators
	public void nodeList(NodeList n) {
	  for (int i = 0; i < n.size(); i++)
	    n.elementAt(i).accept(this);
	}
	public void nodeVariable(NodeVariable n) {
		if (vars.containsKey(n.text())) {
			out.println("  iload " + n.text());
		} else System.err.println(n.text() + ": variable not found");
	}
	public void nodeWhile(NodeWhile n) {
		String lbl1 = mklbl(), lbl2 = mklbl();
		out.println(lbl1 + ":");
		n.first().accept(this);
		out.println("  ifeq " + lbl2);
		n.second().accept(this);
		out.println("  goto " + lbl1);
		out.println(lbl2 + ":");
	}
	public void nodeIf(NodeIf n) {
		String lbl1 = mklbl();
		n.first().accept(this);
		out.println("  ifeq " + lbl1);
		n.second().accept(this);
		out.println(lbl1 + ":");
	}
	public void nodeIfelse(NodeIfelse n) {
		String lbl1 = mklbl(), lbl2 = mklbl();
		n.first().accept(this);
		out.println("  ifeq " + lbl1);
		n.second().accept(this);
		out.println("  goto " + lbl2);
		out.println(lbl1 + ":");
		n.third().accept(this);
		out.println(lbl2 + ":");
	}
	public void nodeRead(NodeRead n) {
		String name = ((NodeString)n.first()).text();
		if (vars.containsKey(name)) {
			out.println("  invokestatic Runtime/atoi()I");
			out.println("  istore " + vars.get(name));
		}
		else System.err.println(name + ": variable not found");
	}
	public void nodePrint(NodePrint n) {
		out.println("  getstatic java/lang/System/out Ljava/io/PrintStream;");
		n.first().accept(this);
		out.println("  invokevirtual java/io/PrintStream/println(I)V");
	}
	public void nodeAssign(NodeAssign n) {
		String name = ((NodeString)n.first()).text();
		n.second().accept(this);	// determine the new value
		if (!vars.containsKey(name)) // variable not found ?
			vars.put(name, new Integer(++pos)); // create the var
		out.println("  istore " + vars.get(name));
	}
	public void nodeUminus(NodeUminus n) {
		n.first().accept(this);		// determine the new value
		out.println("  ineg");		// make the 2-compliment
	}
	public void nodeAdd(NodeAdd n) {
		n.first().accept(this);
		n.second().accept(this);
		out.println("  iadd ");
	}
	public void nodeSub(NodeSub n) {
		n.first().accept(this);
		n.second().accept(this);
		out.println("  isub ");
	}
	public void nodeMul(NodeMul n) {
		n.first().accept(this);
		n.second().accept(this);
		out.println("  imul ");
	}
	public void nodeDiv(NodeDiv n) {
		n.first().accept(this);
		n.second().accept(this);
		out.println("  idiv ");
	}
	public void nodeMod(NodeMod n) {
		n.first().accept(this);
		n.second().accept(this);
		out.println("  irem ");
	}
	public void nodeEq(NodeEq n) {
		n.first().accept(this);
		n.second().accept(this);
		cprint("  if_cmpeq");
	}
	public void nodeNe(NodeNe n) {
		n.first().accept(this);
		n.second().accept(this);
		cprint("  if_cmpne");
	}
	public void nodeGe(NodeGe n) {
		n.first().accept(this);
		n.second().accept(this);
		cprint("  if_cmpge");
	}
	public void nodeGt(NodeGt n) {
		n.first().accept(this);
		n.second().accept(this);
		cprint("  if_cmpgt");
	}
	public void nodeLe(NodeLe n) {
		n.first().accept(this);
		n.second().accept(this);
		cprint("  if_cmple");
	}
	public void nodeLt(NodeLt n) {
		n.first().accept(this);
		n.second().accept(this);
		cprint("  if_cmplt");
	}
}
