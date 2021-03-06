# metro [![Build Status](https://travis-ci.org/amarcinkowski/metro.svg?branch=master)](https://travis-ci.org/amarcinkowski/metro)
multi environment task robot - simple antr4 based language

## Build
perform `mvn clean package`

## Definition

```
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
    : name=ID '(' (arguments | commandParameters)? ')'
    ;
parameters
    : (parameter (',' parameter)*)
    ;
parameter
    : ID
    ;
commandParameters
    : (commandParameter (',' commandParameter)*?)
    ;
commandParameter
    : (type ':')? ID
    ;
type
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
    : ('A'..'Z' | 'a'..'z' | '0'..'9' | '_' | POLISH)+
    ;
POLISH
    : 'Ą' | 'Ę' | 'Ś' | 'Ć' | 'Ż' | 'Ź' | 'Ó' | 'Ł' | 'Ń' | 'ą' | 'ę' | 'ś'| 'ć' | 'ż' | 'ź' | 'ó' | 'ł' | 'ń'
    ;
UNICODE
    : '\u00A1' .. '\u03FF'
    ;
SPECIALS
    : '\u0021' | '\u0023' .. '\u002F' | '\u003A' .. '\u0040' | '\u005B' .. '\u0060' | '\u007B' .. '\u007E'
    ;
fragment STRING
    : (ID | WHITESPACE | SPECIALS | UNICODE)+
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
```
