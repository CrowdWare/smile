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

package at.crowdware.nocode

import at.crowdware.nocode.at.crowdware.nocode.utils.*
import java.io.File

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
        "newai" -> {
            val docs = generateCombinedContext(File("sml"))
            /*
            val session = AiSession()
            session.warmUp(docs)

            val layout = session.ask("Create a Page with a Column containing a Image and a Button")
            println("🧠 Layout:\n$layout")

            val extension = session.ask("Now add a Markdown block at the top")
            println("➕ Extension:\n$extension")
            */
            generateSmlProject("Baue eine App mit der man Bücher auflisten kann.", docs, File("test") )
        }
        "compose" -> {
            composeProject()
        }
        "build" -> {
            build()
        }
        "export" -> {
            println("smile export is not yet implemented")
        }
        "preview" -> {
            println("smile preview is not yet implemented")
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
        "version" -> println("smile CLI v1.0.0")
        else -> printUsage()
    }
}







