package profiles;

import java.util.HashMap;

import io.ByteLine;
import io.FileInputReader;
import io.FileOutputWriter;

public class ProfileCalculator {
	
	
	private static HashMap<Integer, String> index2term;		// size 88536
	private static HashMap<String, Annotation> gene2annotation;
	
	
	public static void run(String termIn,String weigthOut,String geneListProfiles,String matrixProbandsIn,
			String matrixProbandsOut,String matrixProbandsOutSVM,String matrixProbandsCluster) {
		

		//read in terms and create index2term
		readTerms(termIn); //ok
		
		//read weightOut and create Annotation objects in HashMap gene2annotation
		createAnnotations(weigthOut,geneListProfiles); //ok, bis auf doppelte zeilen zu genen...
		
		
		// OPTIONAL:create output matrix in Viscovery format (matrixProbandsOut)
		// mode specifies format for output-matrix: Viscovery; SVM; clustering (featProbands.txt)
		// ca 8 min; 4.65 GB
		boolean ignoreNegativeCadd = false;
		createMatrixProbandsOut(matrixProbandsIn,matrixProbandsOut, "Viscovery", ignoreNegativeCadd); // ok
		
		
		
		// OPTIONAL:create output matrix in SVM format (matrixProbandsOutSVM)
		// mode specifies format for output-matrix: 1=Viscovery; 2=SVM; 3=for clustering (featProbands.txt)
		// ca 13 min; 7.11 GB
		ignoreNegativeCadd = false;
		createMatrixProbandsOut(matrixProbandsIn,matrixProbandsOutSVM, "SVM", ignoreNegativeCadd); 
		
		
		
		// OPTIONAL: create matrix for hierarchical clustering (= write output of svmclust.pl: featProbands.txt)
		// mode specifies format for output-matrix: 1=Viscovery; 2=SVM; 3=for clustering (featProbands.txt)
		ignoreNegativeCadd = false;
		createMatrixProbandsOut(matrixProbandsIn,matrixProbandsCluster, "clustering", ignoreNegativeCadd); 
		
		
		
		
		/*
		//for testing: ok
		String line1 = "26\t0:-0.5\t1:10\t3:0.1\t6:2.5";
		Annotation a1 = new Annotation("AB", line1);
		
		String line2 = "26\t0:7\t3:10\t4:-2";
		Annotation a2 = new Annotation("FG", line2);
		
		float[] scores = new float[7];
		for (float f : scores){
			System.out.println("\t"+f);
		}
		System.out.println("--------------");
		
		float cadd1 = (float) 2;
		float cadd2 = (float) -3.5;
		
		scores = addAnnotationToScores_ignoreNegativeCadd(scores, a1, cadd1);
		for (float f : scores){
			System.out.println("\t"+f);
		}
		System.out.println("--------------");
		
		scores = addAnnotationToScores_ignoreNegativeCadd(scores, a2, cadd2);
		for (float f : scores){
			System.out.println("\t"+f);
		}
		System.out.println("--------------");
		*/
		
	}


	// -----------------------------------------------------------------------------


	//read in terms and create index2term
	private static void readTerms(String termIn) {
		FileInputReader reader = new FileInputReader(termIn);
		String line;

		index2term = new HashMap<Integer, String>();
		
		int index=0; // 0 to 88535
		while((line=reader.read())!=null){ // 2p24	8.910131
			index2term.put(index, line.split("\t")[0]);
			index++;
		}
		reader.closer();		
	}

	

	// geneListProfiles gives information about: geneName, oldIndex, newIndex 
	// => newIndex2gene for getting gene for lines in weigthOut
	// read weightOut and create Annotation objects for genes
	private static void createAnnotations(String weigthOut, String geneListProfiles) {
		FileInputReader readerGene = new FileInputReader(geneListProfiles);
		FileInputReader readerWeight = new FileInputReader(weigthOut);
		
		// create and fill newIndex2gene:
		HashMap<Integer, String> newIndex2gene = new HashMap<Integer, String>();
		String line;
		while((line=readerGene.read())!=null){ // LAPTM4A	14084	0
			String [] l = line.split("\t");
			newIndex2gene.put(Integer.parseInt(l[2]), l[0]);
		}
		readerGene.closer();
		
		//read weightOut and create Annotation objects for genes:
		gene2annotation = new HashMap<String, Annotation>();
		int counter = 0;
		while((line=readerWeight.read())!=null){ // 26	0:0.015434295	965:0.020228693	2891:0.01403832
			if(newIndex2gene.containsKey(counter)){
				String gene = newIndex2gene.get(counter);
				Annotation a = new Annotation(gene, line);
				gene2annotation.put(gene, a);
			}
			else{
				System.out.println("Something went wrong: newIndex2gene does not contain a gene for line index "+counter);
			}
			counter++;
		}
		readerWeight.closer();
	}
	

	// go through matrixProbandsIn, transfer Cadd-sum-scores to scores for feature in profiles, and write matrixOut
	// mode specifies format for output-matrix: 1=Viscovery; 2=SVM; 3=clustering (featProbands.txt)
	// ignoreNegativeCadd specifies if negative Cadd scores are used for weighting or ignored
	private static void createMatrixProbandsOut(String matrixProbandsIn, String matrixOut, String mode, boolean ignoreNegativeCadd) {
		FileInputReader reader = new FileInputReader(matrixProbandsIn);
		FileOutputWriter writer = new FileOutputWriter(matrixOut);
		
		// write header of output (features) for Viscovery format
		if(mode.equals("Viscovery")){
			writer.write("Id\tDisease");
			for(int pos=0; pos<index2term.size(); pos++){
				writer.write("\t"+index2term.get(pos));
			}
			writer.write("\n");
		}
		// write header lines for clustering output:
		else if(mode.equals("clustering")){
			writer.write("UID\tNAME\tGWEIGHT");
			for (int pos=0; pos<index2term.size(); pos++){		// index2term.size() = 88536 = number of columns
				String t = index2term.get(pos);
				writer.write("\t"+t);	
			}
			writer.write("\n"); 
			
			// write second line with EWEIGHT (always 1)
			writer.write("EWEIGHT\t\t");
			for(int c=0; c<index2term.size(); c++){
				writer.write("\t1");
			}
			writer.write("\n");
		}

		
		// input header line of input matrix contains names of genes: Id	Disease	A1BG	A1BG-AS1	A1CF	A2M	A2M-AS1	A2ML1
		String line=reader.read(); // header line
		String[] genes = line.split("\t"); // length 20892

		
		// transfer scores for each proband
		// for each proband: collect sums in float[] scores
		while((line=reader.read())!=null){ // 106943	0	-0.02218738	-0.06134299	-0.21537343871
			String[] l = line.split("\t");
			String id = l[0];
			String label = l[1];
			
			// write first columns (before data values)
			if(mode.equals("Viscovery")){
				writer.write(id+"\t"+label);
				}
			else if(mode.equals("SVM")){
				if(Integer.parseInt(label)>0){writer.write("1");} // case
				else{writer.write("-1");} // control
			}
			else if(mode.equals("clustering")){
				writer.write(id+"\t"+id+"\t1");
			}
			else{
				System.out.println("Error: choose outpur format properly.");
			}
			
			
			float[] scores = new float[index2term.size()]; //88536 = number of columns = number of features
			for(int pos=2; pos<genes.length; pos++){
				if(gene2annotation.containsKey(genes[pos])){ // annotation for this gene available
					// --> transfer Cadd-sum-scores to features (refresh float[] scores)
					float cadd = Float.parseFloat(l[pos]);
					/*
					//use  float or double for cadd score?!?!? TODO
					float f = (float)-0.022187386973792553;
					double d = -0.022187386973792553;
					double fd = f*d;
					System.out.println(f);	//-0.022187388
					System.out.println(d);	//-0.022187386973792553
					System.out.println(fd);	//4.922801539694967E-4
					 */
					
					if(ignoreNegativeCadd){
						scores = addAnnotationToScores_ignoreNegativeCadd(scores, gene2annotation.get(genes[pos]), cadd);
					}else{
						scores = addAnnotationToScores(scores, gene2annotation.get(genes[pos]), cadd);
					}
				}
				else{
					//System.out.println("n"+genes[pos]);
				}
			}
			
			// write output line
			for (int pos=0; pos<scores.length; pos++){
				float f = scores[pos];
				
				if(mode.equals("Viscovery")){
					writer.write("\t"+f);
					}
				else if(mode.equals("SVM")){
					writer.write(" "+(pos+1)+":"+f);
				}
				else if(mode.equals("clustering")){
					writer.write("\t"+f);
				}
				else{
					System.out.println("Error: choose outpur format properly.");
				}
				
			}
			writer.write("\n");
			
		}
		
		reader.closer();
		writer.closer();

	}


	// transfer given cadd-sum score for this gene to the given annotation values for the features
	// add values for features to scores collected so far
	// ok, works
	private static float[] addAnnotationToScores(float[] scores, Annotation annotation, float cadd) {

		// for testing:
		//System.out.println("cadd:" +cadd);

		// multiply cadd-sum-score by values for features
		// add it to float[] scores
		HashMap<Integer, Float> values = annotation.getValues();
		for(int index : values.keySet()){
			scores[index] += cadd * values.get(index);
		}
	
		return scores;
	}
	
	//similar to addAnnotationToScores, but ignores negative cadd-sum-scores
	//ok, works
	private static float[] addAnnotationToScores_ignoreNegativeCadd(float[] scores, Annotation annotation, float cadd) {

		// for testing:
		//System.out.println("cadd:" +cadd);

		// multiply cadd-sum-score by values for features
		//  ignore negative cadd-sum-scores
		// add it to float[] scores
		if(cadd<0){
			return scores;
		}
		HashMap<Integer, Float> values = annotation.getValues();
		for(int index : values.keySet()){
			scores[index] += cadd * values.get(index);
		}
	
		return scores;
	}

	
	
	
	

}
