package main;

import profiles.ProfileGenerator;

public class MainProfiles {

	public static void main(String[] args) {
		
		String path = "/home/ibis/carolin.prexler/Documents/ProfileAnalysis/";
		//String path = "D:/MA_data/Dataset_2_part/ProfileAnalysis/";

		//input
		//String geneList = "/media/carolin/Daten/MA_data/Files/noNorm/!GeneList.txt";
		String geneList = "/storageNGS/ngs3/projects/other/Schizo_SVM/Schizo_data/Dataset_2/noNorm/!GeneList.txt";
		//String geneList = "D:/MA_data/Dataset_2_part/Files/noNorm/!GeneList.txt";
		String mapIn = path+"profiles/dbset.map";
		String termIn = path+"profiles/dbset.term";
		String weightIn = path+"profiles/dbset.weight";
		
		//output:
		String mapOut = path+"dbset_filtered.map";
		String termOut = path+"dbset_filtered.term";
		String weigthOut = path+"dbset_filtered.weigth";
		String weigthOutComplete = path+"dbset_filtered_complete.weigth";
		String geneListProfiles = path+"GeneListProfiles.txt"; //only for shift of indices (filtering not only rows, but also columns)
		String infoOut = path+"rows_kept.info"; 	//=old-indices (0-based)
		
		ProfileGenerator.run(geneList,mapIn,termIn,weightIn, mapOut,termOut,weigthOut,weigthOutComplete,geneListProfiles,infoOut);
		
		
		
	}

}
