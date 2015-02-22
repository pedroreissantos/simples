//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 2 "i386j.y"
/* compile with: byaccj -J -Jsemantic=Tree -Jclass=ParserOpt */
import java.io.*;
//#line 20 "ParserOpt.java"




public class ParserOpt
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//## **user defined:Tree
String   yytext;//user variable to return contextual strings
Tree yyval; //used to return semantic vals from action routines
Tree yylval;//the 'lval' (result) I got from yylex()
Tree valstk[] = new Tree[YYSTACKSIZE];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
final void val_init()
{
  yyval=new Tree();
  yylval=new Tree();
  valptr=-1;
}
final void val_push(Tree val)
{
  try {
    valptr++;
    valstk[valptr]=val;
  }
  catch (ArrayIndexOutOfBoundsException e) {
    int oldsize = valstk.length;
    int newsize = oldsize*2;
    Tree[] newstack = new Tree[newsize];
    System.arraycopy(valstk,0,newstack,0,oldsize);
    valstk = newstack;
    valstk[valptr]=val;
  }
}
final Tree val_pop()
{
  return valstk[valptr--];
}
final void val_drop(int cnt)
{
  valptr -= cnt;
}
final Tree val_peek(int relative)
{
  return valstk[valptr-relative];
}
final Tree dup_yyval(Tree val)
{
  return val;
}
//#### end semantic value section ####
public final static short INTEGER=257;
public final static short VARIABLE=258;
public final static short STRING=259;
public final static short WHILE=260;
public final static short IF=261;
public final static short PRINT=262;
public final static short READ=263;
public final static short PROGRAM=264;
public final static short END=265;
public final static short IFX=266;
public final static short ELSE=267;
public final static short GE=268;
public final static short LE=269;
public final static short EQ=270;
public final static short NE=271;
public final static short UMINUS=272;
public final static short GT=273;
public final static short LT=274;
public final static short ADD=275;
public final static short SUB=276;
public final static short MUL=277;
public final static short DIV=278;
public final static short MOD=279;
public final static short ASSIGN=280;
public final static short JMP=281;
public final static short JZ=282;
public final static short JNZ=283;
public final static short LABEL=284;
public final static short ETIQ=285;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    2,    2,    2,    2,    2,    2,    2,    2,    2,
    1,    1,    3,    3,    3,    3,    3,    3,    3,    3,
    3,    3,    3,    3,    3,    3,    3,
};
final static short yylen[] = {                            2,
    3,    1,    3,    3,    3,    4,    5,    5,    7,    3,
    1,    2,    1,    1,    2,    3,    3,    3,    3,    3,
    3,    3,    3,    3,    3,    3,    3,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    2,    0,    0,
   11,    0,    0,    0,   13,   14,    0,    0,    0,    0,
    0,    0,    1,   12,    0,    0,    0,    3,   15,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    4,    5,   10,    6,    0,    0,   27,    0,    0,
    0,    0,    0,    0,    0,    0,   18,   19,   20,    7,
    0,    0,    9,
};
final static short yydgoto[] = {                          2,
   10,   11,   20,
};
final static short yysindex[] = {                      -263,
  -45,    0,  -58,  -22,  -21,  -36, -238,    0,  -45,  -57,
    0,  -29,  -29,  -29,    0,    0,  -38,  -29,  -29,  -37,
  -35,   -2,    0,    0,  -30,   -6,    1,    0,    0,    8,
  -29,  -29,  -29,  -29,  -29,  -29,  -29,  -29,  -29,  -29,
  -29,    0,    0,    0,    0,  -45,  -45,    0,   94,   94,
   94,   94,   94,   94,   60,   60,    0,    0,    0,    0,
 -241,  -45,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   30,   34,
   39,   44,   50,   54,   17,   24,    0,    0,    0,    0,
   68,    0,    0,
};
final static short yygindex[] = {                         0,
   18,   78,  116,
};
final static int YYTABLESIZE=333;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         41,
    1,    8,   12,   19,   39,   37,   41,   38,   18,   40,
   19,   39,   37,    8,   38,   18,   40,   13,   14,   21,
   28,   42,   36,   43,   35,   62,   22,    0,   45,   36,
   41,   35,    0,    0,   46,   39,   37,   41,   38,    0,
   40,   47,   39,   37,   41,   38,    0,   40,   48,   39,
   37,    0,   38,   36,   40,   35,    8,   16,    0,   16,
   36,   16,   35,    0,   17,    9,   17,   36,   17,   35,
   23,    0,    0,    0,   24,   16,   16,    9,   16,   26,
    0,    0,   17,   17,   25,   17,    0,   24,   23,   23,
   22,   23,   24,   24,   21,   24,   41,   26,   26,   24,
   26,   39,   25,   25,    0,   25,   40,    0,   22,   22,
    0,   22,   21,   21,    0,   21,    0,    0,    0,    0,
    9,    0,   44,   60,   61,    0,    8,   25,   26,   27,
   41,    0,    0,   29,   30,   39,   37,    0,   38,   63,
   40,    0,    0,    0,    0,    0,   49,   50,   51,   52,
   53,   54,   55,   56,   57,   58,   59,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    8,    0,    8,    0,    0,    0,    0,    0,    0,    0,
    3,    0,    4,    5,    6,    7,    0,   23,    0,    0,
    0,    0,    3,    0,    4,    5,    6,    7,    0,    0,
   15,   16,   17,    0,    0,    0,    0,   15,   16,    0,
   31,   32,   33,   34,    0,    0,    0,   31,   32,   33,
   34,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    3,    0,    4,    5,    6,
    7,   31,   32,   33,   34,    0,    0,    0,   31,   32,
   33,   34,    0,    0,    0,   31,   32,   33,   34,    0,
    0,    0,    0,    0,   16,   16,   16,   16,    0,    0,
    0,   17,   17,   17,   17,    0,    0,   23,   23,   23,
   23,   24,   24,   24,   24,    0,   26,   26,   26,   26,
    0,   25,   25,   25,   25,    0,    0,   22,   22,   22,
   22,   21,   21,   21,   21,    8,    0,    8,    8,    8,
    8,    0,    8,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         37,
  264,   59,   61,   40,   42,   43,   37,   45,   45,   47,
   40,   42,   43,   59,   45,   45,   47,   40,   40,  258,
   59,   59,   60,   59,   62,  267,    9,   -1,   59,   60,
   37,   62,   -1,   -1,   41,   42,   43,   37,   45,   -1,
   47,   41,   42,   43,   37,   45,   -1,   47,   41,   42,
   43,   -1,   45,   60,   47,   62,   59,   41,   -1,   43,
   60,   45,   62,   -1,   41,  123,   43,   60,   45,   62,
   41,   -1,   -1,   -1,   41,   59,   60,  123,   62,   41,
   -1,   -1,   59,   60,   41,   62,   -1,   10,   59,   60,
   41,   62,   59,   60,   41,   62,   37,   59,   60,   22,
   62,   42,   59,   60,   -1,   62,   47,   -1,   59,   60,
   -1,   62,   59,   60,   -1,   62,   -1,   -1,   -1,   -1,
  123,   -1,  125,   46,   47,   -1,   59,   12,   13,   14,
   37,   -1,   -1,   18,   19,   42,   43,   -1,   45,   62,
   47,   -1,   -1,   -1,   -1,   -1,   31,   32,   33,   34,
   35,   36,   37,   38,   39,   40,   41,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  123,   -1,  125,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  258,   -1,  260,  261,  262,  263,   -1,  265,   -1,   -1,
   -1,   -1,  258,   -1,  260,  261,  262,  263,   -1,   -1,
  257,  258,  259,   -1,   -1,   -1,   -1,  257,  258,   -1,
  268,  269,  270,  271,   -1,   -1,   -1,  268,  269,  270,
  271,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  258,   -1,  260,  261,  262,
  263,  268,  269,  270,  271,   -1,   -1,   -1,  268,  269,
  270,  271,   -1,   -1,   -1,  268,  269,  270,  271,   -1,
   -1,   -1,   -1,   -1,  268,  269,  270,  271,   -1,   -1,
   -1,  268,  269,  270,  271,   -1,   -1,  268,  269,  270,
  271,  268,  269,  270,  271,   -1,  268,  269,  270,  271,
   -1,  268,  269,  270,  271,   -1,   -1,  268,  269,  270,
  271,  268,  269,  270,  271,  258,   -1,  260,  261,  262,
  263,   -1,  265,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=285;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"'%'",null,null,"'('","')'","'*'","'+'",null,
"'-'",null,"'/'",null,null,null,null,null,null,null,null,null,null,null,"';'",
"'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
"'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"INTEGER","VARIABLE","STRING","WHILE","IF",
"PRINT","READ","PROGRAM","END","IFX","ELSE","GE","LE","EQ","NE","UMINUS","GT",
"LT","ADD","SUB","MUL","DIV","MOD","ASSIGN","JMP","JZ","JNZ","LABEL","ETIQ",
};
final static String yyrule[] = {
"$accept : program",
"program : PROGRAM list END",
"stmt : ';'",
"stmt : PRINT STRING ';'",
"stmt : PRINT expr ';'",
"stmt : READ VARIABLE ';'",
"stmt : VARIABLE '=' expr ';'",
"stmt : WHILE '(' expr ')' stmt",
"stmt : IF '(' expr ')' stmt",
"stmt : IF '(' expr ')' stmt ELSE stmt",
"stmt : '{' list '}'",
"list : stmt",
"list : list stmt",
"expr : INTEGER",
"expr : VARIABLE",
"expr : '-' expr",
"expr : expr '+' expr",
"expr : expr '-' expr",
"expr : expr '*' expr",
"expr : expr '/' expr",
"expr : expr '%' expr",
"expr : expr '<' expr",
"expr : expr '>' expr",
"expr : expr GE expr",
"expr : expr LE expr",
"expr : expr NE expr",
"expr : expr EQ expr",
"expr : '(' expr ')'",
};

//#line 72 "i386j.y"
private int lbl;
String mklbl(int n) { return "_L"+n; }
int newlbl() { return ++lbl; }

private Tree tree; // top level node of the syntax tree
private String filename; // just for error printing
public  Tree syntax() { return tree; }

private LexerOpt lexer; // interface with JFlex lexical analyser
private int line() { return 1 + lexer.line(); } // bug in JFlex
private int yylex() {
	int ret = -1;
	try {
		ret = lexer.yylex();
	} catch (IOException e) {
		System.err.println("Erro: " + e);
	}
	return ret;
}
private int nerrs;
public void yyerror(String msg) {
	nerrs++;
	if (filename == null)
		System.err.println(msg + ": line " + line() +
				" at or before " + lexer.yytext());
	else
		System.err.println(filename + ": line " + line() + ": " +
				msg + " at or before " + lexer.yytext());
}
public int errors() { return nerrs; }

private Tabid<Integer> vars;
public Tabid<Integer> symbols() { return vars; }
public void symbol(String name)
{ if (!vars.containsKey(name)) yyerror(name + ": variable not found."); }

public ParserOpt(Reader r)
{ nerrs = 0; vars = new Tabid<Integer>(); lexer = new LexerOpt(r, this); }
public ParserOpt(Reader r, boolean _debug)
{ this(r); yydebug = _debug; filename = null; }
public ParserOpt(Reader r, boolean _debug, String infile)
{ this(r, _debug); filename = infile; }
// use this 'main' to test the lexical analyser (with 'yylval' and 'return')
// but without a proper grammar
public static void main(String args[]) throws IOException {
        ParserOpt parser = new ParserOpt(new FileReader(args[0]));
	while (parser.yylex() != 0) ;
}
//#line 346 "ParserOpt.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 20 "i386j.y"
{ tree = val_peek(1); }
break;
case 2:
//#line 23 "i386j.y"
{ yyval = new Tree(ParserOpt.END); }
break;
case 3:
//#line 24 "i386j.y"
{ yyval = val_peek(1); }
break;
case 4:
//#line 25 "i386j.y"
{ yyval = new Tree(ParserOpt.PRINT, val_peek(1)); }
break;
case 5:
//#line 26 "i386j.y"
{ yyval = new Tree(ParserOpt.READ, val_peek(1)); symbol(val_peek(1).text()); }
break;
case 6:
//#line 27 "i386j.y"
{ yyval = new Tree(ParserOpt.ASSIGN, val_peek(3), val_peek(1)); vars.put(val_peek(3).text(), 0); }
break;
case 7:
//#line 28 "i386j.y"
{ int lbl1 = ++lbl, lbl2 = ++lbl;
	      yyval = new Tree(ParserOpt.PROGRAM, new Tree(ParserOpt.JMP, mklbl(lbl1)),
		    new Tree(ParserOpt.PROGRAM, new Tree(ParserOpt.LABEL, mklbl(lbl2)),
		     new Tree(ParserOpt.PROGRAM, val_peek(0),
		      new Tree(ParserOpt.PROGRAM, new Tree(ParserOpt.LABEL, mklbl(lbl1)),
		   	new Tree(ParserOpt.JNZ, val_peek(2), new Tree(ParserOpt.ETIQ, mklbl(lbl2))))))); }
break;
case 8:
//#line 34 "i386j.y"
{ int lbl1 = ++lbl, lbl2 = ++lbl;
	      yyval = new Tree(ParserOpt.PROGRAM, new Tree(ParserOpt.JZ, val_peek(2),
	      		new Tree(ParserOpt.ETIQ, mklbl(lbl1))),
		    new Tree(ParserOpt.PROGRAM, val_peek(0),
		     new Tree(ParserOpt.LABEL, mklbl(lbl1)))); }
break;
case 9:
//#line 39 "i386j.y"
{ int lbl1 = ++lbl, lbl2 = ++lbl;
	      yyval = new Tree(ParserOpt.PROGRAM, new Tree(ParserOpt.JZ, val_peek(4),
	      		new Tree(ParserOpt.ETIQ, mklbl(lbl1))),
		    new Tree(ParserOpt.PROGRAM, val_peek(2),
		     new Tree(ParserOpt.PROGRAM, new Tree(ParserOpt.JMP, mklbl(lbl2)),
		      new Tree(ParserOpt.PROGRAM, new Tree(ParserOpt.LABEL, mklbl(lbl1)),
		       new Tree(ParserOpt.PROGRAM, val_peek(0),
		   	new Tree(ParserOpt.LABEL, mklbl(lbl2))))))); }
break;
case 10:
//#line 47 "i386j.y"
{ yyval = val_peek(1); }
break;
case 11:
//#line 50 "i386j.y"
{ yyval = new Tree(ParserOpt.PROGRAM, new Tree(ParserOpt.END), val_peek(0)); }
break;
case 12:
//#line 51 "i386j.y"
{ yyval = new Tree(ParserOpt.PROGRAM, val_peek(1), val_peek(0)); }
break;
case 13:
//#line 54 "i386j.y"
{ yyval = val_peek(0); }
break;
case 14:
//#line 55 "i386j.y"
{ yyval = val_peek(0); symbol(val_peek(0).text()); }
break;
case 15:
//#line 56 "i386j.y"
{ yyval = new Tree(ParserOpt.UMINUS, val_peek(0)); }
break;
case 16:
//#line 57 "i386j.y"
{ yyval = new Tree(ParserOpt.ADD, val_peek(2), val_peek(0)); }
break;
case 17:
//#line 58 "i386j.y"
{ yyval = new Tree(ParserOpt.SUB, val_peek(2), val_peek(0)); }
break;
case 18:
//#line 59 "i386j.y"
{ yyval = new Tree(ParserOpt.MUL, val_peek(2), val_peek(0)); }
break;
case 19:
//#line 60 "i386j.y"
{ yyval = new Tree(ParserOpt.DIV, val_peek(2), val_peek(0)); }
break;
case 20:
//#line 61 "i386j.y"
{ yyval = new Tree(ParserOpt.MOD, val_peek(2), val_peek(0)); }
break;
case 21:
//#line 62 "i386j.y"
{ yyval = new Tree(ParserOpt.LT, val_peek(2), val_peek(0)); }
break;
case 22:
//#line 63 "i386j.y"
{ yyval = new Tree(ParserOpt.GT, val_peek(2), val_peek(0)); }
break;
case 23:
//#line 64 "i386j.y"
{ yyval = new Tree(ParserOpt.GE, val_peek(2), val_peek(0)); }
break;
case 24:
//#line 65 "i386j.y"
{ yyval = new Tree(ParserOpt.LE, val_peek(2), val_peek(0)); }
break;
case 25:
//#line 66 "i386j.y"
{ yyval = new Tree(ParserOpt.NE, val_peek(2), val_peek(0)); }
break;
case 26:
//#line 67 "i386j.y"
{ yyval = new Tree(ParserOpt.EQ, val_peek(2), val_peek(0)); }
break;
case 27:
//#line 68 "i386j.y"
{ yyval = val_peek(1); }
break;
//#line 619 "ParserOpt.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public ParserOpt()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public ParserOpt(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
