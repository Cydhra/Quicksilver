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

import com.labymedia.ultralight.input.UltralightCursor
import org.lwjgl.glfw.GLFW

/**
 * Utility class for controlling GLFW cursors.
 *
 * Taken from:
 * https://github.com/LabyMod/ultralight-java/blob/develop/example/lwjgl3-opengl
 * as allowed by its LGPL license
 *
 * @param window GLFW window handle
 */
@Suppress("JoinDeclarationAndAssignment")
class CursorAdapter(private val window: Long) {
    private val iBeamCursor: Long
    private val crosshairCursor: Long
    private val handCursor: Long
    private val hresizeCursor: Long
    private val vresizeCursor: Long

    /**
     * Creates a new [CursorAdapter] for the given window.
     *
     * @param window The window to manage cursors on
     */
    init {
        iBeamCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR)
        crosshairCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_CROSSHAIR_CURSOR)
        handCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR)
        hresizeCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HRESIZE_CURSOR)
        vresizeCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR)
    }

    /**
     * Signals this [CursorAdapter] that the cursor has been updated and needs to be updated on the GLFW side
     * too.
     *
     * @param cursor The new [UltralightCursor] to display
     */
    fun notifyCursorUpdated(cursor: UltralightCursor?) {
        when (cursor) {
            UltralightCursor.CROSS -> GLFW.glfwSetCursor(window, crosshairCursor)
            UltralightCursor.HAND -> GLFW.glfwSetCursor(window, handCursor)
            UltralightCursor.I_BEAM -> GLFW.glfwSetCursor(window, iBeamCursor)
            UltralightCursor.EAST_WEST_RESIZE -> GLFW.glfwSetCursor(window, hresizeCursor)
            UltralightCursor.NORTH_SOUTH_RESIZE -> GLFW.glfwSetCursor(window, vresizeCursor)
            else ->                 // No matching GLFW cursor
                GLFW.glfwSetCursor(window, 0)
        }
    }

    /**
     * Frees GLFW resources allocated by this [CursorAdapter].
     */
    fun cleanup() {
        GLFW.glfwDestroyCursor(vresizeCursor)
        GLFW.glfwDestroyCursor(hresizeCursor)
        GLFW.glfwDestroyCursor(handCursor)
        GLFW.glfwDestroyCursor(crosshairCursor)
        GLFW.glfwDestroyCursor(iBeamCursor)
    }
}