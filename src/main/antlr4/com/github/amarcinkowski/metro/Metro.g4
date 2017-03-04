grammar Metro;

prog
    : (globalVariables | go | function | block)*
    | NEWLINE*
    ;
// Rules
globalVariables
    : 'var' WHITESPACE name=ID '=' value=TEXT
    ;
function
    : 'function' WHITESPACE name=ID '(' parameters? ')' (NEWLINE | WHITESPACE)? '{' NEWLINE? block? NEWLINE? '}'
    ;
go
    : 'go' WHITESPACE name=ID '(' arguments? ')'
    ;
block
    : (WHITESPACE* command NEWLINE?)+
    ;
command
    : 'print' '(' (arguments | parameters)? ')'
    ;
parameters
    : (parameter (',' parameter)*)
    ;
parameter
    : ID
    ;
arguments
    : (argument (',' argument)*)
    ;
argument
    : TEXT
    ;
// TOKENS
COMMENT
    : (WHITESPACE* '//' ~[\r?\n]*)+ -> skip
    ;
ID
    : ('A'..'Z' | 'a'..'z' | '0'..'9' | POLISH)+
    ;
POLISH
    : 'Ą' | 'Ę' | 'Ś' | 'Ć' | 'Ż' | 'Ź' | 'Ó' | 'Ł' | 'Ń' | 'ą' | 'ę' | 'ś'| 'ć' | 'ż' | 'ź' | 'ó' | 'ł' | 'ń'
    ;
fragment STRING
    : (ID | WHITESPACE)+
    ;
TEXT
    : '"' STRING '"'
    ;
WHITESPACE
    : ( '\t' | ' ' )+
    ;
NEWLINE :
    [\r?\n]+ -> skip
    ;
INT
    :[0-9]+
    ;
