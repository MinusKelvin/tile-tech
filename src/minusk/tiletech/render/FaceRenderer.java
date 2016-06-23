package minusk.tiletech.render;

import java.nio.ByteBuffer;

/**
 * Created by MinusKelvin on 1/25/16.
 */
public final class FaceRenderer {
	static int vertexAO(boolean s1, boolean s2, boolean c) {
		int factor;
		if (s1 && s2)
			factor = 3;
		else
			factor = (s1?1:0) + (s2?1:0) + (c?1:0);
		return 255 - (factor * 48);
	}
	
	public static int renderWestFace(ByteBuffer vertices, int aoBits, int x, int y, int z, int faceID, float wavy) {
		int[] vals = {255,255,255,255};
		
		vals[0] = vertexAO((aoBits & 64) != 0, (aoBits & 16) != 0, (aoBits & 32) != 0);
		vals[1] = vertexAO((aoBits & 64) != 0, (aoBits & 1) != 0, (aoBits & 128) != 0);
		vals[2] = vertexAO((aoBits & 4) != 0, (aoBits & 16) != 0, (aoBits & 8) != 0);
		vals[3] = vertexAO((aoBits & 4) != 0, (aoBits & 1) != 0, (aoBits & 2) != 0);
		
		vertices.putFloat(x);          // X position
		vertices.putFloat(y);          // Y position
		vertices.putFloat(z + 1.0f);   // Z position
		vertices.putFloat(1.0f);       // Tex X
		vertices.putFloat(1.0f);       // Tex Y
		vertices.putFloat(faceID);     // Tex Z
		vertices.putFloat(1.0f);       // Normal X
		vertices.putFloat(0.0f);       // Normal Y
		vertices.putFloat(0.0f);       // Normal Z
		vertices.put((byte) 0);        // Quad interpolation X
		vertices.put((byte) 0);        // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);  // AO 1
		vertices.put((byte) vals[1]);  // AO 2
		vertices.put((byte) vals[2]);  // AO 3
		vertices.put((byte) vals[3]);  // AO 4
		
		vertices.putFloat(x);
		vertices.putFloat(y);
		vertices.putFloat(z);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.put((byte) 0xFF);     // Quad interpolation X
		vertices.put((byte) 0);        // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x);
		vertices.putFloat(y + 1.0f);
		vertices.putFloat(z + 1.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.put((byte) 0);        // Quad interpolation X
		vertices.put((byte) 0xFF);     // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x);
		vertices.putFloat(y + 1.0f);
		vertices.putFloat(z);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.put((byte) 0xFF);     // Quad interpolation X
		vertices.put((byte) 0xFF);     // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x);
		vertices.putFloat(y + 1.0f);
		vertices.putFloat(z + 1.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.put((byte) 0);        // Quad interpolation X
		vertices.put((byte) 0xFF);     // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x);
		vertices.putFloat(y);
		vertices.putFloat(z);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.put((byte) 0xFF);     // Quad interpolation X
		vertices.put((byte) 0);        // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		return 6;
	}
	
	public static int renderEastFace(ByteBuffer vertices, int aoBits, int x, int y, int z, int faceID, float wavy) {
		int[] vals = {255,255,255,255};
		
		vals[0] = vertexAO((aoBits & 64) != 0, (aoBits & 16) != 0, (aoBits & 32) != 0);
		vals[1] = vertexAO((aoBits & 64) != 0, (aoBits & 1) != 0, (aoBits & 128) != 0);
		vals[2] = vertexAO((aoBits & 4) != 0, (aoBits & 16) != 0, (aoBits & 8) != 0);
		vals[3] = vertexAO((aoBits & 4) != 0, (aoBits & 1) != 0, (aoBits & 2) != 0);
		
		vertices.putFloat(x + 1.0f);
		vertices.putFloat(y);
		vertices.putFloat(z);
		vertices.putFloat(1.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(-1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.put((byte) 0);        // Quad interpolation X
		vertices.put((byte) 0);        // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x + 1.0f);
		vertices.putFloat(y);
		vertices.putFloat(z + 1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(-1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.put((byte) 0xFF);     // Quad interpolation X
		vertices.put((byte) 0);        // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x + 1.0f);
		vertices.putFloat(y + 1.0f);
		vertices.putFloat(z);
		vertices.putFloat(1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(-1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.put((byte) 0);        // Quad interpolation X
		vertices.put((byte) 0xFF);     // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x + 1.0f);
		vertices.putFloat(y + 1.0f);
		vertices.putFloat(z + 1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(-1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.put((byte) 0xFF);     // Quad interpolation X
		vertices.put((byte) 0xFF);     // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x + 1.0f);
		vertices.putFloat(y + 1.0f);
		vertices.putFloat(z);
		vertices.putFloat(1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(-1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.put((byte) 0);        // Quad interpolation X
		vertices.put((byte) 0xFF);     // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x + 1.0f);
		vertices.putFloat(y);
		vertices.putFloat(z + 1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(-1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.put((byte) 0xFF);     // Quad interpolation X
		vertices.put((byte) 0);        // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		return 6;
	}
	
	public static int renderNorthFace(ByteBuffer vertices, int aoBits, int x, int y, int z, int faceID, float wavy) {
		int[] vals = {255,255,255,255};
		
		vals[0] = vertexAO((aoBits & 64) != 0, (aoBits & 16) != 0, (aoBits & 32) != 0);
		vals[1] = vertexAO((aoBits & 64) != 0, (aoBits & 1) != 0, (aoBits & 128) != 0);
		vals[2] = vertexAO((aoBits & 4) != 0, (aoBits & 16) != 0, (aoBits & 8) != 0);
		vals[3] = vertexAO((aoBits & 4) != 0, (aoBits & 1) != 0, (aoBits & 2) != 0);
		
		vertices.putFloat(x);
		vertices.putFloat(y);
		vertices.putFloat(z);
		vertices.putFloat(1.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.put((byte) 0);        // Quad interpolation X
		vertices.put((byte) 0);        // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x + 1.0f);
		vertices.putFloat(y);
		vertices.putFloat(z);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.put((byte) 0xFF);     // Quad interpolation X
		vertices.put((byte) 0);        // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x);
		vertices.putFloat(y + 1.0f);
		vertices.putFloat(z);
		vertices.putFloat(1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.put((byte) 0);        // Quad interpolation X
		vertices.put((byte) 0xFF);     // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x + 1.0f);
		vertices.putFloat(y + 1.0f);
		vertices.putFloat(z);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.put((byte) 0xFF);     // Quad interpolation X
		vertices.put((byte) 0xFF);     // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x);
		vertices.putFloat(y + 1.0f);
		vertices.putFloat(z);
		vertices.putFloat(1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.put((byte) 0);        // Quad interpolation X
		vertices.put((byte) 0xFF);     // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x + 1.0f);
		vertices.putFloat(y);
		vertices.putFloat(z);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.put((byte) 0xFF);     // Quad interpolation X
		vertices.put((byte) 0);        // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		return 6;
	}
	
	public static int renderSouthFace(ByteBuffer vertices, int aoBits, int x, int y, int z, int faceID, float wavy) {
		int[] vals = {255,255,255,255};
		
		vals[0] = vertexAO((aoBits & 64) != 0, (aoBits & 16) != 0, (aoBits & 32) != 0);
		vals[1] = vertexAO((aoBits & 64) != 0, (aoBits & 1) != 0, (aoBits & 128) != 0);
		vals[2] = vertexAO((aoBits & 4) != 0, (aoBits & 16) != 0, (aoBits & 8) != 0);
		vals[3] = vertexAO((aoBits & 4) != 0, (aoBits & 1) != 0, (aoBits & 2) != 0);
		
		vertices.putFloat(x + 1.0f);
		vertices.putFloat(y);
		vertices.putFloat(z + 1.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(-1.0f);
		vertices.put((byte) 0);        // Quad interpolation X
		vertices.put((byte) 0);        // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x);
		vertices.putFloat(y);
		vertices.putFloat(z + 1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(-1.0f);
		vertices.put((byte) 0xFF);     // Quad interpolation X
		vertices.put((byte) 0);        // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x + 1.0f);
		vertices.putFloat(y + 1.0f);
		vertices.putFloat(z + 1.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(-1.0f);
		vertices.put((byte) 0);        // Quad interpolation X
		vertices.put((byte) 0xFF);     // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x);
		vertices.putFloat(y + 1.0f);
		vertices.putFloat(z + 1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(-1.0f);
		vertices.put((byte) 0xFF);     // Quad interpolation X
		vertices.put((byte) 0xFF);     // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x + 1.0f);
		vertices.putFloat(y + 1.0f);
		vertices.putFloat(z + 1.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(-1.0f);
		vertices.put((byte) 0);        // Quad interpolation X
		vertices.put((byte) 0xFF);     // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x);
		vertices.putFloat(y);
		vertices.putFloat(z + 1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(-1.0f);
		vertices.put((byte) 0xFF);     // Quad interpolation X
		vertices.put((byte) 0);        // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		return 6;
	}
	
	public static int renderTopFace(ByteBuffer vertices, int aoBits, int x, int y, int z, int faceID, float wavy) {
		int[] vals = {255,255,255,255};
		
		vals[0] = vertexAO((aoBits & 64) != 0, (aoBits & 16) != 0, (aoBits & 32) != 0);
		vals[1] = vertexAO((aoBits & 64) != 0, (aoBits & 1) != 0, (aoBits & 128) != 0);
		vals[2] = vertexAO((aoBits & 4) != 0, (aoBits & 16) != 0, (aoBits & 8) != 0);
		vals[3] = vertexAO((aoBits & 4) != 0, (aoBits & 1) != 0, (aoBits & 2) != 0);
		
		vertices.putFloat(x);
		vertices.putFloat(y + 1.0f);
		vertices.putFloat(z);
		vertices.putFloat(1.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(0.0f);
		vertices.put((byte) 0);        // Quad interpolation X
		vertices.put((byte) 0);        // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte)vals[0]);
		vertices.put((byte)vals[1]);
		vertices.put((byte)vals[2]);
		vertices.put((byte)vals[3]);
		
		vertices.putFloat(x + 1.0f);
		vertices.putFloat(y + 1.0f);
		vertices.putFloat(z);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(0.0f);
		vertices.put((byte) 0xFF);     // Quad interpolation X
		vertices.put((byte) 0);        // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte)vals[0]);
		vertices.put((byte)vals[1]);
		vertices.put((byte)vals[2]);
		vertices.put((byte)vals[3]);
		
		vertices.putFloat(x);
		vertices.putFloat(y + 1.0f);
		vertices.putFloat(z + 1.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(0.0f);
		vertices.put((byte) 0);        // Quad interpolation X
		vertices.put((byte) 0xFF);     // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte)vals[0]);
		vertices.put((byte)vals[1]);
		vertices.put((byte)vals[2]);
		vertices.put((byte)vals[3]);
		
		vertices.putFloat(x + 1.0f);
		vertices.putFloat(y + 1.0f);
		vertices.putFloat(z + 1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(0.0f);
		vertices.put((byte) 0xFF);     // Quad interpolation X
		vertices.put((byte) 0xFF);     // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte)vals[0]);
		vertices.put((byte)vals[1]);
		vertices.put((byte)vals[2]);
		vertices.put((byte)vals[3]);
		
		vertices.putFloat(x);
		vertices.putFloat(y + 1.0f);
		vertices.putFloat(z + 1.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(0.0f);
		vertices.put((byte) 0);        // Quad interpolation X
		vertices.put((byte) 0xFF);     // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte)vals[0]);
		vertices.put((byte)vals[1]);
		vertices.put((byte)vals[2]);
		vertices.put((byte)vals[3]);
		
		vertices.putFloat(x + 1.0f);
		vertices.putFloat(y + 1.0f);
		vertices.putFloat(z);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(0.0f);
		vertices.put((byte) 0xFF);     // Quad interpolation X
		vertices.put((byte) 0);        // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte)vals[0]);
		vertices.put((byte)vals[1]);
		vertices.put((byte)vals[2]);
		vertices.put((byte)vals[3]);
		
		return 6;
	}
	
	public static int renderBottomFace(ByteBuffer vertices, int aoBits, int x, int y, int z, int faceID, float wavy) {
		int[] vals = {255,255,255,255};
		
		vals[0] = vertexAO((aoBits & 64) != 0, (aoBits & 16) != 0, (aoBits & 32) != 0);
		vals[1] = vertexAO((aoBits & 64) != 0, (aoBits & 1) != 0, (aoBits & 128) != 0);
		vals[2] = vertexAO((aoBits & 4) != 0, (aoBits & 16) != 0, (aoBits & 8) != 0);
		vals[3] = vertexAO((aoBits & 4) != 0, (aoBits & 1) != 0, (aoBits & 2) != 0);
		
		vertices.putFloat(x);
		vertices.putFloat(y);
		vertices.putFloat(z + 1.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(-1.0f);
		vertices.putFloat(0.0f);
		vertices.put((byte) 0);        // Quad interpolation X
		vertices.put((byte) 0);        // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x + 1.0f);
		vertices.putFloat(y);
		vertices.putFloat(z + 1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(-1.0f);
		vertices.putFloat(0.0f);
		vertices.put((byte) 0xFF);     // Quad interpolation X
		vertices.put((byte) 0);        // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x);
		vertices.putFloat(y);
		vertices.putFloat(z);
		vertices.putFloat(1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(-1.0f);
		vertices.putFloat(0.0f);
		vertices.put((byte) 0);        // Quad interpolation X
		vertices.put((byte) 0xFF);     // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x + 1.0f);
		vertices.putFloat(y);
		vertices.putFloat(z);
		vertices.putFloat(0.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(-1.0f);
		vertices.putFloat(0.0f);
		vertices.put((byte) 0xFF);     // Quad interpolation X
		vertices.put((byte) 0xFF);     // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x);
		vertices.putFloat(y);
		vertices.putFloat(z);
		vertices.putFloat(1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(-1.0f);
		vertices.putFloat(0.0f);
		vertices.put((byte) 0);        // Quad interpolation X
		vertices.put((byte) 0xFF);     // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		vertices.putFloat(x + 1.0f);
		vertices.putFloat(y);
		vertices.putFloat(z + 1.0f);
		vertices.putFloat(0.0f);
		vertices.putFloat(1.0f);
		vertices.putFloat(faceID);
		vertices.putFloat(0.0f);
		vertices.putFloat(-1.0f);
		vertices.putFloat(0.0f);
		vertices.put((byte) 0xFF);     // Quad interpolation X
		vertices.put((byte) 0);        // Quad interpolation Y
		vertices.put((byte) (wavy*255));// Wavy
		vertices.put((byte) 0);        // Padding
		vertices.put((byte) vals[0]);
		vertices.put((byte) vals[1]);
		vertices.put((byte) vals[2]);
		vertices.put((byte) vals[3]);
		
		return 6;
	}
}
