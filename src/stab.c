/* $Id: code.c,v 1.7 2004/12/09 17:25:13 prs Exp $ */
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include "node.h"
#include "y.tab.h"
#include "tabid.h"
#define viaI386GAS
#include "via.h"

#define MAIN "__main"

static int lbl;
static void eval(Node *p);
static FILE *out;
static char *mklbl(int);

void stab(FILE *out, int decl, char *name, int val)
{
  char *lb;
  static char *type[] = {
    /* 0 */		"",
    /* 1 - INT */	"=r1;-2147483648;2147483647;",
    /* 2 - CHAR */	"=r2;-128;127;",
    /* 3 - LONG */	"=r3;-2147483648;2147483647;",
    /* 4 - SHORT */	"=r4;-32768;32767;",
    /* 5 - UCHAR */	"=r5;0;255;",
    /* 6 - USHORT */	"=r6;0;65535;",
    /* 7 - ULONG */	"=r7;0;4294967296;",
    /* 8 - UINT */	"=r8;0;4294967296;",
    /* 9 - FLOAT */	"=r9;1.17549435E-38;3.40282347e+38;",
    /* 10 - DOUBLE */	"=r10;2.2250738585072014E-308;1.7976931348623157E+308;",
    /* ... */		0
  };

  switch (decl) {
    case 32: /* N_GSYM: global variable (val is type) */
	fprintf(out, ".stabs \"%s:G%d\",%d,0,%d,0\n", name, val, decl, val);
	break;
    case 36: /* N_FUN: global function (val is type) */
	if (*(lb = name) == '_') lb++;
	fprintf(out, ".stabs \"%s:F%d\",%d,0,0,%s\n", lb, val, decl /* ,line */, name);
	break;
    case 68: /* N_SLINE: line number in .text (val is line; name is func) */
	lb = mklbl(- ++lbl);
	fprintf(out, ".stabn %d,0,%d,%s-%s\n%s:\n", decl, val, lb, name, lb);
	break;
    case 100: /* N_SO: name of source file */
	lb = mklbl(- ++lbl);
	fprintf(out, ".stabs \"%s\",%d,0,0,%s\n%s:\n", name, decl, lb, lb);
	break;
    case 128: /* N_LSYM: type descriptions (or variable in stack) */
	fprintf(out, ".stabs \"%s:t%d%s\",%d,0,0,0\n", name, val, type[val], decl);
	break;
    default:
	fprintf(stderr, "stab: unknown declaration %d\n", decl);
	break;
  }
}

static int data(int type, char *name, int attrib, int user) {
  if (name == 0) return;
  fprintf(out, viaLABEL, name);		/* name variable location     */
  fprintf(out, viaINTEGER, 0);		/* initialize it to 0 (zero)  */
  stab(out, 32, name, 1);
}

void evaluate(Node *p) {
  extern char *outfile, **yynames, *infile;
  extern int errors, tree, yylineno;

  if (errors > 0) return;
  if (tree > 0) { printNode(p, stdout, yynames); return; }
  if (outfile == 0) outfile = "out.asm";
  if ((out = fopen(outfile, "w")) == 0) {
    perror(outfile);
    exit(2);
  }
  fprintf(out, viaBEGIN, infile);
  fprintf(out, viaDATA);
  fprintf(out, viaALIGN);
  IDevery(data, 0); /* reserve space for variables */
  fprintf(out, viaTEXT);
  fprintf(out, viaALIGN);
  stab(out, 100, infile, 0);
  stab(out, 128, "inteiro", 1);
  fprintf(out, viaGLOBL, MAIN);
  fprintf(out, viaLABEL, MAIN);
  stab(out, 36, MAIN, 1);
  fprintf(out, viaENTER, 0);
  stab(out, 68, MAIN, 1);
  eval(p);
  stab(out, 68, MAIN, yylineno);
  fprintf(out, viaINT, 0);
  fprintf(out, viaPOP);
  fprintf(out, viaLEAVE);
  fprintf(out, viaRET);
  stab(out, 100, "", 0);
  /* import library functions */
  fprintf(out, viaEXTRN, "_readi");
  fprintf(out, viaEXTRN, "_printi");
  fprintf(out, viaEXTRN, "_prints");
  fprintf(out, viaEXTRN, "_println");
  fprintf(out, viaEND);
  fclose(out);
}


static void eval(Node *p) {
    extern char **yynames;
    int i, lbl1, lbl2;
    char *name;

    if (p == 0) return;
    switch(p->attrib) {
    case INTEGER:
        fprintf(out, viaINT, p->value.i);		/* push an integer	      */
        break;
    case STRING:
	/* generate the string */
	fprintf(out, viaRODATA);			/* strings are DATA readonly  */
	fprintf(out, viaALIGN);			/* make sure we are aligned   */
	fprintf(out, viaLABEL, mklbl(lbl1 = ++lbl));	/* give the string a name     */
	fprintf(out, viaSTR, p->value.s);		/* output string characters   */
	/* make the call */
	fprintf(out, viaTEXT);			/* return to the TEXT segment */
	fprintf(out, viaADDR, mklbl(lbl1));		/* the string to be printed   */
	fprintf(out, viaCALL, "_prints");		/* call the print rotine      */
	fprintf(out, viaCALL, "_println");		/* print a newline	      */
	fprintf(out, viaTRASH, 4);			/* remove the string label    */
        break;
    case VARIABLE:
	fprintf(out, viaADDR, p->value.s);
	fprintf(out, viaLOAD);
	break;
    case WHILE:
	fprintf(out, viaLABEL, mklbl(lbl1 = ++lbl));
	eval(p->SUB(0));
	fprintf(out, viaJZ, mklbl(lbl2 = ++lbl));
	eval(p->SUB(1));
	fprintf(out, viaJMP, mklbl(lbl1));
	fprintf(out, viaLABEL, mklbl(lbl2));
	break;
    case IF:
	eval(p->SUB(0));
	fprintf(out, viaJZ, mklbl(lbl1 = ++lbl));
	eval(p->SUB(1));
	if (p->value.sub.num > 2) { /* if else */
	    fprintf(out, viaJMP, mklbl(lbl2 = ++lbl));
	    fprintf(out, viaLABEL, mklbl(lbl1));
	    eval(p->SUB(2));
	    lbl1 = lbl2;
	}
	fprintf(out, viaLABEL, mklbl(lbl1));
	break;
    case READ:
	fprintf(out, viaCALL, "_readi");
	fprintf(out, viaPUSH);
	fprintf(out, viaADDR, p->value.s);
	fprintf(out, viaSTORE);
	break;
    case PRINT:
	eval(p->SUB(0));			/* determine the value        */
	fprintf(out, viaCALL, "_printi");		/* call the print function    */
	fprintf(out, viaCALL, "_println");		/* print a newline	      */
	fprintf(out, viaTRASH, 4);			/* delete the printed value   */
	break;
    case ';':
	for (i = 0; i < p->value.sub.num; i++) {
	  int line = p->SUB(i)->line;
	  if (p->SUB(i)->attrib == IF || p->SUB(i)->attrib == WHILE)
	    line = p->SUB(i)->SUB(0)->line;
	  /* printf("line=%d (%s)\n", line, yynames[p->SUB(i)->attrib]); */
	  stab(out, 68, MAIN, line);
	  eval(p->SUB(i));
	}
	break;
    case '=':
	name = p->SUB(0)->value.s;
	eval(p->SUB(1));			/* determine the new value    */
	fprintf(out, viaADDR, name);		/* where to store the value   */
	fprintf(out, viaSTORE);			/* store the value at address */
	break;
    case UMINUS:
	eval(p->SUB(0));			/* determine the value	      */
	fprintf(out, viaNEG);			/* make the 2-compliment      */
	break;
    default:
	eval(p->SUB(0));			/* evaluate first argument    */
	eval(p->SUB(1));			/* evaluate second argument   */
	switch(p->attrib) {			/* make the operation ...     */
	case '+':   fprintf(out, viaADD); break;
	case '-':   fprintf(out, viaSUB); break;
	case '*':   fprintf(out, viaMUL); break;
	case '/':   fprintf(out, viaDIV); break;
	case '%':   fprintf(out, viaMOD); break;
	case '<':   fprintf(out, viaLT); break;
	case '>':   fprintf(out, viaGT); break;
	case GE:    fprintf(out, viaGE); break;
	case LE:    fprintf(out, viaLE); break;
	case NE:    fprintf(out, viaNE); break;
	case EQ:    fprintf(out, viaEQ); break;
	default:    printf("unknown %d ('%c')\n", p->attrib, p->attrib);
	}
    }
}

static char *mklbl(int n) {
  static char str[20];
  if (n < 0)
    sprintf(str, ".L%d", -n);
  else
    sprintf(str, "_L%d", n);
  return str;
}
