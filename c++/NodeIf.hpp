#ifndef _NODEIF_HPP_
#define _NODEIF_HPP_

#include "NodeBinary.hpp"

class NodeIf : public NodeBinary {
public:
	inline NodeIf(Node& arg1, Node& arg2, int lineno = 0) :
		NodeBinary(arg1, arg2, lineno) { }
	void accept(Compiler& c);
	void print(std::ostream *out = 0);
};
#endif /* _NODEIF_HPP_ */
