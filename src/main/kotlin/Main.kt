package at.crowdware.nocode

import at.crowdware.nocode.utils.SmlNode
import at.crowdware.nocode.utils.getStringValue
import at.crowdware.nocode.utils.parseSML
import java.io.File

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        printUsage()
        return
    }
    when (args[0]) {
        "new" -> {
            val name = args.getOrNull(1)
            if (name == null) {
                printNewUsage()
            } else {
                createNewProject(name)
            }
        }
        "compose" -> {
            composeProject()
        }
        "doc" -> {
            val src = args.getOrNull(1)
            val out = args.getOrNull(2)
            if (src == null || out == null) {
                printDocUsage()
            } else {
                document(src, out)
            }
        }
        "version" -> println("smile CLI v0.1.0")
        else -> printUsage()
    }
}

fun printUsage() {
    println("""
        smile - Simple Markup Language CLI 😄

        Usage:
          smile new <project>           Create a new SML project
          smile compose <src>           Generate Compose UI code from SML
          smile build [--target=...]    Build the project (wasm, android, desktop)
          smile preview                 Start live preview (if supported)
          smile export --plugin=...     Export via plugin (e.g. ebook3)
          smile doc <src>               Generate doc files from sml definition code
          smile version                 Show version info

        Example:
          smile new yoga-app
          smile build --target=wasm
    """.trimIndent())
}

fun printNewUsage() {
    println("""
        smile new - create a new SML project

        Usage:
          smile new <projectName>

        Example:
          smile new yoga-app
    """.trimIndent())
}

fun printDocUsage() {
    println("""
        smile doc - create documentation

        Usage:
          smile doc <source> <out>

        Example:
          smile doc sml docs
    """.trimIndent())
}

fun createNewProject(projectName: String) {
    val root = File(projectName)
    if (root.exists()) {
        println("❌ Project '$projectName' already exists.")
        return
    }

    println("✨ Creating project: $projectName")

    File(root, "pages").mkdirs()

    File(root, "app.sml").writeText(
        """
        App {
            name: "$projectName"
            
            Theme {
                primary: "#FF5722"
                onPrimary: "#FFFFFF"
            }
        }
        """.trimIndent()
    )

    File(root.resolve("pages"), "home.sml").writeText(
        """
        Page {
            name: "home"
    
            Column {
                Markdown { text: "# Hello SML 👋" }
                Button { label: "Let's go!" link: "page:next" }
            }
        }
        """.trimIndent()
    )

    File(root.resolve("pages"), "next.sml").writeText(
        """
        Page {
            name: "next"
    
            Column {
                Markdown { text: "# Whats next?" }
                Button { label: "Back home" link: "page:home" }
            }
        }
        """.trimIndent()
    )

    println("✅ Project '$projectName' created.")
}

fun composeProject() {
    // here we have to call the default compose plugin
    println("✅ Compose app created.")
}

fun document(source: String, outputDir: String) {
    // we create documentation here
    val all = loadAllElementDefinitions(File(source))
    val childrenIndex = buildAllowedChildrenIndex(all)

    all.forEach { node ->
        smlToDoc(node, outputDir, childrenIndex)
    }
    /*
    val dir = File(source)
    if (dir.exists() && dir.isDirectory) {
        dir.listFiles()?.forEach { file ->
            if (file.isFile) {
                val sml = file.readText()
                val (parsedSml, error) = parseSML(sml)
                if (parsedSml != null) {
                    smlToDoc(parsedSml, outputDir, childrenIndex)
                }
            }
        }
    }*/
    println("✅ Documents created.")
}

//fun smlToDoc(node: SmlNode, outputDir: String) {
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