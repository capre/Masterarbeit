package profiles;

import java.util.HashMap;

public class Annotation {
	
	private String gene;
	private HashMap<Integer, Float> values;
	
	
	public Annotation(String gene, String line){
		this.gene = gene;
		this.values = new HashMap<Integer, Float>();
		fill(line);
	}

	
	// getter
	public String getGene() {
		return gene;
	}
	public HashMap<Integer, Float> getValues() {
		return values;
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
	

	
	
	

}
