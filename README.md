# metro [![Build Status](https://travis-ci.org/amarcinkowski/metro.svg?branch=master)](https://travis-ci.org/amarcinkowski/metro)
multi environment task robot - simple antr4 based language

language definition
---
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
```