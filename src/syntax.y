%{
/* $Id: gram.y,v 1.4 2004/12/09 17:25:13 prs Exp $ */
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include "via.h"
#include "tabid.h"
#include "node.h"

void yyerror(char *s);
static int lbl;
static void prtstr(char*), readint(char*), loadvar(char*);
static char *mklbl(int);
static FILE *out;
extern char *outfile;
%}

%union {
	regint i;		/* integer value */
	char *s;		/* symbol name or string literal */
};

%token <i> INTEGER
%token <s> VARIABLE STRING
%token WHILE IF PRINT READ PROGRAM END
%nonassoc IFX
%nonassoc ELSE

%left GE LE EQ NE '>' '<'
%left '+' '-'
%left '*' '/' '%'
%nonassoc UMINUS

%type<i> do done then else
%%

program	: PROGRAM			{ if (outfile == 0) outfile = "out.asm";
					  if ((out = fopen(outfile, "w")) == 0)
					    { perror(outfile); exit(2); }
					  fprintf(out, viaGLOBL, "_main", viaFUNC);
					  fprintf(out, viaLABEL, "_main");
					  fprintf(out, viaSTART); }
	  list END		  	{ fprintf(out, viaIMM, 0);
	  				  fprintf(out, viaPOP);
					  fprintf(out, viaLEAVE);
					  fprintf(out, viaRET);
					  fclose(out);
					  if (yynerrs > 0) unlink(outfile); }
	;

stmt	: ';'
	| PRINT STRING ';'		  { prtstr($2); }
	| READ VARIABLE ';'		  { readint($2); }
	| PRINT expr ';'		  { fprintf(out, viaEXTRN, "_printi");
					    fprintf(out, viaCALL, "_printi");
					    fprintf(out, viaTRASH, 4);
					    fprintf(out, viaEXTRN, "_println");
					    fprintf(out, viaCALL, "_println"); }
	| VARIABLE '=' expr ';'		  { newvar($1);
					    fprintf(out, viaADDR, $1);
					    fprintf(out, viaSTORE); }
	| WHILE '(' do expr ')' done stmt { fprintf(out, viaJMP, mklbl($3));
	  				    fprintf(out, viaLABEL, mklbl($6));}
	| IF '(' expr ')' then stmt fi
	| '{' list '}'
	;

do	:			  { fprintf(out, viaLABEL, mklbl($$ = ++lbl));}
	;

done	:			  { fprintf(out, viaJZ, mklbl($$ = ++lbl)); }
	;

then	:			  { fprintf(out, viaJZ, mklbl($$ = ++lbl)); }
	;

fi	: %prec IFX		  { fprintf(out, viaLABEL, mklbl($<i>-1)); }
	| ELSE else stmt	  { fprintf(out, viaLABEL, mklbl($2)); }
	;

else	:			  { fprintf(out, viaJMP, mklbl($$ = ++lbl));
	  			    fprintf(out, viaLABEL, mklbl($<i>-2)); }
	;

list	: stmt
	| list stmt
	;

expr	: INTEGER			  { fprintf(out, viaIMM, $1); }
	| VARIABLE			  { loadvar($1); }
	| '-' expr %prec UMINUS		  { fprintf(out, viaNEG); }
	| expr '+' expr			  { fprintf(out, viaADD); }
	| expr '-' expr			  { fprintf(out, viaSUB); }
	| expr '*' expr			  { fprintf(out, viaMUL); }
	| expr '/' expr			  { fprintf(out, viaDIV); }
	| expr '%' expr			  { fprintf(out, viaMOD); }
	| expr '<' expr			  { fprintf(out, viaLT); }
	| expr '>' expr			  { fprintf(out, viaGT); }
	| expr GE expr			  { fprintf(out, viaGE); }
	| expr LE expr			  { fprintf(out, viaLE); }
	| expr NE expr			  { fprintf(out, viaNE); }
	| expr EQ expr			  { fprintf(out, viaEQ); }
	| '(' expr ')'
	;

%%
static void prtstr(char *s)
{
  fprintf(out, viaRODATA);
  fprintf(out, viaALIGN);
  fprintf(out, viaLABEL, mklbl(++lbl));
  fprintf(out, viaSTR, s);
  fprintf(out, viaTEXT);
  fprintf(out, viaADDR, mklbl(lbl));
  fprintf(out, viaEXTRN, "_prints");
  fprintf(out, viaCALL, "_prints");
  fprintf(out, viaTRASH, 4);
  fprintf(out, viaEXTRN, "_println");
  fprintf(out, viaCALL, "_println");
}

static void readint(char *s)
{
  if (IDfind(s, 0) >= 0) {
    fprintf(out, viaEXTRN, "_readi");
    fprintf(out, viaCALL, "_readi");
    fprintf(out, viaPUSH);
    fprintf(out, viaADDR, s);
    fprintf(out, viaSTORE);
  }
  else
    yynerrs++;
}

static void newvar(char *s)
{
  if (IDfind(s, (int*)IDtest) < 0) {
    IDnew(0, s, 0);
    fprintf(out, viaDATA);
    fprintf(out, viaALIGN);
    fprintf(out, viaLABEL, s);
#ifdef _64bits_
    fprintf(out, viaLONG, 0LL);
#else
    fprintf(out, viaINTEGER, 0);
#endif
    fprintf(out, viaTEXT);
  }
}

static void loadvar(char *s)
{
  if (IDfind(s, 0) >= 0) {
    fprintf(out, viaADDR, s);
    fprintf(out, viaLOAD);
  }
  else
    yynerrs++;
}

static char *mklbl(int n) {
  static char str[20];
  if (n < 0)
    sprintf(str, ".L%d", -n);
  else
    sprintf(str, "_#L%d", n);
  return str;
}
