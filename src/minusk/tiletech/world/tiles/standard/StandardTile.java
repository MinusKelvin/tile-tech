package minusk.tiletech.world.tiles.standard;

import minusk.tiletech.world.tiles.Tile;
import minusk.tiletech.world.entities.Entity;
import org.joml.RayAabIntersection;

import java.nio.ByteBuffer;

import static minusk.tiletech.render.FaceRenderer.*;
import static minusk.tiletech.utils.Util.*;

/**
 * Created by MinusKelvin on 2/11/16.
 */
public abstract class StandardTile extends Tile {
	protected int top, bottom, east, west, north, south;
	protected float wavy;
	
	public StandardTile(short id, int top, int bottom, int east, int west, int north, int south, float wavy) {
		super(id);
		this.top = top;
		this.bottom = bottom;
		this.east = east;
		this.west = west;
		this.north = north;
		this.south = south;
		this.wavy = wavy;
	}
	
	private static Tile[][][] blocks = new Tile[3][3][3];
	@Override
	public int render(Tile[][][] chunkAndEdges, ByteBuffer vertices, int verticesLeft, int x, int y, int z, int dim) {
		if (verticesLeft < 36)
			return -1;
		
		for (int i = -1; i < 2; i++)
			for (int j = -1; j < 2; j++)
				for (int k = -1; k < 2; k++)
					blocks[i + 1][j + 1][k + 1] = chunkAndEdges[cnkIdx(x)+1+i][cnkIdx(z)+1+j][cnkIdx(y)+1+k];
		
		int verts = 0;
		
		if (blocks[1][0][1].isTransparentSouth(x,y,z-1,dim)) {
			int aoBits = 0;
			
			if (blocks[1][0][2].contributesAO(x,y+1,z-1,dim))
				aoBits |= 1;
			if (blocks[2][0][2].contributesAO(x+1,y+1,z-1,dim))
				aoBits |= 2;
			if (blocks[2][0][1].contributesAO(x+1,y,z-1,dim))
				aoBits |= 4;
			if (blocks[2][0][0].contributesAO(x+1,y-1,z-1,dim))
				aoBits |= 8;
			if (blocks[1][0][0].contributesAO(x,y-1,z-1,dim))
				aoBits |= 16;
			if (blocks[0][0][0].contributesAO(x-1,y-1,z-1,dim))
				aoBits |= 32;
			if (blocks[0][0][1].contributesAO(x-1,y,z-1,dim))
				aoBits |= 64;
			if (blocks[0][0][2].contributesAO(x-1,y+1,z-1,dim))
				aoBits |= 128;
			
			verts += renderNorthFace(vertices, aoBits, x, y, z, north, wavy);
		}
		
		if (blocks[1][2][1].isTransparentNorth(x,y,z+1,dim)) {
			int aoBits = 0;
			
			if (blocks[1][2][2].contributesAO(x,y+1,z+1,dim))
				aoBits |= 1;
			if (blocks[0][2][2].contributesAO(x-1,y+1,z+1,dim))
				aoBits |= 2;
			if (blocks[0][2][1].contributesAO(x-1,y,z+1,dim))
				aoBits |= 4;
			if (blocks[0][2][0].contributesAO(x-1,y-1,z+1,dim))
				aoBits |= 8;
			if (blocks[1][2][0].contributesAO(x,y-1,z+1,dim))
				aoBits |= 16;
			if (blocks[2][2][0].contributesAO(x+1,y-1,z+1,dim))
				aoBits |= 32;
			if (blocks[2][2][1].contributesAO(x+1,y,z+1,dim))
				aoBits |= 64;
			if (blocks[2][2][2].contributesAO(x+1,y+1,z+1,dim))
				aoBits |= 128;
			
			verts += renderSouthFace(vertices, aoBits, x, y, z, south, wavy);
		}
		
		if (blocks[0][1][1].isTransparentEast(x-1,y,z,dim)) {
			int aoBits = 0;
			
			if (blocks[0][1][2].contributesAO(x-1,y+1,z,dim))
				aoBits |= 1;
			if (blocks[0][0][2].contributesAO(x-1,y+1,z-1,dim))
				aoBits |= 2;
			if (blocks[0][0][1].contributesAO(x-1,y,z-1,dim))
				aoBits |= 4;
			if (blocks[0][0][0].contributesAO(x-1,y-1,z-1,dim))
				aoBits |= 8;
			if (blocks[0][1][0].contributesAO(x-1,y-1,z,dim))
				aoBits |= 16;
			if (blocks[0][2][0].contributesAO(x-1,y-1,z+1,dim))
				aoBits |= 32;
			if (blocks[0][2][1].contributesAO(x-1,y,z+1,dim))
				aoBits |= 64;
			if (blocks[0][2][2].contributesAO(x-1,y+1,z+1,dim))
				aoBits |= 128;
			
			verts += renderWestFace(vertices, aoBits, x, y, z, west, wavy);
		}
		
		if (blocks[2][1][1].isTransparentWest(x+1,y,z,dim)) {
			int aoBits = 0;
			
			if (blocks[2][1][2].contributesAO(x+1,y+1,z,dim))
				aoBits |= 1;
			if (blocks[2][2][2].contributesAO(x+1,y+1,z+1,dim))
				aoBits |= 2;
			if (blocks[2][2][1].contributesAO(x+1,y,z+1,dim))
				aoBits |= 4;
			if (blocks[2][2][0].contributesAO(x+1,y-1,z+1,dim))
				aoBits |= 8;
			if (blocks[2][1][0].contributesAO(x+1,y-1,z,dim))
				aoBits |= 16;
			if (blocks[2][0][0].contributesAO(x+1,y-1,z-1,dim))
				aoBits |= 32;
			if (blocks[2][0][1].contributesAO(x+1,y,z-1,dim))
				aoBits |= 64;
			if (blocks[2][0][2].contributesAO(x+1,y+1,z-1,dim))
				aoBits |= 128;
			
			verts += renderEastFace(vertices, aoBits, x, y, z, east, wavy);
		}
		
		if (blocks[1][1][0].isTransparentTop(x,y-1,z,dim)) {
			int aoBits = 0;
			
			if (blocks[1][0][0].contributesAO(x,y-1,z+1,dim))
				aoBits |= 1;
			if (blocks[2][0][0].contributesAO(x-1,y-1,z+1,dim))
				aoBits |= 2;
			if (blocks[2][1][0].contributesAO(x-1,y-1,z,dim))
				aoBits |= 4;
			if (blocks[2][2][0].contributesAO(x-1,y-1,z-1,dim))
				aoBits |= 8;
			if (blocks[1][2][0].contributesAO(x,y-1,z-1,dim))
				aoBits |= 16;
			if (blocks[0][2][0].contributesAO(x+1,y-1,z-1,dim))
				aoBits |= 32;
			if (blocks[0][1][0].contributesAO(x+1,y-1,z,dim))
				aoBits |= 64;
			if (blocks[0][0][0].contributesAO(x+1,y-1,z+1,dim))
				aoBits |= 128;
			
			verts += renderBottomFace(vertices, aoBits, x, y, z, bottom, wavy);
		}
		
		if (blocks[1][1][2].isTransparentBottom(x,y+1,z,dim)) {
			int aoBits = 0;
			
			if (blocks[1][2][2].contributesAO(x,y+1,z+1,dim))
				aoBits |= 1;
			if (blocks[2][2][2].contributesAO(x+1,y+1,z+1,dim))
				aoBits |= 2;
			if (blocks[2][1][2].contributesAO(x+1,y+1,z,dim))
				aoBits |= 4;
			if (blocks[2][0][2].contributesAO(x+1,y+1,z-1,dim))
				aoBits |= 8;
			if (blocks[1][0][2].contributesAO(x,y+1,z-1,dim))
				aoBits |= 16;
			if (blocks[0][0][2].contributesAO(x-1,y+1,z-1,dim))
				aoBits |= 32;
			if (blocks[0][1][2].contributesAO(x-1,y+1,z,dim))
				aoBits |= 64;
			if (blocks[0][2][2].contributesAO(x-1,y+1,z+1,dim))
				aoBits |= 128;
			
			verts += renderTopFace(vertices, aoBits, x, y, z, top, wavy);
		}
		
		return verts;
	}
	
	@Override
	public float highX(int x, int y, int z, int dim, Entity entity) {
		return x+1;
	}
	
	@Override
	public float lowX(int x, int y, int z, int dim, Entity entity) {
		return x;
	}
	
	@Override
	public float highY(int x, int y, int z, int dim, Entity entity) {
		return y+1;
	}
	
	@Override
	public float lowY(int x, int y, int z, int dim, Entity entity) {
		return y;
	}
	
	@Override
	public float highZ(int x, int y, int z, int dim, Entity entity) {
		return z+1;
	}
	
	@Override
	public float lowZ(int x, int y, int z, int dim, Entity entity) {
		return z;
	}
	
	@Override
	public boolean raytrace(int x, int y, int z, int dim, RayAabIntersection ray) {
		return true;
	}
}
