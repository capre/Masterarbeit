package profiles;

import io.ByteLine;
import io.ByteReader;
import io.FileInputReader;
import io.FileOutputWriter;

import java.util.HashSet;

import org.omg.Messaging.SyncScopeHelper;

public class ProfileGenerator {
	
	//static variables:
	private static HashSet<String> geneSet;
	private static HashSet<String> termSet;
	private static HashSet<Integer> rowSet;
	
	
	
	
	// static run method 
	public static void run(String geneList, String mapIn, String termIn, String weightIn, 
			String mapOut, String termOut, String weigthOut, String infoOut){
		
		//read in list of genes -> genes to keep in filtering
		readGenes(geneList); //ok
		
		//filter .map (2nd column) + get term-ids (1st column)
		filterMap(mapIn, mapOut); //ok
		
		//filter .term (1st column) + get row-indices (0-based), which rows are kept -> for matrix filtering
		filterTerm(termIn, termOut, infoOut); //ok
		
		//read .weight -> translate it read (keeping original row indices!!!)//TODO
		filterWeight(weightIn, weigthOut);
		

		
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
	// -> Term in 1.Spalte merken (TermSet)
	private static void filterMap(String mapIn, String mapOut) {
		FileInputReader reader = new FileInputReader(mapIn);
		FileOutputWriter writer = new FileOutputWriter(mapOut);
		String line;
		
		termSet = new HashSet<String>();
		int c = 1;

		while((line=reader.read())!=null){ // pm0100050	LNK	lymphocyte adaptor protein
			String[] l = line.split("\t");
			if(geneSet.contains(l[1])){
				writer.write(line+"\t"+c+"\n");
				termSet.add(l[0]);
			}
			c++;
		}
		reader.closer();
		writer.closer();
		
	}
	
	
	// durch .term laufen: falls es die 1.Spalte im TermSet gibt:
	// -> Zeile rausschreiben 
	// -> row number (0-based) auch rauschreiben/speichern (damit man weiss, was behalten wurde) -> wichtig fuer matrix filtering!
	private static void filterTerm(String termIn, String termOut, String infoOut) {
		FileInputReader reader = new FileInputReader(termIn);
		FileOutputWriter writerTerm = new FileOutputWriter(termOut);
		FileOutputWriter writerRows = new FileOutputWriter(infoOut);
		String line;
		
		rowSet = new HashSet<Integer>();
		int c = 0;
		
		while((line=reader.read())!=null){ // 2p24    8.910131
			String[] l = line.split("\t");
			if(termSet.contains(l[0])){
				writerTerm.write(line+"\n");
				writerRows.write(c+"\n");
				rowSet.add(c);
			}
			c++;
		}
		reader.closer();
		writerTerm.closer();
		writerRows.closer();
	}
	
	
	// read .weigth file, filter it (keeping original row indices!!!) and translate it ...
	private static void filterWeight(String weightIn, String weigthOut){
		ByteReader reader = new ByteReader(weightIn);
		FileOutputWriter writer = new FileOutputWriter(weigthOut);

		writer.write("reading .weigth\n");
		// 3 int numbers: Zahl der Spalten (88536); Zahl der Zeilen (88536); Zahl der sparse elements (15213762)
		for (int n=1; n<=3; n++){
			int i = reader.readInt();
			writer.write(i+"\t");
		}
		writer.write("\n");
		
		// read all "lines" in file:
		int number; // get length of line, or -1 if file is completely read
		while((number = reader.readInt())!=-1){
			ByteLine bl = reader.readElements(number);
			int[] ind = bl.getIndices();
			float[] val = bl.getValues();
			writer.write(bl.getSize()+"\t");
			for(int pos=0; pos<bl.getSize(); pos++){
				writer.write(ind[pos]+":"+val[pos]+"\t");
			}
			writer.write("\n");
		}
		
		

		

		
		writer.closer();
	}
	
	
	
	
}
