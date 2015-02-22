#ifndef _NODE_HPP_
#define _NODE_HPP_

#include <fstream>
#include "Compiler.hpp"

class Compiler;

class Node {
	int	line; // user defined attributes
public:
	inline Node(int lineno = 0) { line = lineno; }
	virtual ~Node();
	inline int lineno() { return line; }
	virtual void accept(Compiler& c);
	virtual void print(std::ostream *out = 0);
};
#endif /* _NODE_HPP_ */
