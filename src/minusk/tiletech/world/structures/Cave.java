package minusk.tiletech.world.structures;

import org.joml.Vector3f;

import java.util.ArrayList;

/**
 * @author MinusKelvin
 */
public class Cave {
	public final ArrayList<Segment> segments = new ArrayList<>();
	
	public static class Segment {
		public final Vector3f p1, p2, dir;
		public final float length, size1, size2;
		
		public Segment(Vector3f p1, Vector3f p2, float size1, float size2) {
			this.p1 = p1;
			this.p2 = p2;
			dir = new Vector3f(p2).sub(p1);
			length = dir.length();
			dir.mul(1/length);
			this.size1 = size1;
			this.size2 = size2;
		}
	}
}
