package br.com.poupex.plugins.maven.gitrelease

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.transport.CredentialsProvider
import java.io.File


class GitRepo {

    // TODO
    private val credentialsProvider = CredentialsProvider.getDefault()

    private val repo: Git = try {
        Git.open(File("."))
    } catch (e: Exception) {
        throw RuntimeException("Couldn't open repository.", e)
    }

    fun hasRemote() = try {
        repo.remoteList().call().size > 0
    } catch (e: Exception) {
        throw RuntimeException("Couldn't check if repository has remotes.", e)
    }

    fun isClean() = try {
        repo.status().call().isClean
    } catch (e: Exception) {
        throw RuntimeException("Couldn't check repository status.", e)
    }

    fun commit(pattern: String, author: PersonIdent, message: String) = try {
        repo.add().addFilepattern(pattern).call()
        repo.commit().setAuthor(author).setMessage(message).call()
    } catch (e: Exception) {
        throw RuntimeException("Couldn't commit changes.", e)
    }

    fun tag(tagger: PersonIdent, name: String): Ref = try {
        repo.tag().setName(name).setTagger(tagger).call()
    } catch (e: Exception) {
        throw RuntimeException("Couldn't perform tag.", e)
    }

    fun push(tag: Ref) = try {
        repo.push().setCredentialsProvider(credentialsProvider).add(tag.target).add(tag).call()
    } catch (e: Exception) {
        throw RuntimeException("Couldn't push changes.", e)
    }

}