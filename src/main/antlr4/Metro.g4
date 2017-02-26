grammar Metro;

prog:
    (command NEWLINE)*
    ;

command:
    ('print')
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