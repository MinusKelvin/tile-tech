package minusk.tiletech.world.tiles;

import minusk.tiletech.world.entities.Entity;
import minusk.tiletech.world.tiles.standard.StandardEmptyTile;
import minusk.tiletech.world.tiles.standard.StandardSolidTile;
import minusk.tiletech.world.tiles.standard.StandardTransparentTile;
import org.joml.RayAabIntersection;

import java.nio.ByteBuffer;

/**
 * Created by MinusKelvin on 1/25/16.
 */
public abstract class Tile {
	private static final Tile[] TILES = new Tile[1024];
	
	public static final Tile Air = new StandardEmptyTile((short) 0,0,0,0,0,0,0,0) {
		@Override public int render(Tile[][][] c, ByteBuffer v, int l, int x, int y, int z, int d) {return 0;}
	};
	public static final Tile Grass = new StandardSolidTile((short) 1,2, 0, 1, 1, 1, 1, 0);
	public static final Tile Dirt = new StandardSolidTile((short) 2,0, 0, 0, 0, 0, 0, 0);
	public static final Tile Maple_Log = new StandardSolidTile((short) 3,4, 4, 3, 3, 3, 3, 0);
	public static final Tile Maple_Leaves = new StandardTransparentTile((short) 4,5, 5, 5, 5, 5, 5, 0.5f);
	public static final Tile Bedrock = new StandardSolidTile((short) 5,6,6,6,6,6,6,0);
	public static final Tile Stone = new StandardSolidTile((short) 6,7,7,7,7,7,7,0);
	
	public static Tile getTile(int id) {
		return TILES[id & 0xFFFF];
	}
	
	public final short id;
	public Tile(short id) {
		this.id = id;
		if (getTile(id) != null)
			System.err.println("Overwriting " + getTile(id) + " with " + this + " at id " + (id & 0xFFFF));
		TILES[id & 0xFFFF] = this;
	}
	
	public abstract boolean isTransparentTop(int x, int y, int z, int dim);
	public abstract boolean isTransparentBottom(int x, int y, int z, int dim);
	public abstract boolean isTransparentNorth(int x, int y, int z, int dim);
	public abstract boolean isTransparentSouth(int x, int y, int z, int dim);
	public abstract boolean isTransparentEast(int x, int y, int z, int dim);
	public abstract boolean isTransparentWest(int x, int y, int z, int dim);
	
	public abstract boolean contributesAO(int x, int y, int z, int dim);
	
	/** Only called when the entity intersects the block, exists for blocks that aren't fully solid */
	public abstract boolean collide(int x, int y, int z, int dim, Entity entity);
	public abstract boolean raytrace(int x, int y, int z, int dim, RayAabIntersection ray);
	public abstract float highX(int x, int y, int z, int dim, Entity entity);
	public abstract float lowX(int x, int y, int z, int dim, Entity entity);
	public abstract float highY(int x, int y, int z, int dim, Entity entity);
	public abstract float lowY(int x, int y, int z, int dim, Entity entity);
	public abstract float highZ(int x, int y, int z, int dim, Entity entity);
	public abstract float lowZ(int x, int y, int z, int dim, Entity entity);
	
	public abstract int render(Tile[][][] chunkAndEdges, ByteBuffer vertices, int verticesLeft, int x, int y, int z, int dim);
	
	public void onDelete(int x, int y, int z, int dim) {}
	public void onCreate(int x, int y, int z, int dim) {}
}
