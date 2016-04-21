package main;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import io.FileInputReader;
import io.FileOutputWriter;

public class MainSchizoParser2 {
	
	public static void main(String[] args) {
		
		//specify method: createAllProbands, createMatrixViscovery, or createInputSVM
		args=new String[1];
		args[0]="createInputSVM";
		
		String path = "";
		
		if(args.length==0){
			System.out.println("Specify method to run");
		}
		else{
			if(args.length==1){ //default path
				path = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/Dataset_2/Files/";
			}
			else if(args.length==2){ //set general path
				path = args[1];
			}
		
			
		
			// PART 1 : createAllProbands
			//parse all single files of cases and controls (dataset 2) -> generate 1 collection file ("!AllProbands.txt")
			// add information about "diseased = 1 vs 0"
			if(args[0].equals("createAllProbands")){
				String pathOut = "";
				String pathCases = "";
				String pathControls = "";
				if(args.length==1 || args.length==2){ //default files
					//String path = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/Dataset_2/Files/";
					pathOut = path+"!AllProbands.txt";
					pathCases = path+"case_ids_study.txt";
					pathControls = path+"control_ids_study.txt";
				}
				else {
					pathOut = path+args[2];
					pathCases = path+args[3];
					pathControls = path+args[4];
				}
				parseFiles(path, pathOut,pathCases,pathControls);
			}


		
			// PART 2 : createMatrixViscovery
			//parse a list (Id, Disease, Gene, Score) into a matrix as input for viscovery
			//matrix: patients in rows, disease status in column, genes in columns
			else if(args[0].equals("createMatrixViscovery")){
				String pathIn = "";
				String pathOut = "";
				if(args.length==1 || args.length==2){ //default files
					//String path = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/Dataset_2/Files/";
					pathIn = path+"!AllProbandsList_candGenes.txt";
					pathOut = path+"!AllProbandsMatrix_candGenes.txt";
				}
				else{
					pathIn = path+args[2];
					pathOut = path+args[3];
				}
				createMatrixFromList(pathIn,pathOut);
			}
		
		
		
			// PART 3 : createInputSVM
			// modify matrix for viscovery -> proper input for libsvm and clustering worflow (feat.scale,...)
			else if(args[0].equals("createInputSVM")){
				String pathIn = "";
				String pathOut = "";
				String pathOutGenes = "";
				String pathOutIds = "";
				if(args.length==1 || args.length==2){ //default files
					//String path = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/Dataset_2/Files/";
					pathIn = path+"!AllProbandsMatrix_candGenes.txt";
					pathOut = path+"!feat.scale";
					pathOutGenes = path+"!feat.name";
					pathOutIds = path+"!feat.ids";
				}
				else{
					pathIn = path+args[2];
					pathOut = path+args[3];
					pathOutGenes = path+args[4];
					pathOutIds = path+args[5];
				}
				createMatrixSVM(pathIn,pathOut,pathOutGenes, pathOutIds);
			}
			
		}
		
	}

	
	// ---------------------- PART 1 --------------------------------------------	


	// call "parseFile" for each single file in the directory
	private static void parseFiles(String path, String pathOut, String pathCases, String pathControls) {
		FileOutputWriter writer = new FileOutputWriter(pathOut);
		writer.write("Id\tDisease\tPhred\tGene\n"); //write header line
		//keep all columns? :
		//writer.write("#Id\tDisease\tChrom\tPos\tRef\tAlt\tRawScore\tPhred\tFunc\tGene");
		
		// read in list of ids for cases and controls
		FileInputReader readerCase = new FileInputReader(pathCases);
		FileInputReader readerControl = new FileInputReader(pathControls);
		String line;
		// TODO hashset checken... ist dar hier richitge datenstruktur?!??
		HashSet<String> cases = new HashSet<String>();
		while((line=readerCase.read())!=null){
			cases.add(line);
		}
		HashSet<String> controls = new HashSet<String>();
		while((line=readerControl.read())!=null){
			controls.add(line);
		}
		
		
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				// only look at *.withGenes.tsv files...
				if(file.getName().endsWith(".withGenes.tsv")){
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
	// keep only most important columns: Id,Disease,Phred,Gene
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
		
		
		String line;
		String out; 
		while((line=reader.read())!=null){
			String[] array = line.split("\t");
			if(array[5].equals(".")){ // no Phred Score available
				System.out.println(line);
			}	
			else{
				out = patientId+"\t"+disease+"\t"+array[5]+"\t"+array[7];
				//out = patientId+"\t"+disease+"\t"+line;
				writer.write(out+"\n");
			}
		}
		reader.closer();
	}
	
	
	
	//----------------------- PART 2 ---------------------------------------
	
	// disease status: healthy(control) = 0, diseased(case) = 1 // -1 not allowed any more (skipped)
	private static void createMatrixFromList(String pathIn, String pathOut) {
		FileInputReader reader = new FileInputReader(pathIn);
		String line=reader.read(); //ignore header
		
		algorithm.CreatePatientData.initializePatientData();
		while((line=reader.read())!=null){
			String[] data = line.split("\t");
			String patientId = data[0];
			boolean disease = false;
			String gene = data[2];
			double score = Double.parseDouble(data[3]);
			if(data[1].equals("0")){ disease = false;}
			else if(data[1].equals("1")){ disease = true;}
			else{ 
				System.out.println(line+" is skipped (unknown diseases status");
				break;
				}
			algorithm.CreatePatientData.addEntry(patientId, gene, score, disease);
		}
		algorithm.CreatePatientData.hashmapToMatrix();
		String[] patients = algorithm.CreatePatientData.getPatients();
		HashMap<String,Boolean> status = algorithm.CreatePatientData.getStatus();
		String[] genes = algorithm.CreatePatientData.getGenes();
		double[][] scores = algorithm.CreatePatientData.getScores();
		
		FileOutputWriter writer = new FileOutputWriter(pathOut);
		// 1.row: Id, Disease, Genes (names)
		writer.write("Id\tDisease");
		for (int g=0; g<genes.length;g++){
			writer.write("\t"+genes[g]);
		}
		writer.write("\n");
		
		for (int p=0; p<patients.length;p++){
			String s = "0"; //healthy(control) = 0, diseased(case) = 1
			if (status.get(patients[p])){ s = "1"; }
			writer.write(patients[p]+"\t"+s);
			for (int g=0; g<genes.length;g++){
				writer.write("\t"+scores[p][g]);
			}
			writer.write("\n");
		}
		writer.closer();
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
		
		//write ids of patients / controls
		FileOutputWriter writerIds = new FileOutputWriter(pathOutIds);
		
		while((line=reader.read())!=null){
			String[] l = line.split("\t");
			writerIds.write(l[0]+"\n"); //write ids
			writer.write(l[1]); //disease status
			for(int col = 2; col<l.length; col++) {
				writer.write(" "+(col-1)+":"+l[col]);
			}
			writer.write("\n");
		}
		writer.closer();
		writerIds.closer();	
		
	}



}
