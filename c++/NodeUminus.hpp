#ifndef _NODEUMINUS_HPP_
#define _NODEUMINUS_HPP_

#include "NodeUnary.hpp"

class NodeUminus : public NodeUnary {
public:
	inline NodeUminus(Node& arg1, int lineno = 0) : NodeUnary(arg1, lineno)
		{ }
	void accept(Compiler& c);
	void print(std::ostream *out = 0);
};
#endif /* _NODEUMINUS_HPP_ */
