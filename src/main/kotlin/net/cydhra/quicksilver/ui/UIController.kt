package net.cydhra.quicksilver.ui

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.GL_MULTISAMPLE
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.nio.IntBuffer
import java.util.concurrent.Executors


object UIController {

    private val webController = WebController()

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
        if(!glfwInit()) {
            throw IllegalStateException("Failed to initialize GLFW");
        }

        glfwWindowHandle = glfwCreateWindow(1280, 720, "Quicksilver", MemoryUtil.NULL, MemoryUtil.NULL);
        if(glfwWindowHandle == MemoryUtil.NULL) {
            throw IllegalStateException("Failed to create a GLFW window");
        }

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
        glEnable(GL_MULTISAMPLE)
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        // Load a local test file
        webController.loadURL("https://google.com")

        var lastTime: Double = glfwGetTime()
        var frameCount = 0

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
    }

    /**
     * Cleanup GLFW resources and close window
     */
    private fun doStop() {
        if(glfwWindowHandle != MemoryUtil.NULL) {
            glfwDestroyWindow(glfwWindowHandle);
        }

        glfwTerminate();
    }
}