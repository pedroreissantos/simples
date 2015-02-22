#ifndef _NODEASSIGN_HPP_
#define _NODEASSIGN_HPP_

#include "NodeBinary.hpp"

class NodeAssign : public NodeBinary {
public:
	inline NodeAssign(Node& arg1, Node& arg2, int lineno = 0)
		: NodeBinary(arg1, arg2, lineno) { }
	void accept(Compiler& c);
	void print(std::ostream *out = 0);
};
#endif /* _NODEASSIGN_HPP_ */
