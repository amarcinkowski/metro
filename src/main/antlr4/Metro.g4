grammar Metro;

prog:
    (command NEWLINE)*
    | NEWLINE*
    ;

command
    : ('print') '(' arguments? ')'
    ;
arguments
    : TEXT+
    ;
TEXT
    : ('A'..'Z' | 'a'..'z')+
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