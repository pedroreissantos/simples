#ifndef _NODELE_HPP_
#define _NODELE_HPP_

#include "NodeBinary.hpp"

class NodeLe : public NodeBinary {
public:
	inline NodeLe(Node& arg1, Node& arg2, int lineno = 0) :
		NodeBinary(arg1, arg2, lineno) { }
	void accept(Compiler& c);
	void print(std::ostream *out = 0);
};
#endif /* _NODELE_HPP_ */
