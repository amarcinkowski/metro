import spock.lang.Specification
import spock.lang.Unroll


/**
 * Created by amarcinkowski on 26.02.17.
 */
class SimpleSpec extends Specification {

    @Unroll
    def "Metro.run with non-existing file should throw exception"()
    {
        given:
            def metro = new Metro()
        when:
            metro.run("")
        then:
            thrown(IOException)

    }

}