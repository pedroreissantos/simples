import java.io.*;

public class Via extends Compiler {
	private Analyser parser;
	private PrintWriter out;
	private VIAopcodes via;
	private Tabid<Integer> vars;

	Via() throws IOException  { this(new Byaccj()); } // read from stdin
	Via(String in) throws IOException { this(in, false); }
	Via(String in, boolean debug) throws IOException {
		this(new Byaccj(in, debug));
	}
	Via(Analyser an) throws IOException {
		parser = an;
		out = new PrintWriter(new PrintWriter(System.out), true);
		vars = new Tabid<Integer>();
	}
	Via(String in, String out) throws IOException { this(in, out, false); }
	Via(String in, String out, boolean debug) throws IOException {
		this(new Byaccj(in, debug), out, false);
	}
	Via(Analyser an, String outfile, boolean symbol) throws IOException {
		String in = "out";
		parser = an;
		if (an.filename() != null) in = an.filename();
		if (symbol)
		  	via = new VIAdebug(outfile == null ? extension(in, ".stk") : outfile);
		else
		  	via = new VIAi386(outfile == null ? extension(in, ".asm") : outfile);
		vars = new Tabid<Integer>();
	}
	Via(String args[]) throws IOException {
		String in = null, out = null;
		boolean syndeb = false, debug = false, cup = false;
		int files = 0;

		for (int i = 0; i < args.length; i++)
			if (args[i].charAt(0) == '-') {
				if (args[i].equals("-debug")) syndeb = true;
			        if (args[i].equals("-bytecode")) debug = true;
			        if (args[i].equals("-cup")) cup = true;
			}
			else {
				if (++files == 1) in = args[i];
				if (files == 2) out = args[i];
				if (files > 2)
					System.err.println(args[i] + ": too many arguments.");
			}

		if (in == null) {
			System.err.println("No input file.");
			System.exit(1);
		}
		if (cup) parser = new Cup(in, syndeb);
		else parser = new Byaccj(in, syndeb);

		if (debug)
		  	via = new VIAdebug(out == null ? extension(in, ".stk") : out);
		else
		  	via = new VIAi386(out == null ? extension(in, ".asm") : out);
		vars = new Tabid<Integer>();
	}

	public static void main(String args[]) throws IOException {
		Via via = new Via(args);
		if (via.parse())
			via.generate();
	}

	public boolean parse() { return parser.parse(); }
	public boolean generate() {
		if (parser.errors() > 0) return false;
		via.TEXT();
		via.ALIGN();
		via.GLOBLfunc("_main");
		via.LABEL("_main");
		via.START();
		parser.accept(this);
		via.INT(0);
		via.POP();
		via.LEAVE();
		via.RET();
		// import library functions
		via.EXTRN("readi");
		via.EXTRN("printi");
		via.EXTRN("prints");
		via.EXTRN("println");
		return true;
	}
	public void node(Node n) {}
	public void nodeInteger(NodeInteger n) { via.INT(n.integer()); }
	public void nodeReal(NodeReal n) {} // No reals
	public void nodeString(NodeString n) {
		String lbl = via.label();
		via.RODATA();			// strings are DATA readonly
		via.ALIGN();			// make sure we are aligned
		via.LABEL(lbl);			// give the string a name
		via.STR(n.text());		// output string characters
		// make the call
		via.TEXT();			// return to the TEXT segment
		via.ADDR(lbl);			// the string to be printed
		via.CALL("prints");		// call the print rotine
		via.CALL("println");		// print a newline
		via.TRASH(4);			// remove the string argument
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
	  		via.ADDR(n.text());
	  		via.LOAD();
		}
	}
	public void nodeWhile(NodeWhile n) {
		String lbl1 = via.label(), lbl2 = via.label();
		via.LABEL(lbl1);
		n.first().accept(this);
		via.JZ(lbl2);
		n.second().accept(this);
		via.JMP(lbl1);
		via.LABEL(lbl2);
	}
	public void nodeIf(NodeIf n) {
		String lbl1 = via.label();
		n.first().accept(this);
		via.JZ(lbl1);
		n.second().accept(this);
		via.LABEL(lbl1);
	}
	public void nodeIfelse(NodeIfelse n) {
		String lbl1 = via.label(), lbl2 = via.label();
		n.first().accept(this);
		via.JZ(lbl1);
		n.second().accept(this);
		via.JMP(lbl2);
		via.LABEL(lbl1);
		n.third().accept(this);
		via.LABEL(lbl2);
	}
	public void nodeRead(NodeRead n) {
		String name = ((NodeString)n.first()).text();
		if (vars.containsKey(name)) {
			via.CALL("readi");
			via.PUSH();
			via.ADDR(name);
			via.STORE();
		}
		else
			System.err.println(name + ": variable not found");
	}
	public void nodePrint(NodePrint n) {
		n.first().accept(this);
		via.CALL("printi");		// call the print function
		via.CALL("println");		// print a newline
		via.TRASH(4);			// delete the printed value
	}
	public void nodeAssign(NodeAssign n) {
		String name = ((NodeString)n.first()).text();
		if (!vars.containsKey(name)) {	// variable not found ?
			via.DATA();		// variables are DATA
			via.ALIGN();		// make sure we are aligned
			via.LABEL(name);		// name variable location
			via.CONST(0);		// initialize it to 0 (zero)
			via.TEXT();		// return to the TEXT segment
			vars.put(name, new Integer(0));	// create the var
		}
		n.second().accept(this);	// determine the new value
		via.ADDR(name);			// where to store the value
		via.STORE();			// store the value at address
	}
	public void nodeUminus(NodeUminus n) {
		n.first().accept(this);		// determine the new value
		via.NEG();			// make the 2-compliment
	}
	public void nodeAdd(NodeAdd n) {
		n.first().accept(this);
		n.second().accept(this);
		via.ADD();
	}
	public void nodeSub(NodeSub n) {
		n.first().accept(this);
		n.second().accept(this);
		via.SUB();
	}
	public void nodeMul(NodeMul n) {
		n.first().accept(this);
		n.second().accept(this);
		via.MUL();
	}
	public void nodeDiv(NodeDiv n) {
		n.first().accept(this);
		n.second().accept(this);
		via.DIV();
	}
	public void nodeMod(NodeMod n) {
		n.first().accept(this);
		n.second().accept(this);
		via.MOD();
	}
	public void nodeEq(NodeEq n) {
		n.first().accept(this);
		n.second().accept(this);
		via.EQ();
	}
	public void nodeNe(NodeNe n) {
		n.first().accept(this);
		n.second().accept(this);
		via.NE();
	}
	public void nodeGe(NodeGe n) {
		n.first().accept(this);
		n.second().accept(this);
		via.GE();
	}
	public void nodeGt(NodeGt n) {
		n.first().accept(this);
		n.second().accept(this);
		via.GT();
	}
	public void nodeLe(NodeLe n) {
		n.first().accept(this);
		n.second().accept(this);
		via.LE();
	}
	public void nodeLt(NodeLt n) {
		n.first().accept(this);
		n.second().accept(this);
		via.LT();
	}
}
