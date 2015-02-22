#ifndef _NODEEQ_HPP_
#define _NODEEQ_HPP_

#include "NodeBinary.hpp"

class NodeEq : public NodeBinary {
public:
	inline NodeEq(Node& arg1, Node& arg2, int lineno = 0) :
		NodeBinary(arg1, arg2, lineno) { }
	void accept(Compiler& c);
	void print(std::ostream *out = 0);
};
#endif /* _NODEEQ_HPP_ */
