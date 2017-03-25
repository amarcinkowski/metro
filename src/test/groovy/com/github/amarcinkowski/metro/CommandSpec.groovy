package com.github.amarcinkowski.metro

import com.github.amarcinkowski.metro.command.SystemCommand
import spock.lang.Specification

/**
 * Created by amarcinkowski on 25.03.17.
 */
class CommandSpec extends Specification {

    def 'running bash command should give expected ouput'() {
        given:
        def typedArgs = [command: command, commandArgs: arguments, timeout: timeout] as HashMap
        def systemCommand = new SystemCommand([] as Vector, typedArgs)
        expect:
        systemCommand.execute() == output
        where:
        command     | arguments                          | output                    | timeout
        '/bin/bash' | '-c|echo "Hello world from bash";' | 'Hello world from bash\n' | "120"
        '/bin/bash' | '-c|echo "Hello world from bash";' | 'NO-OUTPUT'               | "0"
        '/bin/bash' | 'exit'                             | ''                        | "120"
        '/bin/bash' | ''                                 | ''                        | "1"
        '/bin/bash' | null                               | ''                        | "1"
        '/bin/bash' | ''                                 | 'NO-OUTPUT'               | "0"
        '/bin/bash' | null                               | 'NO-OUTPUT'               | "0"
        '/bin/bash' | '-c|echo "abc" > out'              | 'NO-OUTPUT'               | "0"
        '/bin/bash' | '-c|echo "zxc" >> out'             | 'NO-OUTPUT'               | "0"
        '/bin/bash' | '-c|cat out'                       | 'abc\nzxc\n'              | "1"
    }
}
