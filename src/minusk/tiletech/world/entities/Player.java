package minusk.tiletech.world.entities;

import minusk.tiletech.render.FaceRenderer;
import minusk.tiletech.render.GLHandler;
import minusk.tiletech.utils.DirectionalBoolean;
import minusk.tiletech.world.Tile;
import minusk.tiletech.world.World;
import org.joml.Vector3f;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.system.jemalloc.JEmalloc.*;

/**
 * Created by MinusKelvin on 1/26/16.
 */
public class Player extends Entity {
	private float yaw, pitch;
	private int buffer;
	private World.RaytraceResult raytrace;
	
	public Player() {
		super(0.5f, 1.75f);
		buffer = glGenBuffers();
	}
	
	public Vector3f getEye(float alpha) {
		Vector3f eye = lastCenter.lerp(center, alpha, new Vector3f());
		eye.y += halfHeight - 0.1f;
		return eye;
	}
	
	@Override
	public void update() {
		lastCenter.set(center);
		lastLook.set(look);
		
		Vector3f mv = new Vector3f();
		if (GLHandler.getKey(GLFW_KEY_W)) {
			mv.z += look.z;
			mv.x += look.x;
		}
		
		if (GLHandler.getKey(GLFW_KEY_S)) {
			mv.z -= look.z;
			mv.x -= look.x;
		}
		
		if (GLHandler.getKey(GLFW_KEY_A)) {
			mv.z -= look.x;
			mv.x += look.z;
		}
		
		if (GLHandler.getKey(GLFW_KEY_D)) {
			mv.z += look.x;
			mv.x -= look.z;
		}
		if (!(mv.x == mv.y && mv.y == mv.z && mv.x == 0))
			mv.normalize();
		
		velocity.add(mv.mul(0.15f));
		velocity.mul(0.5f, 0.95f, 0.5f);
		velocity.y -= 0.12f;
		
		DirectionalBoolean collides = move();
		
		if (collides.down && GLHandler.getKey(GLFW_KEY_SPACE))
			velocity.y = 0.7f;
		
		if (GLHandler.getTap(GLFW_KEY_ESCAPE))
			GLHandler.toggleMouse();
		
		Vector3f eye = getEye(1);
		raytrace = World.getWorld().raytrace(eye.x, eye.y, eye.z, dimension, look.x, look.y, look.z, 4);
		if (raytrace == null) {
//			System.out.println("Not looking at a block");
		} else {
			if (GLHandler.getMouseTap(GLFW_MOUSE_BUTTON_LEFT)) {
				System.out.printf("Looking at %d, %d, %d id: %d\n", raytrace.pos.x, raytrace.pos.y, raytrace.pos.z,
						World.getWorld().getTile(raytrace.pos.x, raytrace.pos.y, raytrace.pos.z, dimension).id);
				World.getWorld().setTile(raytrace.pos.x, raytrace.pos.y, raytrace.pos.z, dimension, Tile.Air.id);
			}
		}
	}
	
	@Override
	public void render(boolean shadowpass) {
		if (raytrace == null)
			return;
		
		ByteBuffer buffer = je_calloc(44,36);
		
		if (raytrace.side.up)
			FaceRenderer.renderTopFace(buffer, 0, raytrace.pos.x, raytrace.pos.y, raytrace.pos.z, 6, 0);
		else if (raytrace.side.down)
			FaceRenderer.renderBottomFace(buffer, 0, raytrace.pos.x, raytrace.pos.y, raytrace.pos.z, 6, 0);
		else if (raytrace.side.west)
			FaceRenderer.renderWestFace(buffer, 0, raytrace.pos.x, raytrace.pos.y, raytrace.pos.z, 6, 0);
		else if (raytrace.side.east)
			FaceRenderer.renderEastFace(buffer, 0, raytrace.pos.x, raytrace.pos.y, raytrace.pos.z, 6, 0);
		else if (raytrace.side.north)
			FaceRenderer.renderNorthFace(buffer, 0, raytrace.pos.x, raytrace.pos.y, raytrace.pos.z, 6, 0);
		else if (raytrace.side.south)
			FaceRenderer.renderSouthFace(buffer, 0, raytrace.pos.x, raytrace.pos.y, raytrace.pos.z, 6, 0);
		
		buffer.position(0);
		
		glBindBuffer(GL_ARRAY_BUFFER, this.buffer);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STREAM_DRAW);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 44, 0);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 44, 12);
		glVertexAttribPointer(2, 3, GL_FLOAT, false, 44, 24);
		glVertexAttribPointer(3, 2, GL_UNSIGNED_SHORT, true, 44, 36);
		glVertexAttribPointer(4, 4, GL_UNSIGNED_BYTE, true, 44, 40);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		glEnableVertexAttribArray(4);
		
		glDrawArrays(GL_TRIANGLES, 0, 36);
		
		je_free(buffer);
	}
	
	public void turn(float yawAmt, float pitchAmt) {
		yaw += yawAmt;
		pitch += pitchAmt;
		if (yaw > Math.PI)
			yaw -= Math.PI*2;
		else if (yaw < -Math.PI)
			yaw += Math.PI*2;
		if (pitch > Math.PI/2-0.01f)
			pitch = (float) Math.PI/2-0.01f;
		else if (pitch < 0.01f-Math.PI/2)
			pitch = 0.01f-(float) Math.PI/2;
		
		look.x = (float) Math.sin(yaw);
		look.z = (float) -Math.cos(yaw);
		
		look.y = (float) -Math.sin(pitch);
		float cos = (float) Math.cos(pitch);
		look.x *= cos;
		look.z *= cos;
	}
	
	public void spawn() {
		center.x =0.5f;
		center.z = center.x;
		for (int i = 255; i >= 0; i--) {
			center.y = i+halfHeight+0.1f;
			if (World.getWorld().getTile(0, i, 0, 0).collide(0,i,0,0,this)) {
				center.y = World.getWorld().getTile(0, i, 0, 0).highY(0,i,0,0,this) + halfHeight + 0.1f;
				break;
			}
		}
		lastCenter.set(center);
	}
}
