package br.com.poupex.plugins.maven.gitrelease

import br.com.poupex.plugins.maven.gitrelease.Increment.*
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import org.apache.maven.settings.Server
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File

@Mojo(name = "execute")
class GitReleaseMojo : AbstractMojo() {

    @Parameter(defaultValue = "\${project}", readonly = true)
    lateinit var project: MavenProject

    @Parameter(defaultValue = "\${project.scm.developerConnection}", readonly = true)
    var repoUri: String = ""

    @Parameter(defaultValue = "\${session.settings.servers}", readonly = true)
    var servers: List<Server> = emptyList()

    @Parameter(property = "increment")
    var increment: String = "patch"

    @Parameter(property = "tagOnly")
    var tagOnly: Boolean = false

    override fun execute() = GitRepo().use { repo ->
        log.info("Checking for issues before the release...")
        GitReleaseMojoRequirements.inspect(repo, repoUri, servers).let { issues ->
            if (issues.isNotEmpty()) {
                issues.forEach(log::error)
                throw MojoFailureException("There are issues with the project. Resolve them and try again.")
            }
        }

        val releaseVersion = if (tagOnly) {
            log.info("Tag only: skipping version bumping...")
            project.version
        } else getReleaseVersion(project).apply {
            log.info("Bumping version to $this...")
            PomHandler.updateVersion(to = this, on = File(project.basedir, "pom.xml"))
            log.info("Committing changes...")
            repo.commit("pom.xml", "Release $this")
        }

        log.info("Tagging release...")
        val tag = repo.tag(releaseVersion)
        log.info("Pushing changes to remote...")
        repo.push(tag, getRepositoryCredentials(repoUri, servers))
        log.info("All done.")
    }

    private fun getRepositoryCredentials(repoUri: String, servers: List<Server>) =
        Uri.getHost(repoUri).let { host ->
            servers.find { server -> server.id == host }.let { server ->
                UsernamePasswordCredentialsProvider(server?.username, server?.password)
            }
        }

    private fun getReleaseVersion(project: MavenProject): String = try {
        val (major, minor, patch) = project.version.split('.').map(Integer::parseInt)
        when (Increment.from(increment)) {
            MAJOR -> "${major + 1}.0.0"
            MINOR -> "$major.${minor + 1}.0"
            PATCH -> "$major.$minor.${patch + 1}"
        }
    } catch (e: NumberFormatException) {
        throw RuntimeException("Error while parsing project version: invalid number(s).")
    }

}
