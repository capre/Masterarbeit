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
		 */
		args=new String[1];
		args[0]="scaleViscovery";
		
		
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
				
				String pathIn = path+ "Analysis_caddSum/";
				//String pathIn = "/storageNGS/ngs2/projects/exome/schizo_ionTorrent/Analysis/Final/CADD/Swedish_Case_Control/Analysis/";
				//String pathOut = path+"!AllProbandsList.txt";
				String pathOutCase = path+"!AllCasesList.txt";
				String pathOutControl = path+"!AllControlsList.txt";
				String pathCases = path+"case_ids_study.txt";
				String pathControls = path+"control_ids_study.txt";
				if(args.length>1){ //set files
					path = args[1];
					pathIn = path+ args[2];
					//pathOut = path+"!AllProbandsList.txt";
					pathOutCase = path+args[3];
					pathOutControl = path+args[4];
					pathCases = path+args[5];
					pathControls = path+args[6];
					
				}
				//parseFiles(pathIn, pathOut,pathCases,pathControls);
				parseFiles(pathIn, pathOutCase, pathOutControl,pathCases,pathControls);
			}


			
			// PART 2 : createMatrixViscovery
			//parse a list (Id, Disease, Gene, Score) into a matrix as input for viscovery
			//matrix: patients in rows, disease status in column, genes in columns
			else if(args[0].equals("createMatrixViscovery")){
				String path = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/Dataset_2/Files/";
				
				String pathIn = path+"!AllProbandsList.txt";
				String pathInGenes = path+"!GeneList.txt";
				String pathOut = path+"!AllProbandsMatrix.txt";
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
				String path = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/Dataset_2/Files/";
				
				String pathIn = path+"noNorm_candGenes/!AllProbandsMatrix_candGenes_scale.txt";
				String pathOut = path+"!candGenes_SVM_scale2.txt";
				String pathOutGenes = path+"!AllProbandsMatrix_SVM.name";
				String pathOutIds = path+"!AllProbandsMatrix_SVM.ids";
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
				String path = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/Dataset_2/Files/";
				
				String pathIn = path+"!AllProbandsList_candGenes.txt";
				String pathOut = path+"!GeneList.txt";
				if(args.length>1){ //set files
					path = args[1];
					pathIn = path+args[2];
					pathOut = path+args[3];
				}
				createGeneList(pathIn,pathOut);
			}
			
			
			
			// PART 5 :filterCandGenes
			// filter matrix for viscovery: keep only columns with candidate genes
			else if(args[0].equals("filterCandGenes")){
				String path = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/Dataset_2/Files/";
				
				String pathIn = path+"noNorm/!AllProbandsMatrix.txt";
				String pathOut = path+"!AllProbandsMatrix_candGenes.txt";
				String pathGenes = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/candidateGenes/candidate_genes_uniq.csv";
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
				String path = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/Dataset_2/Files/";
				
				String pathIn = path+"noNorm/!AllProbandsMatrix.txt";
				String pathOut = path+"!AllProbandsMatrix_scale.txt"; 
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
		// TODO hashset checken... ist dar hier richitge datenstruktur?!??
		HashSet<String> cases = new HashSet<String>();
		while((line=readerCase.read())!=null){
			cases.add(line);
		}
		readerCase.closer(); // damit noch nicht durchgelaufen ... egal?
		HashSet<String> controls = new HashSet<String>();
		while((line=readerControl.read())!=null){
			controls.add(line);
		}
		readerControl.closer(); // damit noch nicht durchgelaufen ... egal?
		
		
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
		// TODO hashset checken... ist dar hier richitge datenstruktur?!??
		HashSet<String> cases = new HashSet<String>();
		while((line=readerCase.read())!=null){
			cases.add(line);
		}
		readerCase.closer(); // damit noch nicht durchgelaufen ... egal?
		HashSet<String> controls = new HashSet<String>();
		while((line=readerControl.read())!=null){
			controls.add(line);
		}
		readerControl.closer(); // damit noch nicht durchgelaufen ... egal?
		
		
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
			if(l[1].equals("1")){ // diseased (case)
				writer.write("+1");
			}
			else if(l[1].equals("0")){ // healthy (control)
				writer.write("-1");
			}else{System.out.println(line);}
			 
			for(int col = 2; col<l.length; col++) {
				writer.write(" "+(col-1)+":"+l[col]);
			}
			writer.write("\n");
		}
		reader.closer(); // damit noch nicht durchgelaufen ... egal?
		writer.closer();
		writerIds.closer();	
		
	}
	
	
	
	//----------------------- PART 4 ---------------------------------------	
	

	//generate list of all genes from !AllProbandsList.txt (Id, Disease, Gene, CADDsum)
	private static void createGeneList(String pathIn, String pathOut) {
		FileInputReader reader = new FileInputReader(pathIn);
		FileOutputWriter writer = new FileOutputWriter(pathOut);
		
		HashSet<String> geneSet = new HashSet<String>();
		String line;
		while((line=reader.read())!=null){
			String[] l = line.split("\t");
			geneSet.add(l[2]);			
		}
		reader.closer(); // damit noch nicht durchgelaufen ... egal?
		
		List<String> geneList= new ArrayList<String>(geneSet);
		Collections.sort(geneList);

		for(String g : geneList){
			writer.write(g+"\n");
		}
		writer.closer();
		
	}

	
	//----------------------- PART 5 ---------------------------------------	

	// filter viscovery matrix: keep only columns 1 + 2 + genes in file pathGenes
	private static void filterGenes(String pathIn, String pathOut, String pathGenes) {
		FileInputReader reader = new FileInputReader(pathIn);
		FileOutputWriter writer = new FileOutputWriter(pathOut);
		
		// read in genes (to keep in filtering)
		FileInputReader readerGenes = new FileInputReader(pathGenes);
		String line;
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
	
	
	
	

	
	
	
	
	

}
