package minusk.tiletech.world.tiles.standard;

/**
 * Created by MinusKelvin on 2/4/16.
 */
public class StandardTransparentTile extends StandardSolidTile {
	public StandardTransparentTile(short id, int top, int bottom, int east, int west, int north, int south) {
		super(id, top, bottom, east, west, north, south);
	}
	
	@Override
	public boolean isTransparentTop(int x, int y, int z, int dim) {
		return true;
	}
	
	@Override
	public boolean isTransparentBottom(int x, int y, int z, int dim) {
		return true;
	}
	
	@Override
	public boolean isTransparentNorth(int x, int y, int z, int dim) {
		return true;
	}
	
	@Override
	public boolean isTransparentSouth(int x, int y, int z, int dim) {
		return true;
	}
	
	@Override
	public boolean isTransparentEast(int x, int y, int z, int dim) {
		return true;
	}
	
	@Override
	public boolean isTransparentWest(int x, int y, int z, int dim) {
		return true;
	}
	
	@Override
	public boolean contributesAO(int x, int y, int z, int dim) {
		return false;
	}
}
