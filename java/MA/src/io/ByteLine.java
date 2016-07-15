package io;

import java.util.HashMap;

public class ByteLine {
	
	private int size;
	private int[] indices;
	private float[] values;
	private int counter;	// counts position in arrays (next free array fields to be filled)
	private String gene;
	
	
	// create new Line object (line contains "size" elements)
	public ByteLine(int size, String gene){
		this.size = size;
		this.indices = new int[size];
		this.values = new float[size];
		this.counter = 0;
		this.gene = gene;
	}
	

	// getter
	public int[] getIndices() {
		return indices;
	}
	public float[] getValues() {
		return values;
	}
	public int getSize() {
		return size;
	}
	public String getGene() {
		return gene;
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
	
	
	
	// write "complete" ByteLine to a file (using the given writer); fill non-existing values with "0" (also keep 0-columns)
	// overall "n" columns in complete line
	// format (tab-separated): size, values 
	public void writeComplete(FileOutputWriter writer, int n) {
		writer.write(size+"");
		int indexComplete = 0;
		for(int pos=0; pos<size; pos++){
			
			// replace non-existing values with 0
			while(indices[pos]!=indexComplete){
				writer.write("\t0");
				indexComplete++;
			}
			writer.write("\t"+values[pos]);
			indexComplete++;
		}
		
		// fill with "0" up to end of line
		while(indexComplete<n){
			writer.write("\t0");
			indexComplete++;
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
		ByteLine blFiltered = new ByteLine(s, this.gene);
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
