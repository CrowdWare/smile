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

import java.io.File

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