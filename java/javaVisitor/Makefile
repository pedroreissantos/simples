# make Simples.jar and then run the examples with the interpreter:
# java -cp Simples.jar Interp ../examples/tri.spl
#
# to create i386 executables:
# Compile:	java -cp Simples.jar Simples tri.spl
# 	or	java -jar Simples.jar tri.spl
# Assemble:	nasm -felf tri.asm
# Link:		ld -o tri tri.o ../runtime/lib.o ../runtime/linux.o
# and run:	./tri
ID=Simples
EXT=.spl
VER=0b
BID=$(ID)-$(VER)

.SUFFIXES: $(EXT) .asm .java .class

LIB=jar# where the .jar files reside
#LIB=$$HOME/lib/java# where the .jar files reside
JAVA=java
JPATH=-cp $(LIB)/java-cup-v11a-runtime.jar:$(LIB)/antlr-3.2.jar:.
JAVAC=javac $(JPATH)
JFLEX=$(JAVA) -cp $(LIB)/JFlex.jar JFlex.Main
BYACCJ=byaccj -J -Jsemantic=Node -Jclass=Bparser
CUP=$(JAVA) -jar $(LIB)/java-cup-v11a.jar
JLEX=$(JAVA) -jar $(LIB)/JLex.jar
ANTLR=$(JAVA) -jar $(LIB)/antlr-3.2.jar
JAR=jar

CLASS= Analyser.class Byaccj.class Cup.class Antlr.class Simples.class \
Compiler.class Interp.class Jasmin.class Msil.class Tree.class Sexp.class \
NodeAdd.class NodeAssign.class NodeBinary.class NodeData.class \
NodeDiv.class NodeEq.class NodeGe.class NodeGt.class NodeIfelse.class \
NodeIf.class NodeInteger.class Node.class NodeLe.class NodeList.class \
NodeLt.class NodeMod.class NodeMul.class NodeNe.class NodePrint.class \
NodeRead.class NodeReal.class NodeString.class NodeSub.class NodeTernary.class \
NodeUminus.class NodeUnary.class NodeVariable.class NodeWhile.class \
VIAdebug.class VIAi386.class VIAopcodes.class Via.class Tabid.class
PARSERS=Bparser.class parser.class

GEN=Simples.y Simples.l Simples.cup Simples.jlex simples.g Makefile README MANIFEST

all: $(ID).jar

run: $(ID).jar
	$(JAVA) $(JPATH) $(ID) $E 

src:
	$(JAR) -cf $(BID)_.zip $(CLASS:.class=.java) $(GEN)

allsrc:
	$(JAR) -cf $(BID).zip $(CLASS:.class=.java) $(GEN) jar/*

$(ID).jar: $(CLASS) $(PARSERS) MANIFEST
	$(JAR) -cmf MANIFEST $@ *.class
#	$(JAR) cmf MANIFEST $@ $(JAVA:.java=.class)

Simples.class: Byaccj.class Cup.class Antlr.class

Byaccj.class: Bparser.class
Bparser.class: Yylex.class Bparser.java

Yylex.java: $(ID).l
	$(JFLEX) $<

Bparser.java: $(ID).y
	$(BYACCJ) $<

lexer.java: $(ID).jlex
	$(JLEX) $<
	mv $<.java $@

Cup.class: parser.class
parser.class: lexer.java
parser.java: $(ID).cup
	$(CUP) $<

Antlr.class: simplesParser.class
simplesParser.java: simples.g
	$(ANTLR) $<

clean:
	rm -f *.class *~ simples.tokens
	-rm -fi Yylex.java Bparser.java lexer.java parser.java simplesLexer.java simplesParser.java sym.java
	-rm -fi $(ID).jar

cleanall:
	rm -f *.class *~ simples.tokens $(ID).jar Yylex.java Bparser.java lexer.java parser.java simplesLexer.java simplesParser.java sym.java

backup: clean
	PWD=`pwd`; CWD=`basename $$PWD`; cd ..; tar cf - $$CWD | gzip -9 > ../$@/$(BID).tgz

.java.class:
	$(JAVAC) $<

%:%.asm
	$(NASM) $*.asm
	$(LD) $*.o -l$(ID) -o $@

$(EXT).asm:
	$(ID) $<

.PHONY: all clean backup
