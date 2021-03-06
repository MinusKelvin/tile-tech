package minusk.tiletech.world;

import minusk.tiletech.world.structures.Cave;
import minusk.tiletech.world.tiles.Tile;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

import static minusk.tiletech.utils.Util.getCnk;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.jemalloc.JEmalloc.je_malloc;
import static org.lwjgl.system.jemalloc.JEmalloc.je_realloc;

/**
 * Created by MinusKelvin on 1/25/16.
 */
public final class Chunk {
	public static final int BYTES_PER_VERTEX = 48;
	public static final int CHUNK_SIZE = 32;
	
	// Note: All of these block arrays are [x][z][y]
	// SSSS RRRR GGGG BBBB + 16 bits ID
	/** One-dimensional to avoid additional array allocations. Access through provided methods. */
	private int[] blockdata = new int[CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE];
	HashMap<Vector3i, Object> blockMeta = new HashMap<>(32);
	/** World space */
	private int x,y,z;
	private int vbo = -1, verts, dim;
	boolean needsUpdate = false;
	
	public Chunk(int x, int y, int z, int dim, int[][] hs, List<Cave.Segment> nearbySegments) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.dim = dim;
		
		// i = x, j = z, k = y
		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 32; j++) {
				for (int k = 0; k < 32; k++) {
					if (k+y == 0)
						rawSetTile(i,k,j, Tile.Bedrock.id);
					else if (k + y > hs[i][j] || inCave(x+i,y+k,z+j,nearbySegments))
						rawSetTile(i,k,j, Tile.Air.id);
					else if (k + y == hs[i][j])
						rawSetTile(i,k,j, Tile.Grass.id);
					else if (k+y >= hs[i][j]-3)
						rawSetTile(i,k,j, Tile.Dirt.id);
					else
						rawSetTile(i,k,j, Tile.Stone.id);
				}
			}
		}
	}
	
	private static boolean inCave(int x, int y, int z, List<Cave.Segment> segments) {
		for (Cave.Segment segment : segments) {
			float t = new Vector3f(x,y,z).sub(segment.p1).dot(segment.dir) / segment.length;
			t = t < 0 ? 0 : t > 1 ? 1 : t;
			float d = segment.size1*t + segment.size2*(1-t);
			if (new Vector3f(segment.dir).mul(t*segment.length).add(segment.p1).sub(x,y,z).lengthSquared() < d*d)
				return true;
		}
		return false;
	}
	
	private static int maxVerts = 1024;
	private static ByteBuffer data = je_malloc(maxVerts*BYTES_PER_VERTEX);
	private static Tile[][][] chunkEdges = new Tile[34][34][34];
	
	/**
	 * Updates this {@code Chunk}'s vertex buffer.
	 */
	void updateVBO() {
		double t = glfwGetTime();
		
		verts = 0;
		data.position(0);
		
		// i = x, j = z, k = y
		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 32; j++) {
				for (int k = 0; k < 32; k++)
					chunkEdges[i + 1][j + 1][k + 1] = Tile.getTile(getTile(i,k,j) & 0xFFFF);
				if (y != 0) chunkEdges[i+1][j+1][0] = World.getWorld().getTile(x+i,y-1,z+j,dim);
				else chunkEdges[i+1][j+1][0] = Tile.Bedrock;
				chunkEdges[i+1][j+1][33] = World.getWorld().getTile(x+i,y+32,z+j,dim);
				chunkEdges[0][j+1][i+1] = World.getWorld().getTile(x-1,y+i,z+j,dim);
				chunkEdges[33][j+1][i+1] = World.getWorld().getTile(x+32,y+i,z+j,dim);
				chunkEdges[i+1][0][j+1] = World.getWorld().getTile(x+i,y+j,z-1,dim);
				chunkEdges[i+1][33][j+1] = World.getWorld().getTile(x+i,y+j,z+32,dim);
			}
			chunkEdges[i+1][0][33] = World.getWorld().getTile(x+i,y+32,z-1,dim);
			chunkEdges[i+1][33][33] = World.getWorld().getTile(x+i,y+32,z+32,dim);
			chunkEdges[0][0][i+1] = World.getWorld().getTile(x-1,y+i,z-1,dim);
			chunkEdges[33][0][i+1] = World.getWorld().getTile(x+32,y+i,z-1,dim);
			chunkEdges[0][33][i+1] = World.getWorld().getTile(x-1,y+i,z+32,dim);
			chunkEdges[33][33][i+1] = World.getWorld().getTile(x+32,y+i,z+32,dim);
			chunkEdges[0][i+1][33] = World.getWorld().getTile(x-1,y+32,z+i,dim);
			chunkEdges[33][i+1][33] = World.getWorld().getTile(x+32,y+32,z+i,dim);
			if (y != 0) {
				chunkEdges[0][i + 1][0] = World.getWorld().getTile(x - 1, y - 1, z + i, dim);
				chunkEdges[33][i + 1][0] = World.getWorld().getTile(x + 32, y - 1, z + i, dim);
				chunkEdges[i + 1][0][0] = World.getWorld().getTile(x + i, y - 1, z - 1, dim);
				chunkEdges[i + 1][33][0] = World.getWorld().getTile(x + i, y - 1, z + 32, dim);
			} else {
				chunkEdges[0][i + 1][0] = Tile.Bedrock;
				chunkEdges[33][i + 1][0] = Tile.Bedrock;
				chunkEdges[i + 1][0][0] = Tile.Bedrock;
				chunkEdges[i + 1][33][0] = Tile.Bedrock;
			}
		}
		chunkEdges[0][0][33] = World.getWorld().getTile(x-1,y+32,z-1,dim);
		chunkEdges[0][33][33] = World.getWorld().getTile(x-1,y+32,z+32,dim);
		chunkEdges[33][0][33] = World.getWorld().getTile(x+32,y+32,z-1,dim);
		chunkEdges[33][33][33] = World.getWorld().getTile(x+32,y+32,z+32,dim);
		if (y != 0) {
			chunkEdges[0][0][0] = World.getWorld().getTile(x - 1, y - 1, z - 1, dim);
			chunkEdges[0][33][0] = World.getWorld().getTile(x - 1, y - 1, z + 32, dim);
			chunkEdges[33][0][0] = World.getWorld().getTile(x + 32, y - 1, z - 1, dim);
			chunkEdges[33][33][0] = World.getWorld().getTile(x + 32, y - 1, z + 32, dim);
		} else {
			chunkEdges[0][0][0] = Tile.Bedrock;
			chunkEdges[0][33][0] = Tile.Bedrock;
			chunkEdges[33][0][0] = Tile.Bedrock;
			chunkEdges[33][33][0] = Tile.Bedrock;
		}
		
		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 32; j++) {
				for (int k = 0; k < 32; k++) {
					int returnedVerts = chunkEdges[i+1][j+1][k+1].render(chunkEdges, data, maxVerts - verts, x+i, y+k, z+j, dim);
					while (returnedVerts == -1) {
						maxVerts += 1024;
						int pos = data.position();
						data.position(0);
						data = je_realloc(data, maxVerts*BYTES_PER_VERTEX);
						data.position(pos);
						returnedVerts = chunkEdges[i+1][j+1][k+1].render(chunkEdges, data, maxVerts- verts, x+i, y+k, z+j, dim);
					}
					verts += returnedVerts;
				}
			}
		}
		
		data.position(0);
		
		if (verts != 0) {
			if (vbo == -1)
				vbo = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			glBufferData(GL_ARRAY_BUFFER, verts * BYTES_PER_VERTEX, data, GL_STATIC_DRAW);
		}
		
		double t2 = glfwGetTime() - t;
		if (t2 > 0.01)
			System.out.println(verts + " verts: " + Math.round(t2*1000) + " ms "+x+","+y+","+z);
	}
	
	void render(boolean shadowPass) {
		if (verts == 0)
			return;
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, BYTES_PER_VERTEX, 0); // Position
		glVertexAttribPointer(1, 3, GL_FLOAT, false, BYTES_PER_VERTEX, 12); // Texture coordinates
		if (shadowPass) {
			glVertexAttribPointer(2, 1, GL_UNSIGNED_BYTE, true, BYTES_PER_VERTEX, 38); // Wavy
		} else {
			glVertexAttribPointer(2, 3, GL_FLOAT, false, BYTES_PER_VERTEX, 24); // Normals
			glVertexAttribPointer(3, 3, GL_UNSIGNED_BYTE, true, BYTES_PER_VERTEX, 36); // Quad interop + wavy factor
			glVertexAttribPointer(4, 4, GL_UNSIGNED_BYTE, true, BYTES_PER_VERTEX, 40); // Ambient occlusion
			glVertexAttribPointer(5, 4, GL_UNSIGNED_BYTE, true, BYTES_PER_VERTEX, 44); // Color v0
//			glVertexAttribPointer(6, 4, GL_UNSIGNED_BYTE, true, BYTES_PER_VERTEX, 48); // Color v1
//			glVertexAttribPointer(7, 4, GL_UNSIGNED_BYTE, true, BYTES_PER_VERTEX, 52); // Color v2
//			glVertexAttribPointer(8, 4, GL_UNSIGNED_BYTE, true, BYTES_PER_VERTEX, 56); // Color v3
		}
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		if (shadowPass) {
			glDisableVertexAttribArray(3);
			glDisableVertexAttribArray(4);
			glDisableVertexAttribArray(5);
//			glDisableVertexAttribArray(6);
//			glDisableVertexAttribArray(7);
//			glDisableVertexAttribArray(8);
		} else {
			glEnableVertexAttribArray(3);
			glEnableVertexAttribArray(4);
			glEnableVertexAttribArray(5);
//			glEnableVertexAttribArray(6);
//			glEnableVertexAttribArray(7);
//			glEnableVertexAttribArray(8);
		}
		
		glDrawArrays(GL_TRIANGLES, 0, verts);
	}
	
	/**
	 * Sets the tile at the specified position to the specified type id.
	 * This method calls the relevant delete and create methods, and requests that the chunk and its neighbours be re-rendered.
	 * To set a tile without calling any of these, use {@link Chunk#rawSetTile}.
	 */
	public void setTile(int x, int y, int z, short id) {
		Tile.getTile(getTile(x,y,z) & 0xFFFF).onDelete(x+this.x, y+this.y, z+this.z, dim);
		rawSetTile(x,y,z,id);
		Tile.getTile(id).onCreate(x+this.x, y+this.y, z+this.z, dim);
		for (int i = -1; i < 2; i++)
			for (int j = -1; j < 2; j++)
				for (int k = -1; k < 2; k++)
					World.getWorld().requestChunkRender(i+getCnk(x+this.x), k+getCnk(y+this.y), j+getCnk(z+this.z), dim);
		World.getWorld().requestLightUpdate(this.x + x, this.y + y, this.z + z, dim);
	}
	
	public short getTile(int x, int y, int z) {
		if (blockdata == null)
			return 0;
		return (short) blockdata[x*CHUNK_SIZE*CHUNK_SIZE + z*CHUNK_SIZE + y];
	}
	
	/**
	 * Do not use unless you know what you're doing.
	 */
	public void rawSetTile(int x, int y, int z, short id) {
		if (blockdata == null)
			blockdata = new int[CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE];
		blockdata[x*CHUNK_SIZE*CHUNK_SIZE + z*CHUNK_SIZE + y] = id & 0xFFFF;
		blockMeta.remove(new Vector3i(x,y,z));
	}
	
	public int getLight(int x, int y, int z, LightChannel channel) {
		if (blockdata == null)
			return 0;
		if (channel == null)
			return blockdata[x*CHUNK_SIZE*CHUNK_SIZE + z*CHUNK_SIZE + y] >> 16 & 0xFFFF;
		return blockdata[x*CHUNK_SIZE*CHUNK_SIZE + z*CHUNK_SIZE + y] >> channel.getShiftAmmount()+16 & 0xF;
	}
	
	/**
	 * Sets the light value for the specified light channel.
	 * 
	 * @param amount The light amount, ranging from 0 to 15
	 */
	void setLight(int x, int y, int z, LightChannel channel, int amount) {
		if (rawSetLight(x, y, z, channel, amount)) {
			World.getWorld().requestLightUpdate(this.x + x, this.y + y, this.z + z - 1, dim);
			World.getWorld().requestLightUpdate(this.x + x, this.y + y, this.z + z + 1, dim);
			World.getWorld().requestLightUpdate(this.x + x, this.y + y - 1, this.z + z, dim);
			World.getWorld().requestLightUpdate(this.x + x, this.y + y + 1, this.z + z, dim);
			World.getWorld().requestLightUpdate(this.x + x - 1, this.y + y, this.z + z, dim);
			World.getWorld().requestLightUpdate(this.x + x + 1, this.y + y, this.z + z, dim);
			for (int i = -1; i < 2; i++)
				for (int j = -1; j < 2; j++)
					for (int k = -1; k < 2; k++)
						World.getWorld().requestChunkRender(i+getCnk(x+this.x), k+getCnk(y+this.y), j+getCnk(z+this.z), dim);
		}
	}
	
	/**
	 * Do not use unless you know what you're doing.
	 * Returns true if the light value changed.
	 */
	boolean rawSetLight(int x, int y, int z, LightChannel channel, int amount) {
		if (blockdata == null)
			blockdata = new int[CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE];
		if (channel == null) {
			if (amount == blockdata[x * CHUNK_SIZE * CHUNK_SIZE + z * CHUNK_SIZE + y] >>> 16)
				return false;
			blockdata[x * CHUNK_SIZE * CHUNK_SIZE + z * CHUNK_SIZE + y] &= 0xFFFF;
			blockdata[x * CHUNK_SIZE * CHUNK_SIZE + z * CHUNK_SIZE + y] |= (0xFFFF & amount) << 16;
		} else {
			if (amount == ((blockdata[x * CHUNK_SIZE * CHUNK_SIZE + z * CHUNK_SIZE + y] >>> channel.getShiftAmmount()+16) & 0xF))
				return false;
			blockdata[x * CHUNK_SIZE * CHUNK_SIZE + z * CHUNK_SIZE + y] &= ~(0xF << channel.getShiftAmmount()+16);
			blockdata[x * CHUNK_SIZE * CHUNK_SIZE + z * CHUNK_SIZE + y] |= (0xF & amount) << channel.getShiftAmmount()+16;
		}
		return true;
	}
}
