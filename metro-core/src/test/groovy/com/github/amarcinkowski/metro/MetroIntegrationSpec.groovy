package com.github.amarcinkowski.metro

import geb.spock.GebSpec

class MetroIntegrationSpec extends GebSpec {

    def "test1"() {
        print "qwe"
        when:
            to GooglePage
//            go "google.com"
//            $("#sidebar .sidemenu a", text: "jQuery-like API").click()
        then:
            assert 1 + 1 == 2
//            assert $("#main h1")*.text() == ["Navigating Content", "Form Control Shortcuts"]
//            assert $("#sidebar .sidemenu a", text: "jQuery-like API").parent().hasClass("selected")
    }
}
