package br.com.poupex.plugins.maven.gitrelease

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.revwalk.RevObject
import java.io.File


class GitRepo {

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

    fun tag(revision: RevObject, tagger: PersonIdent, name: String, message: String) = try {
        repo.tag().setName(name).setMessage(message).setTagger(tagger).setObjectId(revision).call()
    } catch (e: Exception) {
        throw RuntimeException("Couldn't perform tag.", e)
    }

}