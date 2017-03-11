package com.github.amarcinkowski.metro

import com.github.amarcinkowski.metro.command.Command
import com.github.amarcinkowski.metro.command.PrintCommand
import com.github.amarcinkowski.metro.exceptions.DuplicateFunctionException
import com.github.amarcinkowski.metro.exceptions.MissingCommandException
import com.github.amarcinkowski.metro.exceptions.MissingFunctionException
import com.github.amarcinkowski.metro.exceptions.MissingMetroSourceException
import com.github.amarcinkowski.metro.exceptions.NonUniqueParamsException
import com.github.amarcinkowski.metro.exceptions.ParamNotSetException
import com.github.amarcinkowski.metro.exceptions.WrongNumberArgsException
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
        file                     | exception
        "missing.file"           | MissingMetroSourceException
        "missing_function.metro" | MissingFunctionException
        "missing_command.metro"  | MissingCommandException
        "repeated.metro"         | DuplicateFunctionException
        "wrong_num_args.metro"   | WrongNumberArgsException
        "param_not_set.metro"    | ParamNotSetException
        "non_unique_params.metro"| NonUniqueParamsException

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
        "global_variables.metro"    | ['print("qwe var")', 'print("qwe ņ~!@#$%^&*(){}:")', 'print("zxc var")']
        "local_before_global.metro" | ['print("zxc local")']
        "many_args.metro"           | ['print("a1","a2","a3","a4")', 'print("a1","a2","a3","a4","a5","a6","a7")']
        "mixed_args_params.metro"   | []
        "param_type.metro"          | ['print("zxc")', 'print("zxc","qwe")']
        "multiparam_command.metro"  | ['print("q")','print("w")','print("q","w","q","q","w")']
        "parameters.metro"          | ['print("f3a")','print("f3b")','print("f2")','print("f1")']
        "simple.metro"              | ['print("foobar")', 'print()', 'print("Hello world","Cześć Świecie")']
    }

    @Unroll
    "#file should give commands as expected"()
    {
        given:
        def metro = new Metro()
        when:
        metro.run(new File("src/test/resources/" + file))
        then:
        def commands = metro.metroWalker.getCommandsFifo()
        assert commands == commandsResult as Vector<Command>
        where:
        file                        | commandsResult
        "comments.metro"            | [getPrintCommand(['"block"']),getPrintCommand(['"cf21"']), getPrintCommand(['"cf22"'])]
        "function.metro"            | [getPrintCommand(['"abc4"']),getPrintCommand(['"abc5"']),getPrintCommand(['"hello"']),getPrintCommand(['"hello2"']), getPrintCommand([])]
        "global_variables.metro"    | [getPrintCommand(['"qwe var"']), getPrintCommand(['"qwe ņ~!@#$%^&*(){}:"']), getPrintCommand(['"zxc var"'])]
        "local_before_global.metro" | [getPrintCommand(['"zxc local"'])]
        "many_args.metro"           | [getPrintCommand(['"a1","a2","a3","a4"']), getPrintCommand(['"a1","a2","a3","a4","a5","a6","a7"'])]
        "param_type.metro"          | [getPrintCommand(['"zxc"']), getPrintCommand(['"zxc"', '"qwe"'])]
        "multiparam_command.metro"  | [getPrintCommand(['"q"']),getPrintCommand(['"w"']),getPrintCommand(['"q"', '"w"', '"q"', '"q"', '"w"'])]
        "parameters.metro"          | [getPrintCommand(['"f3a"']),getPrintCommand(['"f3b"']),getPrintCommand(['"f2"']),getPrintCommand(['"f1"'])]
        "simple.metro"              | [getPrintCommand(['"foobar"']), getPrintCommand([]), getPrintCommand(['"Hello world","Cześć Świecie"'])]
    }

    def getPrintCommand(args) {
        return new PrintCommand(args as Vector<String>)
    }

    def "Lombok equals and hashcode works for Commands"()
    {
        given:
        def printCommand1 = new PrintCommand(["a","b","c"] as Vector<String>)
        def printCommands1 = [new PrintCommand(["a","b","c"] as Vector<String>),new PrintCommand(["a","b","c"] as Vector<String>)]
        def printCommand2 = getPrintCommand(["a","b","c"])
        def printCommands2 = [getPrintCommand(["a","b","c"]),getPrintCommand(["a","b","c"])]
        expect:
        printCommand1 == printCommand2
        printCommands1 == printCommands2
    }

}