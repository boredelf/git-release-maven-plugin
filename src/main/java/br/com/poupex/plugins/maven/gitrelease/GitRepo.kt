package br.com.poupex.plugins.maven.gitrelease

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.transport.CredentialsProvider
import java.io.File


class GitRepo : AutoCloseable {

    private val repo: Git = try {
        Git.open(File("."))
    } catch (e: Exception) {
        throw RuntimeException("Couldn't open repository.", e)
    }

    override fun close() = repo.close()

    fun hasRemote() = try {
        repo.remoteList().call().size > 0
    } catch (e: Exception) {
        throw RuntimeException("Couldn't inspect if repository has remotes.", e)
    }

    fun isClean() = try {
        repo.status().call().isClean
    } catch (e: Exception) {
        throw RuntimeException("Couldn't inspect repository status.", e)
    }

    fun commit(pattern: String, message: String) {
        try {
            repo.add().addFilepattern(pattern).call()
            repo.commit().setMessage(message).call()
        } catch (e: Exception) {
            throw RuntimeException("Couldn't commit changes.", e)
        }
    }

    fun tag(name: String): Ref = try {
        repo.tag().setName(name).call()
    } catch (e: Exception) {
        throw RuntimeException("Couldn't perform tag.", e)
    }

    fun push(tag: Ref, credentialsProvider: CredentialsProvider) {
        try {
            repo.push().setCredentialsProvider(credentialsProvider).add(repo.repository.branch).add(tag).call()
        } catch (e: Exception) {
            throw RuntimeException("Couldn't push changes.", e)
        }
    }

}
