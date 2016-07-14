package io;

// getestet in profileGenerator mit :
/*
 * 		io.ByteLine bl = new io.ByteLine(2);
		int i1 = 0;
		float f1 = (float) 0.04407013;
		int i2 = 12;
		float f2 = (float) 0.0011428349;
		
		bl.addElement(i1, f1);
		bl.addElement(i2, f2);
		
		int[] ind = bl.getIndices();
		System.out.println(ind[1]);
		
		float[] val= bl.getValues();
		System.out.println(val[1]);
 */

public class ByteLine {
	
	private int size;
	private int[] indices;
	private float[] values;
	private int counter;	// counts position on arrays (value of next free array files to be filled)
	
	
	// create new Line object (line contains "size" elements)
	public ByteLine(int size){
		this.size = size;
		this.indices = new int[size];
		this.values = new float[size];
		this.counter = 0;
	}
	

	// getter for arrays and size
	public int[] getIndices() {
		return indices;
	}
	public float[] getValues() {
		return values;
	}
	public int getSize() {
		return size;
	}
	
	// add one element consisting of index and value (add to arrays)
	public void addElement(int i, float f){
		indices[counter] = i;
		values[counter] = f;
		counter++;
		//System.out.println(counter);
	}

	

	
	
	
	
	
	

}
