ID=simples
EXT=.spl
VER=1h

BID=$(ID)-$(VER)
COPY="$(BID) (C)prs, 2012"

.SUFFIXES: $(EXT) .brg .asm
LOADLIBS=$(UTIL) # -lfl
CC=gcc
CFLAGS=-g -DYYDEBUG -I$(LIB)
#CFLAGS=-O2 -I$(LIB)
LEX=flex
LFLAGS=-l
YACC=byacc
YFLAGS=-dv
BURG=pburg
BFLAGS= -T
NASM=nasm -felf32

BID=$(ID)-$(VER)
LIB=../lib
UTIL=$(LIB)/libutil.a
BIN=$(ID)
RUN=../runtime/librun.a

all: $(UTIL) $(BIN) $(RUN)

.brg.c:
	$(BURG) $(BFLAGS) $< $@

%:%.asm
	$(NASM) $*.asm -o $*.o
	$(LD) $*.o $(RUN) -o $@

$(EXT).asm:
	$(ID) $<

$(ID): scan.o gram.o i386.o $(UTIL)
	$(LINK.c) -o $@ $^

full: all android pepe postfix trans gram greedy interp stab jasmin msil via

via: scan.o gram.o via.o $(UTIL)
	$(LINK.c) -o $@ $^

android: scan.o gram.o android.o $(UTIL)
	$(LINK.c) -o $@ $^

pepe: scan.o gram.o pepe.o $(UTIL)
	$(LINK.c) -o $@ $^

postfix: scan.o tree.o postfix.o $(UTIL)
	$(LINK.c) -o $@ $^

trans: scan.o tree.o trans.o $(UTIL)
	$(LINK.c) -o $@ $^

gram: scan.o syntax.o $(UTIL)
	$(LINK.c) -o $@ $^

greedy: scan.o tree.o greedy.o $(UTIL)
	$(LINK.c) -o $@ $^

interp: scan.o tree.o interp.o $(UTIL)
	$(LINK.c) -o $@ $^

stab: scan.o tree.o stab.o $(UTIL)
	$(LINK.c) -o $@ $^

jasmin: scan.o tree.o jasmin.o $(UTIL)
	$(LINK.c) -o $@ $^

msil: scan.o tree.o msil.o $(UTIL)
	$(LINK.c) -o $@ $^

scan.o: tree.c syntax.c gram.c

$(UTIL):
	$(MAKE) -C $(LIB) $(MFLAGS) CC=$(CC)

$(RUN):
	$(MAKE) -C ../runtime $(MFLAGS) CC=$(CC)

ver.c: Makefile
	echo "char *version = \"" $(VER) "\";" > $@

lex.yy.c: $(ID).l y.tab.c
	$(LEX) $(LFLAGS) $<

y.tab.c: $(ID).y
	$(YACC) $(YFLAGS) $<

examples: $(RUN) $(ID)
	$(MAKE) -C ../examples $(MFLAGS) BIN=../demo/compact

clean:
	rm -f *~ *.o *.exe core a.out $(BIN) y.tab.* lex.yy.* y.output *.asm tree.c syntax.c gram.c android pepe postfix trans gram greedy interp stab jasmin msil
	for i in *.brg; do rm -f `basename $$i .brg`.c; done

cleanall: clean
	$(MAKE) -C $(LIB) $(MFLAGS) clean
	$(MAKE) -C ../runtime $(MFLAGS) clean
	$(MAKE) -C ../examples $(MFLAGS) clean

backup: clean
	PWD=`pwd`; CWD=`basename $$PWD`; cd ..; tar cf - $$CWD | gzip -9 > $@/$(BID).tgz

.PHONY: all clean cleanall backup examples
