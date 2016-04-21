package main;

import java.io.File;
import java.util.HashMap;

import io.FileInputReader;
import io.FileOutputWriter;

public class MainSchizoParser1 {

	public static void main(String[] args) {
		
		//specify method: createAllPatients, createMatrixViscovery, or createInputSVM
		//args=new String[1];
		//args[0]="createAllPatients";
		
		String path = "";
		
		if(args.length==0){
			System.out.println("Specify method to run");
		}
		else{
			if(args.length==1){ //default path
				path = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/Dataset_1/PatientFiles/";
			}
			else if(args.length==2){ //set general path
				path = args[1];
			}
		
			
		
			// PART 1 : createAllPatients
			//parse all single files of the patients (dataset 1) -> generate 1 collection file ("!AllPatients.txt")
			if(args[0].equals("createAllPatients")){
				String pathOut = "";
				if(args.length==1 || args.length==2){ //default files
					//String path = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/Dataset_1/PatientFiles/";
					pathOut = path+"!AllPatients.txt";
				}
				else {
					pathOut = path+args[2];
				}
				parseFiles(path, pathOut);
			}


		
			// PART 2 : createMatrixViscovery
			//parse a list (patient, gene, score) into a matrix as input for viscovery
			//matrix: patients in rows, genes in columns
			else if(args[0].equals("createMatrixViscovery")){
				String pathIn = "";
				String pathOut = "";
				if(args.length==1 || args.length==2){ //default files
					//String path = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/Dataset_1/PatientFiles/";
					pathIn = path+"!AllPatientsList_candGenes.txt";
					pathOut = path+"!AllPatientsMatrix_candGenes.txt";
				}
				else{
					pathIn = path+args[2];
					pathOut = path+args[3];
				}
				createMatrixFromList(pathIn,pathOut);
			}

		
		
			// PART 3 : createInputSVM
			// merge Cases("!AllPatientsList[_candGenes].txt") and Controls („!AllControlsList[_candGenes].txt“)
			// -> proper input for libsvm and clustering worflow (feat.scale,...)
			else if(args[0].equals("createInputSVM")){
				String pathInCase = "";
				String pathInControl = "";
				String pathOut = "";
				String pathOutGenes = "";
				String pathOutIds = "";
				if(args.length==1 || args.length==2){ //default files
					//String path = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/Dataset_1/PatientFiles/";
					pathInCase = path+"!AllPatientsList_candGenes.txt";
					pathInControl = path+"!AllControlsList_candGenes.txt";
					pathOut = path+"!feat.scale";
					pathOutGenes = path+"!feat.name";
					pathOutIds = path+"!feat.ids";
				}
				else{
					pathInCase = path+args[2];
					pathInControl = path+args[3];
					pathOut = path+args[4];
					pathOutGenes = path+args[5];
					pathOutIds = path+args[6];
				}
				createMatrixFromCaseControl(pathInCase,pathInControl,pathOut,pathOutGenes, pathOutIds);
			}
		}
		
	}
	


	//-------------------- PART 1 ----------------------------------------
	
	// call "parseFile" for each single patient file in the directory
	private static void parseFiles(String path, String pathOut) {
		FileOutputWriter writer = new FileOutputWriter(pathOut);
		
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		boolean firstLine = true;
		for (File file : listOfFiles) {
			if (file.isFile()) {
				// only look at .tsv files...
				if(file.getName().endsWith(".tsv")){
					String pathIn = path+file.getName();
					//TODO read patient id from file name?!
					String[] a = file.getName().split("\\.");
					String patientId = a[1];
					parseFile(patientId, pathIn, writer, firstLine);
					firstLine = false;
				}
				else{
					System.out.println(file.getName());
				}
			}
			else{
				System.out.println("is no file");
			}
		}
		writer.closer();
	}
	
	//parse 1 file of a patient (dataset 1) and append data to the collection file
	//keep only "interesting" columns
	private static void parseFile(String patientId, String pathIn, FileOutputWriter writer, boolean firstLine) {
		FileInputReader reader = new FileInputReader(pathIn);
		String line=reader.read();
		String out;
		if(firstLine){
			String[] array = line.split("\t");
			out = "PatientId\t"+array[0]+"\t"+array[1]+"\t"+array[2]+"\t"+array[3]+"\t"+array[4]+"\t"+array[5]+"\t"+array[6]+"\t"+array[8]+"\t"+array[17]+"\t"+array[37]+"\t"+array[38];
			writer.write(out+"\n");
		}
		while((line=reader.read())!=null){
			String[] array = line.split("\t");
			if(!array[38].equals(".")){
				out = patientId+"\t"+array[0]+"\t"+array[1]+"\t"+array[2]+"\t"+array[3]+"\t"+array[4]+"\t"+array[5]+"\t"+array[6]+"\t"+array[8]+"\t"+array[17]+"\t"+array[37]+"\t"+array[38];
				writer.write(out+"\n");
			}	
		}
		reader.closer();
	}

	
	//----------------------- PART 2 ---------------------------------------

	private static void createMatrixFromList(String pathIn, String pathOut) {
		FileInputReader reader = new FileInputReader(pathIn);
		String line=reader.read(); //ignore header
		
		algorithm.CreatePatientData.initializePatientData();
		while((line=reader.read())!=null){
			String[] data = line.split("\t");
			String patientId = data[0];
			String gene = data[1];
			double score = Double.parseDouble(data[2]);
			algorithm.CreatePatientData.addEntry(patientId, gene, score, true);
		}
		algorithm.CreatePatientData.hashmapToMatrix();
		String[] patients = algorithm.CreatePatientData.getPatients();
		String[] genes = algorithm.CreatePatientData.getGenes();
		double[][] scores = algorithm.CreatePatientData.getScores();
		
		FileOutputWriter writer = new FileOutputWriter(pathOut);
		// 1.row: Genes
		for (int g=0; g<genes.length;g++){
			writer.write("\t"+genes[g]);
		}
		writer.write("\n");
		
		for (int p=0; p<patients.length;p++){
			writer.write(patients[p]);
			for (int g=0; g<genes.length;g++){
				writer.write("\t"+scores[p][g]);
			}
			writer.write("\n");
		}
		writer.closer();
	}
	
	
	
	
	
	//----------------------- PART 3 ---------------------------------------	
	

	// merge list of Cases and list of Controls
	private static void createMatrixFromCaseControl(String pathInCase, String pathInControl, String pathOut, String pathOutGenes, String pathOutIds) {
		FileInputReader readerCase = new FileInputReader(pathInCase);
		FileInputReader readerControl = new FileInputReader(pathInControl);
		String line=readerCase.read(); //ignore header
		line=readerControl.read(); //ignore header
		
		algorithm.CreatePatientData.initializePatientData();
		// read cases
		while((line=readerCase.read())!=null){
			String[] data = line.split("\t");
			String patientId = data[0];
			String gene = data[1];
			double score = Double.parseDouble(data[2]);
			algorithm.CreatePatientData.addEntry(patientId, gene, score, true); // true = diseased
		}
		// read Controls
		while((line=readerControl.read())!=null){
			String[] data = line.split("\t");
			String patientId = data[0];
			String gene = data[1];
			double score = Double.parseDouble(data[2]);
			algorithm.CreatePatientData.addEntry(patientId, gene, score, false); // false = healthy
		}
		
		algorithm.CreatePatientData.hashmapToMatrix();
		String[] patients = algorithm.CreatePatientData.getPatients();
		String[] genes = algorithm.CreatePatientData.getGenes();
		double[][] scores = algorithm.CreatePatientData.getScores();
		HashMap<String,Boolean> status = algorithm.CreatePatientData.getStatus();
		
		
		FileOutputWriter writer = new FileOutputWriter(pathOut);
		for (int p=0; p<patients.length;p++){
			if(status.get(patients[p])){ // true = diseased = 1
				writer.write("1");
			}
			else{ // false = healthy = 0
				writer.write("0");
			}
			
			for (int g=0; g<genes.length;g++){
				writer.write(" "+(g+1)+":"+scores[p][g]);
			}
			writer.write("\n");
		}
		writer.closer();
		
		//write gene names
		FileOutputWriter writerGenes = new FileOutputWriter(pathOutGenes);
		for (int g=0; g<genes.length;g++){
			writerGenes.write(genes[g]+"\n");
		}
		writerGenes.closer();
		
		//write ids of patients / controls
		FileOutputWriter writerIds = new FileOutputWriter(pathOutIds);
		for (int p=0; p<patients.length;p++){
			writerIds.write(patients[p]+"\n");
		}
		writerIds.closer();
	}
	
	
	
}
