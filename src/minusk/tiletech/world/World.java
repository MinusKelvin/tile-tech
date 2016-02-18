package minusk.tiletech.world;

import minusk.tiletech.render.GLHandler;
import minusk.tiletech.utils.DirectionalBoolean;
import minusk.tiletech.utils.OpenSimplexNoise;
import minusk.tiletech.world.entities.Player;
import org.joml.*;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.system.jemalloc.JEmalloc.je_malloc;
import static minusk.tiletech.utils.Util.*;

/**
 * Created by MinusKelvin on 1/25/16.
 */
public class World {
	private static World currentWorld;
	
	public final Player player = new Player();
	public final OpenSimplexNoise noise2Da = new OpenSimplexNoise(System.currentTimeMillis());
	public final OpenSimplexNoise noise2Db = new OpenSimplexNoise(System.currentTimeMillis()*31);
	public final OpenSimplexNoise noise2Dc = new OpenSimplexNoise(System.currentTimeMillis()*31*31);
	
	private final ConcurrentHashMap<Vector2i, VerticalChunk> world = new ConcurrentHashMap<>(4096);
	private final Matrix4f lookaround = new Matrix4f();
	private final ByteBuffer matrixUpload = je_malloc(64);
	private final FrustumIntersection culler = new FrustumIntersection();
	private final Vector2i index2a = new Vector2i();
	private final Vector2i index2b = new Vector2i();
	private final Vector3i index3 = new Vector3i();
	private final ArrayDeque<Chunk> updateList = new ArrayDeque<>();
	private final ArrayDeque<Vector3i> generatePoints = new ArrayDeque<>();
	
	public World() {
		currentWorld = this;
		for (int i = -1; i <= 1; i++)
			for (int j = -1; j <= 1; j++)
				rawGenerateChunk(i,j,0);
		player.spawn();
		Thread worldGenThread = new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!generatePoints.isEmpty()) {
					Vector3i v = generatePoints.poll();
					rawGenerateChunk(v.x, v.y, v.z);
				}
			}
		});
		worldGenThread.setDaemon(true);
		worldGenThread.setName("World Generation Thread");
		worldGenThread.setPriority(Thread.MIN_PRIORITY);
		worldGenThread.start();
		for (int i = -16; i <= 16; i++)
			for (int j = -16; j <= 16; j++)
				if (i < -1 || i > 1 || j < -1 || j > 1)
					generatePoints.add(new Vector3i(i,j,0));
	}
	
	public Tile getTile(int x, int y, int z, int dim) {
		if (y < 0)
			return Tile.TILES[5];
		else if (y >= 256)
			return Tile.TILES[0];
		else if (!world.containsKey(index2a.set(getCnk(x), getCnk(z))))
			return Tile.TILES[5];
		else
			return Tile.TILES[world.get(index2a).chunks[getCnk(y)].blockIDs[cnkIdx(x)][cnkIdx(z)][cnkIdx(y)]];
	}
	
	public Object getMeta(int x, int y, int z, int dim) {
		if (y < 0 || y >= 256)
			return null;
		return world.get(index2a.set(getCnk(x),getCnk(z))).chunks[getCnk(y)].blockMeta.get(index3.set(cnkIdx(x),cnkIdx(y),cnkIdx(z)));
	}
	
	public Chunk getChunk(int x, int y, int z, int dim) {
		if (y < 0 || y >= 8)
			return null;
		if (!world.containsKey(index2a.set(x, z)))
			return null;
		return world.get(index2a).chunks[y];
	}
	
	public void setTile(int x, int y, int z, int dim, int id) {
		Chunk chunk = getChunk(getCnk(x), getCnk(y), getCnk(z), dim);
		chunk.setTile(cnkIdx(x),cnkIdx(y),cnkIdx(z),id);
	}
	
	public Tile genGetTile(int x, int y, int z, int dim) {
		if (y < 0)
			return Tile.TILES[5];
		else if (y >= 256)
			return Tile.TILES[0];
		else if (!world.containsKey(index2b.set(getCnk(x), getCnk(z))))
			return Tile.TILES[5];
		else
			return Tile.TILES[world.get(index2b).chunks[getCnk(y)].blockIDs[cnkIdx(x)][cnkIdx(z)][cnkIdx(y)]];
	}
	
	public boolean genLimitedReplace(int x, int y, int z, int dim, int[] canReplace, int id) {
		if (!world.containsKey(index2b.set(getCnk(x),getCnk(z))))
			rawGenerateChunk(getCnk(x),getCnk(z),dim);
		Chunk chunk = world.get(index2b.set(getCnk(x),getCnk(z))).chunks[getCnk(y)];
		if (contains(chunk.blockIDs[cnkIdx(x)][cnkIdx(z)][cnkIdx(y)], canReplace)) {
			chunk.blockIDs[cnkIdx(x)][cnkIdx(z)][cnkIdx(y)] = id;
			if (!chunk.needsUpdate) {
				chunk.needsUpdate = true;
				updateList.add(chunk);
			}
			return true;
		}
		return false;
	}
	
	public void requestChunkRender(int x, int y, int z, int dim) {
		Chunk chunk = getChunk(x,y,z,dim);
		if (chunk == null)
			return;
		chunk.needsUpdate = true;
		updateList.add(chunk);
	}
	
	public int random(int min, int max) {
		return (int) (Math.random()*(max-min)+min);
	}
	
	public void tick() {
		player.update();
		
		int c = 0;
		while (!updateList.isEmpty() && c++ < 32) {
			Chunk chunk = updateList.poll();
			chunk.needsUpdate = false;
			chunk.updateVBO();
		}
	}
	
	public void renderWorld(float alpha) {
		lookaround.identity();
		lookaround.lookAlong(player.look.x, player.look.y, player.look.z, 0, 1, 0);
		Vector3f eye = player.getEye(alpha);
		lookaround.translate(-eye.x, -eye.y, -eye.z);
		GLHandler.projection.mul(lookaround, lookaround);
		
		lookaround.get(matrixUpload);
		glUniformMatrix4fv(GLHandler.getProjLoc(), false, matrixUpload.asFloatBuffer());
		glUniform4f(GLHandler.getSundirLoc(), 0.440225f, 0.880451f, -0.17609f, 1);
		
		glCullFace(GL_BACK);
		glDepthFunc(GL_LEQUAL);
		
		culler.set(lookaround);
		
		world.values().forEach(cp -> {
			if (cp.isGened())
				cp.render(culler);
		});
		
		player.render(false);
	}
	
	private VerticalChunk getChunk(int x, int z, int dim) {
		return world.get(index2b.set(x,z));
	}
	
	private void checkChunk(int x, int z, int dim) {
		VerticalChunk vc = getChunk(x,z,dim);
		if (vc != null && getChunk(x-1,z,dim) != null && getChunk(x+1,z,dim) != null && getChunk(x,z-1,dim) != null && getChunk(x,z+1,dim) != null) {
			vc.generate();
			for (int i = 0; i < 8; i++)
				updateList.add(vc.chunks[i]);
		}
	}
	
	private void rawGenerateChunk(int x, int z, int dim) {
		world.put(new Vector2i(x,z), new VerticalChunk(x*32,z*32,dim));
		checkChunk(x-1,z,dim);
		checkChunk(x+1,z,dim);
		checkChunk(x,z-1,dim);
		checkChunk(x,z+1,dim);
	}
	
	public RaytraceResult raytrace(float posx, float posy, float posz, int dimension, float dirx, float diry, float dirz, float maxBlocks) {
		RayAabIntersection intersect = new RayAabIntersection(posx, posy, posz, dirx, diry, dirz);
		
		int cx = (int) Math.floor(posx), cy = (int) Math.floor(posy), cz = (int) Math.floor(posz);
		int dx = (int) Math.signum(dirx), dy = (int) Math.signum(diry), dz = (int) Math.signum(dirz);
		
		if (getTile(cx,cy,cz,dimension).raytrace(cx,cy,cz,dimension,intersect)) {
			RaytraceResult result = new RaytraceResult();
			result.pos.set(cx, cy, cz);
			if (Math.abs(dirx) > Math.abs(diry)) {
				if (Math.abs(dirx) > Math.abs(dirz)) {
					if (dx == 1)
						result.side.west = true;
					else
						result.side.east = true;
				} else {
					if (dz == 1)
						result.side.north = true;
					else
						result.side.south = true;
				}
			} else {
				if (Math.abs(diry) > Math.abs(dirz)) {
					if (dx == 1)
						result.side.east = true;
					else
						result.side.west = true;
				} else {
					if (dz == 1)
						result.side.north = true;
					else
						result.side.south = true;
				}
			}
			return result;
		}
		
		for (int i = 0; i < maxBlocks; i++) {
			if (maxBlocks-i < 1) {
				dirx *= maxBlocks-i;
				diry *= maxBlocks-i;
				dirz *= maxBlocks-i;
			}
			float oldx = posx;
			float oldy = posy;
			float oldz = posz;
			
			posx += dirx;
			posy += diry;
			posz += dirz;
			
			boolean xchange = Math.floor(oldx) != Math.floor(posx);
			boolean ychange = Math.floor(oldy) != Math.floor(posy);
			boolean zchange = Math.floor(oldz) != Math.floor(posz);
			int changes = (xchange?1:0) + (ychange?1:0) + (zchange?1:0);
			
			for (int j = 0; j < changes; j++) {
				if (xchange && intersect.test(cx+dx,cy,cz,cx+dx+1,cy+1,cz+1)) {
					cx += dx;
					if (getTile(cx,cy,cz,dimension).raytrace(cx,cy,cz,dimension,intersect)) {
						RaytraceResult result = new RaytraceResult();
						result.pos.set(cx, cy, cz);
						if (dx == 1)
							result.side.east = true;
						else
							result.side.west = true;
						return result;
					}
					xchange = false;
				} else if (ychange && intersect.test(cx,cy+dy,cz,cx+1,cy+dy+1,cz+1)) {
					cy += dy;
					if (getTile(cx,cy,cz,dimension).raytrace(cx,cy,cz,dimension,intersect)) {
						RaytraceResult result = new RaytraceResult();
						result.pos.set(cx, cy, cz);
						if (dy == 1)
							result.side.down = true;
						else
							result.side.up = true;
						return result;
					}
					ychange = false;
				} else if (zchange && intersect.test(cx,cy,cz+dz,cx+1,cy+1,cz+dz+1)) {
					cz += dz;
					if (getTile(cx,cy,cz,dimension).raytrace(cx,cy,cz,dimension,intersect)) {
						RaytraceResult result = new RaytraceResult();
						result.pos.set(cx, cy, cz);
						if (dz == 1)
							result.side.north = true;
						else
							result.side.south = true;
						return result;
					}
					zchange = false;
				}
			}
		}
		return null;
	}
	
	public static class RaytraceResult {
		public final Vector3i pos = new Vector3i();
		public final DirectionalBoolean side = new DirectionalBoolean(false);
	}
	
	public static World getWorld() {
		return currentWorld;
	}
}
