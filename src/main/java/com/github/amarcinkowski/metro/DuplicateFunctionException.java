package com.github.amarcinkowski.metro;

import groovy.transform.InheritConstructors;

public class DuplicateFunctionException extends RuntimeException {

    public DuplicateFunctionException(String msg) {
        super(msg);
    }

}
