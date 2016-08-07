package minusk.tiletech.gui;

import minusk.tiletech.gui.menus.PauseMenu;
import minusk.tiletech.gui.nodes.GuiNode;
import minusk.tiletech.gui.render.NinepatchData;
import minusk.tiletech.gui.render.RenderData;
import minusk.tiletech.gui.render.TextData;
import minusk.tiletech.inventory.ItemStack;
import minusk.tiletech.inventory.Slot;
import minusk.tiletech.render.GLHandler;
import minusk.tiletech.world.World;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Scanner;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.system.jemalloc.JEmalloc.je_free;
import static org.lwjgl.system.jemalloc.JEmalloc.je_malloc;

/**
 * @author MinusKelvin
 */
public abstract class Gui extends GuiNode{
	public static final Slot[] NO_SLOTS = new Slot[0];
	
	private static int vbo, bufsize=0;
	private static int[] charWidths = new int[256];
	private static Gui current;
	private static Slot[] slotMap;
	
	private static ArrayList<RenderData> renderQueue = new ArrayList<>();
	
	public Gui() {
		super(0,0);
	}
	
	public static void init() {
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		
		setGui(PauseMenu.get(), NO_SLOTS);
		
		Scanner scanner = new Scanner(Gui.class.getResourceAsStream("/res/fontspace.dat"));
		for (int i = 0; i < 256; i++)
			charWidths[i] = scanner.nextInt();
		scanner.close();
	}
	
	public static void render() {
		GLHandler.prepareGUI();
		
		renderQueue.clear();
		
		if (World.getWorld() != null)
			Hud.render();
		
		if (current != null)
			current.draw();
		
		int vertices = 0;
		for (RenderData data : renderQueue)
			vertices += data.vertexCount();
		
		ByteBuffer buffer = je_malloc(vertices*24);
		renderQueue.forEach((r)->r.render(buffer));
		buffer.position(0);
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		if (vertices*24 > bufsize) {
			bufsize = vertices*24;
			glBufferData(GL_ARRAY_BUFFER, buffer, GL_STREAM_DRAW);
		} else
			glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
		
		je_free(buffer);
		
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 24, 0);
		glVertexAttribPointer(1, 4, GL_UNSIGNED_BYTE, true, 24, 8);
		glVertexAttribPointer(2, 3, GL_FLOAT, false, 24, 12);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glDrawArrays(GL_TRIANGLES, 0, vertices);
	}
	
	public static void tock() {
		if (GLHandler.getTap(GLFW_KEY_ESCAPE) && World.getWorld() != null) {
			if (current == null)
				setGui(PauseMenu.get(), NO_SLOTS);
			else
				setGui(null, null);
		}
	}
	
	public static void setGui(Gui gui, Slot[] slotIDs) {
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
	
	public static Slot getSlot(int id) {
		return slotMap[id];
	}
	
	public static void drawNinepatch(int id, int x, int y, int w, int h, int color) {
		drawThing(new NinepatchData(x, y, w, h, id, color));
	}
	
	public static void drawText(String text, int x, int y, int color) {
		drawThing(new TextData(text, x, y, color));
	}
	
	public static void drawItem(ItemStack stack, int x, int y) {
		
	}
	
	public static void drawThing(RenderData thing) {
		renderQueue.add(thing);
	}
	
	public static int getCharWidth(int ch) {
		return charWidths[ch];
	}
	
	public static int getTextWidth(String text) {
		int len = 0;
		for (char ch : text.toCharArray()) {
			len += getCharWidth(ch);
			len += 2;
		}
		return len;
	}
	
	public abstract void onOpen(Slot[] slots, Gui closing);
	public abstract void onClose(Slot[] slots, Gui opening);
}
