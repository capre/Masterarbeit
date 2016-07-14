package main;

import profiles.ProfileGenerator;

public class MainProfiles {

	public static void main(String[] args) {
		
		//String path = "/home/ibis/carolin.prexler/Documents/ProfileAnalysis/";
		String path = "D:/MA_data/Dataset_2_part/ProfileAnalysis/";

		//input
		//String geneList = "/media/carolin/Daten/MA_data/Files/noNorm/!GeneList.txt";
		//String geneList = "/storageNGS/ngs3/projects/other/Schizo_SVM/Schizo_data/Dataset_2/noNorm/!GeneList.txt";
		String geneList = "D:/MA_data/Dataset_2_part/Files/noNorm/!GeneList.txt";
		String mapIn = path+"profiles/dbset.map";
		String termIn = path+"profiles/dbset.term";
		String weightIn = path+"profiles/dbset.weight";
		
		//output:
		String mapOut = path+"dbset_filtered.map";
		String termOut = path+"dbset_filtered.term";
		String weigthOut = path+"dbset_filtered_originalIndex.weigth"; // keeping original row indices!!!
		String infoOut = path+"rows_kept.info";
		
		ProfileGenerator.run(geneList,mapIn,termIn,weightIn, mapOut,termOut,weigthOut,infoOut);
		
		
		
	}

}
