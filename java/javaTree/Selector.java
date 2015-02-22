/*
generated at Wed Feb 12 21:16:19 2014
by $Id: pburg.c,v 2.4a 2014/02/12 15:33:16 prs Exp $
*/

import java.io.*;

public   class Selector  {

public static final String PBURG_PREFIX="Selector";
public static final String PBURG_VERSION="2.4a";
public static final int MAX_COST=0x7fff;
public void panic(String rot, String msg, int val) {
	System.err.println("Internal Error in "+rot+": "+msg+" "+val+"\nexiting...");
	System.exit(2);
}
private static final String ntname[] = {
	"",
	"stat",
	"cond",
	"reg",
	"then",
	"body",
	"begin",
	""
};

private static final short stat_NT=1;
private static final short cond_NT=2;
private static final short reg_NT=3;
private static final short then_NT=4;
private static final short body_NT=5;
private static final short begin_NT=6;

private static final String termname[] = {
 "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
 "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
 "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
 "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
 "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
 "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
 "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
 "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
 "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
 "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
 "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
 "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
 "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
 "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
 "", "", "", "",
	/* 257 */ "YYERRCODE",
	/* 258 */ "INTEGER",
	/* 259 */ "VARIABLE",
	/* 260 */ "STRING",
	/* 261 */ "WHILE",
	/* 262 */ "IF",
	/* 263 */ "PRINT",
	/* 264 */ "READ",
	/* 265 */ "PROGRAM",
	/* 266 */ "END",
	/* 267 */ "IFX",
	/* 268 */ "ELSE",
	/* 269 */ "GE",
	/* 270 */ "LE",
	/* 271 */ "EQ",
	/* 272 */ "NE",
	/* 273 */ "UMINUS",
	/* 274 */ "GT",
	/* 275 */ "LT",
	/* 276 */ "ADD",
	/* 277 */ "SUB",
	/* 278 */ "MUL",
	/* 279 */ "DIV",
	/* 280 */ "MOD",
	/* 281 */ "ASSIGN",
	/* 282 */ "DO",
	/* 283 */ "START",
		""
};

private static final short nts_[] = { 0 };
private static final short nts_0[] = { stat_NT, stat_NT, 0 };
private static final short nts_1[] = { 0 };
private static final short nts_2[] = { cond_NT, stat_NT, 0 };
private static final short nts_3[] = { reg_NT, 0 };
private static final short nts_4[] = { then_NT, stat_NT, 0 };
private static final short nts_5[] = { body_NT, reg_NT, 0 };
private static final short nts_6[] = { begin_NT, stat_NT, 0 };
private static final short nts_7[] = { reg_NT, reg_NT, 0 };

private static final short nts[][] = {
	nts_,	/* 0 */
	nts_0,	/* 1 */
	nts_1,	/* 2 */
	nts_2,	/* 3 */
	nts_3,	/* 4 */
	nts_4,	/* 5 */
	nts_2,	/* 6 */
	nts_5,	/* 7 */
	nts_6,	/* 8 */
	nts_1,	/* 9 */
	nts_1,	/* 10 */
	nts_3,	/* 11 */
	nts_1,	/* 12 */
	nts_3,	/* 13 */
	nts_3,	/* 14 */
	nts_1,	/* 15 */
	nts_1,	/* 16 */
	nts_7,	/* 17 */
	nts_7,	/* 18 */
	nts_3,	/* 19 */
	nts_7,	/* 20 */
	nts_7,	/* 21 */
	nts_7,	/* 22 */
	nts_7,	/* 23 */
	nts_7,	/* 24 */
	nts_7,	/* 25 */
	nts_7,	/* 26 */
	nts_7,	/* 27 */
	nts_7,	/* 28 */
};


private static final String string[] = {
/* 0 */	"",
/* 1 */	"stat: PROGRAM(stat,stat)",
/* 2 */	"stat: END",
/* 3 */	"stat: IF(cond,stat)",
/* 4 */	"cond: reg",
/* 5 */	"stat: ELSE(then,stat)",
/* 6 */	"then: IF(cond,stat)",
/* 7 */	"stat: WHILE(body,reg)",
/* 8 */	"body: DO(begin,stat)",
/* 9 */	"begin: START",
/* 10 */	"stat: STRING",
/* 11 */	"stat: PRINT(reg)",
/* 12 */	"stat: READ",
/* 13 */	"stat: ASSIGN(VARIABLE,reg)",
/* 14 */	"stat: reg",
/* 15 */	"reg: VARIABLE",
/* 16 */	"reg: INTEGER",
/* 17 */	"reg: ADD(reg,reg)",
/* 18 */	"reg: SUB(reg,reg)",
/* 19 */	"reg: UMINUS(reg)",
/* 20 */	"reg: MUL(reg,reg)",
/* 21 */	"reg: DIV(reg,reg)",
/* 22 */	"reg: MOD(reg,reg)",
/* 23 */	"reg: EQ(reg,reg)",
/* 24 */	"reg: NE(reg,reg)",
/* 25 */	"reg: LT(reg,reg)",
/* 26 */	"reg: LE(reg,reg)",
/* 27 */	"reg: GE(reg,reg)",
/* 28 */	"reg: GT(reg,reg)",
};

public void trace(Tree p, int eruleno, int cost, int bestcost)
{
	int op = p.label();
	String tname = termname[op] != null ? termname[op] : "?";
	System.err.println(p.hashCode()+":"+tname+" matched "+string[eruleno]+" with cost "+cost+" vs. "+bestcost);
}

private void kids(Tree p, int eruleno, Tree kids[]) {
	if (p == null)
		panic("kids", "Null tree in rule", eruleno);
	if (kids == null)
		panic("kids", "Null kids in", p.label());
	switch (eruleno) {
	case 28: // reg: GT(reg,reg)
	case 27: // reg: GE(reg,reg)
	case 26: // reg: LE(reg,reg)
	case 25: // reg: LT(reg,reg)
	case 24: // reg: NE(reg,reg)
	case 23: // reg: EQ(reg,reg)
	case 22: // reg: MOD(reg,reg)
	case 21: // reg: DIV(reg,reg)
	case 20: // reg: MUL(reg,reg)
	case 18: // reg: SUB(reg,reg)
	case 17: // reg: ADD(reg,reg)
	case 8: // body: DO(begin,stat)
	case 7: // stat: WHILE(body,reg)
	case 6: // then: IF(cond,stat)
	case 5: // stat: ELSE(then,stat)
	case 3: // stat: IF(cond,stat)
	case 1: // stat: PROGRAM(stat,stat)
		kids[0] = p.left();
		kids[1] = p.right();
		break;
	case 16: // reg: INTEGER
	case 15: // reg: VARIABLE
	case 12: // stat: READ
	case 10: // stat: STRING
	case 9: // begin: START
	case 2: // stat: END
		break;
	case 14: // stat: reg
	case 4: // cond: reg
		kids[0] = p;
		break;
	case 19: // reg: UMINUS(reg)
	case 11: // stat: PRINT(reg)
		kids[0] = p.left();
		break;
	case 13: // stat: ASSIGN(VARIABLE,reg)
		kids[0] = p.right();
		break;
	default:
		panic("kids", "Bad rule number", eruleno);
	}
}

private int maximal(Tree p, int eruleno)
{
  short[] ntsr = nts[eruleno];
  Tree kids[] = new Tree[2];
  int i;

  for (kids(p, eruleno, kids), i = 0; ntsr[i] != 0; i++)
    if (munch(kids[i], ntsr[i], 0) == 0) return 0;

  System.err.println("0x"+(Object)p+": "+string[eruleno]);
  switch(eruleno) {
	case 1: /* stat: PROGRAM(stat,stat) */

		break;
	case 2: /* stat: END */

		break;
	case 3: /* stat: IF(cond,stat) */
{ pf.LABEL(mklbl(p.left().place())); }
		break;
	case 4: /* cond: reg */
{ p.place(++lbl); pf.JZ(mklbl(lbl)); }
		break;
	case 5: /* stat: ELSE(then,stat) */
{ pf.LABEL(mklbl(p.left().place())); }
		break;
	case 6: /* then: IF(cond,stat) */
{ p.place(++lbl); pf.JMP(mklbl(lbl));
			   pf.LABEL(mklbl(p.left().place())); }
		break;
	case 7: /* stat: WHILE(body,reg) */
{ pf.JNZ(mklbl(p.left().left().place()+1)); }
		break;
	case 8: /* body: DO(begin,stat) */
{ pf.LABEL(mklbl(p.left().place())); }
		break;
	case 9: /* begin: START */
{ p.place(++lbl); pf.JMP(mklbl(lbl));
			    pf.LABEL(mklbl(++lbl)); }
		break;
	case 10: /* stat: STRING */
{ String l = mklbl(++lbl);
		    pf.RODATA(); pf.ALIGN(); pf.LABEL(l); pf.STR(p.text());
		    pf.TEXT(); pf.ADDR(l); pf.CALL("_prints"); pf.TRASH(4);
		    pf.CALL("_println"); }
		break;
	case 11: /* stat: PRINT(reg) */
{ pf.CALL("_printi"); pf.TRASH(4); pf.CALL("_println"); }
		break;
	case 12: /* stat: READ */
{ pf.CALL("_readi"); pf.PUSH(); pf.ADDRA(p.left().text()); }
		break;
	case 13: /* stat: ASSIGN(VARIABLE,reg) */
{ pf.ADDRA(p.left().text()); }
		break;
	case 14: /* stat: reg */
{ pf.TRASH(4); }
		break;
	case 15: /* reg: VARIABLE */
{ pf.ADDRV(p.text()); }
		break;
	case 16: /* reg: INTEGER */
{ pf.INT(p.value()); }
		break;
	case 17: /* reg: ADD(reg,reg) */
{ pf.ADD(); }
		break;
	case 18: /* reg: SUB(reg,reg) */
{ pf.SUB(); }
		break;
	case 19: /* reg: UMINUS(reg) */
{ pf.NEG(); }
		break;
	case 20: /* reg: MUL(reg,reg) */
{ pf.MUL(); }
		break;
	case 21: /* reg: DIV(reg,reg) */
{ pf.DIV(); }
		break;
	case 22: /* reg: MOD(reg,reg) */
{ pf.MOD(); }
		break;
	case 23: /* reg: EQ(reg,reg) */
{ pf.EQ(); }
		break;
	case 24: /* reg: NE(reg,reg) */
{ pf.NE(); }
		break;
	case 25: /* reg: LT(reg,reg) */
{ pf.LT(); }
		break;
	case 26: /* reg: LE(reg,reg) */
{ pf.LE(); }
		break;
	case 27: /* reg: GE(reg,reg) */
{ pf.GE(); }
		break;
	case 28: /* reg: GT(reg,reg) */
{ pf.GT(); }
		break;
	default: break;
  }
	return 1;
}

private int munch(Tree a, int goalnt, int loop) {
	Tree kids[] = new Tree[2];
	if (a == null)
		panic("munch", "Null in tree for rule", goalnt);
	switch (a.label()) {
	case 256: /* YYERRCODE */
		break;
	case 257: /* INTEGER */
		if (goalnt == 3	/* reg: INTEGER */
		) {
			if (maximal(a, 16) == 1) return 1;
			System.err.println("0x"+(Object)a+": reg: INTEGER");
		}
		break;
	case 258: /* VARIABLE */
		if (goalnt == 3	/* reg: VARIABLE */
		) {
			if (maximal(a, 15) == 1) return 1;
			System.err.println("0x"+(Object)a+": reg: VARIABLE");
		}
		break;
	case 259: /* STRING */
		if (goalnt == 1	/* stat: STRING */
		) {
			if (maximal(a, 10) == 1) return 1;
			System.err.println("0x"+(Object)a+": stat: STRING");
		}
		break;
	case 260: /* WHILE */
		if (goalnt == 1	/* stat: WHILE(body,reg) */
		) {
			if (maximal(a, 7) == 1) return 1;
			System.err.println("0x"+(Object)a+": stat: WHILE(body,reg)");
		}
		break;
	case 261: /* IF */
		if (goalnt == 1	/* stat: IF(cond,stat) */
		) {
			if (maximal(a, 3) == 1) return 1;
			System.err.println("0x"+(Object)a+": stat: IF(cond,stat)");
		}
		if (goalnt == 4	/* then: IF(cond,stat) */
		) {
			if (maximal(a, 6) == 1) return 1;
			System.err.println("0x"+(Object)a+": then: IF(cond,stat)");
		}
		break;
	case 262: /* PRINT */
		if (goalnt == 1	/* stat: PRINT(reg) */
		) {
			if (maximal(a, 11) == 1) return 1;
			System.err.println("0x"+(Object)a+": stat: PRINT(reg)");
		}
		break;
	case 263: /* READ */
		if (goalnt == 1	/* stat: READ */
		) {
			if (maximal(a, 12) == 1) return 1;
			System.err.println("0x"+(Object)a+": stat: READ");
		}
		break;
	case 264: /* PROGRAM */
		if (goalnt == 1	/* stat: PROGRAM(stat,stat) */
		) {
			if (maximal(a, 1) == 1) return 1;
			System.err.println("0x"+(Object)a+": stat: PROGRAM(stat,stat)");
		}
		break;
	case 265: /* END */
		if (goalnt == 1	/* stat: END */
		) {
			if (maximal(a, 2) == 1) return 1;
			System.err.println("0x"+(Object)a+": stat: END");
		}
		break;
	case 266: /* IFX */
		break;
	case 267: /* ELSE */
		if (goalnt == 1	/* stat: ELSE(then,stat) */
		) {
			if (maximal(a, 5) == 1) return 1;
			System.err.println("0x"+(Object)a+": stat: ELSE(then,stat)");
		}
		break;
	case 268: /* GE */
		if (goalnt == 3	/* reg: GE(reg,reg) */
		) {
			if (maximal(a, 27) == 1) return 1;
			System.err.println("0x"+(Object)a+": reg: GE(reg,reg)");
		}
		break;
	case 269: /* LE */
		if (goalnt == 3	/* reg: LE(reg,reg) */
		) {
			if (maximal(a, 26) == 1) return 1;
			System.err.println("0x"+(Object)a+": reg: LE(reg,reg)");
		}
		break;
	case 270: /* EQ */
		if (goalnt == 3	/* reg: EQ(reg,reg) */
		) {
			if (maximal(a, 23) == 1) return 1;
			System.err.println("0x"+(Object)a+": reg: EQ(reg,reg)");
		}
		break;
	case 271: /* NE */
		if (goalnt == 3	/* reg: NE(reg,reg) */
		) {
			if (maximal(a, 24) == 1) return 1;
			System.err.println("0x"+(Object)a+": reg: NE(reg,reg)");
		}
		break;
	case 272: /* UMINUS */
		if (goalnt == 3	/* reg: UMINUS(reg) */
		) {
			if (maximal(a, 19) == 1) return 1;
			System.err.println("0x"+(Object)a+": reg: UMINUS(reg)");
		}
		break;
	case 273: /* GT */
		if (goalnt == 3	/* reg: GT(reg,reg) */
		) {
			if (maximal(a, 28) == 1) return 1;
			System.err.println("0x"+(Object)a+": reg: GT(reg,reg)");
		}
		break;
	case 274: /* LT */
		if (goalnt == 3	/* reg: LT(reg,reg) */
		) {
			if (maximal(a, 25) == 1) return 1;
			System.err.println("0x"+(Object)a+": reg: LT(reg,reg)");
		}
		break;
	case 275: /* ADD */
		if (goalnt == 3	/* reg: ADD(reg,reg) */
		) {
			if (maximal(a, 17) == 1) return 1;
			System.err.println("0x"+(Object)a+": reg: ADD(reg,reg)");
		}
		break;
	case 276: /* SUB */
		if (goalnt == 3	/* reg: SUB(reg,reg) */
		) {
			if (maximal(a, 18) == 1) return 1;
			System.err.println("0x"+(Object)a+": reg: SUB(reg,reg)");
		}
		break;
	case 277: /* MUL */
		if (goalnt == 3	/* reg: MUL(reg,reg) */
		) {
			if (maximal(a, 20) == 1) return 1;
			System.err.println("0x"+(Object)a+": reg: MUL(reg,reg)");
		}
		break;
	case 278: /* DIV */
		if (goalnt == 3	/* reg: DIV(reg,reg) */
		) {
			if (maximal(a, 21) == 1) return 1;
			System.err.println("0x"+(Object)a+": reg: DIV(reg,reg)");
		}
		break;
	case 279: /* MOD */
		if (goalnt == 3	/* reg: MOD(reg,reg) */
		) {
			if (maximal(a, 22) == 1) return 1;
			System.err.println("0x"+(Object)a+": reg: MOD(reg,reg)");
		}
		break;
	case 280: /* ASSIGN */
		if (goalnt == 1	/* stat: ASSIGN(VARIABLE,reg) */
&& 			a.left().label() == 258 // VARIABLE
		) {
			if (maximal(a, 13) == 1) return 1;
			System.err.println("0x"+(Object)a+": stat: ASSIGN(VARIABLE,reg)");
		}
		break;
	case 281: /* DO */
		if (goalnt == 5	/* body: DO(begin,stat) */
		) {
			if (maximal(a, 8) == 1) return 1;
			System.err.println("0x"+(Object)a+": body: DO(begin,stat)");
		}
		break;
	case 282: /* START */
		if (goalnt == 6	/* begin: START */
		) {
			if (maximal(a, 9) == 1) return 1;
			System.err.println("0x"+(Object)a+": begin: START");
		}
		break;
	default:
		panic("munch", "Bad terminal", a.label());
	}
	if (goalnt == 2 && loop <= 6) if (munch(a, 3, loop+1) > 0) { /* cond: reg */
		System.err.println("0x"+(Object)a+": cond: reg");
		return 1;
	}
	if (goalnt == 1 && loop <= 6) if (munch(a, 3, loop+1) > 0) { /* stat: reg */
		System.err.println("0x"+(Object)a+": stat: reg");
		return 1;
	}
	System.err.println("0x"+(Object)a+": NO MATCH for "+termname[a.label()]+" to "+ntname[goalnt]);
	return 0;
}

public boolean select(Tree p)  {
	if (munch(p,1,0) == 0) {
		System.err.println("No match for start symbol ("+ntname[1]+").\n");
		return false;
	}
	return true;
}


private int lbl;
private String mklbl(int n) { return "_L"+n; }

  public boolean evaluate() {
    if (parser.yyparse() > 0) return false;
    pf.TEXT();
    pf.ALIGN();
    pf.GLOBL("_main");
    pf.LABEL("_main");
    pf.START();
    // System.out.println(parser.syntax());
    if (!select(parser.syntax()))
      System.err.println("No match for start symbol");;
    pf.INT(0);
    pf.POP();
    pf.LEAVE();
    pf.RET();
    // declare used variables
    pf.DATA();
    pf.ALIGN();
    for (String s: parser.symbols().keys()) {
      pf.LABEL(s);
      pf.CONST(0);
    }
    // import library functions
    pf.EXTRN("_readi");
    pf.EXTRN("_printi");
    pf.EXTRN("_prints");
    pf.EXTRN("_println");
    return true;
  }

  private Parser parser;
  private Postfix pf;
  public static String extension(String filename, String ext) {
	  int pos = filename.lastIndexOf('.');
	  if (pos == -1) return filename + "." + ext;
	  else return filename.substring(0, pos) + ext;
  }
  Selector(String in, String out) throws IOException {
	  parser = new Parser(new FileReader(in));
	  pf = new PFi386(out);
  }
  Selector(String in) throws IOException {
	  parser = new Parser(new FileReader(in));
	  pf = new PFi386(extension(in, ".asm"));
  }
  Selector() {
	  parser = new Parser(new InputStreamReader(System.in));
	  pf = new PFi386("out.asm");
  }

  public static void main(String args[]) throws IOException {
    Selector s;

    if (args.length > 0)
      if (args.length > 1)
        s = new Selector(args[0], args[1]);
      else
        s = new Selector(args[0]);
    else
      s = new Selector();

    s.evaluate();
  }

}
