package profiles;

import io.ByteLine;
import io.ByteReader;
import io.FileInputReader;
import io.FileOutputWriter;

import java.util.HashMap;
import java.util.HashSet;

public class ProfileGenerator {
	
	//static variables:
	private static HashSet<String> geneSet;
	private static HashMap<String, String> term2gene;
	private static HashMap<Integer, String> old_indices;
	private static HashMap<String, Integer> new_indices;
	
	
	
	// static run method 
	// aufruf: (geneList,mapIn,termIn,weightIn, mapOut,termOut,weigthOut,geneListProfiles,infoOut);
	public static void run(String geneList, String mapIn, String termIn, String weightIn, 
			String mapOut, String termOut, String weigthOut, String geneListProfiles, String infoOut){
		
		//read in list of genes -> genes to keep in filtering
		readGenes(geneList); //ok
		
		//filter .map (2nd column) + get term-ids (1st column)
		filterMap(mapIn, mapOut); //ok
		
		//filter .term (1st column) + get row-indices (0-based), which rows are kept -> for matrix filtering
		filterTerm(termIn, termOut, infoOut); //ok
		
		//read .weight -> translate it and filter it
		filterWeight(weightIn, weigthOut, geneListProfiles);
		

		
	}

	//-----------------------------------------------------------------------



	//read in list of genes -> genes to keep in filtering
	private static void readGenes(String pathGeneList) {
		FileInputReader reader = new FileInputReader(pathGeneList);
		String line;
		
		geneSet = new HashSet<String>();
		while((line=reader.read())!=null){
			geneSet.add(line);
		}
		reader.closer();
	}

	
	
	// durch .map laufen und 2.spalte checken: falls es was im GeneSet gibt:
	// -> Zeile rausschreiben in file  (+ zusaetzliche Spalte, welche zeilen behalten wurden? 1-based)
	// -> Term in 1.Spalte merken (TermSet/term2gene)
	private static void filterMap(String mapIn, String mapOut) {
		FileInputReader reader = new FileInputReader(mapIn);
		FileOutputWriter writer = new FileOutputWriter(mapOut);
		String line;
		
		term2gene = new HashMap<String,String>();
		int c = 1;

		while((line=reader.read())!=null){ // pm0100050	LNK	lymphocyte adaptor protein
			String[] l = line.split("\t");
			if(geneSet.contains(l[1])){
				writer.write(line+"\t"+c+"\n");
				term2gene.put(l[0], l[1]);
			}
			c++;
		}
		reader.closer();
		writer.closer();
		
	}
	
	
	// durch .term laufen: falls es die 1.Spalte im TermSet/term2gene gibt:
	// -> Zeile rausschreiben 
	// -> row number (0-based) auch rauschreiben/speichern (damit man weiss, was behalten wurde) 
	//		= old index! (-> wichtig fuer matrix filtering!)
	//		auch gleich new_indices aufbauen
	private static void filterTerm(String termIn, String termOut, String infoOut) {
		FileInputReader reader = new FileInputReader(termIn);
		FileOutputWriter writerTerm = new FileOutputWriter(termOut);
		FileOutputWriter writerRows = new FileOutputWriter(infoOut);
		String line;
		
		old_indices = new HashMap<Integer, String>();
		new_indices = new HashMap<String, Integer>();
		int oldIndex = 0;
		int newIndex = 0;
		
		while((line=reader.read())!=null){ // 2p24    8.910131
			String[] l = line.split("\t");
			if(term2gene.containsKey(l[0])){
				writerTerm.write(line+"\n");
				writerRows.write(oldIndex+"\n");
				
				String gene = term2gene.get(l[0]);
				old_indices.put(oldIndex, gene);
				new_indices.put(gene, newIndex);
				newIndex++;
			}
			oldIndex++;
		}
		reader.closer();
		writerTerm.closer();
		writerRows.closer();
	}
	
	
	// read .weight file, translate it, and filter it
	private static void filterWeight(String weightIn, String weigthOut, String geneListProfiles){
		ByteReader reader = new ByteReader(weightIn);
		FileOutputWriter writer = new FileOutputWriter(weigthOut);
		FileOutputWriter writerGenes = new FileOutputWriter(geneListProfiles);

		// 3 int numbers: Zahl der Spalten (88536); Zahl der Zeilen (88536); Zahl der sparse elements (15213762)
		for (int n=1; n<=3; n++){
			int i = reader.readInt();
			//writer.write(i+"\t");
		}
		//writer.write("\n");
		
		// read all "lines" in file:
		int row = 0;
		int number; // get length of line, or -1 if file is completely read
		while((number = reader.readInt())!=-1){
			ByteLine bl = reader.readElements(number);
			//bl.write(writer);	//write all ByteLines (unfiltered)
			
			//filtering (write only filtered ByteLines):
			// if line has to be kept (row contained in old_indices): 
			// filter this ByteLine (adjust indices) and write it
			if(old_indices.containsKey(row)){
				ByteLine blFiltered = bl.filter(old_indices, new_indices);
				blFiltered.write(writer);
				
				// also write geneListProfiles
				String gene = old_indices.get(row);
				int oldInd = row;
				int newInd = new_indices.get(gene);
				writerGenes.write(gene+"\t"+oldInd+"\t"+newInd+"\n");
				
			}
			row++;
		}
		writer.closer();
		writerGenes.closer();
	}
	
	
	
	
}
