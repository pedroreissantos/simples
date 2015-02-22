#include <string>
#define String std::string
#include "Compiler.hpp"
#include "Simples.hpp"
#include "Interp.hpp"
#include "string.h"


int main(int argc, char *argv[]) {
  Compiler *compiler;

  if (argc > 1 && strcmp(argv[1], "-I") == 0)
	if (argc > 2)
  		compiler = new Interp(String(argv[2]));
	else
		compiler = new Interp(&std::cin);
  else
  	compiler = new Simples(String(argv[1]));

  if (compiler->parse() == false) return 2;
  int ret = compiler->generate() == false;

  delete compiler;
  return ret;
}
