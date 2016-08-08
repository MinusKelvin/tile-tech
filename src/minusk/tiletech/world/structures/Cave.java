package minusk.tiletech.world.structures;

import org.joml.Vector3i;

import java.util.ArrayList;

/**
 * @author MinusKelvin
 */
public class Cave {
	public final ArrayList<Segment> segments = new ArrayList<>();
	public final ArrayList<Float> segmentSizes = new ArrayList<>();
	
	public static class Segment {
		public final Vector3i p1, p2;
		
		public Segment(Vector3i p1, Vector3i p2) {
			this.p1 = p1;
			this.p2 = p2;
		}
	}
}
