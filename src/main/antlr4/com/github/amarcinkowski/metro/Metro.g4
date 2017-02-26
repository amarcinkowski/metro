grammar Metro;

prog:
    (command NEWLINE)*
    | NEWLINE*
    ;

command
    : ('print') '(' arguments? ')'
    ;
arguments
    : (argument (',' argument)*)
    ;
argument
    : TEXT+
    ;
TEXT
    : '"' ('A'..'Z' | 'a'..'z' | '\u0100' .. '\u017E' | WHITESPACE)+ '"'
    ;

WHITESPACE
    : ( '\t' | ' ' |'\u000C' )+ -> skip
    ;

NEWLINE :
    [\r\n]+
    ;
INT     :
    [0-9]+
    ;