package minusk.tiletech.render;

import minusk.tiletech.world.World;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Scanner;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LEVEL;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.system.jemalloc.JEmalloc.je_free;
import static org.lwjgl.system.jemalloc.JEmalloc.je_malloc;

/**
 * Created by MinusKelvin on 1/25/16.
 */
public class GLHandler {
	private static int[] shadowTex = new int[4];
	private static int fbo, blockTexture, baseShader, shadowShader, shadowProjLoc, sprojLoc, projLoc, sundirLoc;
	private static int width=1024, height=576, shadowmapSize=1024;
	public static final Matrix4f projection = new Matrix4f().setPerspective((float) Math.toRadians(90), 1024/576f, 0.1f, 1512);
	private static GLFWFramebufferSizeCallback fbs;
	private static GLFWCursorPosCallback cp;
	private static GLFWKeyCallback k;
	private static GLFWMouseButtonCallback mb;
	private static ByteBuffer clearDepth;
	private static long window;
	private static boolean grabbed;
	private static boolean[] taps = new boolean[GLFW_KEY_LAST+1], mTaps = new boolean[GLFW_MOUSE_BUTTON_LAST+1];
	
	public static int getProjLoc() {
		return projLoc;
	}
	
	public static int getSprojLoc() {
		return sprojLoc;
	}
	
	public static int getShadowProjLoc() {
		return shadowProjLoc;
	}
	
	public static int getSundirLoc() {
		return sundirLoc;
	}
	
	public static boolean getKey(int key) {
		return glfwGetKey(window, key) == GLFW_PRESS;
	}
	
	public static boolean getTap(int key) {
		return taps[key];
	}
	
	public static boolean getMouseTap(int button) {
		return mTaps[button];
	}
	
	public static boolean getMouse(int button) {
		return glfwGetMouseButton(window, button) == GLFW_PRESS;
	}
	
	public static void init(long window) {
		GLHandler.window = window;
		glfwSetFramebufferSizeCallback(window, fbs = GLFWFramebufferSizeCallback.create((win, width, height) -> {
			glViewport(0, 0, width, height);
			projection.setPerspective((float) Math.toRadians(90), (float) width/height, 0.1f, 1512);
			GLHandler.width = width;
			GLHandler.height = height;
		}));
		
		glfwSetCursorPosCallback(window, cp = GLFWCursorPosCallback.create((win, x, y) -> {
			if (grabbed) {
				World.getWorld().player.turn((float) x * 0.01f, (float) y * 0.01f);
				glfwSetCursorPos(window, 0, 0);
			}
		}));
		
		glfwSetKeyCallback(window, k = GLFWKeyCallback.create((win, key, scan, act, mod) -> {
			if (key != GLFW_KEY_UNKNOWN && act == GLFW_PRESS)
				taps[key] = true;
		}));
		
		glfwSetMouseButtonCallback(window, mb = GLFWMouseButtonCallback.create((win, button, action, mods) -> {
			if (action == GLFW_PRESS)
				mTaps[button] = true;
		}));
		
		glBindVertexArray(glGenVertexArrays());
		
		// Base Shader
		{
			int vertex = glCreateShader(GL_VERTEX_SHADER);
			glShaderSource(vertex, new Scanner(GLHandler.class.getResourceAsStream("/res/shaders/base.vs.glsl")).useDelimiter("\\Z").next());
			glCompileShader(vertex);
			if (glGetShaderi(vertex, GL_COMPILE_STATUS) != 1) {
				System.err.println(glGetShaderInfoLog(vertex));
				return;
			}
			
			int fragment = glCreateShader(GL_FRAGMENT_SHADER);
			glShaderSource(fragment, new Scanner(GLHandler.class.getResourceAsStream("/res/shaders/base.fs.glsl")).useDelimiter("\\Z").next());
			glCompileShader(fragment);
			if (glGetShaderi(fragment, GL_COMPILE_STATUS) != 1) {
				System.err.println(glGetShaderInfoLog(fragment));
				return;
			}
			
			baseShader = glCreateProgram();
			glAttachShader(baseShader, vertex);
			glAttachShader(baseShader, fragment);
			glLinkProgram(baseShader);
			if (glGetProgrami(baseShader, GL_LINK_STATUS) != 1) {
				System.err.println(glGetProgramInfoLog(baseShader));
				return;
			}
			glDeleteShader(vertex);
			glDeleteShader(fragment);
			glUseProgram(baseShader);
			
			projLoc = glGetUniformLocation(baseShader, "proj");
			sprojLoc = glGetUniformLocation(baseShader, "sproj");
			sundirLoc = glGetUniformLocation(baseShader, "sundir");
			glUniform1i(glGetUniformLocation(baseShader, "shadow1"), 1);
			glUniform1i(glGetUniformLocation(baseShader, "shadow2"), 2);
			glUniform1i(glGetUniformLocation(baseShader, "shadow3"), 3);
			glUniform1i(glGetUniformLocation(baseShader, "shadow4"), 4);
		}
		
		// Shadow Shader
		{
			int vertex = glCreateShader(GL_VERTEX_SHADER);
			glShaderSource(vertex, new Scanner(GLHandler.class.getResourceAsStream("/res/shaders/shadow.vs.glsl")).useDelimiter("\\Z").next());
			glCompileShader(vertex);
			if (glGetShaderi(vertex, GL_COMPILE_STATUS) != 1) {
				System.err.println(glGetShaderInfoLog(vertex));
				return;
			}
			
			int fragment = glCreateShader(GL_FRAGMENT_SHADER);
			glShaderSource(fragment, new Scanner(GLHandler.class.getResourceAsStream("/res/shaders/shadow.fs.glsl")).useDelimiter("\\Z").next());
			glCompileShader(fragment);
			if (glGetShaderi(fragment, GL_COMPILE_STATUS) != 1) {
				System.err.println(glGetShaderInfoLog(fragment));
				return;
			}
			
			shadowShader = glCreateProgram();
			glAttachShader(shadowShader, vertex);
			glAttachShader(shadowShader, fragment);
			glLinkProgram(shadowShader);
			
			if (glGetProgrami(shadowShader, GL_LINK_STATUS) != 1) {
				System.err.println(glGetProgramInfoLog(shadowShader));
				return;
			}
			glDeleteShader(vertex);
			glDeleteShader(fragment);
			glUseProgram(shadowShader);
			
			shadowProjLoc = glGetUniformLocation(shadowShader, "proj");
		}
		
		// Block textures
		blockTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D_ARRAY, blockTexture);
		
		ByteBuffer buffer = je_malloc(12);
		ByteBuffer img = stbi_load("res/blocks.png", buffer.slice().order(ByteOrder.nativeOrder()).asIntBuffer(),
				((ByteBuffer) buffer.position(4)).slice().order(ByteOrder.nativeOrder()).asIntBuffer(),
				((ByteBuffer) buffer.position(8)).slice().order(ByteOrder.nativeOrder()).asIntBuffer(), 4);
		int w = buffer.getInt(0);
		int h = buffer.getInt(4);
		System.out.println(w+", "+h+", "+buffer.getInt(8)+", "+img.capacity());
		je_free(buffer);
		buffer = je_malloc(img.capacity());
		
		for (int i = 0; i < 16; i++)
			for (int j = 0; j < 16; j++)
				for (int k = 0; k < h / 16; k++)
					for (int l = 0; l < w / 16; l++)
						buffer.putInt((i * (w / 16) * (h / 16) * 16 + j * (w / 16) * (h / 16) + k * (w / 16) + l) * 4,
								img.getInt((i * w * (h / 16) + j * (w / 16) + k * w + l) * 4));
		
		stbi_image_free(img);
		glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_RGBA8, w/16, h/16, 256, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		je_free(buffer);
		
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAX_LEVEL, 0);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		
		// Shadowmaps
		for (int i = 0; i < 4; i++) {
			shadowTex[i] = glGenTextures();
			glActiveTexture(GL_TEXTURE1+i);
			glBindTexture(GL_TEXTURE_2D, shadowTex[i]);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32F, shadowmapSize, shadowmapSize, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		}
		glActiveTexture(GL_TEXTURE0);
		
		fbo = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		glDrawBuffer(GL_NONE);
		
		// Enables
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glFrontFace(GL_CW);
		
		// Other
		clearDepth = je_malloc(4);
		clearDepth.putFloat(0, 256);
	}
	
	public static void clearTaps() {
		for (int i = 0; i < taps.length; i++)
			taps[i] = false;
		for (int i = 0; i < mTaps.length; i++)
			mTaps[i] = false;
	}
	
	public static void grabMouse() {
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		glfwSetCursorPos(window, 0, 0);
		grabbed = true;
	}
	
	public static void releaseMouse() {
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		glfwSetCursorPos(window, width/2, height/2);
		grabbed = false;
	}
	
	public static void toggleMouse() {
		if (grabbed)
			releaseMouse();
		else
			grabMouse();
	}
	
	public static void prepareShadow(int phase) {
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, shadowTex[phase], 0);
		glUseProgram(shadowShader);
		glViewport(0,0,shadowmapSize,shadowmapSize);
		glClearBufferfv(GL_DEPTH, 0, clearDepth);
	}
	
	public static void prepareScene() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glUseProgram(baseShader);
		glViewport(0,0,width,height);
	}
}
