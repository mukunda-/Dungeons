package com.mukunda.dungeons;

public class Point {
	public float[] coords;
	
	public Point( Point p ) {
		coords = new float[3];
		coords[0] = p.coords[0];
		coords[1] = p.coords[1];
		coords[2] = p.coords[2];
	}
		 
	public Point( float x, float y, float z ) {
		coords = new float[3];
		coords[0] = x;
		coords[1] = y;
		coords[2] = z;
	}
	
	public Point( float[] pos ) {
		coords = new float[3];
		for( int i = 0; i < 3; i++ ) {
			coords[i] = pos[i];
		}
	}
}
