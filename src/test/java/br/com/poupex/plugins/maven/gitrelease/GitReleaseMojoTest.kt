package br.com.poupex.plugins.maven.gitrelease

import org.eclipse.jgit.api.Git
import org.junit.Test
import java.io.File

internal class GitReleaseMojoTest {

    @Test
    fun execute() {
        val repo = Git.open(File("."))
    }
}