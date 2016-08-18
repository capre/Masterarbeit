package main;

import profiles.ProfileCalculator;
import profiles.ProfileGenerator;

public class MainProfiles {

	public static void main(String[] args) {
		
		//candGenes, nonCandGenes: WITH header
		String candGenes = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/CandidateGenes/candidate_genes_uniq.csv";
		String nonCandGenes = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/CandidateGenes/no_candidate_genes_uniq.csv";
		
		//String path = "/storageNGS/ngs3/projects/other/Schizo_SVM/Schizo_data/Dataset_2/ProfileAnalysis/";
		String path = "D:/MA_data/Dataset_2_part/ProfileAnalysis/";
		
		//String pathProbands = "/storageNGS/ngs3/projects/other/Schizo_SVM/Schizo_data/Dataset_2/";
		String pathProbands = "D:/MA_data/Dataset_2_part/Files/";
		
		String mode = "noNorm/"; 
		// use noNorm/ (all genes) OR normL/ (genes with length, for Cadd-score normalisation) OR clusterCandGenes/
		
		
		//input
		//String geneList = candGenes;
		String geneList = pathProbands+mode+"!GeneList.txt";
		//String geneList = pathProbands+mode+"!GeneList_normL.txt";
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
		String clusterMatrix = path +mode+"featGenes.txt";	// for hierarchical clustering of genes based on their features/ their annotation
		
		// translate input (write output files)
		//ProfileGenerator.run(geneList,mapIn,termIn,weightIn, mapOut,termOut,weigthOut,geneListProfiles,infoOut);
		//ProfileGenerator.writeViscoveryMatrix(weigthOutComplete);
		//ProfileGenerator.writeClusterMatrix(clusterMatrix);
		//ProfileGenerator.writeClusterMatrix(clusterMatrix, candGenes); // candGenes for color coding in tree
		
		
		//-------------------------------------------------------------------------
		

		String matrixProbandsIn = pathProbands+mode+"!Matrix_scale.txt";
		//String matrixProbandsIn = pathProbands+mode+"!Matrix_normL.txt";
		String matrixProbandsOut = path+mode+"!Matrix_scale_profile.txt";
		String matrixProbandsOutSVM = path+mode+"!SVM_scale_profile.txt";
		String matrixProbandsCluster = path+mode+"featProbands_scale.txt";
		//String matrixProbandsCluster = path+mode+"featProbands_normL.txt";
		
		String geneGroup = candGenes; // WITH header (ignored) eg candGenes OR nonCandGenes
		String profileForGenes = path +mode+"profile_CandidateGenes.txt";
		
		String probandGroup = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/Dataset_2/Files/case_ids_study.txt"; //eg case_ids_study.txt OR control_ids_study.txt 
		String profileForProbands = path +mode+"profile_cases.txt";
		
		String genePrediction = path +mode+"genePrediction.txt";
		
		
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
