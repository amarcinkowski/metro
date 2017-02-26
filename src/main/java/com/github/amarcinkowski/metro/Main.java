package com.github.amarcinkowski.metro;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        new Metro().run(new File("src/main/resources/simple.metro"));
    }

    public static int max(int a, int b) {
        return a > b ? a : b;
    }
}
