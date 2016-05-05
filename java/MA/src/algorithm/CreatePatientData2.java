package algorithm;

import java.util.ArrayList;
import java.util.List;

import io.FileInputReader;
import io.FileOutputWriter;

public class CreatePatientData2 {
	private static FileInputReader reader;
	private static FileInputReader readerGenes;
	private static FileOutputWriter writer;
	private static List<String> geneList;
	

	public static void initialize(String pathIn, String pathInGenes, String pathOut) {
		reader = new FileInputReader(pathIn);
		readerGenes = new FileInputReader(pathInGenes);
		writer = new FileOutputWriter(pathOut);
		readInGenes();
		run();
	}


	private static void readInGenes() {
		geneList= new ArrayList<String>();
		String line;
		while((line=readerGenes.read())!=null){
			geneList.add(line);
		}
		readerGenes.closer();
	}


	// input: !AllProbandsList.txt (Id, Disease, Gene, Score) +Header
	// file consists of "blocks" for each patient = same id in multiple consecutive lines
	// output: (1.row:) Id, Disease, Genes (names)
	private static void run() {
		String line = reader.read(); //ignore header
		System.out.println(line);
		
		writeHeader();
		
		String actId = "";
		String actDisease = "";
		double[] scores = new double[geneList.size()];
		boolean first = true;
		int counter = 1;
		
		while((line=reader.read())!=null){
			if(first){ //first line after header
				String[] data = line.split("\t");
				actId = data[0];
				actDisease = data[1];
				scores[geneList.indexOf(data[2])] = Double.parseDouble(data[3]);
				first = false;
			}
			else{ // next lines
				String[] data = line.split("\t");
				String id = data[0];
				if(actId.equals(id)){ //same proband
					if(scores[geneList.indexOf(data[2])]!=0.0){
						System.out.println("2 values for same gene in line: "+line);
					}
					scores[geneList.indexOf(data[2])] = Double.parseDouble(data[3]);
				}
				else{ // first line of a new proband => write old proband and initialize new one
					counter++;
					writeLine(actId, actDisease, scores,counter);
					actId = data[0];
					actDisease = data[1];
					scores = new double[geneList.size()];
					scores[geneList.indexOf(data[2])] = Double.parseDouble(data[3]);
				}
			}
		}
		reader.closer();
		writer.closer();
		
		
		
		
	}


	// write header line to file: Id, Disease, Genes (names)
	private static void writeHeader() {
		writer.write("Id\tDisease");
		for(String g : geneList){
			writer.write("\t"+g);
		}
		writer.write("\n");
	}


	// write line for 1 proband
	private static void writeLine(String actId, String actDisease, double[] scores, int counter) {
		if((counter%100)==0){
			System.out.println("writing proband nr "+counter);
		}
		
		writer.write(actId+"\t"+actDisease);
		for (double s : scores){
			writer.write("\t"+s);
		}
		writer.write("\n");
		
	}

	
	
	
	
	
	
	

	
	

}
