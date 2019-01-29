package br.com.poupex.plugins.maven.gitrelease

import java.net.URI


object Uri {

    fun getHost(scmDevConnection: String): String = try {
        URI.create(scmDevConnection.replace("scm:git:", ""))?.host ?: ""
    } catch (e: IllegalArgumentException) {
        ""
    }

}