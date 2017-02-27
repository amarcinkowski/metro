grammar Metro;

prog
    : ((go | function | block) NEWLINE)*
    | NEWLINE*
    ;
// Rules
function
    : 'function' WHITESPACE name=ID '(' arguments? ')' (NEWLINE | WHITESPACE)? '{' NEWLINE? block? '}'
    ;
go
    : 'go' WHITESPACE name=ID '(' arguments? ')'
    ;
block
    : (WHITESPACE* command NEWLINE?)*
    ;
command
    : 'print' '(' arguments? ')'
    ;
arguments
    : (argument (',' argument)*)
    ;
argument
    : TEXT+
    ;
// TOKENS
ID
    : ('A'..'Z' | 'a'..'z' | '0'..'9' | POLISH)+
    ;
POLISH
    : 'Ą' | 'Ę' | 'Ś' | 'Ć' | 'Ż' | 'Ź' | 'Ó' | 'Ł' | 'Ń' | 'ą' | 'ę' | 'ś'| 'ć' | 'ż' | 'ź' | 'ó' | 'ł' | 'ń'
    ;
TEXT
    : '"' (ID | WHITESPACE)+ '"'
    ;

WHITESPACE
    : ( '\t' | ' ' )+
    ;

NEWLINE :
    [\r?\n]+
    ;
INT     :
    [0-9]+
    ;