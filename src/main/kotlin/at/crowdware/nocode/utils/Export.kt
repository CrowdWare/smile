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

import at.crowdware.nocode.plugin.ExportPlugin
import at.crowdware.nocode.plugin.PluginManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

fun printExportUsage() {
    println("""
        smile export - export project

        Usage:
          smile export --plugin=<plugin>

        Example:
          smile export html
    """.trimIndent())
}

suspend fun exportProject(pluginName: String, source: String, output: String) {
    val pluginToUse = PluginManager.all()
        .filterIsInstance<ExportPlugin>()
        .firstOrNull { it.id == pluginName }
    if (pluginToUse != null) {
        var outputDir = File(output)
        pluginToUse.export(source, outputDir) { line ->
            println(line)
            }
    } else {
        println("Plugin with the name $pluginName not installed")
    }
    println("✅ Compose app exported.")
}