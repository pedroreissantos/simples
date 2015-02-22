#ifndef _NODEMOD_HPP_
#define _NODEMOD_HPP_

#include "NodeBinary.hpp"

class NodeMod : public NodeBinary {
public:
	inline NodeMod(Node& arg1, Node& arg2, int lineno = 0) :
		NodeBinary(arg1, arg2, lineno) { }
	void accept(Compiler& c);
	void print(std::ostream *out = 0);
};
#endif /* _NODEMOD_HPP_ */
