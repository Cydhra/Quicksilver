/*
 * Ultralight Java - Java wrapper for the Ultralight web engine
 * Copyright (C) 2020 - 2021 LabyMedia and contributors
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package net.cydhra.quicksilver.ui.util

import com.labymedia.ultralight.plugin.logging.UltralightLogLevel
import com.labymedia.ultralight.plugin.logging.UltralightLogger

/**
 * Example implementation of a logger
 */
class ExampleLogger : UltralightLogger {
    /**
     * This is called by Ultralight every time a message needs to be logged. Note that Ultralight messages may include
     * new lines, so if you want really pretty log output reformat the string accordingly.
     *
     *
     * This logger is **NOT** called for `console.log` messages, see [ExampleViewListener.onAddConsoleMessage] for that
     * instead.
     *
     * @param level   The level of the message
     * @param message The message to log
     */
    override fun logMessage(level: UltralightLogLevel, message: String) {
        when (level) {
            UltralightLogLevel.ERROR -> System.err.println("[Ultralight/ERR] $message")
            UltralightLogLevel.WARNING -> System.err.println("[Ultralight/WARN] $message")
            UltralightLogLevel.INFO -> println("[Ultralight/INFO] $message")
        }
    }
}