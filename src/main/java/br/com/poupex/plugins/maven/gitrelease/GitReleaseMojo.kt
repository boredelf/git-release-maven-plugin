package br.com.poupex.plugins.maven.gitrelease

import br.com.poupex.plugins.maven.gitrelease.Increment.PATCH
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import org.eclipse.jgit.revwalk.RevObject


@Mojo(name = "execute")
class GitReleaseMojo : AbstractMojo() {

    @Parameter(defaultValue = "\${session}", readonly = true)
    private lateinit var session: MavenSession

    @Parameter(defaultValue = "\${project}", readonly = true)
    private lateinit var project: MavenProject

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

        val releaseVersion = if (tagOnly) project.version else {
            // Increment logic
            ""
        }

        // Stopped here
        repo.tag(revision = "", tagger = "", name = "", message = "")
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