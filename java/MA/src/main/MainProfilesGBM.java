package main;

import profiles.ProfileCalculator;
import profiles.ProfileGenerator;

public class MainProfilesGBM {

	public static void main(String[] args) {
		
		// candGenes, nonCandGenes: WITH header
		String pathCand = "D:/MA_data/Glioblastoma/";
		String candGenes = pathCand + "Cand.txt";
		String nonCandGenes = pathCand  +"nonCand.txt";

		
		//TODO String pathProfileIn = "/storageNGS/ngs3/projects/other/Schizo_SVM/Schizo_data/Dataset_2/ProfileAnalysis/";
		String pathProfileIn = "D:/MA_data/Dataset_2_part/ProfileAnalysis/";
		
		String pathProfileOut = "D:/MA_data/Glioblastoma/ProfileAnalysis/";
		
		//TODO String pathProbands = "/storageNGS/ngs3/projects/other/Schizo_SVM/Schizo_data/Dataset_2/";
		String pathProbands = "D:/MA_data/Glioblastoma/data/";
		
		//use all/ (all genes) OR 100CADD/ (100 best CADD genes) OR 200Trans/ (200 best genes for Transcripts)
		String mode = "all/"; 
		
		
		
		//input
		String geneList = pathProbands+"SVM_GBM.name";
		//String geneList = pathProbands+"100_feature_genes_CADD_sum_score.txt";
		
		String mapIn = pathProfileIn+"profiles/dbset.map";
		String termIn = pathProfileIn+"profiles/dbset.term";
		String weightIn = pathProfileIn+"profiles/dbset.weight";
		
		//output:
		String mapOut = pathProfileOut+mode+"dbset_filtered.map";
		String termOut = pathProfileOut+mode+"dbset_filtered.term";
		String weigthOut = pathProfileOut+mode+"dbset_filtered.weigth";
		String weigthOutComplete = pathProfileOut+mode+"dbset_filtered_complete.weigth";
		String geneListProfiles = pathProfileOut+mode+"GeneListProfiles.txt"; // for shift of indices; row index of genes in filtered file
		String infoOut = pathProfileOut+mode+"rows_kept.info"; 	//=old-indices (0-based)
		String clusterMatrix = pathProfileOut +mode+"featGenes.txt";	// for hierarchical clustering of genes based on their features/ their annotation
		
		// translate input (write output files)
		//ProfileGenerator.run(geneList,mapIn,termIn,weightIn, mapOut,termOut,weigthOut,geneListProfiles,infoOut);
		//TODO ProfileGenerator.writeViscoveryMatrix(weigthOutComplete);
		//ProfileGenerator.writeClusterMatrix(clusterMatrix);
		//TODO ProfileGenerator.writeClusterMatrix(clusterMatrix, candGenes); // candGenes for color coding in tree
		
		
		//-------------------------------------------------------------------------
		
		
		String matrixProbandsIn = pathProbands+"GBM.txt";
		String matrixProbandsOut = pathProfileOut+mode+"GBM_profile.txt";
		String matrixProbandsOutSVM = pathProfileOut+mode+"SVM_GBM_profile.txt";
		String matrixProbandsCluster = pathProfileOut+mode+"featProbands.txt";
		
		
		String geneGroup = candGenes; // WITH header (ignored) eg candGenes OR nonCandGenes
		String profileForGenes = pathProfileOut +mode+"results/profile_CandidateGenes.txt";
		
		String probandGroup = pathProbands+"ids_control.txt"; //eg ids_case.txt OR ids_control.txt 
		String profileForProbands = pathProfileOut +mode+"results/profile_control.txt";
		
		String genePrediction = pathProfileOut +mode+"results/genePrediction.txt";
		
		
		// generate a matrix of probands "annotated" by features of profiles
		ProfileCalculator.run(termIn,weigthOut,geneListProfiles);
		
		//ProfileCalculator.writeViscoveryMatrix(matrixProbandsIn, matrixProbandsOut);
		//ProfileCalculator.writeSvmMatrix(matrixProbandsIn, matrixProbandsOutSVM);
		//ProfileCalculator.writeClusterMatrix(matrixProbandsIn, matrixProbandsCluster);
		
		//ProfileCalculator.writeProfileForProbands(matrixProbandsIn, probandGroup, profileForProbands);
		
		//predict gene: candGene or nonCandGene?
		ProfileCalculator.predictGenes(candGenes, nonCandGenes, geneList, genePrediction);
		
		//removes self-associations of genes first! --> annotations without self-associations afterwards!
		//ProfileCalculator.writeProfileForGenes(geneGroup, profileForGenes);
		
	
		
	}

	
	
}
