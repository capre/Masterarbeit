package io;

import java.util.HashMap;

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
	private int counter;	// counts position in arrays (next free array fields to be filled)
	
	
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
	
	
	// write ByteLine to a file (using the given writer)
	// format (tab-separated): size, index:value pairs
	public void write(FileOutputWriter writer) {
		writer.write(size+"");
		for(int pos=0; pos<size; pos++){
			writer.write("\t"+indices[pos]+":"+values[pos]);
			
			// for plotting: write only values
			//writer.write("\t"+values[pos]);
			//writer.write(values[pos]+"\n"); // one value per line -> distribution?!
		}
		writer.write("\n");
	}
	
	
	
	
	//filter line: keep only indices contained in old_indices (keys)
	// return new (filtered) ByteLine object with new, adjusted indices
	public ByteLine filter(HashMap<Integer, String> old_indices, HashMap<String,Integer> new_indices){
		//get size of new (filtered) ByteLine:
		int s = 0;
		for (int pos=0; pos<this.size; pos++){
			if(old_indices.containsKey(this.indices[pos])){
				s++;
			}	
		}
		
		//create new (filtered) ByteLine object
		ByteLine blFiltered = new ByteLine(s);
		for (int pos=0; pos<this.size; pos++){
			if(old_indices.containsKey(this.indices[pos])){
				// get new index: oldIndex -> gene -> newIndex:
				String gene = old_indices.get(this.indices[pos]);
				int newIndex = new_indices.get(gene);
				float val = this.values[pos];
				blFiltered.addElement(newIndex, val);
			}	
		}
		return blFiltered;
	}




	

	
	
	
	
	
	

}
