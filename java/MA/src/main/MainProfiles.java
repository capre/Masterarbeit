package main;

import profiles.ProfileCalculator;
import profiles.ProfileGenerator;

public class MainProfiles {

	public static void main(String[] args) {
		
		String path = "/home/ibis/carolin.prexler/Documents/ProfileAnalysis/";
		//String path = "D:/MA_data/Dataset_2_part/ProfileAnalysis/";
		
		String pathProbands = "/storageNGS/ngs3/projects/other/Schizo_SVM/Schizo_data/Dataset_2/";
		//String pathProbands = "D:/MA_data/Dataset_2_part/Files/";
		
		String mode = "noNorm/"; // use noNorm/ (all genes) OR normL/ (genes with length, for Cadd-score normalisation)

		//input
		//String geneList = pathProbands+mode+"!GeneList_normL.txt";
		String geneList = pathProbands+mode+"!GeneList.txt";
		String mapIn = path+"profiles/dbset.map";
		String termIn = path+"profiles/dbset.term";
		String weightIn = path+"profiles/dbset.weight";
		
		//output:
		String mapOut = path+mode+"dbset_filtered.map";
		String termOut = path+mode+"dbset_filtered.term";
		String weigthOut = path+mode+"dbset_filtered.weigth";
		String weigthOutComplete = path+mode+"dbset_filtered_complete.weigth";
		String geneListProfiles = path+mode+"GeneListProfiles.txt"; // for shift of indices; row index of genes in filtered file
		String infoOut = path+mode+"rows_kept.info"; 	//=old-indices (0-based)
		String clusterMatrix = path +mode+"feat.txt";	// for hierarchical clustering of genes based on their features/ their annotation
		
		// translate input (write output files)
		//ProfileGenerator.run(geneList,mapIn,termIn,weightIn, mapOut,termOut,weigthOut,weigthOutComplete,geneListProfiles,infoOut,clusterMatrix);
		

		//String matrixProbandsIn = pathProbands+mode+"!Matrix_normL.txt";
		String matrixProbandsIn = pathProbands+mode+"!Matrix_scale.txt";
		String matrixProbandsOut = path+mode+"!Matrix_scale_profile.txt";
		String matrixProbandsOutSVM = path+mode+"!SVM_scale_profile.txt";
		String matrixProbandsCluster = path+mode+"featProbands.txt";
		
		// generate a matrix of probands "annotated" by features of profiles
		ProfileCalculator.run(termIn,weigthOut,geneListProfiles,matrixProbandsIn, matrixProbandsOut,matrixProbandsOutSVM,matrixProbandsCluster);
		
		
	}

}
