#ifndef _NODEREAL_HPP_
#define _NODEREAL_HPP_

#include "Node.hpp"

class NodeReal : public Node {
protected:
	double value; // value of literal floating point
public:
	inline NodeReal(double val, int lineno = 0) : Node(lineno)
		{ value = val; }
	inline double real() { return value; }
	void accept(Compiler& c);
	void print(std::ostream *out = 0);
};
#endif /* _NODEREAL_HPP_ */
