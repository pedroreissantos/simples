/* $Id: via.c,v 1.8 2011/06/19 17:25:13 prs Exp $ */
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include "node.h"
#include "y.tab.h"
#include "tabid.h"
/*
#define viaI386GAS
#define viaAMD64
#define viaDEBUG
*/
#include "via.h"

static int lbl;
static void eval(Node *p);
static FILE *out;
static char *mklbl(int);

static int data(int type, char *name, int attrib, int user) {
  if (name == 0) return;
  fprintf(out, viaLABEL, name);		/* name variable location     */
#ifdef _64bits_
  fprintf(out, viaLONG, 0LL);		/* initialize it to 0 (zero)  */
#else
  fprintf(out, viaINTEGER, 0);		/* initialize it to 0 (zero)  */
#endif
}

void evaluate(Node *p) {
  extern char *infile, *outfile, **yynames;
  extern int errors, tree;

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
  fprintf(out, viaGLOBL, "_main", viaFUNC);
  fprintf(out, viaLABEL, "_main");
  fprintf(out, viaENTER, 0);
  eval(p);
  fprintf(out, viaIMM, 0);
  fprintf(out, viaPOP);
  fprintf(out, viaLEAVE);
  fprintf(out, viaRET);
  /* import library functions */
  fprintf(out, viaEXTRN, "_readi");
  fprintf(out, viaEXTRN, "_printi");
  fprintf(out, viaEXTRN, "_prints");
  fprintf(out, viaEXTRN, "_println");
  fprintf(out, viaEND);
  fclose(out);
}


static void eval(Node *p) {
    int i, lbl1, lbl2;
    char *name;

    if (p == 0) return;
    switch(p->attrib) {
    case INTEGER:
        fprintf(out, viaIMM, p->value.i);	/* push an integer immediate */
        break;
    case STRING:
	/* generate the string */
	fprintf(out, viaRODATA);			/* strings are DATA readonly  */
	fprintf(out, viaALIGN);			/* make sure we are aligned   */
	fprintf(out, viaLABEL, mklbl(lbl1 = ++lbl));	/* name the string    */
	fprintf(out, viaSTR, p->value.s);	/* output string characters   */
	/* make the call */
	fprintf(out, viaTEXT);			/* return to the TEXT segment */
	fprintf(out, viaADDR, mklbl(lbl1));	/* the string to be printed   */
	fprintf(out, viaCALL, "_prints");	/* call the print rotine      */
	fprintf(out, viaCALL, "_println");	/* print a newline	      */
	fprintf(out, viaTRASH, sizeof(regint));		/* remove the string address: 4/8 bytes    */
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
	fprintf(out, viaCALL, "_printi");	/* call the print function    */
	fprintf(out, viaCALL, "_println");	/* print a newline	      */
	fprintf(out, viaTRASH, sizeof(regint));		/* remove the printed value: 4/8 bytes   */
	break;
    case ';':
	for (i = 0; i < p->value.sub.num; i++)
	  eval(p->SUB(i));
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
	    default:    printf("unknown %d\n", p->attrib);
	}
    }
}

static char *mklbl(int n) {
  static char str[20];
  if (n < 0)
    sprintf(str, ".L%d", -n);
  else
    sprintf(str, "_#L%d", n);
  return str;
}
