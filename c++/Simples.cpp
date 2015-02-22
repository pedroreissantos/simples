#include "Simples.hpp"
#include "Node.hpp"
#include "NodeInteger.hpp"
#include "NodeReal.hpp"
#include "NodeString.hpp"
#include "NodeData.hpp"
#include "NodeUnary.hpp"
#include "NodeBinary.hpp"
#include "NodeTernary.hpp"
#include "NodeList.hpp"

#include "NodePrint.hpp"
#include "NodeRead.hpp"
#include "NodeVariable.hpp"
#include "NodeAssign.hpp"
#include "NodeIf.hpp"
#include "NodeIfelse.hpp"
#include "NodeWhile.hpp"
#include "NodeAdd.hpp"
#include "NodeSub.hpp"
#include "NodeMul.hpp"
#include "NodeDiv.hpp"
#include "NodeMod.hpp"
#include "NodeEq.hpp"
#include "NodeNe.hpp"
#include "NodeGt.hpp"
#include "NodeGe.hpp"
#include "NodeLt.hpp"
#include "NodeLe.hpp"
#include "NodeUminus.hpp"

#include "SymbolNode.hpp"

bool Simples::parse() {
	if (parser->parse() == 0)
		tree(parser->syntax());
	return parser->errors() == 0;
}
bool Simples::generate() {
	if (parser->errors() > 0) return false;
	pf->TEXT();
	pf->ALIGN();
	pf->GLOBLfunc("_main");
	pf->LABEL("_main");
	pf->START();
	tree()->accept(*this);
	pf->INT(0);
	pf->POP();
	pf->LEAVE();
	pf->RET();
	// import library functions
	pf->EXTRN("readi");
	pf->EXTRN("printi");
	pf->EXTRN("prints");
	pf->EXTRN("println");
	return true;
}
void Simples::node(Node& n) {}
void Simples::nodeInteger(NodeInteger& n) { pf->INT(n.integer()); }
void Simples::nodeReal(NodeReal& n) {} // No reals
void Simples::nodeString(NodeString& n) {
	String lbl = pf->label();
	pf->RODATA();			// strings are DATA readonly
	pf->ALIGN();			// make sure we are aligned
	pf->LABEL(lbl);			// give the string a name
	pf->STR(n.text());		// output string characters
	// make the call
	pf->TEXT();			// return to the TEXT segment
	pf->ADDR(lbl);			// the string to be printed
	pf->CALL("prints");		// call the print rotine
	pf->CALL("println");		// print a newline
	pf->TRASH(4);			// remove the string argument
}
void Simples::nodeData(NodeData& n) { } // No opaque data literals
void Simples::nodeUnary(NodeUnary& n) { } // Use uminus operator
void Simples::nodeBinary(NodeBinary& n) { } // Use specific operators
void Simples::nodeTernary(NodeTernary& n) { } // Use specific operators
void Simples::nodeList(NodeList& n) {
  for (int i = 0; i < n.size(); i++)
    n.elementAt(i).accept(*this);
}
void Simples::nodeVariable(NodeVariable& n) {
	if (vars.containsKey(n.text())) {
		pf->ADDR(n.text());
		pf->LOAD();
	}
}
void Simples::nodeWhile(NodeWhile& n) {
	String lbl1 = pf->label(), lbl2 = pf->label();
	pf->LABEL(lbl1);
	n.first().accept(*this);
	pf->JZ(lbl2);
	n.second().accept(*this);
	pf->JMP(lbl1);
	pf->LABEL(lbl2);
}
void Simples::nodeIf(NodeIf& n) {
	String lbl1 = pf->label();
	n.first().accept(*this);
	pf->JZ(lbl1);
	n.second().accept(*this);
	pf->LABEL(lbl1);
}
void Simples::nodeIfelse(NodeIfelse& n) {
	String lbl1 = pf->label(), lbl2 = pf->label();
	n.first().accept(*this);
	pf->JZ(lbl1);
	n.second().accept(*this);
	pf->JMP(lbl2);
	pf->LABEL(lbl1);
	n.third().accept(*this);
	pf->LABEL(lbl2);
}
void Simples::nodeRead(NodeRead& n) {
	String name = ((NodeString*)&n.first())->text();
	if (vars.containsKey(name)) {
		pf->CALL("readi");
		pf->PUSH();
		pf->ADDR(name);
		pf->STORE();
	}
	else
		std::cerr << name + ": variable not found";
}
void Simples::nodePrint(NodePrint& n) {
	n.first().accept(*this);
	pf->CALL("printi");		// call the print function
	pf->CALL("println");		// print a newline
	pf->TRASH(4);			// delete the printed value
}
void Simples::nodeAssign(NodeAssign& n) {
	String name = ((NodeString*)&n.first())->text();
	if (!vars.containsKey(name)) {	// variable not found ?
		pf->DATA();		// variables are DATA
		pf->ALIGN();		// make sure we are aligned
		pf->LABEL(name);		// name variable location
		pf->CONST(0);		// initialize it to 0 (zero)
		pf->TEXT();		// return to the TEXT segment
		vars.put(name, new SymbolNode());	// create the var
	}
	n.second().accept(*this);	// determine the new value
	pf->ADDR(name);			// where to store the value
	pf->STORE();			// store the value at address
}
void Simples::nodeUminus(NodeUminus& n) {
	n.first().accept(*this);		// determine the new value
	pf->NEG();			// make the 2-compliment
}
void Simples::nodeAdd(NodeAdd& n) {
	n.first().accept(*this);
	n.second().accept(*this);
	pf->ADD();
}
void Simples::nodeSub(NodeSub& n) {
	n.first().accept(*this);
	n.second().accept(*this);
	pf->SUB();
}
void Simples::nodeMul(NodeMul& n) {
	n.first().accept(*this);
	n.second().accept(*this);
	pf->MUL();
}
void Simples::nodeDiv(NodeDiv& n) {
	n.first().accept(*this);
	n.second().accept(*this);
	pf->DIV();
}
void Simples::nodeMod(NodeMod& n) {
	n.first().accept(*this);
	n.second().accept(*this);
	pf->MOD();
}
void Simples::nodeEq(NodeEq& n) {
	n.first().accept(*this);
	n.second().accept(*this);
	pf->EQ();
}
void Simples::nodeNe(NodeNe& n) {
	n.first().accept(*this);
	n.second().accept(*this);
	pf->NE();
}
void Simples::nodeGe(NodeGe& n) {
	n.first().accept(*this);
	n.second().accept(*this);
	pf->GE();
}
void Simples::nodeGt(NodeGt& n) {
	n.first().accept(*this);
	n.second().accept(*this);
	pf->GT();
}
void Simples::nodeLe(NodeLe& n) {
	n.first().accept(*this);
	n.second().accept(*this);
	pf->LE();
}
void Simples::nodeLt(NodeLt& n) {
	n.first().accept(*this);
	n.second().accept(*this);
	pf->LT();
}
