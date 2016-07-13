package main;

import java.io.IOException;

import profiles.ProfileGenerator;

public class MainProfiles {

	public static void main(String[] args) throws IOException {
		

		String path = "/home/ibis/carolin.prexler/Documents/ProfileAnalysis/";

		//input
		//String geneList = "/media/carolin/Daten/MA_data/Files/noNorm/!GeneList.txt";
		String geneList = "/storageNGS/ngs3/projects/other/Schizo_SVM/Schizo_data/Dataset_2/noNorm/!GeneList.txt";
		String mapIn = path+"profiles/dbset.map";
		String termIn = path+"profiles/dbset.term";
		String weightIn = path+"profiles/dbset.weight";
		
		//output:
		String mapOut = path+"dbset.map";
		String termOut = path+"dbset.term";
		String weigthOut = path+"dbset.weigth";
		String infoOut = path+"rows_kept.info";
		
		ProfileGenerator.run(geneList,mapIn,termIn,weightIn, mapOut,termOut,weigthOut,infoOut);
		
		
		
	}

}
