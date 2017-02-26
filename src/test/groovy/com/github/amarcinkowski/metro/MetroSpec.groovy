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

    def "com.github.amarcinkowski.metro.Metro.run with simple.metro should parse properly"()
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

}