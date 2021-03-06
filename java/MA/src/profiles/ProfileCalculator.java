package profiles;

import java.util.HashMap;
import java.util.HashSet;

import io.ByteLine;
import io.FileInputReader;
import io.FileOutputWriter;

public class ProfileCalculator {
	
	
	private static HashMap<Integer, String> index2term;		// size 88536
	private static HashMap<String, Annotation> gene2annotation; // size 9720
	
	
	/*
	// run method 
	aufruf: ProfileCalculator.run(termIn,weigthOut,geneListProfiles);
	*/
	public static void run(String termIn,String weigthOut,String geneListProfiles) {
		System.out.println("++ run ProfileCalculator ++");
		
		
		//read in terms and create index2term
		System.out.println("read .term");
		readTerms(termIn); //ok
		
		//read weightOut and create Annotation objects in HashMap gene2annotation
		System.out.println("read filtered .weight and create annotations of genes");
		createAnnotations(weigthOut,geneListProfiles); //ok
		
		
		/*
		//for testing: ok
		String line1 = "26\t0:-0.5\t1:10\t3:0.1\t6:2.5";
		Annotation a1 = new Annotation("AB", line1, 1, 1);
		
		String line2 = "26\t0:7\t3:10\t4:-2";
		Annotation a2 = new Annotation("FG", line2, 2, 2);
		*/
		/*
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
		/*
		//test addAnnotation: ok
		Annotation sum = new Annotation("sum");
		sum.addAnnotation(a1);
		sum.addAnnotation(a2);
		Annotation copy = sum.subtractAnnotation(a1); // does NOT change the sum object! but returns a copy
		System.out.println(sum.getNumberOfAnnotations());
		System.out.println(copy.getNumberOfAnnotations());
		
		Annotation copy2 = sum.subtractAnnotation(a2);
		System.out.println(copy2.getNumberOfAnnotations());
		System.out.println(sum.getNumberOfAnnotations());
		
		for(int i: sum.getValues().keySet()){
			System.out.println(i+":"+sum.getValues().get(i));
		}
		System.out.println("###########");
		*/
		/*
		Annotation.calcScalarProduct(a1, a2);
		//System.out.println("#####");
		//Annotation.calcScalarProduct(a2, a1);
		System.out.println("###########");
		
		
		Annotation comp1 = sum.subtractAnnotation(a1);
		Annotation comp2 = sum;
		Annotation.calcScalarProduct(a1, comp1);
		System.out.println("#####");
		Annotation.calcScalarProduct(a1, comp2);
		*/
	}
	
	
	// OPTIONAL:create output matrix in Viscovery format (matrixProbandsOut)
	// Cadd Scores in matrixProbandsIn are used as weights (define, if negative cadd scores should be ignored or not)
	// mode specifies format for output-matrix: Viscovery; SVM; clustering (featProbands.txt)
	// ca 8 min; 4.65 GB
	public static void writeViscoveryMatrix(String matrixProbandsIn,String matrixProbandsOut){
		System.out.println("write matrix in Viscovery format");
		boolean ignoreNegativeCadd = false;
		createMatrixProbandsOut(matrixProbandsIn,matrixProbandsOut, "Viscovery", ignoreNegativeCadd); // ok
	}
	
	// OPTIONAL:create output matrix in SVM format (matrixProbandsOutSVM)
	// Cadd Scores in matrixProbandsIn are used as weights (define, if negative cadd scores should be ignored or not)
	// mode specifies format for output-matrix: 1=Viscovery; 2=SVM; 3=for clustering (featProbands.txt)
	// ca 13 min; 7.11 GB
	public static void writeSvmMatrix(String matrixProbandsIn,String matrixProbandsOutSVM){
		System.out.println("write matrix in SVM format");
		boolean ignoreNegativeCadd = false;
		createMatrixProbandsOut(matrixProbandsIn,matrixProbandsOutSVM, "SVM", ignoreNegativeCadd);
	}
	
	// OPTIONAL: create matrix for hierarchical clustering (= write output of svmclust.pl: featProbands.txt)
	// Cadd Scores in matrixProbandsIn are used as weights (define, if negative cadd scores should be ignored or not)
	// mode specifies format for output-matrix: 1=Viscovery; 2=SVM; 3=for clustering (featProbands.txt)
	// ca 8 min
	public static void writeClusterMatrix(String matrixProbandsIn,String matrixProbandsCluster){
		System.out.println("write matrix for hierarchical clustering");
		boolean ignoreNegativeCadd = false;
		createMatrixProbandsOut(matrixProbandsIn,matrixProbandsCluster, "clustering", ignoreNegativeCadd); 
	}

	
	
	// OPTIONAL: get profile vector for group of genes (eg candGenes vs nonCandGenes)
	// first: remove self-associations from annotation vectors
	// output is one vector: first column=feature, second column=value OR one column=value OR 1st=val 2nd=occurenceOfThisTerm
	public static void writeProfileForGenes(String geneGroup, String profileForGenes){
		System.out.println("remove self-associations of all genes");
		for (String gene : gene2annotation.keySet()){
			gene2annotation.get(gene).removeSelfAssociation();
		}
		System.out.println("write profile vector for given group of genes");
		calcProfileForGenes(geneGroup, profileForGenes);
	}
	
	// OPTIONAL: get profile vector for group of probands (eg cases vs controls)
	// cadd scores in matrixProbandsIn are used as weights (specify if negative Cadd scores should be ignored or not)
	// output is one vector: first column=feature, second column=value OR one column=value
	public static void writeProfileForProbands(String matrixProbandsIn, String probandGroup, String profileForProbands){
		System.out.println("write profile vector for given group of probands");
		boolean ignoreNegativeCadd = false;
		calcProfileForProbands(matrixProbandsIn, probandGroup, profileForProbands, ignoreNegativeCadd);
	}
	
	
	// OPTIONAL: predict genes of geneList(NO header):
	// compare annotation to profile vector of candGenes and nonCandGenes (WITH header)
	public static void predictGenes(String candGenes, String nonCandGenes, String geneList, String genePrediction) {
		// TODO Auto-generated method stub
		System.out.println("predict genes (compare annotation to cand profile and nonCand profile)");
		calcPredictionForGenes(candGenes, nonCandGenes, geneList, genePrediction);
		
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
		HashMap<String, Integer> gene2newIndex = new HashMap<String, Integer>();
		HashMap<String, Integer> gene2oldIndex = new HashMap<String, Integer>();
		String line;
		while((line=readerGene.read())!=null){ // LAPTM4A	14084	0
			String [] l = line.split("\t");
			String gene = l[0];
			int oldIndex = Integer.parseInt(l[1]);
			int newIndex = Integer.parseInt(l[2]);
			newIndex2gene.put(newIndex, gene);
			gene2newIndex.put(gene, newIndex);
			gene2oldIndex.put(gene, oldIndex);
			
		}
		readerGene.closer();
		
		//read weightOut and create Annotation objects for genes:
		gene2annotation = new HashMap<String, Annotation>();
		int counter = 0;
		while((line=readerWeight.read())!=null){ // 26	0:0.015434295	965:0.020228693	2891:0.01403832
			if(newIndex2gene.containsKey(counter)){
				String gene = newIndex2gene.get(counter);
				int oldIndex = gene2oldIndex.get(gene);
				int newIndex = gene2newIndex.get(gene);
				Annotation a = new Annotation(gene, line, oldIndex, newIndex);
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
				String s="";
				if(Integer.parseInt(label)>0){s="1_"+id;} // case
				else{s="-1_"+id;} // control
				writer.write(s+"\t"+s+"\t1");
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
					//System.out.println(genes[pos]);
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
			//System.out.print(cadd+"*"+values.get(index)+"="+(cadd * values.get(index))+" + "+scores[index]+" = ");
			scores[index] += cadd * values.get(index);
			//System.out.println(scores[index]);
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

	
	// calculate profile (one vector) for the given group of genes (WITH header) 
	// by summing up the values for each feature; then calculate mean
	private static void calcProfileForGenes(String geneGroup, String profileForGenes) {
		FileOutputWriter writer = new FileOutputWriter(profileForGenes);
		
		Annotation sum = getSumAnnotationForGenes(geneGroup);
		
		for(int i=0; i<index2term.size(); i++){
			float val = (float)0;
			int count = 0;
			if(sum.getValues().containsKey(i)){
				val = (sum.getValues().get(i))/sum.getNumberOfAnnotations(); //calc mean
				count = sum.getOccurence().get(i);
			}
			// also write terms:
			//writer.write(index2term.get(i)+"\t");	
			writer.write(val+"");	
			// write occurence:
			writer.write("\t"+count);
			writer.write("\n");
		}
		writer.closer();
	}
	

	// calculate profile (one vector) for the given group of probands by summing up the values for each feature
	// then calculate mean
	// cadd scores in matrixProbandsIn are used as weights
	private static void calcProfileForProbands(String matrixProbandsIn, String probandGroup, String profileForProbands, boolean ignoreNegativeCadd) {
		FileInputReader readerMatrix = new FileInputReader(matrixProbandsIn);
		FileInputReader readerIds = new FileInputReader(probandGroup);
		FileOutputWriter writer = new FileOutputWriter(profileForProbands);
		
		int counterAll = 0;		//counts whole number of probands in group
		int counterUsed = 0;	//counts number of used probands --> used for calculation of mean for values
		float[] scores = new float[index2term.size()]; // to sum up weighted values for features/terms
		String line;
		
		// read ids for which profile will be calculated
		HashSet<String> idSet = new HashSet<String>();
		while((line=readerIds.read())!=null){
			idSet.add(line);
			counterAll++;
		}
		readerIds.closer();

		// input header line of input matrix contains names of genes: Id	Disease	A1BG	A1BG-AS1	A1CF	A2M	A2M-AS1	A2ML1
		line=readerMatrix.read(); // header line
		String[] genes = line.split("\t"); // length 20892
		
		
		// transfer scores for probands appearing in idSet : collect sums in float[] scores
		while((line=readerMatrix.read())!=null){ // 106943	0	-0.02218738	-0.06134299	-0.21537343871
			String[] l = line.split("\t");
			String id = l[0];
			//String label = l[1];
			
			if(!idSet.contains(id)){
				continue;
			}
			//System.out.println(id); // diese ids werden bearbeitet
			counterUsed++;
			
			for(int pos=2; pos<genes.length; pos++){
				if(gene2annotation.containsKey(genes[pos])){ // annotation for this gene available
					// --> transfer Cadd-sum-scores to features (refresh float[] scores)
					float cadd = Float.parseFloat(l[pos]);
					
					if(ignoreNegativeCadd){
						scores = addAnnotationToScores_ignoreNegativeCadd(scores, gene2annotation.get(genes[pos]), cadd);
					}else{
						scores = addAnnotationToScores(scores, gene2annotation.get(genes[pos]), cadd);
					}
				}
				else{
					//System.out.println(genes[pos]);
				}
			}
		}
		readerMatrix.closer();
		System.out.println(counterUsed+" probands of "+counterAll+" probands used for calculation of profile.");
		
		// write output
		for (int i=0; i<scores.length; i++){
			float val = scores[i]/counterUsed;	//calc mean
			// also write terms:
			//writer.write(index2term.get(i)+"\t");	
			writer.write(val+"\n");
		}
		writer.closer();	
	}




	// calc prediction for each gene in geneList (NO header)
	// by comparing their annotation to the profile vector of candGenes and nonCandGenes (WITH header)
	private static void calcPredictionForGenes(String candGenes, String nonCandGenes, String geneList, String genePrediction) {
		FileInputReader reader = new FileInputReader(geneList);
		FileOutputWriter writer = new FileOutputWriter(genePrediction);
		
		// calc sum of annotations for candGenes and nonCandGenes:
		Annotation cand = getSumAnnotationForGenes(candGenes);
		Annotation nonCand = getSumAnnotationForGenes(nonCandGenes);
		
		//go through geneList:
		// - check if annotation is available for this gene
		// - subtract annotation from a copy of the profile vector, it was originally added to
		// - calc scalar product of annotation to both profile vectors
		// - calc means of scalar products
		// - compare
		
		// annotation objects for comparison (do not contain annotation for actual gene)
		Annotation candComp = cand;	
		Annotation nonCandComp = nonCand;
		
		String line;
		while((line=reader.read())!=null){
			// if no Annotation available -> go to next line
			if(!gene2annotation.containsKey(line)){
				//System.out.println("No annotation available for "+ line);
				continue;
			}
				
			Annotation actAnn = gene2annotation.get(line);
			
			//check, if this gene was used in cand profile vector OR nonCand profile vector
			if(cand.getGenes().contains(line)){
				candComp = cand.subtractAnnotation(actAnn);
				nonCandComp = nonCand;
			}
			else if(nonCand.getGenes().contains(line)){
				candComp = cand;	
				nonCandComp = nonCand.subtractAnnotation(actAnn);
			}
			else{System.out.println("Warning: "+line+" was used twice (for both profiles).");}
			
			//calc scalar product of annotation to both profile vectors
			float scalarCand = Annotation.calcScalarProduct(actAnn, candComp);
			float scalarNonCand = Annotation.calcScalarProduct(actAnn, nonCandComp);
			
			//calc means of scalar products
			scalarCand = scalarCand/candComp.getNumberOfAnnotations();
			scalarNonCand = scalarNonCand/nonCandComp.getNumberOfAnnotations();
			
			//compare and write output
			// CandGene?(T=1), gene, scalarCand, scalarNonCand, prediction(cand=1)
			if(cand.getGenes().contains(line)){writer.write("1\t");}
			else if(nonCand.getGenes().contains(line)){writer.write("0\t");}
			else{writer.write("NULL\t");}
			
			writer.write(line+"\t"+scalarCand+"\t"+scalarNonCand+"\t");
			
			int c = java.lang.Float.compare(scalarCand, scalarNonCand); 
			if(c>0){writer.write("1\n");}
			else if(c<0){writer.write("0\n");}
			else{writer.write("NULL\n");}

		}
		reader.closer();
		writer.closer();
	}
	
	
	//returns the sum of annotation vectors for the given geneGroup (WITH header)
	private static Annotation getSumAnnotationForGenes(String geneGroup){
		FileInputReader reader = new FileInputReader(geneGroup);
		
		int counterAll = 0;		//counts whole number of input genes
		Annotation sum = new Annotation("GroupOfGenes");
		String line=reader.read(); //ignore header

		while((line=reader.read())!=null){
			if(gene2annotation.containsKey(line)){ // add values of this gene
				sum.addAnnotation(gene2annotation.get(line));
			}
			else{
				//System.out.println("no annotation for " + line + " available.");
			}
			counterAll++;
		}
		reader.closer();
		System.out.println(sum.getNumberOfAnnotations()+" genes of "+counterAll+" genes used for calculation of profile.");
		
		return sum;
	}
	
	

}
