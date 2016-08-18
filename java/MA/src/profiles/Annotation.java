package profiles;

import java.util.HashMap;
import java.util.HashSet;

public class Annotation {
	
	private String gene;
	private int indexOld;
	private int indexNew;
	private HashMap<Integer, Float> values;
	
	private HashMap<Integer, Integer> occurence; // counts, how often a term occurs (only for sum of annotations)
	private HashSet<String> genes; //which single annotations are used for the sum of annotations?

	
	// create "filled" Annotation object
	public Annotation(String gene, String line, int oldIndex, int newIndex){
		this.gene = gene;
		this.indexOld = oldIndex;
		this.indexNew = newIndex;
		this.values = new HashMap<Integer, Float>();
		fill(line);
	}
	
	// create "empty" Annotation object; for sum of Annotations
	public Annotation(String gene){
		this.gene = gene;
		this.values = new HashMap<Integer, Float>();
		this.occurence = new HashMap<Integer, Integer>();
		this.genes = new HashSet<String>();
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
	public int getNumberOfAnnotations() {
		return this.genes.size();
	}
	public HashSet<String> getGenes() {
		return genes;
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
		this.genes.add(a.gene);
		
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
	
	/*
	//subtract annotation:
	// if gene was used for the given annotation sum: subtract it and return a copy
	// if gene was NOT used: just return annotation sum object
	public Annotation subtractAnnotationIfContained(Annotation a){
		if(this.genes.contains(a.gene)){
			Annotation copy = this.subtractAnnotation(a);	
			return copy;
		}
		else{
			return this;
		}
	}
	*/
	// Subtract values of new Annotation a from a copy of an Annotation object		//ok
	public Annotation subtractAnnotation(Annotation a){
		if(!this.genes.contains(a.gene)){
			System.out.println("Warning: subtracting annotation from profile it was not originally added to!");
		}
		Annotation copy = this.getCopySum();
		
		copy.genes.remove(a.gene);
		
		for(int index : a.getValues().keySet()){
			float val = copy.values.get(index);	// value so far
			int count = copy.occurence.get(index);	// occurences so far
			
			copy.values.put(index, val-a.getValues().get(index));
			copy.occurence.put(index, count-1);
		}
		//System.out.println(this.getNumberOfAnnotations()+"\t"+copy.getNumberOfAnnotations());
		return copy;
	}
	
	//returns a copy of the Annotation sum object		//ok
	private Annotation getCopySum(){
		Annotation copy = new Annotation("copy");
		
		copy.values = new HashMap<Integer, Float>();
		copy.values.putAll(this.values);
		
		copy.occurence = new HashMap<Integer, Integer>();
		copy.occurence.putAll(this.occurence);
		
		copy.genes = new HashSet<String>();
		copy.genes.addAll(this.genes);

		return copy;
	}
	
	

	
	// removes annotation value for self-association		//ok
	public void removeSelfAssociation() {
		int oldSize = this.values.size();
		this.values.remove(this.indexOld);
		int newSize = this.values.size();
		//System.out.println(this.gene+"\t"+this.indexOld+"\t"+this.indexNew+"\t"+oldSize+"\t"+newSize);
	}

	
	
	
	
	
	// calc scalar product of the given Annotation object a1 and a2		//ok
	public static float calcScalarProduct(Annotation a1, Annotation a2) {
		float scalar = (float)0;
		
		for(int index : a1.values.keySet()){
			if(a2.values.containsKey(index)){ // both vectors contain a value for this index => update scalar
				//System.out.print(scalar);
				float prod = a1.values.get(index) * a2.values.get(index);
				scalar += prod;
				//System.out.println("\t+\t"+prod+"\t=\t"+scalar);
			}
		}
		return scalar;
	}
	
	
	
	
	

}
