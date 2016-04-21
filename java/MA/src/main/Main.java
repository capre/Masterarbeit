package main;

import io.FileInputReader;

public class Main {

	public static void main(String[] args) {
		//String pathIn = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/example.vcf";
		//String pathIn = "F:/AllVariants_323Samples_filtered_HC.final_CasesOnly.annovarIN.hg19_multianno_withFreq.exac.tsv";
		String pathIn = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/robert_expression_Data/!data.selected.txt";
		
		
		FileInputReader reader = new FileInputReader(pathIn);
		//String line;
		String line=reader.read();
		System.out.println(line);
		while((line=reader.read())!=null){
			String[] array = line.split("\t");
			System.out.print(array[0]);
			for (int pos=1;pos<array.length;pos++){
				//System.out.println(Integer.getInteger(array[pos])*100000000000000);
			}
			System.out.println("");

			
			
			
			if (!line.startsWith("##")){
				if (line.startsWith("#")){
					String[] samples = line.split("\t");
					//pos0-8: info etc.
					for (int pos=9;pos<samples.length;pos++){
						/*
						TODO sample ids abspeichern... 
						zb HashMap<Integer,HashMap<String,Double>>
						Key = sample id (muss ja eindeutig sein, oder? ist die immer integer?!?)
						Value = Gene (Namen), die mutiert sind bei diesem Patient
							gespeichert in HashMap<String,Double>, wobei Key=Genname und Value=Score (wie sehr kaputt)
						?? braucht man noch eine Info dazu?
						- homo-/heterozygot?
						- art der Mutation (Intron, Downstream, Non-syn, syn)?
						
						
						*/
						
					}
					
					
				}
				String[] lineArray = line.split("\t");
				//pos0-8: info etc.
				for (int pos=9;pos<lineArray.length;pos++){
					
					
					
					
					
				}
				
				
				
				
			}
		
			
		}
		
		
		
		
		
	
		reader.closer();

	}

}

