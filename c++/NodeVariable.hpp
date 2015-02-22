#ifndef _NODEVARIABLE_HPP_
#define _NODEVARIABLE_HPP_

#include "NodeString.hpp"

class NodeVariable : public NodeString {
public:
	inline NodeVariable(std::string val, int lineno = 0) :
		NodeString(val, lineno) { }
	void accept(Compiler& c);
	void print(std::ostream *out = 0);
};
#endif /* _NODEVARIABLE_HPP_ */
