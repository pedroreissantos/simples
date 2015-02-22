#ifndef _SIMPLES_HPP_
#define _SIMPLES_HPP_

#include <string>
#define String std::string
#include "Compiler.hpp"
#include "Parser.hpp"
#include "PFi386.hpp"
#include "Tabid.hpp"

class Simples : public Compiler {
	Parser *parser;
	Postfix *pf;
	Tabid vars;
public:
	/*
	static void main(String args[]) throws IOException {
		Simples lang;

		if (args.length > 1)
			lang = new Simples(args);
		else
			lang = new Simples(args[0]);

		lang.parser.yyparse();
		if (lang.parser.errors() == 0)
			lang.generate();
	}
	Simples(String args[]) throws IOException {
		String in = null, out = null;
		boolean lexdeb = false, syndeb = false, treeprt = false;
		boolean debug = false;
		int files;

		for (int i; i < args.length; i++)
			if (args[i].charAt(0) == '-') {
				if (args[i].equals("-lexical")) lexdeb = true;
				if (args[i].equals("-syntax")) syndeb = true;
				if (args[i].equals("-tree")) treeprt = true;
			        if (args[i].equals("-debug")) debug = true;
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
		parser = new Parser(new FileReader(in), syndeb);
		if (debug)
		  	pf = new PFdebug(out == null ? extension(in, ".stk") : out);
		else
		  	pf = new PFi386(out == null ? extension(in, ".asm") : out);
		vars = new Tabid();
	}
	*/
	Simples(String in, bool debug = false) {
		parser = new Parser(new std::ifstream(in.c_str()));
		pf = new PFi386(extension(in, ".asm"));
	}
	bool parse();
	bool generate();
	void node(Node& n);
	void nodeInteger(NodeInteger& n);
	void nodeReal(NodeReal& n);
	void nodeString(NodeString& n);
	void nodeData(NodeData& n);
	void nodeUnary(NodeUnary& n);
	void nodeBinary(NodeBinary& n);
	void nodeTernary(NodeTernary& n);
	void nodeList(NodeList& n);

	void nodePrint(NodePrint& n);
	void nodeRead(NodeRead& n);
	void nodeVariable(NodeVariable& n);
	void nodeAssign(NodeAssign& n);
	void nodeIf(NodeIf& n);
	void nodeIfelse(NodeIfelse& n);
	void nodeWhile(NodeWhile& n);
	void nodeAdd(NodeAdd& n);
	void nodeSub(NodeSub& n);
	void nodeMul(NodeMul& n);
	void nodeDiv(NodeDiv& n);
	void nodeMod(NodeMod& n);
	void nodeEq(NodeEq& n);
	void nodeNe(NodeNe& n);
	void nodeGt(NodeGt& n);
	void nodeGe(NodeGe& n);
	void nodeLt(NodeLt& n);
	void nodeLe(NodeLe& n);
	void nodeUminus(NodeUminus& n);
};
#endif /* _SIMPLES_HPP_ */
