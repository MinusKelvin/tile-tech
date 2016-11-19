package minusk.tiletech;

import minusk.tiletech.gui.Gui;
import minusk.tiletech.gui.menus.PauseMenu;
import minusk.tiletech.render.GLHandler;
import minusk.tiletech.world.World;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import static minusk.tiletech.gui.Gui.NO_SLOTS;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by MinusKelvin on 1/22/16.
 */
public class TileTech {
	public static final TileTech game = new TileTech();
	
	private long window;
	private boolean paused = false;
	private double now;
	
	private void run() {
		glfwInit();
		
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, 1);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		
		window = glfwCreateWindow(1024, 576, "Tile Tech", 0, 0);
		glfwMakeContextCurrent(window);
		GLCapabilities capabilities = GL.createCapabilities();
//		glfwSwapInterval(0);
		
		GLHandler.init(window, capabilities);
		Gui.init();
		new World();
		Gui.setGui(PauseMenu.get(), NO_SLOTS);
		
		double time = glfwGetTime();
		float between = 0;
		while (true) {
			double t = glfwGetTime();
			if (glfwWindowShouldClose(window) == 1)
				break;
			
			int c = 0;
			while (glfwGetTime() - time >= 0.05) {
				if (!paused) {
					time += 0.05;
					World.getWorld().tick();
				}
				Gui.tock();
				GLHandler.clearTaps();
				if (c++ == 5 && !paused) {
					time = glfwGetTime();
					break;
				}
				if (paused)
					break;
			}
			
			if (!paused)
				between = (float) ((glfwGetTime()-time) / 0.05);
			else
				between = (float) ((now - time) / 0.05);
			World.getWorld().renderWorld(between);
			Gui.render();
			
			glfwSwapBuffers(window);
			glfwPollEvents();
			System.out.println((glfwGetTime() - t) * 1000);
		}
	}
	
	public void pause() {
		paused = true;
		now = glfwGetTime();
	}
	
	public void unpause() {
		paused = false;
		glfwSetTime(now);
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public double getNow() {
		return now;
	}
	
	public static void main(String[] args) {
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY-1);
		game.run();
	}
}
