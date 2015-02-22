import java.io.*;
import java.util.*;

public class Interp extends Compiler {
	private Analyser parser;
	private PrintWriter out;
	private StreamTokenizer in;
	private Stack<NodeInteger> stk;
	private Tabid<NodeInteger> vars;

	Interp() throws IOException  { this(new Byaccj()); } // read from stdin
	Interp(String in) throws IOException { this(in, false); }
	Interp(String in, boolean debug) throws IOException {
		this(new Byaccj(in, debug));
	}
	Interp(Analyser an) throws IOException {
		parser = an;
		Writer outp = new PrintWriter(System.out);
		out = new PrintWriter(outp, true);
		Reader inp = new InputStreamReader(System.in);
		in = new StreamTokenizer(inp);
		in.eolIsSignificant(true);
		stk = new Stack<NodeInteger>();
		vars = new Tabid<NodeInteger>();
	}
	Interp(String args[]) throws IOException {
		String infile = null;
		boolean lexdeb = false, syndeb = false, cup = false;
		int files = 0;

		for (int i = 0; i < args.length; i++)
			if (args[i].charAt(0) == '-') {
				if (args[i].equals("-lexical")) lexdeb = true;
			        if (args[i].equals("-debug")) syndeb = true;
			        if (args[i].equals("-cup")) cup = true;
			}
			else {
				if (++files == 1) infile = args[i];
				if (files > 1)
					System.err.println(args[i] + ": too many arguments.");
			}

		if (infile == null) {
			System.err.println("Reading from stdin ...");
			if (cup) parser = new Cup(syndeb);
			else parser = new Byaccj(syndeb);
		} else {
			if (cup) parser = new Cup(infile, syndeb);
			else parser = new Byaccj(infile, syndeb);
		}

		Writer outp = new PrintWriter(System.out);
		out = new PrintWriter(outp, true);
		Reader inp = new InputStreamReader(System.in);
		in = new StreamTokenizer(inp);
		in.eolIsSignificant(true);
		stk = new Stack<NodeInteger>();
		vars = new Tabid<NodeInteger>();
	}

	public static void main(String args[]) throws IOException {
		Interp interp = new Interp(args[0]);
		if (interp.parse())
			interp.generate();
	}

	public boolean parse() { return parser.parse(); }
	public boolean generate() {
		if (parser.errors() > 0) return false;
		parser.accept(this);
		return true;
	}
	public void node(Node n) {}
	public void nodeInteger(NodeInteger n) { stk.push(n); }
	public void nodeReal(NodeReal n) {} // No reals
	public void nodeString(NodeString n) { out.println(n.text()); }
	public void nodeData(NodeData n) { } // No opaque data literals
	public void nodeUnary(NodeUnary n) { } // Use uminus operator
	public void nodeBinary(NodeBinary n) { } // Use specific operators
	public void nodeTernary(NodeTernary n) { } // Use specific operators
	public void nodeList(NodeList n) {
	  for (int i = 0; i < n.size(); i++)
	    n.elementAt(i).accept(this);
	}
	public void nodeVariable(NodeVariable n) {
		stk.push(vars.get(n.text()));
	}
	public void nodeWhile(NodeWhile n) {
		n.first().accept(this); // eval condition to TOS
		while (((NodeInteger)stk.pop()).integer() != 0) {
			n.second().accept(this); // eval instruction
			n.first().accept(this); // eval condition, again...
		}
	}
	public void nodeIf(NodeIf n) {
		n.first().accept(this); // eval condition to TOS
		if (((NodeInteger)stk.pop()).integer() != 0)
			n.second().accept(this); // eval instruction
	}
	public void nodeIfelse(NodeIfelse n) {
		n.first().accept(this); // eval condition to TOS
		if (((NodeInteger)stk.pop()).integer() != 0)
			n.second().accept(this); // eval then
		else
			n.third().accept(this); // eval else
	}
	public void nodeRead(NodeRead n) {
		String name = ((NodeString)n.first()).text();
		if (vars.containsKey(name)) {
		    try {
			while (in.nextToken() != StreamTokenizer.TT_EOF) {
			    if (in.ttype == StreamTokenizer.TT_NUMBER) {
				vars.put(name, new NodeInteger((int)in.nval));
				break;
			    }
			    if (in.ttype != StreamTokenizer.TT_EOL)
				System.err.println("Parse error at line "+in.lineno());
		        }
		    } catch (IOException e) {
		    	System.err.println("IO error: " + e);
			System.exit(2);
		    }
		}
		else
		    System.err.println(name + ": variable not found");
	}
	public void nodePrint(NodePrint n) {
		n.first().accept(this); // eval condition to TOS
		out.println(((NodeInteger)stk.pop()).integer());
	}
	public void nodeAssign(NodeAssign n) {
		n.second().accept(this); // eval expression to TOS
		vars.put(((NodeString)n.first()).text(), stk.pop());
	}
	public void nodeUminus(NodeUminus n) {
		n.first().accept(this); // eval condition to TOS
		stk.push(new NodeInteger(-((NodeInteger)stk.pop()).integer()));
	}
	public void nodeAdd(NodeAdd n) {
		n.first().accept(this); // eval condition to TOS
		int arg1 = ((NodeInteger)stk.pop()).integer();
		n.second().accept(this); // eval expression to TOS
		int arg2 = ((NodeInteger)stk.pop()).integer();
		stk.push(new NodeInteger(arg1 + arg2));
	}
	public void nodeSub(NodeSub n) {
		n.first().accept(this); // eval condition to TOS
		int arg1 = ((NodeInteger)stk.pop()).integer();
		n.second().accept(this); // eval expression to TOS
		int arg2 = ((NodeInteger)stk.pop()).integer();
		stk.push(new NodeInteger(arg1 - arg2));
	}
	public void nodeMul(NodeMul n) {
		n.first().accept(this); // eval condition to TOS
		int arg1 = ((NodeInteger)stk.pop()).integer();
		n.second().accept(this); // eval expression to TOS
		int arg2 = ((NodeInteger)stk.pop()).integer();
		stk.push(new NodeInteger(arg1 * arg2));
	}
	public void nodeDiv(NodeDiv n) {
		n.first().accept(this); // eval condition to TOS
		int arg1 = ((NodeInteger)stk.pop()).integer();
		n.second().accept(this); // eval expression to TOS
		int arg2 = ((NodeInteger)stk.pop()).integer();
		stk.push(new NodeInteger(arg1 / arg2));
	}
	public void nodeMod(NodeMod n) {
		n.first().accept(this); // eval condition to TOS
		int arg1 = ((NodeInteger)stk.pop()).integer();
		n.second().accept(this); // eval expression to TOS
		int arg2 = ((NodeInteger)stk.pop()).integer();
		stk.push(new NodeInteger(arg1 % arg2));
	}
	public void nodeEq(NodeEq n) {
		n.first().accept(this); // eval condition to TOS
		int arg1 = ((NodeInteger)stk.pop()).integer();
		n.second().accept(this); // eval expression to TOS
		int arg2 = ((NodeInteger)stk.pop()).integer();
		stk.push(new NodeInteger(arg1 == arg2 ? 1 : 0));
	}
	public void nodeNe(NodeNe n) {
		n.first().accept(this); // eval condition to TOS
		int arg1 = ((NodeInteger)stk.pop()).integer();
		n.second().accept(this); // eval expression to TOS
		int arg2 = ((NodeInteger)stk.pop()).integer();
		stk.push(new NodeInteger(arg1 != arg2 ? 1 : 0));
	}
	public void nodeGe(NodeGe n) {
		n.first().accept(this); // eval condition to TOS
		int arg1 = ((NodeInteger)stk.pop()).integer();
		n.second().accept(this); // eval expression to TOS
		int arg2 = ((NodeInteger)stk.pop()).integer();
		stk.push(new NodeInteger(arg1 >= arg2 ? 1 : 0));
	}
	public void nodeGt(NodeGt n) {
		n.first().accept(this); // eval condition to TOS
		int arg1 = ((NodeInteger)stk.pop()).integer();
		n.second().accept(this); // eval expression to TOS
		int arg2 = ((NodeInteger)stk.pop()).integer();
		stk.push(new NodeInteger(arg1 > arg2 ? 1 : 0));
	}
	public void nodeLe(NodeLe n) {
		n.first().accept(this); // eval condition to TOS
		int arg1 = ((NodeInteger)stk.pop()).integer();
		n.second().accept(this); // eval expression to TOS
		int arg2 = ((NodeInteger)stk.pop()).integer();
		stk.push(new NodeInteger(arg1 <= arg2 ? 1 : 0));
	}
	public void nodeLt(NodeLt n) {
		n.first().accept(this); // eval condition to TOS
		int arg1 = ((NodeInteger)stk.pop()).integer();
		n.second().accept(this); // eval expression to TOS
		int arg2 = ((NodeInteger)stk.pop()).integer();
		stk.push(new NodeInteger(arg1 < arg2 ? 1 : 0));
	}
}
