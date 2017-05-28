package minusk.tiletech.world;

import minusk.tiletech.world.structures.Cave;
import minusk.tiletech.world.structures.Trees;
import minusk.tiletech.world.tiles.Tile;
import org.joml.FrustumIntersection;

import java.util.List;

/**
 * Created by MinusKelvin on 2/9/16.
 */
class VerticalChunk {
	Chunk[] chunks = new Chunk[8];
	private int[][] highPoints = new int[Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE];
	private int x,z,dim;
	private boolean gen;
	
	public VerticalChunk(int x, int z, int dim, List<Cave.Segment> nearbySegments) {
		this.x = x;
		this.z = z;
		this.dim = dim;
		
		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 32; j++) {
				double h1 = World.getWorld().noise1.eval((x+i) / 100.0, (j+z) / 100.0) * 1.55 + 0.18;
				h1 *= h1*h1;
				double h2 = World.getWorld().noise2.eval((x+i) / 15.0, (j+z) / 15.0) / 10;
				highPoints[i][j] = (int) ((h1 + h2 + 2.0) * 20.0) + 55;
			}
		}
		for (int i = 0; i < 8; i++)
			chunks[i] = new Chunk(x, i*32, z, dim, highPoints, nearbySegments);
		
		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 32; j++) {
				for (int k = 255; k >= 0; k--) {
					if (chunks[k >> 5].getTile(i, k & 0x1F, j) != Tile.Air.id)
						break;
					chunks[k >> 5].rawSetLight(i, k & 0x1F, j, LightChannel.SUN, 15);
				}
			}
		}
	}
	
	public void generate() {
		for (int i = 0; i < 32; i++)
			for (int j = 0; j < 32; j++)
				if (World.getWorld().noise3.eval(x+i,z+j) > 0.8)
					Trees.genMapleTree(x+i, highPoints[i][j], z+j, dim);
		gen = true;
	}
	
	public void render(FrustumIntersection culler, boolean shadowPass) {
		for (int k = 0; k < 8; k++)
			if (culler.testAab(x, k*32, z, x+32, k*32+32, z+32))
				chunks[k].render(shadowPass);
	}
	
	public boolean isGened() {
		return gen;
	}
}
