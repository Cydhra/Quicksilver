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
package net.cydhra.quicksilver.ui.input

import com.labymedia.ultralight.plugin.clipboard.UltralightClipboard
import org.lwjgl.glfw.GLFW

/**
 * Example clipboard using GLFW as backend.
 *
 * Taken from:
 * https://github.com/LabyMod/ultralight-java/blob/develop/example/lwjgl3-opengl
 * as allowed by its LGPL license
 */
class ClipboardAdapter : UltralightClipboard {
    /**
     * This is called by Ultralight when the clipboard should be cleared.
     */
    override fun clear() {
        GLFW.glfwSetClipboardString(0, "")
    }

    /**
     * This is called by Ultralight when the clipboard is requested as a string.
     *
     * @return The clipboard content as a string
     */
    override fun readPlainText(): String {
        return GLFW.glfwGetClipboardString(0)!!
    }

    /**
     * This is called by Ultralight when the clipboard content should be overwritten.
     *
     * @param text The plain text to write to the clipboard
     */
    override fun writePlainText(text: String) {
        GLFW.glfwSetClipboardString(0, text)
    }
}