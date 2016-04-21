package algorithm;

import java.util.HashMap;

public class CalcSimilarity {
	
	private HashMap<Integer,HashMap<String,Double>> patients;
	private double[][] simScores;
	
	//konstruktor
	public CalcSimilarity(HashMap<Integer,HashMap<String,Double>> patients){
		this.patients = patients;
		//TODO liefert ".size()" die anzahl der keys (= anzahl der patienten)?????
		this.simScores = new double[patients.size()][patients.size()];
	}
	
	
	

}
