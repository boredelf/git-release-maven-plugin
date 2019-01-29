package br.com.poupex.plugins.maven.gitrelease

import org.apache.maven.settings.Server

object GitReleaseMojoRequirements {

    fun inspect(repo: GitRepo, repoUri: String, servers: List<Server>) = mutableListOf<String>().apply {
        if (!repo.hasRemote()) {
            add("The repository doesn't appear to have a remote.")
        }
        if (!repo.isClean()) {
            add("The repository must be clean before release.")
        }
        if (repoUri.isEmpty()) {
            add("There is no developerConnection defined in project's pom.xml")
        }
        if (servers.isEmpty()) {
            add("There are no <server> entries in settings.xml.")
        }

        if (repoUri.isNotEmpty() && servers.isNotEmpty()) {
            val repoUriHost = Uri.getHost(repoUri)
            if (repoUriHost.isNotEmpty()) {
                servers.filter { server -> server.id == repoUriHost }.ifEmpty {
                    add("No server found in settings.xml for this host: $repoUri.")
                }
            } else {
                add("The repository host in developerConnection is invalid: \"$repoUriHost\".")
            }
        }
    }

}