package com.github.amarcinkowski.metro

import spock.lang.Specification

class MetroSpec extends Specification {

    def "com.github.amarcinkowski.metro.Metro.run with non-existing file should throw exception"()
    {
        given:
        def metro = new Metro()
        when:
        metro.run(new File(""))
        then:
        thrown(IOException)

    }

    def "simple.metro should parse properly"()
    {
        given:
        def metro = new Metro()
        when:
        metro.run(new File("src/test/resources/simple.metro"))
        then:
        def commands = metro.metroWalker.getCommands()
        assert commands.size() == 3
        assert commands.get(0).matches("print\\(\"foobar\"\\)")
        assert commands.get(1) == "print()"
        assert commands.get(2).split(",").length == 2
        assert commands.get(2).contains("Cześć Świecie")
    }

    def "function.metro should parse properly"()
    {
        given:
        def metro = new Metro()
        when:
        metro.run(new File("src/test/resources/function.metro"))
        then:
        def commands = metro.metroWalker.getCommands()
        assert commands.size() == 4
        assert commands.get(0).matches("print\\(\"abc4\"\\)")
        assert commands.get(1).matches("print\\(\"abc5\"\\)")
        assert commands.get(3) == "print()"
        assert commands.get(2) == "print(\"hello\")"
    }

    def "repeated.metro should throw Ex"()
    {
        given:
        def metro = new Metro()
        when:
        metro.run(new File("src/test/resources/repeated.metro"))
        then:
        thrown(RuntimeException)
    }

    def "missing.metro should throw Ex"()
    {
        given:
        def metro = new Metro()
        when:
        metro.run(new File("src/test/resources/missing.metro"))
        then:
        thrown(RuntimeException)
    }
}