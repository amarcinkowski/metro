package com.github.amarcinkowski.metro.exceptions;

import java.io.IOException;

public class MissingMetroSourceException extends IOException {

    public MissingMetroSourceException(String msg) {
        super(msg);
    }

}
