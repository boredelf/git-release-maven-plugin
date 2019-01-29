package br.com.poupex.plugins.maven.gitrelease

import org.apache.maven.shared.utils.xml.Xpp3DomBuilder
import org.apache.maven.shared.utils.xml.Xpp3DomWriter
import org.apache.maven.shared.utils.xml.pull.XmlPullParserException
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException


object PomHandler {

    fun updateVersion(to: String, on: File) = try {
        val pom = Xpp3DomBuilder.build(FileReader(on))
        pom.getChild("version").value = to
        Xpp3DomWriter.write(FileWriter(on), pom)
    } catch (e: XmlPullParserException) {
        throw RuntimeException("Couldn't parse POM file.", e)
    } catch (e: IOException) {
        throw RuntimeException("Couldn't write to POM file.", e)
    }

}