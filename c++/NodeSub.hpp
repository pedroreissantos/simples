#ifndef _NODESUB_HPP_
#define _NODESUB_HPP_

#include "NodeBinary.hpp"

class NodeSub : public NodeBinary {
public:
	inline NodeSub(Node& arg1, Node& arg2, int lineno = 0) :
		NodeBinary(arg1, arg2, lineno) { }
	void accept(Compiler& c);
	void print(std::ostream *out = 0);
};
#endif /* _NODESUB_HPP_ */
