package at.crowdware.nocode

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
        "version" -> println("smile CLI v0.1.0")
        else -> printUsage()
    }
}

fun printUsage() {
    println("""
        smile - Simple Markup Language CLI 😄

        Usage:
          smile new <project>           Create a new SML project
          smile build [--target=...]    Build the project (wasm, android, desktop)
          smile compose <src>           Generate Compose UI code from SML
          smile preview                 Start live preview (if supported)
          smile export --plugin=...     Export via plugin (e.g. ebook3)
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