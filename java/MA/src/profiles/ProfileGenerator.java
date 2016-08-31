package profiles;

import io.ByteLine;
import io.ByteReader;
import io.FileInputReader;
import io.FileOutputWriter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class ProfileGenerator {
	
	private static HashSet<String> geneSet;
	private static HashMap<String, String> term2gene;
	private static HashMap<Integer, String> index2term;
	private static HashMap<Integer, String> old_indices; // original_index --> Gene
	private static HashMap<String, Integer> new_indices; // Gene --> new, shifted index (only used for index_shift in case of column-filtering)
	private static LinkedList<ByteLine> lines;

	
	/*
	// run method 
	aufruf: ProfileGenerator.run(geneList,mapIn,termIn,weightIn, mapOut,termOut,weigthOut,geneListProfiles,infoOut);
	*/
	public static void run(String geneList, String mapIn, String termIn, String weightIn, 
			String mapOut, String termOut, String weigthOut, String geneListProfiles, String infoOut){

		System.out.println("++ run ProfileGenerator ++");

		//read in list of genes -> genes to keep in filtering
		System.out.println("read genes");
		readGenes(geneList); //ok
		
		//filter .map (2nd column) + get term-ids (1st column)
		System.out.println("filter .map for genes");
		filterMap(mapIn, mapOut); //ok
		
		//filter .term (1st column) + get row-indices (0-based), which rows are kept -> for matrix filtering
		System.out.println("read .term and get row indices");
		filterTerm(termIn, termOut, infoOut); //ok
		
		//read .weight -> translate it and filter it (filter only rows; keep all columns, keep indices = no shift of indices necessary)
		System.out.println("read .weight, translate it keeping only certain rows");
		filterWeight(weightIn, weigthOut, geneListProfiles); //ok
		
		
		/*
		//sizes: // stimmt nicht mehr ganz...
		System.out.println(geneSet.size());		//20890		
		System.out.println(term2gene.size());	//12300 [12330] 
		System.out.println(index2term.size());	//88536 	= number of columns (= number of terms)
		System.out.println(old_indices.size());	// 9717 [9744]		= number of rows after filtering
		System.out.println(new_indices.size());	// 9717 [9722]	(22 genes occur twice as they have two terms assigned...)
		System.out.println(lines.size());		// 9717 [9744]		= number of rows after filtering
		*/
		
		/*
		//for testing
		ByteLine bl1 = new ByteLine(3, "AB0");
		int i1 = 1;
		int i2 = 3;
		int i3 = 10;
		float f1 = (float) 1;
		float f2 = (float) 1.6;
		float f3 = (float) 18.2;
		bl1.addElement(i1, f1);
		bl1.addElement(i2, f2);
		bl1.addElement(i3, f3);
		
		ByteLine bl2 = new ByteLine(3, "AB0");
		int i21 = 3;
		int i22 = 5;
		int i23 = 10;
		float f21 = (float) 2;
		float f22 = (float) 2.6;
		float f23 = (float) 28.2;
		bl2.addElement(i21, f21);
		bl2.addElement(i22, f22);
		bl2.addElement(i23, f23);
		
		LinkedList<ByteLine> list = new LinkedList<ByteLine>();
		list.add(bl1);
		list.add(bl2);
		
		ByteLine b = ByteLine.sumFeatureValues(list, 11);
		for(int pos=0; pos<b.getSize(); pos++){
			System.out.print(pos);
			System.out.println("\t"+b.getIndices()[pos]+":"+b.getValues()[pos]);
		}
		*/
	}
	
	//OPTIONAL: create complete matrix (non-existing values -> 0) a la viscovery format:
	//gene, number of non-0-elements, list of values for features
	public static void writeViscoveryMatrix(String weigthOutComplete){
		System.out.println("write complete matrix in Viscovery format");
		createCompleteMatrix(weigthOutComplete); //ok
	}
	
	// OPTIONAL: create matrix for hierarchical clustering (= write output of svmclust.pl: feat.txt)
	public static void writeClusterMatrix(String clusterMatrix){
		System.out.println("write matrix for hierarchical clustering (without colour-coding)");
		boolean colouring = false;
		createClusterMatrix(clusterMatrix, "", colouring); //ok
	}
	// OPTIONAL: create matrix for hierarchical clustering (= write output of svmclust.pl: feat.txt)
	// use candGenes for color coding in tree (cand->1; nonCand->0)
	public static void writeClusterMatrix(String clusterMatrix, String candGenes){
		System.out.println("write matrix for hierarchical clustering (with colour-coding)");
		String clusterMatrix2 = clusterMatrix.replace(".txt", "_color.txt");
		boolean colouring = true;
		createClusterMatrix(clusterMatrix2, candGenes, colouring); //ok
	}
	

	//-----------------------------------------------------------------------



	//read in list of genes -> genes to keep in filtering (keep these rows)
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
		
		// which terms should be ignored? eg, some genes have 2 terms... -> ignore one (explicitly defined here)
		HashSet<String> ignore = new HashSet<String>();
		ignore.add("cardiotrophin-like cytokine");
		ignore.add("CAZ-associated structural protein");
		ignore.add("malignancy-associated protein");
		ignore.add("p53-responsive gene 4");
		ignore.add("receptor-interacting factor 1");
		
		// use copy of geneSet and remove genes already found --> ignore 2nd occurence of a gene
		HashSet<String> geneSetCopy = new HashSet<String>();
		geneSetCopy.addAll(geneSet);
		
		term2gene = new HashMap<String,String>();
		int c = 1;

		while((line=reader.read())!=null){ // pm0100050	LNK	lymphocyte adaptor protein
			String[] l = line.split("\t");
			if(geneSetCopy.contains(l[1])){
				if(!ignore.contains(l[2])){
					writer.write(line+"\t"+c+"\n");
					term2gene.put(l[0], l[1]);
					// if there are two terms for one gene -> remove gene from set to ignore second line
					geneSetCopy.remove(l[1]);
				}
				else{
					System.out.println("ignore line: \""+line+"\" in .map file");
				}
			}
			c++;
		}
		reader.closer();
		writer.closer();
		
		//System.out.println("geneSet: "+geneSet.size());			// still 20890
		//System.out.println("geneSetCopy: "+geneSetCopy.size());	// 8590 => 12300 removed

	}
	
	
	// durch .term laufen: falls es die 1.Spalte im TermSet/term2gene gibt:
	// -> Zeile rausschreiben 
	// -> row number (0-based) auch rauschreiben/speichern (damit man weiss, was behalten wurde) 
	//		= old index! (-> wichtig fuer matrix filtering!)
	//		auch gleich new_indices aufbauen // braucht man nur fuer spalten filtern...
	private static void filterTerm(String termIn, String termOut, String infoOut) {
		FileInputReader reader = new FileInputReader(termIn);
		FileOutputWriter writerTerm = new FileOutputWriter(termOut);
		FileOutputWriter writerRows = new FileOutputWriter(infoOut);
		String line;
		
		index2term = new HashMap<Integer, String>();
		old_indices = new HashMap<Integer, String>();
		new_indices = new HashMap<String, Integer>();
		int oldIndex = 0;
		int newIndex = 0;
		
		while((line=reader.read())!=null){ // 2p24    8.910131
			String[] l = line.split("\t");
			index2term.put(oldIndex, l[0]);
			
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
	
	
	// read .weight file, translate it, and filter it (filter only rows!)
	// kept ByteLines ---> LinkedList lines
	private static void filterWeight(String weightIn, String weigthOut, String geneListProfiles){
		ByteReader reader = new ByteReader(weightIn);
		FileOutputWriter writer = new FileOutputWriter(weigthOut);
		FileOutputWriter writerGenes = new FileOutputWriter(geneListProfiles);
		lines = new LinkedList<ByteLine>();

		// 3 int numbers: Zahl der Spalten (88536); Zahl der Zeilen (88536); Zahl der sparse elements (15213762)
		for (int n=1; n<=3; n++){
			int i = reader.readInt();
			//System.out.println(i+"\t");
		}
		
		// read all "lines" in file:
		int row = 0;
		int number; // get length of line, or -1 if file is completely read
		while((number = reader.readInt())!=-1){
			
			String gene =old_indices.get(row); // returns null, if line does not belong to one of "my" genes (does not matter)
			ByteLine bl = reader.readElements(number, gene);
			//bl.write(writer);	//write all ByteLines (unfiltered)
			
			//filtering (write only filtered ByteLines):
			// if line has to be kept (row contained in old_indices): keep line OR 
			// filter this ByteLine for columns (adjust indices) before writing
			if(old_indices.containsKey(row)){
				/*
				// for filtering columns:
				ByteLine blFiltered = bl.filter(old_indices, new_indices);
				blFiltered.write(writer);
				*/
				
				// also write geneListProfiles
				int oldInd = row;
				int newInd = new_indices.get(gene);
				writerGenes.write(gene+"\t"+oldInd+"\t"+newInd+"\n");
				
				
				bl.write(writer);
				lines.add(bl);	
			}
			row++;
		}
		writer.closer();
		writerGenes.closer();
	}
	
	
	// OPTIONAL
	// create complete matrix from ByteLine list (fill with "0"); keep 0-columns
	private static void createCompleteMatrix(String weigthOutComplete) {
		FileOutputWriter writer = new FileOutputWriter(weigthOutComplete);
		
		HashSet<String> uniqTerms = new HashSet<String>(); // after char replacement, some terms end up being identical -> add "_" to end
	 	
		//write header
		// Replace non-alphanumeric characters -> "_"
		writer.write("Gene\tnon0elements");
		for (int pos=0; pos<index2term.size(); pos++){ // index2term.size() = 88536 = number of columns
			String t = index2term.get(pos);
			t = t.replaceAll("[^A-Za-z0-9]", "_");
			// test, if term is still unique; otherwise add "_"
			if(uniqTerms.contains(t)){
				t = t+"_";
			}
			uniqTerms.add(t);
			writer.write("\t"+t);	
		}
		
		writer.write("\n");
		
		// write matrix:
		// check, if Gene is unique (otherwise add "_" to end)
		HashSet<String> uniqGenes = new HashSet<String>();
		for (ByteLine bl : lines) {
			
			String g = bl.getGene();
			if(uniqGenes.contains(g)){
				g = g+"_";
			}
			uniqGenes.add(g);
			
			writer.write(g+"\t"+bl.getSize());
            bl.writeComplete(writer, index2term.size());
        }
        
		writer.closer();
	}
	
	
	// OPTIONAL
	// create matrix for hierarchical clustering of genes based on their annotation/ their feature values
	// (= write output of svmclust.pl: feat.txt)
	// candGenes is only used, if colouring=true (cand->1; nonCand->0)
	private static void createClusterMatrix(String clusterMatrix, String candGenes, boolean colouring) {
		FileOutputWriter writer = new FileOutputWriter(clusterMatrix);
		
		//in case of colouring : read candGenes
		HashSet<String> candSet = new HashSet<String>();
		if(colouring){
			FileInputReader reader = new FileInputReader(candGenes);
			String line;
			while((line=reader.read())!=null){
				candSet.add(line);
			}
			reader.closer();
		}
		
		// write header
		writer.write("UID\tNAME\tGWEIGHT");
		for (int pos=0; pos<index2term.size(); pos++){		// index2term.size() = 88536 = number of columns
			String t = index2term.get(pos);
			writer.write("\t"+t);	
		}
		writer.write("\n"); 
		
		// write second line with EWEIGHT (entweder gewichtete summe, oder 1)
		writer.write("EWEIGHT\t\t");
		//	gewichtete summe:
		//ByteLine bl_sum = ByteLine.sumFeatureValues(lines, index2term.size()); // index2term.size() = 88536 = number of columns
		//bl_sum.writeComplete(writer,index2term.size());	
		//	1er:
		for(int c=0; c<index2term.size(); c++){
			writer.write("\t1");
		}
		writer.write("\n");
		
		
		// write actual matrix with feature values for genes
		for (ByteLine bl : lines) {
			String g = bl.getGene();
			if(colouring){
				if(candSet.contains(g)){
					g = "1_"+g;
				}
				else{
					g = "0_"+g;
				}
			}
			//System.out.println(g);
			writer.write(g+"\t"+g+"\t1");
            bl.writeComplete(writer, index2term.size());
        }

		writer.closer();
	}
	
}
