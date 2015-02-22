ID=Simples
EXT=.spl
VER=1a

.SUFFIXES: $(EXT) .asm
CC=g++
CXXFLAGS=-g
LEX=flex
YACC=byacpp
YFLAGS=-dv
NASM=nasm -felf

BID=$(ID)-$(VER)
LIB=$$HOME/lib/
BIN=$(ID)
RUN=../../runtime/
RTL=$(RUN)/lib$(ID).a

OBJ = Simples.o Compiler.o Interp.o NodeAdd.o NodeAssign.o NodeBinary.o \
	Node.o NodeData.o NodeDiv.o NodeEq.o NodeGe.o NodeGt.o NodeIf.o \
	NodeIfelse.o NodeInteger.o NodeLe.o NodeList.o NodeLt.o NodeMod.o \
	NodeMul.o NodeNe.o NodePrint.o NodeRead.o NodeReal.o NodeString.o \
	NodeSub.o NodeTernary.o NodeUminus.o NodeUnary.o NodeVariable.o \
	NodeWhile.o VIAdebug.o VIAi386.o Postfix.o SymbolNode.o Symbol.o \
	Tabid.o y.tab.o yyFlexLexer.o main.o

all: $(BIN) $(RTL)

%:%.asm
	$(NASM) $*.asm
	$(LD) $*.o -l$(ID) -o $@

$(EXT).asm:
	$(ID) $<

$(ID): $(OBJ)
	$(LINK.c) -o $@ $^

$(RTL): $(RUN)/lib.o $(RUN)/linux.o $(RUN)/sys.o
	ar crs	$@ $?

$(RUN)/%.o:$(RUN)/%.asm
	$(NASM) $<

ver.c: Makefile
	echo "char *version = \"" $(VER) "\";" > $@

yyFlexLexer.cpp: $(ID).l y.tab.c
	$(LEX) $(LFLAGS) $<

y.tab.c: $(ID).y
	$(YACC) $(YFLAGS) $<

../examples: $(ID) $(RTL)
	cp $< $@
	cd $@;for i in *$(EXT);do $(MAKE) $(MFLAGS) -f ../Makefile `basename $$i $(EXT)`;done

clean:
	rm -f *.o core a.out $(BIN) y.tab.* lex.yy.* y.output *.asm $(OBJ)

backup: clean
	PWD=`pwd`; CWD=`basename $$PWD`; cd ..; tar cf - $$CWD | gzip -9 > ../$@/$(BID).tgz

depend:
	g++ -MM $(OBJ:.o=.cpp) >> Makefile
	makedepend $(ID).y $(ID).l

.PHONY: all clean backup examples
#
Simples.o: Simples.cpp Simples.hpp Compiler.hpp Parser.hpp VIAi386.hpp \
  Postfix.hpp Tabid.hpp Symbol.hpp Node.hpp NodeInteger.hpp NodeReal.hpp \
  NodeString.hpp NodeData.hpp NodeUnary.hpp NodeBinary.hpp NodeTernary.hpp \
  NodeList.hpp NodePrint.hpp NodeRead.hpp NodeVariable.hpp NodeAssign.hpp \
  NodeIf.hpp NodeIfelse.hpp NodeWhile.hpp NodeAdd.hpp NodeSub.hpp NodeMul.hpp \
  NodeDiv.hpp NodeMod.hpp NodeEq.hpp NodeNe.hpp NodeGt.hpp NodeGe.hpp NodeLt.hpp \
  NodeLe.hpp NodeUminus.hpp SymbolNode.hpp
Compiler.o: Compiler.cpp Compiler.hpp
Interp.o: Interp.cpp Interp.hpp Compiler.hpp Parser.hpp Tabid.hpp Symbol.hpp \
  Node.hpp NodeInteger.hpp NodeReal.hpp NodeString.hpp NodeData.hpp \
  NodeUnary.hpp NodeBinary.hpp NodeTernary.hpp NodeList.hpp NodePrint.hpp \
  NodeRead.hpp NodeVariable.hpp NodeAssign.hpp NodeIf.hpp NodeIfelse.hpp \
  NodeWhile.hpp NodeAdd.hpp NodeSub.hpp NodeMul.hpp NodeDiv.hpp NodeMod.hpp \
  NodeEq.hpp NodeNe.hpp NodeGt.hpp NodeGe.hpp NodeLt.hpp NodeLe.hpp \
  NodeUminus.hpp SymbolNode.hpp
NodeAdd.o: NodeAdd.cpp NodeAdd.hpp NodeBinary.hpp NodeUnary.hpp Node.hpp \
  Compiler.hpp
NodeAssign.o: NodeAssign.cpp NodeAssign.hpp NodeBinary.hpp NodeUnary.hpp \
  Node.hpp Compiler.hpp
NodeBinary.o: NodeBinary.cpp NodeBinary.hpp NodeUnary.hpp Node.hpp \
  Compiler.hpp
Node.o: Node.cpp Node.hpp Compiler.hpp
NodeData.o: NodeData.cpp NodeData.hpp Node.hpp Compiler.hpp
NodeDiv.o: NodeDiv.cpp NodeDiv.hpp NodeBinary.hpp NodeUnary.hpp Node.hpp \
  Compiler.hpp
NodeEq.o: NodeEq.cpp NodeEq.hpp NodeBinary.hpp NodeUnary.hpp Node.hpp \
  Compiler.hpp
NodeGe.o: NodeGe.cpp NodeGe.hpp NodeBinary.hpp NodeUnary.hpp Node.hpp \
  Compiler.hpp
NodeGt.o: NodeGt.cpp NodeGt.hpp NodeBinary.hpp NodeUnary.hpp Node.hpp \
  Compiler.hpp
NodeIf.o: NodeIf.cpp NodeIf.hpp NodeBinary.hpp NodeUnary.hpp Node.hpp \
  Compiler.hpp
NodeIfelse.o: NodeIfelse.cpp NodeIfelse.hpp NodeTernary.hpp NodeBinary.hpp \
  NodeUnary.hpp Node.hpp Compiler.hpp
NodeInteger.o: NodeInteger.cpp NodeInteger.hpp Node.hpp Compiler.hpp
NodeLe.o: NodeLe.cpp NodeLe.hpp NodeBinary.hpp NodeUnary.hpp Node.hpp \
  Compiler.hpp
NodeList.o: NodeList.cpp NodeList.hpp Node.hpp Compiler.hpp
NodeLt.o: NodeLt.cpp NodeLt.hpp NodeBinary.hpp NodeUnary.hpp Node.hpp \
  Compiler.hpp
NodeMod.o: NodeMod.cpp NodeMod.hpp NodeBinary.hpp NodeUnary.hpp Node.hpp \
  Compiler.hpp
NodeMul.o: NodeMul.cpp NodeMul.hpp NodeBinary.hpp NodeUnary.hpp Node.hpp \
  Compiler.hpp
NodeNe.o: NodeNe.cpp NodeNe.hpp NodeBinary.hpp NodeUnary.hpp Node.hpp \
  Compiler.hpp
NodePrint.o: NodePrint.cpp NodePrint.hpp NodeUnary.hpp Node.hpp Compiler.hpp
NodeRead.o: NodeRead.cpp NodeRead.hpp NodeUnary.hpp Node.hpp Compiler.hpp
NodeReal.o: NodeReal.cpp NodeReal.hpp Node.hpp Compiler.hpp
NodeString.o: NodeString.cpp NodeString.hpp Node.hpp Compiler.hpp
NodeSub.o: NodeSub.cpp NodeSub.hpp NodeBinary.hpp NodeUnary.hpp Node.hpp \
  Compiler.hpp
NodeTernary.o: NodeTernary.cpp NodeTernary.hpp NodeBinary.hpp NodeUnary.hpp \
  Node.hpp Compiler.hpp
NodeUminus.o: NodeUminus.cpp NodeUminus.hpp NodeUnary.hpp Node.hpp \
  Compiler.hpp
NodeUnary.o: NodeUnary.cpp NodeUnary.hpp Node.hpp Compiler.hpp
NodeVariable.o: NodeVariable.cpp NodeVariable.hpp NodeString.hpp Node.hpp \
  Compiler.hpp
NodeWhile.o: NodeWhile.cpp NodeWhile.hpp NodeBinary.hpp NodeUnary.hpp Node.hpp \
  Compiler.hpp
VIAdebug.o: VIAdebug.cpp VIAdebug.hpp Postfix.hpp
VIAi386.o: VIAi386.cpp VIAi386.hpp Postfix.hpp
Postfix.o: Postfix.cpp Postfix.hpp
SymbolNode.o: SymbolNode.cpp SymbolNode.hpp Symbol.hpp
Symbol.o: Symbol.cpp Symbol.hpp
Tabid.o: Tabid.cpp Tabid.hpp Symbol.hpp
yyFlexLexer.o: yyFlexLexer.cpp NodeVariable.hpp NodeString.hpp Node.hpp \
  Compiler.hpp NodeInteger.hpp y.tab.h
main.o: main.cpp Compiler.hpp Simples.hpp Parser.hpp VIAi386.hpp Postfix.hpp \
  Tabid.hpp Symbol.hpp Interp.hpp
