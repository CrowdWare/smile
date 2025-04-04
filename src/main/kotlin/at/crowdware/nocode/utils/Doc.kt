/*
 * Copyright (C) 2025 CrowdWare
 *
 * This file is part of Smile.
 *
 *  Smile is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Smile is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Smile.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.crowdware.nocode.at.crowdware.nocode.utils

import at.crowdware.nocode.utils.SmlNode
import at.crowdware.nocode.utils.getStringValue
import at.crowdware.nocode.utils.parseSML
import java.io.File

fun printDocUsage() {
    println("""
        smile doc - create documentation

        Usage:
          smile doc <source> <out>

        Example:
          smile doc sml docs
    """.trimIndent())
}

fun document(source: String, outputDir: String) {
    // we create documentation here
    val all = loadAllElementDefinitions(File(source))
    val childrenIndex = buildAllowedChildrenIndex(all)

    // cleanup first
    val out = File(outputDir)
    if (out.exists() && out.isDirectory) {
        out.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                file.deleteRecursively()
            } else {
                file.delete()
            }
        }
    }

    all.forEach { node ->
        smlToDoc(node, outputDir, childrenIndex)
    }
    generateDocsHtml(outputDir)

    println("✅ Documents created.")
}

fun smlToDoc(node: SmlNode, outputDir: String, childrenIndex: Map<String, List<String>>) {
    val name = getStringValue(node, "name", "Unknown")
    val description = getStringValue(node, "description", "")
    val html = StringBuilder()

    html.appendLine("<!DOCTYPE html>")
    html.appendLine("<html><head><meta charset=\"utf-8\"><title>$name</title></head><body>")
    html.appendLine("<!-- WORDRESS PART -->")
    html.appendLine("<h1>$name</h1>")
    if (description.isNotBlank()) {
        html.appendLine("<p>$description</p>")
    }

    node.children.firstOrNull { it.name == "Properties" }?.let { props ->
        val allProps = props.children.filter { it.name == "Property" }
        if (allProps.isNotEmpty()) {
            html.appendLine("<h2>Properties</h2>")
            html.appendLine("<figure class=\"sml-block-table\">")
            html.appendLine("<table class=\"has-fixed-layout\">")
            html.appendLine("<tr><th>Name</th><th>Type</th><th>Default</th><th>Description</th></tr>")
            allProps.forEach { prop ->
                val propName = getStringValue(prop, "name", "")
                val type = getStringValue(prop, "type", "")
                val default = getStringValue(prop, "default", "")
                val desc = getStringValue(prop, "description", "")
                html.appendLine("<tr><td>$propName</td><td>$type</td><td>$default</td><td>$desc</td></tr>")
            }
            html.appendLine("</table>")
            html.appendLine("</figure>")
        }
    }

    node.children.firstOrNull { it.name == "AllowedRoots" }?.let { roots ->
        val entries = roots.children.filter { it.name == "Root" }.map {
            getStringValue(it, "name", "")
        }.filter { it.isNotBlank() }

        if (entries.isNotEmpty()) {
            html.appendLine("<h2>Allowed Roots</h2><ul>")
            entries.forEach { root -> html.appendLine("<li><a href=\"/${root.toLowerCase()}/\">$root</a></li>") }
            html.appendLine("</ul>")
        }
    }

    // Allowed Children
    val allowedChildren = childrenIndex[name]?.sorted()
    if (!allowedChildren.isNullOrEmpty()) {
        html.appendLine("<h2>Allowed Children</h2><ul>")
        allowedChildren.forEach { child ->
            html.appendLine("<li><a href=\"/${child.toLowerCase()}/\">$child</a></li>")
        }
        html.appendLine("</ul>")
    }

    html.appendLine("<style>")
    html.appendLine(".sml-block-table {")
    html.appendLine("overflow-x:auto;")
    html.appendLine("}")
    html.appendLine(".sml-block-table table {")
    html.appendLine("border-collapse:collapse;")
    html.appendLine("width:100%")
    html.appendLine("}")
    html.appendLine(".sml-block-table thead {")
    html.appendLine("border-bottom:3px solid")
    html.appendLine("}")
    html.appendLine(".sml-block-table tfoot {")
    html.appendLine("border-top:3px solid")
    html.appendLine("}")
    html.appendLine(".sml-block-table td,")
    html.appendLine(".sml-block-table th {")
    html.appendLine("border:1px solid;")
    html.appendLine(" padding:.5em")
    html.appendLine("}")
    html.appendLine("</style>")
    html.appendLine("<p><a href=\"/sml-element-reference\">← Back to Element Reference</a></p>")
    html.appendLine("<!-- WORDRESS PART -->")
    html.appendLine("</body></html>")

    val file = File(outputDir, "$name.html")
    file.parentFile?.mkdirs()
    file.writeText(html.toString())
}

fun loadAllElementDefinitions(sourceDir: File): List<SmlNode> =
    sourceDir.listFiles()
        ?.filter { it.isFile && it.extension == "sml" }
        ?.mapNotNull { file ->
            val (parsed, error) = parseSML(file.readText())
            parsed
        } ?: emptyList()

fun buildAllowedChildrenIndex(elements: List<SmlNode>): Map<String, List<String>> {
    val map = mutableMapOf<String, MutableList<String>>()
    for (node in elements) {
        val elementName = getStringValue(node, "name", "")
        node.children.firstOrNull { it.name == "AllowedRoots" }?.children
            ?.filter { it.name == "Root" }
            ?.forEach { root ->
                val rootName = getStringValue(root, "name", "")
                map.getOrPut(rootName) { mutableListOf() }.add(elementName)
            }
    }
    return map
}

fun generateDocsHtml(outputDir: String) {
    val html = StringBuilder()
    val docDir = File(outputDir)

    html.appendLine("<!DOCTYPE html>")
    html.appendLine("<html><head><meta charset=\"utf-8\"><title>SML Documentation Index</title></head><body>")
    html.appendLine("<!-- WORDRESS PART -->")
    html.appendLine("<h1>SML Element Reference</h1>")
    html.appendLine("<ul>")

    val files = docDir.listFiles()?.filter { it.extension == "html" && it.name != "index.html" }?.sortedBy { it.nameWithoutExtension }

    files?.forEach { file ->
        val name = file.nameWithoutExtension
        html.appendLine("<li><a href=\"${file.name.lowercase().substringBefore(".html")}\">$name</a></li>")
    }

    html.appendLine("</ul>")
    html.appendLine("<p><a href=\"/\">← Back to Home</a></p>")
    html.appendLine("<!-- WORDRESS PART -->")
    html.appendLine("</body></html>")

    File(docDir, "index.html").writeText(html.toString())
}