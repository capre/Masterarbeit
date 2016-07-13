package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import io.FileInputReader;
import io.FileOutputWriter;

public class MainSchizoParser2 {
	
	public static void main(String[] args) {
		
		/*
		 * specify method in args[0]: 
		 * 1: createAllProbands 
		 * 2: createMatrixViscovery 
		 * 3: createInputSVM
		 * 4: createGeneList
		 * 5: filterCandGenes
		 * 6: scaleViscovery
		 * 7: mergeViscoveryMatrices
		 */
		args=new String[1];
		args[0]="";
		
		
		if(args.length==0){
			System.out.println("Specify method to run");
		}
		else{			
		
			// PART 1 : createAllProbands (2 files: AllCases and AllControls)
			//parse all single files of cases and controls (dataset 2) 
			// -> generate 1 / 2 collection file(s): "!AllProbandsList.txt" or ...
			// add information about "diseased = 1 vs 0"
			if(args[0].equals("createAllProbands")){
				String path = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/Dataset_2/Files/";
				//String path = "/home/ibis/carolin.prexler/Documents/Dataset_2/";
				
				//String pathIn = path+ "Analysis_caddSum/";
				String pathIn = path+ "variants/Analysis_variants2.0/";
				//String pathIn = "/storageNGS/ngs2/projects/exome/schizo_ionTorrent/Analysis/Final/CADD/Swedish_Case_Control/Analysis/";
				String pathOut = path+"!AllProbandsList_vars2.0.txt";
				//String pathOutCase = path+"!AllCasesList.txt";
				//String pathOutControl = path+"!AllControlsList.txt";
				String pathCases = path+"case_ids_study.txt";
				String pathControls = path+"control_ids_study.txt";
				if(args.length>1){ //set files
					path = args[1];
					pathIn = path+ args[2];
					//pathOut = path+"!AllProbandsList.txt";
					//pathOutCase = path+args[3];
					//pathOutControl = path+args[4];
					pathCases = path+args[5];
					pathControls = path+args[6];
					
				}
				parseFiles(pathIn, pathOut,pathCases,pathControls);
				//parseFiles(pathIn, pathOutCase, pathOutControl,pathCases,pathControls);
			}


			
			// PART 2 : createMatrixViscovery
			//parse a list (Id, Disease, Gene, Score) into a matrix as input for viscovery
			// input file has to consist of "blocks" for each patient = same id in multiple consecutive lines
			//matrix: patients in rows, disease status in column, genes in columns
			else if(args[0].equals("createMatrixViscovery")){
				//String path = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/Dataset_2/Files/";
				//String path = "/home/ibis/carolin.prexler/Documents/Dataset_2/";
				String path = "/storageNGS/ngs3/projects/other/Schizo_SVM/Schizo_data/Dataset_2/";
				
				//String pathIn = path+"noNorm/!AllProbandsList.txt";
				//String pathInGenes = path+"noNorm/!GeneList.txt";
				
				String pathIn = path+"gt_all/!AllProbands_gt_part.txt";
				String pathInGenes = path+"gt_all/!GeneList_gt_part.txt";
				
				String pathOut = path+"gt_all/!Matrix_gt.txt";
				if(args.length>1){ //set files
					path = args[1];
					pathIn = path+args[2];
					pathInGenes = path+args[3];
					pathOut = path+args[4];
				}
				algorithm.CreatePatientData2.initialize(pathIn, pathInGenes, pathOut);
			}
			
		
		
			
			// PART 3 : createInputSVM
			// modify matrix for viscovery -> proper input for libsvm and clustering worflow 
			// +1 diseased (case) vs -1 healthy(control)
			// (-> feat.scale,feat.name, feat.ids)
			else if(args[0].equals("createInputSVM")){
				//String path = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/Dataset_2/Files/";
				//String path = "/home/ibis/carolin.prexler/Documents/Dataset_2/";
				String path = "/storageNGS/ngs3/projects/other/Schizo_SVM/Schizo_data/Dataset_2/";
				
				//String pathIn = path+"noNorm/!Matrix_scale.txt";
				//String pathOut = path+"!Matrix_SVM_scale.txt";
				String pathIn = path+"gt_all/!Matrix_gt.txt";
				String pathOut = path+"gt_all/!SVM_gt.txt";
				
				String pathOutGenes = path+"gt_all/!SVM_gt.name";
				String pathOutIds = path+"gt_all/!SVM_gt.ids";
				if(args.length>1){ //set files
					path = args[1];
					pathIn = path+args[2];
					pathOut = path+args[3];
					pathOutGenes = path+args[4];
					pathOutIds = path+args[5];
				}
				createMatrixSVM(pathIn,pathOut,pathOutGenes, pathOutIds);
			}
			
			
			
			// PART 4 :createGeneList
			// generate list of all genes from !AllProbandsList.txt 
			else if(args[0].equals("createGeneList")){
				//String path = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/Dataset_2/Files/";
				//String path = "/home/ibis/carolin.prexler/Documents/Dataset_2/";
				String path = "/storageNGS/ngs3/projects/other/Schizo_SVM/Schizo_data/Dataset_2/";
				
				String pathIn = path+"gt_all/!AllProbands_gt_part.txt";
				String pathOut = path+"gt_all/!GeneList_gt_part.txt";
				if(args.length>1){ //set files
					path = args[1];
					pathIn = path+args[2];
					pathOut = path+args[3];
				}
				createGeneList(pathIn,pathOut);
			}
			
			
			
			// PART 5 :filterCandGenes
			// filter matrix for viscovery: keep only columns with candidate genes
			// just filter UNscaled matrix?! and scale afterwards?! is that better?!?
			//(candidate_genes_uniq.csv + header)
			else if(args[0].equals("filterCandGenes")){
				String path = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/Dataset_2/Files/";
				
				//String pathIn = path+"noNorm/!Matrix.txt";
				String pathIn = path+"noNorm/!Matrix_scale.txt";
				//String pathOut = path+"noNorm_candGenes/!Matrix_scale_candGenes.txt";
				String pathOut = path+"noNorm_impGenes/!Matrix_impGenes3.5.txt";
				//String pathGenes = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/candidateGenes/candidate_genes_uniq.csv";
				String pathGenes = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/Dataset_2/Files/noNorm_impGenes/impGenes3.5.txt";
				
				if(args.length>1){ //set files
					path = args[1];
					pathIn = path+args[2];
					pathOut = path+args[3];
					pathGenes = path+args[4];
				}
				filterGenes(pathIn,pathOut,pathGenes);
			}
			
			
			// PART 6 :scaleViscovery
			// scale a viscovery matrix (z transformation) -> column-wise and/ or row-wise
			else if(args[0].equals("scaleViscovery")){
				//String path = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/Dataset_2/Files/";
				//String path = "/home/ibis/carolin.prexler/Documents/Dataset_2/";
				String path = "/storageNGS/ngs3/projects/other/Schizo_SVM/Schizo_data/Dataset_2/";
				
				String pathIn = path+"gt_all/!Matrix_gt.txt";
				String pathOut = path+"gt_all/!Matrix_gt_scale.txt"; 
				if(args.length>1){ //set files
					path = args[1];
					pathIn = path+args[2];
					pathOut = path+args[3];
				}
				transform.Scale.scaleColumns(pathIn,pathOut+".col");
				transform.Scale.scaleRows(pathOut+".col",pathOut);
				//just scale rows:
				//transform.Scale.scaleRows(pathIn,pathOut+".row");  
				
			}
			
			
			// PART 7 :mergeViscoveryMatrices
			// merge 2 viscovery matrices (have to have same order of ids! eg lexicographic) side by side 
			// 1st and 2nd column (id, disease) of 2nd matrix are skipped
			// additionally remove genes of a list (NO header) from 1st matrix, if last argument TRUE
			else if(args[0].equals("mergeViscoveryMatrices")){
				//String path = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/Dataset_2/Files/";
				String path = "/home/ibis/carolin.prexler/Documents/Dataset_2/";
				
				String pathFirst = path+"noNorm/!Matrix.txt";
				String pathSec = path+"variants_gt/!Matrix_gt1.5.txt";
				String pathOut = path+"variants_gt/!Matrix_withGt1.5.txt"; 
				String pathGenes = path+ "variants/geneListVariants1.5.txt";
				if(args.length>1){ //set files
					path = args[1];
					pathFirst = path+args[2];
					pathSec = path+args[3];
					pathOut = path+args[4];
					pathGenes = path+args[5];
				}
				//mergeMatrices(pathFirst,pathSec,pathOut,pathGenes,true);
				mergeMatrices(pathFirst,pathSec,pathOut,"",false);
				
				
			
			}
			
		}
		
	}



	// ---------------------- PART 1 --------------------------------------------	



	// create 2 collection files
	// call "parseFile" for each single file in the directory
	private static void parseFiles(String path, String pathOutCase, String pathOutControl, String pathCases, String pathControls) {
		FileOutputWriter writerCase = new FileOutputWriter(pathOutCase);
		FileOutputWriter writerContr = new FileOutputWriter(pathOutControl);
		writerCase.write("Id\tDisease\tGene\tCADDsum\n"); //write header line
		writerContr.write("Id\tDisease\tGene\tCADDsum\n");
		
		// read in list of ids for cases and controls
		FileInputReader readerCase = new FileInputReader(pathCases);
		FileInputReader readerControl = new FileInputReader(pathControls);
		String line;
		
		HashSet<String> cases = new HashSet<String>();
		while((line=readerCase.read())!=null){
			cases.add(line);
		}
		readerCase.closer();
		HashSet<String> controls = new HashSet<String>();
		while((line=readerControl.read())!=null){
			controls.add(line);
		}
		readerControl.closer();
		
		
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				// only look at *.caddSum.tsv files...
				if(file.getName().endsWith(".caddSum.tsv")){
					String pathIn = path+file.getName();
					//read patient id from file name
					String[] a = file.getName().split("\\.");
					String patientId = a[1];
					//find correct writer:
					if(cases.contains(patientId)){
						parseFile(patientId, pathIn, writerCase, cases, controls);
					}
					else if(controls.contains(patientId)){
						parseFile(patientId, pathIn, writerContr, cases, controls);
					}
					else{
						System.out.println(patientId+ " is no case and no control");
					}

				}
				else{
					System.out.println("Ignore file "+file.getName());
				}
			}
			else{
				System.out.println("is no file");
			}
		}
		writerCase.closer();
		writerContr.closer();
		
	}


	// create 1 collection file
	// call "parseFile" for each single file in the directory
	private static void parseFiles(String path, String pathOut, String pathCases, String pathControls) {
		FileOutputWriter writer = new FileOutputWriter(pathOut);
		writer.write("Id\tDisease\tGene\tCADDsum\n"); //write header line
		
		// read in list of ids for cases and controls
		FileInputReader readerCase = new FileInputReader(pathCases);
		FileInputReader readerControl = new FileInputReader(pathControls);
		String line;
		
		HashSet<String> cases = new HashSet<String>();
		while((line=readerCase.read())!=null){
			cases.add(line);
		}
		readerCase.closer(); 
		HashSet<String> controls = new HashSet<String>();
		while((line=readerControl.read())!=null){
			controls.add(line);
		}
		readerControl.closer();
		
		
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				// only look at *.caddSum.tsv files...
				if(file.getName().endsWith(".variants.tsv")){ // or: ".caddSum.tsv"
					String pathIn = path+file.getName();
					//read patient id from file name
					String[] a = file.getName().split("\\.");
					String patientId = a[1];
					parseFile(patientId, pathIn, writer, cases, controls);
				}
				else{
					System.out.println("Ignore file "+file.getName());
				}
			}
			else{
				System.out.println("is no file");
			}
		}
		writer.closer();
	}
	
	
	//parse 1 file of a proband (dataset 2) and append data to the collection file
	// add "disease" column (healthy(control) = 0,diseased(case) = 1)
	// Id\tDisease\tGene\tCADDsum\
	private static void parseFile(String patientId, String pathIn, FileOutputWriter writer, HashSet<String> cases, HashSet<String> controls){
		FileInputReader reader = new FileInputReader(pathIn);
		
		//check disease status of the person
		int disease;
		if(cases.contains(patientId)){
			disease = 1;
		}
		else if(controls.contains(patientId)){
			disease = 0;
		}
		else{
			System.out.println(patientId+ " is no case and no control");
			disease = -1;
		}
		

		String line = reader.read(); //ignore header in case of caddSum files
		String out; 
		while((line=reader.read())!=null){
			String[] array = line.split("\t");
			out = patientId+"\t"+disease+"\t"+array[0]+"\t"+array[1];
			writer.write(out+"\n");
		}
		reader.closer();
	}
	
	
	
	
	//----------------------- PART 3 ---------------------------------------	
	
	// modify viscovery matrix 
	private static void createMatrixSVM(String pathIn, String pathOut, String pathOutGenes,String pathOutIds) {
		FileInputReader reader = new FileInputReader(pathIn);
		FileOutputWriter writer = new FileOutputWriter(pathOut);
		
		String line=reader.read(); // header line with gene names
		String[] genes = line.split("\t");
		//write gene names
		FileOutputWriter writerGenes = new FileOutputWriter(pathOutGenes);
		for (int g=2; g<genes.length;g++){
			writerGenes.write(genes[g]+"\n");
		}
		writerGenes.closer();
		
		//writer for ids of patients / controls
		FileOutputWriter writerIds = new FileOutputWriter(pathOutIds);
		
		while((line=reader.read())!=null){
			String[] l = line.split("\t");
			writerIds.write(l[0]+"\n"); //write ids
			//disease status
			if(l[1].equals("1") | l[1].equals("+1")){ // diseased (case)
				writer.write("+1");
			}
			else if(l[1].equals("0")| l[1].equals("-1")){ // healthy (control)
				writer.write("-1");
			}else{System.out.println("wrong class in: "+line);}
			 
			for(int col = 2; col<l.length; col++) {
				writer.write(" "+(col-1)+":"+l[col]);
			}
			writer.write("\n");
		}
		reader.closer();
		writer.closer();
		writerIds.closer();	
		
	}
	
	
	
	//----------------------- PART 4 ---------------------------------------	
	

	//generate list of all genes from !AllProbandsList.txt (Id, Disease, Gene, CADDsum) +Header
	private static void createGeneList(String pathIn, String pathOut) {
		FileInputReader reader = new FileInputReader(pathIn);
		FileOutputWriter writer = new FileOutputWriter(pathOut);
		
		HashSet<String> geneSet = new HashSet<String>();
		String line=reader.read(); //ignore header
		while((line=reader.read())!=null){
			String[] l = line.split("\t");
			geneSet.add(l[2]);			
		}
		reader.closer();
		
		List<String> geneList= new ArrayList<String>(geneSet);
		Collections.sort(geneList);

		for(String g : geneList){
			writer.write(g+"\n");
		}
		writer.closer();
		
	}

	
	//----------------------- PART 5 ---------------------------------------	

	// filter viscovery matrix: keep only columns 1 + 2 + genes in file pathGenes
	// file pathGenes is list of genes, + header!!
	private static void filterGenes(String pathIn, String pathOut, String pathGenes) {
		FileInputReader reader = new FileInputReader(pathIn);
		FileOutputWriter writer = new FileOutputWriter(pathOut);
		
		// read in genes (to keep in filtering)
		FileInputReader readerGenes = new FileInputReader(pathGenes);
		String line=readerGenes.read(); //ignore header
		HashSet<String> genes = new HashSet<String>();
		while((line=readerGenes.read())!=null){
			genes.add(line);
		}
		readerGenes.closer();
		
		//read header line of matrix file -> define which columns to keep
		line = reader.read();
		String[] l = line.split("\t");
		boolean[] keep = new boolean[l.length];
		keep[0] = true; // keep id column
		keep[1] = true; // keep disease column
		for(int pos = 2; pos<l.length;pos++){
			if(genes.contains(l[pos])){ // keep gene columns of list
				keep[pos] = true;
			}
		}
		
		
		//write header
		writer.write(l[0]);
		for(int pos = 1; pos<l.length; pos++){
			if(keep[pos]){
				writer.write("\t"+l[pos]);
			}
		}
		writer.write("\n");
		//write output
		while((line=reader.read())!=null){
			l = line.split("\t");
			writer.write(l[0]);
			for(int pos = 1; pos<l.length; pos++){
				if(keep[pos]){
					writer.write("\t"+l[pos]);
				}
			}
			writer.write("\n");
		}
		reader.closer();
		writer.closer();
			
	}
	
	
	
	
	//----------------------- PART 7 ---------------------------------------	
	

	//merge to viscovery matrices side by side (same order of ids)
	// additionally possible to remove columns of 1st matrix using pathGenes (last argument TRUE)
	private static void mergeMatrices(String pathFirst, String pathSec, String pathOut, String pathGenes, boolean removeGenes) {
		FileInputReader reader1 = new FileInputReader(pathFirst);
		FileInputReader reader2 = new FileInputReader(pathSec);
		FileOutputWriter writer = new FileOutputWriter(pathOut);
		

		//read header line of 1st matrix file -> define which columns to skip if(removeGenes)
		String line1 = reader1.read();
		String[] l1 = line1.split("\t");
		boolean[] keep1 = new boolean[l1.length];
		keep1[0] = true; // keep id column
		keep1[1] = true; // keep disease column
		if(removeGenes){
			// read in genes (to skip)
			FileInputReader readerGenes = new FileInputReader(pathGenes);
			String line;
			HashSet<String> genes = new HashSet<String>();
			while((line=readerGenes.read())!=null){
				genes.add(line);
			}
			readerGenes.closer();
			
			for(int pos = 2; pos<l1.length;pos++){
				if(!genes.contains(l1[pos])){ // gene should not be skipped
					keep1[pos] = true;
				}
			}
		}
		else{ // no genes should be removed
			for(int pos = 0; pos<keep1.length; pos++){
				keep1[pos] = true;
			}
		}
		
		//read header line of 2nd matrix file (add positions 2+)
		String line2 = reader2.read();
		String[] l2 = line2.split("\t");
		
		
		//write header
		writer.write(l1[0]);
		for(int pos = 1; pos<l1.length; pos++){
			if(keep1[pos]){
				writer.write("\t"+l1[pos]);
			}
		}
		for(int pos = 2; pos<l2.length; pos++){
			writer.write("\t"+l2[pos]);
		}
		writer.write("\n");
		//write output
		while((line1=reader1.read())!=null){
			l1 = line1.split("\t");
			l2 = reader2.read().split("\t");
			if(!l1[0].equals(l2[0])){ //check if ids in the line are different --> WRONG OUTPUT!!!
				System.out.println("ERROR: ids are not in the same order: "+l1[0]+", "+l2[0]);
			}
			writer.write(l1[0]);
			for(int pos = 1; pos<l1.length; pos++){
				if(keep1[pos]){
					writer.write("\t"+l1[pos]);
				}
			}
			for(int pos = 2; pos<l2.length; pos++){
				writer.write("\t"+l2[pos]);
			}
			writer.write("\n");
		}
		reader1.closer();
		reader2.closer();
		writer.closer();

	}
	
	
	
	

}
