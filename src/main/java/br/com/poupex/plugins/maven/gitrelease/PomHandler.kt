package br.com.poupex.plugins.maven.gitrelease

import org.apache.maven.shared.utils.xml.pull.XmlPullParserException
import org.w3c.dom.NodeList
import java.io.File
import java.io.IOException
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPathConstants.NODESET
import javax.xml.xpath.XPathFactory


object PomHandler {

    private val xpath = XPathFactory.newInstance().newXPath()
    private val transformer = TransformerFactory.newInstance().newTransformer()

    fun updateVersion(to: String, on: File) = try {
        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(on).let { pom ->
            (xpath.evaluate("//project/version", pom, NODESET) as NodeList).item(0).setTextContent(to)
            transformer.setOutputProperty(OMIT_XML_DECLARATION, "yes")
            transformer.transform(DOMSource(pom), StreamResult(on))
            on.writeText("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" + on.readText())
        }
    } catch (e: XmlPullParserException) {
        throw RuntimeException("Couldn't parse POM file.", e)
    } catch (e: IOException) {
        throw RuntimeException("Couldn't write to POM file.", e)
    }

}
