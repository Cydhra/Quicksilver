package net.cydhra.quicksilver.ui

import net.cydhra.quicksilver.ui.input.CursorAdapter
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.GL_MULTISAMPLE
import org.lwjgl.system.Callback
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.nio.IntBuffer
import java.util.concurrent.Executors

object UIController {

    private lateinit var webController: WebController

    // dedicated UI thread
    private val uiThreadExecutor = Executors.newSingleThreadExecutor()

    // window handle
    private var glfwWindowHandle: Long = -1

    fun setupWindow() {
        uiThreadExecutor.submit(::doSetupWindow)
    }

    fun stop() {
        // TODO
    }

    /**
     * Window setup
     */
    private fun doSetupWindow() {
        glfwSetErrorCallback(this::onGLFWError);

        if (!glfwInit()) {
            throw IllegalStateException("Failed to initialize GLFW");
        }

        glfwWindowHandle = glfwCreateWindow(1280, 720, "Quicksilver", MemoryUtil.NULL, MemoryUtil.NULL);
        if (glfwWindowHandle == MemoryUtil.NULL) {
            throw IllegalStateException("Failed to create a GLFW window");
        }

        // Make sure to update the framebuffer size when resizing
        glfwSetFramebufferSizeCallback(glfwWindowHandle, this::updateSize);

        webController = WebController(CursorAdapter(glfwWindowHandle))

        // Register all the GLFW callbacks required by this application
        glfwSetWindowContentScaleCallback(glfwWindowHandle, webController.inputAdapter::windowContentScaleCallback);
        glfwSetKeyCallback(glfwWindowHandle, webController.inputAdapter::keyCallback)
        glfwSetCharCallback(glfwWindowHandle, webController.inputAdapter::charCallback)
        glfwSetCursorPosCallback(glfwWindowHandle, webController.inputAdapter::cursorPosCallback)
        glfwSetMouseButtonCallback(glfwWindowHandle, webController.inputAdapter::mouseButtonCallback)
        glfwSetScrollCallback(glfwWindowHandle, webController.inputAdapter::scrollCallback)
        glfwSetWindowFocusCallback(glfwWindowHandle, webController.inputAdapter::focusCallback)

        centerWindow()
        drawLoop()
    }


    private fun centerWindow() {
        // Create a memory stack so we don't have to worry about free's
        MemoryStack.stackPush().use { stack ->
            // Retrieve current monitor of the window
            var monitor = glfwGetWindowMonitor(glfwWindowHandle)
            if (monitor == MemoryUtil.NULL) {
                // The window is not on any monitor, get the primary one
                monitor = glfwGetPrimaryMonitor()
            }

            // If there is no monitor, we can't center the window
            if (monitor == MemoryUtil.NULL) {
                return
            }

            // Retrieve the video mode of the monitor
            val videoMode = glfwGetVideoMode(monitor)
                ?: // The monitor has no video mode?
                return

            // Get a buffer with 2 ints to store the position of the monitor in
            val monitorPosBuffer = stack.callocInt(2)

            // Store the x position in slot 0 and the y position in slot 1 of the buffer
            glfwGetMonitorPos(
                monitor,
                monitorPosBuffer.slice().position(0) as IntBuffer, monitorPosBuffer.slice().position(1) as IntBuffer
            )

            // Extract the x and y positions from the buffer
            val monitorX = monitorPosBuffer[0]
            val monitorY = monitorPosBuffer[1]

            // Get a buffer with 2 ints to store the size of the window in
            val windowSizeBuffer = stack.callocInt(2)

            // Store the window width in slot 0 and the window height in slot 1 of the buffer
            glfwGetWindowSize(
                glfwWindowHandle,
                windowSizeBuffer.slice().position(0) as IntBuffer, windowSizeBuffer.slice().position(1) as IntBuffer
            )

            // Extract the window width and window height from the buffer
            val windowWidth = windowSizeBuffer[0]
            val windowHeight = windowSizeBuffer[1]

            // Center the window on the monitor
            glfwSetWindowPos(
                glfwWindowHandle,
                monitorX + (videoMode.width() - windowWidth) / 2,
                monitorY + (videoMode.height() - windowHeight) / 2
            )
        }
    }

    private fun drawLoop() {
        glfwMakeContextCurrent(glfwWindowHandle);
        glfwSwapInterval(1);

        // setup GL
        GL.createCapabilities();

        // Manually update focus for the first time
        // Manually update focus for the first time
        webController.inputAdapter.focusCallback(
            glfwWindowHandle,
            glfwGetWindowAttrib(glfwWindowHandle, GLFW_FOCUSED) != 0
        )

        MemoryStack.stackPush().use { stack ->
            // Update window size for the first time
            val sizeBuffer = stack.callocInt(2)

            // Retrieve the size into the int buffer
            glfwGetWindowSize(
                glfwWindowHandle,
                sizeBuffer.slice().position(0) as IntBuffer, sizeBuffer.slice().position(1) as IntBuffer
            )

            // Update the size
            updateSize(glfwWindowHandle, sizeBuffer[0], sizeBuffer[1])

            // Retrieve framebuffer size for scale calculation
            val framebufferSizeBuffer = stack.callocInt(2)

            // Retrieve the size into the int buffer
            glfwGetFramebufferSize(
                glfwWindowHandle,
                framebufferSizeBuffer.slice().position(0) as IntBuffer, sizeBuffer.slice().position(1) as IntBuffer
            )

            // Calculate scale
            var xScale = framebufferSizeBuffer[0].toFloat() / sizeBuffer[0].toFloat()
            var yScale = framebufferSizeBuffer[1].toFloat() / sizeBuffer[1].toFloat()

            // Fix up scale in case it gets corrupted... somehow
            if (xScale == 0.0f) {
                xScale = 1.0f
            }
            if (yScale == 0.0f) {
                yScale = 1.0f
            }

            // Update the scale
            webController.inputAdapter.windowContentScaleCallback(glfwWindowHandle, xScale, yScale)
        }

        glEnable(GL_MULTISAMPLE)
        glClearColor(.2f, .2f, .2f, 1.0f)

        // Load a local test file
        webController.loadURL("file:///test.html")

        // Keep running until a window close is requested
        while (!glfwWindowShouldClose(glfwWindowHandle)) {
            // Poll events to keep the window responsive
            glfwPollEvents()

            // Make sure to update the window
            webController.update()

            // Clear the color and depth buffer and then draw
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            webController.render()

            glfwSwapBuffers(glfwWindowHandle)
            // TODO poll external events
        }

        doStop()
        shutdown()
    }

    /**
     * Adjusts the viewport size to the given size.
     *
     * @param window The window the viewport has changed on
     * @param width  The new width of the viewport
     * @param height The new height of the viewport
     */
    private fun updateSize(window: Long, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        webController.resize(width, height)
    }

    /**
     * Cleanup GLFW resources and close window
     */
    private fun doStop() {
        if (glfwWindowHandle != MemoryUtil.NULL) {
            glfwDestroyWindow(glfwWindowHandle);
        }

        glfwTerminate();
    }

    private fun shutdown() {
        this.uiThreadExecutor.shutdown()
    }

    /**
     * Callback notified when an error occurs in GLFW.
     */
    private fun onGLFWError(error: Int, message: Long) {
        val strMessage = MemoryUtil.memUTF8(message)
        System.err.println("[GLFW] Error($error): $strMessage")
    }

    /**
     * Sets a GLFW callback and frees the old callback if it exists.
     *
     * @param setter   The function to use for setting the new callback
     * @param newValue The new callback
     * @param <T>      The type of the new callback
     * @param <C>      The type of the old callback
    </C></T> */
    private fun <T, C : Callback?> setCallback(setter: (T) -> C, newValue: T) {
        val oldValue: C = setter(newValue)
        oldValue?.free()
    }

    /**
     * Sets a GLFW callback and frees the old callback if it exists.
     *
     * @param setter   The function to use for setting the new callback
     * @param newValue The new callback
     * @param <T>      The type of the new callback
     * @param <C>      The type of the old callback
    </C></T> */
    private fun <T, C : Callback?> setCallback(setter: (Long, T) -> C, newValue: T) {
        val oldValue: C? = setter(glfwWindowHandle, newValue)
        oldValue?.free()
    }
}