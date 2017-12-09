package com.github.amarcinkowski.metro

import com.github.amarcinkowski.metro.command.KeyboardCommand
import com.github.amarcinkowski.metro.command.PauseCommand
import com.github.amarcinkowski.metro.command.PrintCommand
import com.github.amarcinkowski.metro.command.SystemCommand
import spock.lang.Specification
import spock.lang.Unroll

class CommandSpec extends Specification {

    def 'running bash command should give expected ouput'() {
        given:
        def typedArgs = [command: command, commandArgs: arguments, timeout: timeout] as HashMap
        def systemCommand = new SystemCommand([] as Vector, typedArgs)
        expect:
        systemCommand.execute() == output
        where:
        command     | arguments                          | output                    | timeout
        '/bin/bash' | '-c\necho "Hello world from bash"' | 'Hello world from bash\n' | "120"
        '/bin/bash' | '-c\necho "Hello world from bash"' | 'NO-OUTPUT'               | "0"
        '/bin/bash' | 'exit'                             | ''                        | "120"
        '/bin/bash' | ''                                 | ''                        | "1"
        '/bin/bash' | null                               | ''                        | "1"
        '/bin/bash' | ''                                 | 'NO-OUTPUT'               | "0"
        '/bin/bash' | null                               | 'NO-OUTPUT'               | "0"
        '/bin/bash' | '-c\necho "abc" > out'             | 'NO-OUTPUT'               | "0"
        '/bin/bash' | '-c\necho "zxc" >> out'            | 'NO-OUTPUT'               | "0"
        '/bin/bash' | '-c\ncat out'                      | 'abc\nzxc\n'              | "1"
    }

    static system = new SystemCommand([] as Vector, [command: '/bin/bash', commandArgs: '-c\necho "Hello world from bash";', timeout: "1"] as HashMap)
    static print = new PrintCommand(["hello"] as Vector, [] as HashMap)
    static pause = new PauseCommand(["100"] as Vector, [] as HashMap)
    static keyboard = new KeyboardCommand(["abc [enter]", "[alt-tab][tab]def", "z[enter]c", "[ctrl-c][ctrl-v]", "ABC", "[ctrl-alt-delete]"] as Vector, [] as HashMap)

    @Unroll
    def 'command #command.class should work properly'() {
        expect:
        def out = command.execute()
        println "Command output:" + out
        assert out.matches(output)
        where:
        command | output
        system  | 'Hello world from bash\n'
        print   | 'hello'
        pause   | 'Paused for 1[0-9]{2}'
        keyboard               | 'Num of chars typed: 19'
    }
}
