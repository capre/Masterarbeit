package io;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

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
	// format (tab-separated): values  (line begins with tab!)
	public void writeComplete(FileOutputWriter writer, int n) {
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




	//merge 2 ByteLines: return a ByteLine object containing average values for each feature (works)
	public ByteLine merge(ByteLine bl2) {
		
		// get size of new ByteLine: count all indices
		TreeSet<Integer> ind = new TreeSet<Integer>();
		for (int pos=0; pos<this.size; pos++){
			ind.add(indices[pos]);
		}
		for (int pos=0; pos<bl2.getSize(); pos++){
			ind.add(bl2.getIndices()[pos]);
		}
		int s = ind.size();
		
		ByteLine bl_new = new ByteLine(s, this.gene);
		// check, if genes are the same
		if(!this.gene.equals(bl2.getGene())){
			System.out.println("Warning: you merge lines with two different genes: "+this.gene+" and "+bl2.getGene());
		}
		
		// create new arrays
		int pos1 = 0;
		int pos2 = 0;
		for (int i: ind){
			if(pos1<this.size && pos2<bl2.getSize() && this.indices[pos1]==i && bl2.getIndices()[pos2]==i){
				// calc average of values
				float f = (this.values[pos1]+bl2.getValues()[pos2])/2;
				bl_new.addElement(i, f);
				pos1++;
				pos2++;
			}
			else if(pos1<this.size && this.indices[pos1]==i){
				bl_new.addElement(this.indices[pos1], this.values[pos1]);
				pos1++;
			}
			else if(pos2<bl2.getSize() && bl2.getIndices()[pos2]==i){
				bl_new.addElement(bl2.getIndices()[pos2], bl2.getValues()[pos2]);
				pos2++;
			}
			else{
				System.out.print("Something went wrong: this index does not exist in bl1 or bl2.");
			}	
		}
		
		return bl_new;
	}
	

	// gets list of ByteLine objects, and number of columns for output (n)
	// calculates sums of values for each feature
	// returns ByteLine object containing the sums
	public static ByteLine sumFeatureValues(LinkedList<ByteLine> lines, int n) {
		ByteLine bl_sum = new ByteLine(n, "");
		
		float[] valSum = new float[n]; // array to sum up values for features
		
		// ...wenn man nicht summe, sondern mittelwerte berechnen will:
		int[] counter = new int[n];
		
		//jede ByteLine abarbeiten, und werte fuer features jeweils in array hinzuaddieren
		for (ByteLine bl : lines){
			for(int pos=0; pos<bl.getSize(); pos++){
				valSum[bl.getIndices()[pos]] += bl.getValues()[pos];
				counter[bl.getIndices()[pos]]++;
			}
		}
		
		// neue ByteLine aufbauen
		for(int pos=0; pos<n; pos++){
			// summe:
			//bl_sum.addElement(pos, valSum[pos]);
			
			//ODER mittelwerte:
			if(counter[pos]!=0){
				bl_sum.addElement(pos, (valSum[pos]/counter[pos]));
			}else{
				bl_sum.addElement(pos, (float)0);
			}
			
		}
		
		return bl_sum;
	}
	
	
	

}
