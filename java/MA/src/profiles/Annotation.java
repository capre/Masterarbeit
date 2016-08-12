package profiles;

import java.util.HashMap;

public class Annotation {
	
	private String gene;
	private int indexOld;
	private int indexNew;
	private HashMap<Integer, Float> values;
	
	private HashMap<Integer, Integer> occurence; // counts, how often a term occurs (only for sum of annotations)

	
	// create "filled" Annotation object
	public Annotation(String gene, String line, int oldIndex, int newIndex){
		this.gene = gene;
		this.indexOld = oldIndex;
		this.indexNew = newIndex;
		this.values = new HashMap<Integer, Float>();
		fill(line);
	}
	
	// create "empty" Annotation object
	public Annotation(String gene){
		this.gene = gene;
		this.values = new HashMap<Integer, Float>();
		this.occurence = new HashMap<Integer, Integer>();
	}

	
	// getter
	public String getGene() {
		return gene;
	}
	public HashMap<Integer, Float> getValues() {
		return values;
	}
	public HashMap<Integer, Integer> getOccurence() {
		return occurence;
	}
	
	

	// read line of dbset_filtered.weight (weightOut) and fill Annotation object
	//26	0:0.015434295	965:0.020228693	2891:0.014038323	4763:0.0128456475	
	private void fill(String line) {
		String[] l = line.split("\t");
		for(int pos=1; pos<l.length; pos++){
			int index = Integer.parseInt((l[pos].split(":")[0]));
			float value = Float.parseFloat((l[pos].split(":")[1]));
			values.put(index, value);
			//System.out.println(index+"\t"+value);
		}
	}
	

	// add values of new Annotation a to Annotation object
	public Annotation addAnnotation(Annotation a){
		//System.out.println("add annotation for "+a.gene);
		
		for(int index : a.getValues().keySet()){
			float val = (float)0;
			int count = 0;
			if(this.values.containsKey(index)){	
				val = this.values.get(index);	// value so far
				count = this.occurence.get(index);	// occurences so far
			}
			this.values.put(index, val+a.getValues().get(index));
			this.occurence.put(index, count+1);
			//System.out.println(count+" -> "+(count+1));
		}
		
		return this;
	}

	
	// removes annotation value for self-association
	public void removeSelfAssociation() {
		int oldSize = this.values.size();
		this.values.remove(this.indexOld);
		int newSize = this.values.size();
		//System.out.println(this.gene+"\t"+this.indexOld+"\t"+this.indexNew+"\t"+oldSize+"\t"+newSize);
	}
	
	
	

}
