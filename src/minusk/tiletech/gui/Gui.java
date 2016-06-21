package minusk.tiletech.gui;

import minusk.tiletech.gui.menus.PauseMenu;
import minusk.tiletech.inventory.Slot;
import minusk.tiletech.render.GLHandler;
import minusk.tiletech.world.World;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

/**
 * @author MinusKelvin
 */
public class Gui {
	public static final Slot[] NO_SLOTS = new Slot[0];
	
	private static int vbo, bufsize, position;
	private static GuiStructure current;
	private static Slot[] slotMap;
	private static ByteBuffer buf;
	
	public static void init() {
		vbo = glGenBuffers();
		bufsize = 512;
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, bufsize*9*4, GL_STREAM_DRAW);
		
		setGui(PauseMenu.get(), NO_SLOTS);
	}
	
	public static void render() {
		GLHandler.prepareGUI();
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		buf = glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY, buf);
		position = 0;
		
		if (World.getWorld() != null)
			Hud.render();
		
		if (current != null)
			current.render();
		
		glUnmapBuffer(GL_ARRAY_BUFFER);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 9 * 4, 0);
		glVertexAttribPointer(1, 4, GL_UNSIGNED_BYTE, true, 9 * 4, 3 * 4);
		glVertexAttribPointer(2, 3, GL_FLOAT, false, 9 * 4, 6 * 4);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glDrawArrays(GL_TRIANGLES, 0, position);
	}
	
	public static void tick() {
		if (GLHandler.getTap(GLFW_KEY_ESCAPE) && World.getWorld() != null) {
			if (current == null)
				setGui(PauseMenu.get(), NO_SLOTS);
			else
				setGui(null, null);
		}
	}
	
	public static void setGui(GuiStructure gui, Slot[] slotIDs) {
		if (current != null)
			current.onClose(slotMap, gui);
		if (gui != null)
			gui.onOpen(slotIDs, current);
		if (gui == null)
			GLHandler.grabMouse();
		else
			GLHandler.releaseMouse();
		current = gui;
		slotMap = slotIDs;
	}
	
	public static boolean isGrabbed() {
		return current == null;
	}
}
