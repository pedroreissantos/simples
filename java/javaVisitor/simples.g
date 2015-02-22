grammar simples;

options {
	language = Java;
	k = 2; // for PRINT instruction
}

SPACE:		( ' ' | '\t' | '\r' '\n' | '\r' | '\n' ) { skip(); };
COMMENT:	'#' (~('\n'|'\r'))* { skip();};
STRING:		'\'' ( ~'\'' )* '\'';
PRINT:		'print';
READ:		'read';
WHILE:		'while';
IF:		'if';
ELSE:		'else';
VARIABLE:	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')* ;
INTEGER:	('0'..'9')+ ;
GE:		'>=';
LE:		'<=';
NE:		'!=';
EQ:		'==';

var returns [Node n]
	: v=VARIABLE	{ $n = new NodeVariable($v.text); }
	;

start returns [Node tree]: 'program' list 'end' EOF { $tree = $list.n; };

stmt returns [Node n]
	: ';'				{ $n = null; }
	| PRINT s=STRING ';'		{ $n = new NodeString($s.text.substring(1, $s.text.length()-1)); }
	| PRINT expr ';'		{ $n = new NodePrint($expr.n); }
	| READ var ';'			{ $n = new NodeRead($var.n); }
	| var '=' expr ';'		{ $n = new NodeAssign($var.n, $expr.n);}
	| WHILE '(' expr ')' w=stmt	{ $n = new NodeWhile($expr.n, $w.n); }
	| IF '(' expr ')' i=stmt
	    ( ( ELSE ) => ELSE e=stmt	{ $n = new NodeIfelse($expr.n, $i.n, $e.n); }
	    | ( )			{ $n = new NodeIf($expr.n, $i.n); }
	    )
	| '{' list '}'			{ $n = $list.n; }
	;

list returns [NodeList n]
	: { $n = new NodeList(); } ( stmt { $n.append($stmt.n); } )*
	;

bexpr returns [Node n]
	: i=INTEGER		{ $n = new NodeInteger(Integer.parseInt($i.text)); }
	| var			{ $n = $var.n; }
	| '(' expr ')'		{ $n = $expr.n; }
	;

nexpr returns [Node n]
	: '-' bexpr		{ $n =  new NodeUminus($bexpr.n); }
	| bexpr			{ $n = $bexpr.n; }
	;

mexpr returns [Node n]
	: ne=nexpr		{ $n = $ne.n; }
		( '*' ne2=nexpr	{ $n = new NodeMul($n, $ne2.n); }
		| '/' ne3=nexpr	{ $n = new NodeDiv($n, $ne3.n); }
		| '%' ne4=nexpr	{ $n = new NodeMod($n, $ne4.n); }
		)*
	;

sexpr returns [Node n]
	: me=mexpr		{ $n = $me.n; }
		( '+' me2=mexpr	{ $n = new NodeAdd($n, $me2.n); }
		| '-' me3=mexpr	{ $n = new NodeSub($n, $me3.n); }
		)*
	;

expr returns [Node n]
	: se=sexpr		{ $n = $se.n; }
		( '<' se2=sexpr	{ $n = new NodeLt($n, $se2.n); }
		| '>' se3=sexpr	{ $n = new NodeGt($n, $se3.n); }
		| GE  se4=sexpr	{ $n = new NodeGe($n, $se4.n); }
		| LE  se5=sexpr	{ $n = new NodeLe($n, $se5.n); }
		| NE  se6=sexpr	{ $n = new NodeNe($n, $se6.n); }
		| EQ  se7=sexpr	{ $n = new NodeEq($n, $se7.n); }
		)*
	;
