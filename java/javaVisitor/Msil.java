import java.io.*;

public class Msil extends Compiler {
	private Analyser parser;
	private PrintWriter out;
	private Tabid<Integer> vars;
	private int lblno;
	private int maxstack = 100;

	Msil() throws IOException  { this(new Byaccj()); } // read from stdin
	Msil(String in) throws IOException { this(in, false); }
	Msil(String in, boolean debug) throws IOException {
		this(new Byaccj(in, debug));
	}
	Msil(Analyser an) throws IOException {
		parser = an;
		out = new PrintWriter(new PrintWriter(System.out), true);
		vars = new Tabid<Integer>();
	}
	Msil(String in, String out) throws IOException { this(in, out, false); }
	Msil(String in, String out, boolean debug) throws IOException {
		this(new Byaccj(in, debug), out);
	}
	Msil(Analyser an, String outfile) throws IOException {
		parser = an;
		out = new PrintWriter(new FileWriter(outfile), true);
		vars = new Tabid<Integer>();
	}
	Msil(String args[]) throws IOException {
		String in = null, outfile = null;
		boolean lexdeb = false, syndeb = false, cup = false;
		int files = 0;

		for (int i = 0; i < args.length; i++)
			if (args[i].charAt(0) == '-') {
			        if (args[i].equals("-debug")) syndeb = true;
			        if (args[i].equals("-cup")) cup = true;
			        if (args[i].equals("-stack") && args.length > i+1)
					maxstack = Integer.parseInt(args[++i]);
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
		Msil msil = new Msil(args);
		if (msil.parse())
			msil.generate();
	}

	public void stack(int max) { maxstack = max; }
	private String mklbl() { return "$L"+ ++lblno; }
	private void cprint(String cond) {
		String lbl1 = mklbl(), lbl2 = mklbl();
		out.println("  " + cond + " " + lbl2 + ":");
		out.println("  ldc.i4.0");
		out.println("  br " + lbl2);
		out.println(lbl1 + ":");
		out.println("  ldc.i4.1");
		out.println(lbl2 + ":");
	}
	public boolean parse() { return parser.parse(); }
	public boolean generate() {
		if (parser.errors() > 0) return false;
		String outclass = "out";
		if (parser.filename() != null) outclass = parser.filename();
		out.println(".assembly _" + outclass + " {}");
		out.println(".class public _" + outclass + " {");
		out.println(".method public static void Main(string[] args) {");
		out.println(".entrypoint");
		out.println(".maxstack " + maxstack);
		parser.accept(this);
		out.println("  ret");
		out.println(" }");
		out.println("}");
		return true;
	}
	public void node(Node n) {}
	public void nodeInteger(NodeInteger n) {
		out.println("  ldc.i4 " + n.integer()); }
	public void nodeReal(NodeReal n) {} // No reals
	public void nodeString(NodeString n) {
		out.println("  ldstr \"" + n.text() + "\"");
		out.println("call void [mscorlib]System.Console::WriteLine(string)");
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
			out.println("  ldloc " + n.text());
		} else System.err.println(n.text() + ": variable not found");
	}
	public void nodeWhile(NodeWhile n) {
		String lbl1 = mklbl(), lbl2 = mklbl();
		out.println(lbl1 + ":");
		n.first().accept(this);
		out.println("  brfalse " + lbl2);
		n.second().accept(this);
		out.println("  br " + lbl1);
		out.println(lbl2 + ":");
	}
	public void nodeIf(NodeIf n) {
		String lbl1 = mklbl();
		n.first().accept(this);
		out.println("  brfalse " + lbl1);
		n.second().accept(this);
		out.println(lbl1 + ":");
	}
	public void nodeIfelse(NodeIfelse n) {
		String lbl1 = mklbl(), lbl2 = mklbl();
		n.first().accept(this);
		out.println("  brfalse " + lbl1);
		n.second().accept(this);
		out.println("  br " + lbl2);
		out.println(lbl1 + ":");
		n.third().accept(this);
		out.println(lbl2 + ":");
	}
	public void nodeRead(NodeRead n) {
		String name = ((NodeString)n.first()).text();
		if (vars.containsKey(name)) {
		out.println("  call string [mscorlib]System.Console::ReadLine()");
		out.println("  call int32 [mscorlib]System.Int32::Parse(string)");
		out.println("  stloc " + name);
		}
		else System.err.println(name + ": variable not found");
	}
	public void nodePrint(NodePrint n) {
		n.first().accept(this);
		out.println("  call void [mscorlib]System.Console::WriteLine(int32)");
	}
	public void nodeAssign(NodeAssign n) {
		String name = ((NodeString)n.first()).text();
		n.second().accept(this);	// determine the new value
		if (!vars.containsKey(name)) // variable not found ?
			vars.put(name, new Integer(0));	// create the var
		out.println("  stloc " + name);
	}
	public void nodeUminus(NodeUminus n) {
		n.first().accept(this);		// determine the new value
		out.println("  neg ");		// make the 2-compliment
	}
	public void nodeAdd(NodeAdd n) {
		n.first().accept(this);
		n.second().accept(this);
		out.println("  add ");
	}
	public void nodeSub(NodeSub n) {
		n.first().accept(this);
		n.second().accept(this);
		out.println("  sub ");
	}
	public void nodeMul(NodeMul n) {
		n.first().accept(this);
		n.second().accept(this);
		out.println("  mul ");
	}
	public void nodeDiv(NodeDiv n) {
		n.first().accept(this);
		n.second().accept(this);
		out.println("  div ");
	}
	public void nodeMod(NodeMod n) {
		n.first().accept(this);
		n.second().accept(this);
		out.println("  rem ");
	}
	public void nodeEq(NodeEq n) {
		n.first().accept(this);
		n.second().accept(this);
		out.println("  ceq ");
	}
	public void nodeNe(NodeNe n) {
		n.first().accept(this);
		n.second().accept(this);
		cprint("bne");
	}
	public void nodeGe(NodeGe n) {
		n.first().accept(this);
		n.second().accept(this);
		cprint("bge");
	}
	public void nodeGt(NodeGt n) {
		n.first().accept(this);
		n.second().accept(this);
		out.println("  cgt ");
	}
	public void nodeLe(NodeLe n) {
		n.first().accept(this);
		n.second().accept(this);
		cprint("ble");
	}
	public void nodeLt(NodeLt n) {
		n.first().accept(this);
		n.second().accept(this);
		out.println("  clt ");
	}
}
