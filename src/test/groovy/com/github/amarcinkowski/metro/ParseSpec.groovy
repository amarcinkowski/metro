package com.github.amarcinkowski.metro

import spock.lang.Specification
import spock.lang.Unroll

class ParseSpec extends Specification {

    @Unroll
    "#file should throw #exception"()
    {
        given:
        def metro = new Metro()
        when:
        metro.run(new File("src/test/resources/" + file))
        then:
        thrown(exception)
        where:
        file                    | exception
        "missing.file"          | IOException
        "missing.metro"         | MissingFunctionException
        "repeated.metro"        | DuplicateFunctionException
        "wrong_num_args.metro"  | WrongNumberArgsException
        "param_not_set.metro"   | ParamNotSetException

    }

    @Unroll
    "#file should parse properly"()
    {
        given:
        def metro = new Metro()
        when:
        metro.run(new File("src/test/resources/" + file))
        then:
        def commands = metro.metroWalker.getCommands()
        assert commands == commandsResult
        where:
        file                        | commandsResult
        "comments.metro"            | ['print("block")','print("cf21")','print("cf22")']
        "function.metro"            | ['print("abc4")','print("abc5")','print("hello")','print("hello2")', 'print()']
        "global_variables.metro"    | ['print("qwe var")', 'print("zxc var")']
        "multiparam_command.metro"  | ['print("q")','print("w")','print("q","w")']
        "parameters.metro"          | ['print("f3a")','print("f3b")','print("f2")','print("f1")']
        "simple.metro"              | ['print("foobar")', 'print()', 'print("Hello world","Cześć Świecie")']
    }

}