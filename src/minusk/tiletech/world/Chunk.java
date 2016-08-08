package minusk.tiletech.world;

import minusk.tiletech.world.structures.Cave;
import minusk.tiletech.world.tiles.Tile;
import org.joml.Intersectionf;
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
public class Chunk {
	// Note: All of these block arrays are [x][z][y]
	short[][][] blockIDs = new short[32][32][32];
	// SSSS RRRR GGGG BBBB YYYY MMMM CCCC WWWW
	int[][][] light = new int[32][32][32];
	HashMap<Vector3i, Object> blockMeta = new HashMap<>(32);
	private int vbo = -1, verts, x,y,z,dim;
	boolean needsUpdate = false;
	
	public Chunk(int x, int y, int z, int dim, int[][] hs, List<Cave.Segment> nearbySegments) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.dim = dim;
		
		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 32; j++) {
				for (int k = 0; k < 32; k++) {
					if (k+y == 0)
						blockIDs[i][j][k] = Tile.Bedrock.id;
					else if (k + y > hs[i][j] || inCave(x+i,y+k,z+j,nearbySegments))
						blockIDs[i][j][k] = Tile.Air.id;
					else if (k + y == hs[i][j])
						blockIDs[i][j][k] = Tile.Grass.id;
					else if (k+y >= hs[i][j]-3)
						blockIDs[i][j][k] = Tile.Dirt.id;
					else
						blockIDs[i][j][k] = Tile.Stone.id;
				}
			}
		}
	}
	
	private static boolean inCave(int x, int y, int z, List<Cave.Segment> segments) {
		for (Cave.Segment segment : segments) {
			if (Intersectionf.testLineSegmentSphere(segment.p1.x, segment.p1.y, segment.p1.z, segment.p2.x, segment.p2.y, segment.p2.z, x,y,z, 6))
				return true;
		}
		return false;
	}
	
	private static int maxVerts = 1024;
	private static ByteBuffer data = je_malloc(maxVerts*44);
	private static Tile[][][] chunkEdges = new Tile[34][34][34];
	
	void updateVBO() {
		double t = glfwGetTime();
		
		verts = 0;
		data.position(0);
		
		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 32; j++) {
				for (int k = 0; k < 32; k++)
					chunkEdges[i + 1][j + 1][k + 1] = Tile.getTile(blockIDs[i][j][k]);
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
						data = je_realloc(data, maxVerts*44);
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
			glBufferData(GL_ARRAY_BUFFER, verts * 44, data, GL_STATIC_DRAW);
		}
		
		double t2 = glfwGetTime() - t;
		if (t2 > 0.01)
			System.out.println(verts + " verts: " + Math.round(t2*1000) + " ms "+x+","+y+","+z);
	}
	
	void render(boolean shadowPass) {
		if (verts == 0)
			return;
		
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 44, 0); // Position
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 44, 12); // Texture coordinates
		if (shadowPass) {
			glVertexAttribPointer(2, 1, GL_UNSIGNED_BYTE, true, 44, 38); // Wavy
		} else {
			glVertexAttribPointer(2, 3, GL_FLOAT, false, 44, 24); // Normals
			glVertexAttribPointer(3, 3, GL_UNSIGNED_BYTE, true, 44, 36); // Quad interop + wavy factor
			glVertexAttribPointer(4, 4, GL_UNSIGNED_BYTE, true, 44, 40); // Ambient occlusion
//			glVertexAttribPointer(5, 4, GL_UNSIGNED_BYTE, true, 60, 44); // Color v0
//			glVertexAttribPointer(6, 4, GL_UNSIGNED_BYTE, true, 60, 48); // Color v1
//			glVertexAttribPointer(7, 4, GL_UNSIGNED_BYTE, true, 60, 52); // Color v2
//			glVertexAttribPointer(8, 4, GL_UNSIGNED_BYTE, true, 60, 56); // Color v3
		}
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		if (shadowPass) {
			glDisableVertexAttribArray(3);
			glDisableVertexAttribArray(4);
		} else {
			glEnableVertexAttribArray(3);
			glEnableVertexAttribArray(4);
		}
		
		glDrawArrays(GL_TRIANGLES, 0, verts);
	}
	
	public void setTile(int x, int y, int z, short id) {
		if (blockIDs == null)
			blockIDs = new short[32][32][32];
		Tile.getTile(blockIDs[x][z][y]).onDelete(x+this.x, y+this.y, z+this.z, dim);
		blockIDs[x][z][y] = id;
		blockMeta.remove(new Vector3i(x,y,z));
		Tile.getTile(id).onCreate(x+this.x, y+this.y, z+this.z, dim);
		for (int i = -1; i < 2; i++)
			for (int j = -1; j < 2; j++)
				for (int k = -1; k < 2; k++)
					World.getWorld().requestChunkRender(i+getCnk(x+this.x), k+getCnk(y+this.y), j+getCnk(z+this.z), dim);
	}
	
	public short getTile(int x, int y, int z) {
		if (blockIDs == null)
			return 0;
		return blockIDs[x][z][y];
	}
	
	public void rawSetTile(int x, int y, int z, short id) {
		if (blockIDs == null)
			blockIDs = new short[32][32][32];
		blockIDs[x][z][y] = id;
	}
	
	public int getLight(int x, int y, int z, int channel) {
		if (light == null)
			return 0;
		return light[x][z][y] >> channel*4 & 0xF;
	}
	
	public void setLight(int x, int y, int z, int channel, int amount) {
		if (light == null)
			light = new int[32][32][32];
		light[x][z][y] = (light[x][z][y] & (0xF << channel*4)) | (0xF & amount) << channel*4;
	}
}
