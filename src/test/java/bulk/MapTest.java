package bulk;

import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class MapTest extends Map {
    public MapTest(){
        super();
    }

    @Test
    public void testUpdatePlayer_notmoving() {
        this.player.xPos = 1;
        this.player.yPos = 1;

        this.tiles.set(getIndexFromCoords(1,0),new Tile(false));
        this.tiles.set(getIndexFromCoords(1,2),new Tile(false));
        this.tiles.set(getIndexFromCoords(0,1),new Tile(false));
        this.tiles.set(getIndexFromCoords(2,1),new Tile(false));

        this.UpdatePlayer(GLFW.GLFW_KEY_LEFT);
        assert this.player.xPos == 1;
        this.UpdatePlayer(GLFW.GLFW_KEY_RIGHT);
        assert this.player.xPos == 1;
        this.UpdatePlayer(GLFW.GLFW_KEY_UP);
        assert this.player.yPos == 1;
        this.UpdatePlayer(GLFW.GLFW_KEY_DOWN);
        assert this.player.yPos == 1;
    }

    @Test
    public void testUpdatePlayer_moving() {
        this.player.xPos = 1;
        this.player.yPos = 1;

        this.tiles.set(getIndexFromCoords(1,0),new Tile(true));
        this.tiles.set(getIndexFromCoords(1,2),new Tile(true));
        this.tiles.set(getIndexFromCoords(0,1),new Tile(true));
        this.tiles.set(getIndexFromCoords(2,1),new Tile(true));

        this.UpdatePlayer(GLFW.GLFW_KEY_LEFT);
        assert this.player.xPos == 0;
        this.player.xPos = 1;
        this.UpdatePlayer(GLFW.GLFW_KEY_RIGHT);
        assert this.player.xPos == 2;
        this.player.xPos = 1;
        this.UpdatePlayer(GLFW.GLFW_KEY_UP);
        assert this.player.yPos == 2;
        this.player.yPos = 1;
        this.UpdatePlayer(GLFW.GLFW_KEY_DOWN);
        assert this.player.yPos == 0;
    }

    @Test
    public void testUpdatePlayer_edges() {
        this.player.xPos = 0;
        this.player.yPos = 0;

        this.UpdatePlayer(GLFW.GLFW_KEY_LEFT);
        assert this.player.xPos == 0;
        this.UpdatePlayer(GLFW.GLFW_KEY_DOWN);
        assert this.player.yPos == 0;
        this.player.xPos = this.getCellColumns()-1;
        this.player.yPos = this.getCellRows()-1;
        this.UpdatePlayer(GLFW.GLFW_KEY_RIGHT);
        assert this.player.xPos == this.getCellColumns()-1;
        this.UpdatePlayer(GLFW.GLFW_KEY_UP);
        assert this.player.yPos == this.getCellRows()-1;
    }

    @Test
    public void drawMe() {
        long window;
        GLFWErrorCallback.createPrint(System.err).set();
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(100,100,"hello",NULL,NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");
        glfwMakeContextCurrent(window);
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically
        glfwSwapInterval(1);
        GL.createCapabilities();

        this.DrawMe();
        glfwTerminate();
    }
}