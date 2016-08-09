package minusk.tiletech.render;

import minusk.tiletech.TileTech;
import minusk.tiletech.gui.Gui;
import minusk.tiletech.world.World;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GLCapabilities;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Scanner;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
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
	private static int fbo, blockTexture, baseShader, shadowShader, shadowProjLoc, sprojLoc, projLoc,
			sundirLoc, timeLoc, shadowTimeLoc, guiShader, guiProjLoc, guiTex;
	private static int width=1024, height=576, shadowmapSize=1024;
	public static final Matrix4f projection = new Matrix4f().setPerspective((float) Math.toRadians(90), 1024/576f, 0.1f, 1512);
	private static GLFWFramebufferSizeCallback fbs;
	private static GLFWCursorPosCallback cp;
	private static GLFWKeyCallback k;
	private static GLFWMouseButtonCallback mb;
	private static ByteBuffer clearDepth, clearColor;
	private static long window;
	private static float time;
	private static boolean grabbed, newtime = true;
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
	
	public static void init(long window, GLCapabilities capabilities) {
		GLHandler.window = window;
		glfwSetFramebufferSizeCallback(window, fbs = GLFWFramebufferSizeCallback.create((win, width, height) -> {
			glViewport(0, 0, width, height);
			projection.setPerspective((float) Math.toRadians(90), (float) width/height, 0.1f, 1512);
			GLHandler.width = width;
			GLHandler.height = height;
		}));
		
		glfwSetCursorPosCallback(window, cp = GLFWCursorPosCallback.create((win, x, y) -> {
			if (Gui.isGrabbed()) {
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
			timeLoc = glGetUniformLocation(baseShader, "time");
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
			shadowTimeLoc = glGetUniformLocation(shadowShader, "time");
		}
		
		// GUI Shader
		{
			int vertex = glCreateShader(GL_VERTEX_SHADER);
			glShaderSource(vertex, new Scanner(GLHandler.class.getResourceAsStream("/res/shaders/gui.vs.glsl")).useDelimiter("\\Z").next());
			glCompileShader(vertex);
			if (glGetShaderi(vertex, GL_COMPILE_STATUS) != 1) {
				System.err.println(glGetShaderInfoLog(vertex));
				return;
			}
			
			int fragment = glCreateShader(GL_FRAGMENT_SHADER);
			glShaderSource(fragment, new Scanner(GLHandler.class.getResourceAsStream("/res/shaders/gui.fs.glsl")).useDelimiter("\\Z").next());
			glCompileShader(fragment);
			if (glGetShaderi(fragment, GL_COMPILE_STATUS) != 1) {
				System.err.println(glGetShaderInfoLog(fragment));
				return;
			}
			
			guiShader = glCreateProgram();
			glAttachShader(guiShader, vertex);
			glAttachShader(guiShader, fragment);
			glLinkProgram(guiShader);
			
			if (glGetProgrami(guiShader, GL_LINK_STATUS) != 1) {
				System.err.println(glGetProgramInfoLog(guiShader));
				return;
			}
			glDeleteShader(vertex);
			glDeleteShader(fragment);
			glUseProgram(guiShader);
			
			guiProjLoc = glGetUniformLocation(guiShader, "proj");
		}
		
		float maxAniso = 1;
		if (capabilities.GL_EXT_texture_filter_anisotropic)
			maxAniso = glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
		
		// Block textures
		blockTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D_ARRAY, blockTexture);
		glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_RGBA8, 32, 32, 256, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
		loadTextureArrayPart("res/blocks.png", 32, 32, 16, 0);
		
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAX_LEVEL, 0);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		if (capabilities.GL_EXT_texture_filter_anisotropic)
			glTexParameterf(GL_TEXTURE_2D_ARRAY, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, maxAniso);
		
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
		
		// GUI textures
		guiTex = glGenTextures();
		glBindTexture(GL_TEXTURE_2D_ARRAY, guiTex);
		glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_RGBA8, 32, 32, 272, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
		loadTextureArrayPart("res/gui.png", 32, 32, 4, 0);
		loadTextureArrayPart("res/font.png", 32, 32, 16, 16);
		
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAX_LEVEL, 0);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		if (capabilities.GL_EXT_texture_filter_anisotropic)
			glTexParameterf(GL_TEXTURE_2D_ARRAY, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, maxAniso);
		
		// Enables
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glFrontFace(GL_CW);
		
		// Other
		clearDepth = je_malloc(4);
		clearDepth.putFloat(0, 1);
		
		clearColor = je_malloc(16);
		clearColor.putFloat(12,1);
	}
	
	private static void loadTextureArrayPart(String resource, int width, int height, int tiles, int startLayer) {
		ByteBuffer buffer = je_malloc(12);
		ByteBuffer img = stbi_load(resource, buffer.slice().order(ByteOrder.nativeOrder()).asIntBuffer(),
				((ByteBuffer) buffer.position(4)).slice().order(ByteOrder.nativeOrder()).asIntBuffer(),
				((ByteBuffer) buffer.position(8)).slice().order(ByteOrder.nativeOrder()).asIntBuffer(), 4);
		int w = buffer.getInt(0);
		int h = buffer.getInt(4);
		System.out.println(w+", "+h+", "+buffer.getInt(8)+", "+img.capacity());
		je_free(buffer);
		buffer = je_malloc(img.capacity());
		
		for (int i = 0; i < tiles; i++)
			for (int j = 0; j < tiles; j++)
				for (int k = 0; k < height; k++)
					for (int l = 0; l < width; l++)
						buffer.putInt((i * width * height * tiles + j * width * height + k * width + l) * 4,
								img.getInt((i * w * height + j * width + k * w + l) * 4));
		
		stbi_image_free(img);
		glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, startLayer, width, height, tiles*tiles, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		je_free(buffer);
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
	}
	
	public static void releaseMouse() {
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		glfwSetCursorPos(window, width/2, height/2);
	}
	
	public static void prepareShadow(int phase) {
		if (newtime) {
			if (TileTech.game.isPaused())
				time = (float) TileTech.game.getNow();
			else
				time = (float) glfwGetTime();
		}
		newtime = false;
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, shadowTex[phase], 0);
		glUseProgram(shadowShader);
		glViewport(0,0,shadowmapSize,shadowmapSize);
		glClearBufferfv(GL_DEPTH, 0, clearDepth);
		glUniform1f(shadowTimeLoc, time);
		glBindTexture(GL_TEXTURE_2D_ARRAY, blockTexture);
	}
	
	public static void prepareScene(float sunpower) {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glUseProgram(baseShader);
		glViewport(0,0,width,height);
		glUniform1f(timeLoc, time);
		newtime = true;
		glBindTexture(GL_TEXTURE_2D_ARRAY, blockTexture);
		glClearBufferfv(GL_DEPTH, 0, clearDepth);
		clearColor.putFloat(0, 0.25f * sunpower);
		clearColor.putFloat(4, 0.5f * sunpower);
		clearColor.putFloat(8, 1 * sunpower);
		glClearBufferfv(GL_COLOR, 0, clearColor);
	}
	
	public static void prepareGUI() {
		glUseProgram(guiShader);
		glBindTexture(GL_TEXTURE_2D_ARRAY, guiTex);
		Matrix4f mat = new Matrix4f().setOrthoSymmetric(width/2,-height/2,-1,1);
		ByteBuffer buf = je_malloc(64);
		mat.get(buf);
		glUniformMatrix4fv(guiProjLoc, 1, false, buf);
		je_free(buf);
	}
}
