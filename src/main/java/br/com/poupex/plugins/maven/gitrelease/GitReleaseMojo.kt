package br.com.poupex.plugins.maven.gitrelease

import br.com.poupex.plugins.maven.gitrelease.Increment.*
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import org.apache.maven.shared.utils.xml.Xpp3DomBuilder
import org.eclipse.jgit.lib.PersonIdent
import java.io.File
import java.io.FileReader

@Mojo(name = "execute")
class GitReleaseMojo : AbstractMojo() {

    @Parameter(defaultValue = "\${session}", readonly = true)
    private lateinit var session: MavenSession

    @Parameter(defaultValue = "\${project}", readonly = true)
    private lateinit var project: MavenProject

    @Parameter
    private var dryRun: Boolean = false

    @Parameter
    private var tagOnly: Boolean = false

    @Parameter
    private var increment: Increment = PATCH

    private val repo = GitRepo()

    override fun execute() {
        /*
        - Check if the repo has a remote
        - Check if it's clean (possible stash changes?)
        - Read poms
        - Only increment if desiresable
            - Increment version (could use parameters like increment="major|minor|patch(default)")
        - Commit and push poms (tag)
        - !!! Use server id to authenticate
         */
        val issues = checkForIssues(repo)
        if (issues.isNotEmpty()) {
            throw RuntimeException("The release couldn't be executed. Issues: " + issues.joinToString("\n"))
        }

        // Get pom
        val pom = Xpp3DomBuilder.build(FileReader(File(project.basedir, "pom.xml")))
        pom.get // TODO
        val releaseVersion = if (!tagOnly) getReleaseVersion(project) else project.version

        val jenkins = PersonIdent("jenkins", "jenkins@poupex.com.br")
        repo.tag(tagger = jenkins, name = releaseVersion)
    }

    private fun getReleaseVersion(project: MavenProject): String = try {
        val (major, minor, patch) = project.version.split('.').map(Integer::parseInt)
        when (increment) {
            MAJOR -> "${major + 1}.$minor.$patch"
            MINOR -> "$major.${minor + 1}.$patch"
            PATCH -> "$major.$minor.${patch + 1}"
        }
    } catch (e: NumberFormatException) {
        throw RuntimeException("Error while parsing project version: invalid number(s).")
    }

    private fun checkForIssues(repo: GitRepo) = mutableListOf<String>().apply {
        if (!repo.hasRemote()) {
            add("The repository doesn't appear to have a remote.")
        }
        if (!repo.isClean()) {
            add("The repository must be clean before release.")
        }
    }

}