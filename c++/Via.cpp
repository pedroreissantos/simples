#include "Postfix.hpp"

String Postfix::label() { lbl++; return "_L" + lbl; }

void Postfix::text(String str) {
	int len = str.size();
	for (int i = 0; i < len; i++)
		CHAR(str[i]);
	CHAR(0);
}
