#ifndef _SYMBOLNODE_HPP_
#define _SYMBOLNODE_HPP_

#include "Symbol.hpp"

class SymbolNode : public Symbol {
	int val;
public:
	inline SymbolNode(int v = 0) { val = v; }
	char *name();
	int integer();
};

#endif /* _SYMBOL_HPP_ */
