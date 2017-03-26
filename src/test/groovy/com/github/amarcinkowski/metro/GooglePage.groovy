package com.github.amarcinkowski.metro

import geb.Page

/**
 * Created by amarcinkowski on 25.03.17.
 */
class GooglePage extends Page {

    static url = "http://google.com"

    static at = { title.startsWith("Google")}

    static content = {

    }
}
