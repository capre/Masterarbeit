package algorithm;

import java.util.HashMap;

public class CreatePatientData {
	
	private static HashMap<String,HashMap<String,Double>> patientData;
	private static HashMap<String,Integer> patientsPos; //sammlung aller patienten + position fuer array spaeter
	private static HashMap<String,Integer> genesPos; //sammlung aller gene + position fuer array spaeter
	
	// disease status of probands (cases vs controls): f=0=healthy; t=1=diseased
	private static HashMap<String,Boolean> status;
	
	//nach Aufruf von hashmapToMatrix() vorhanden:
	private static String[] patientsArray;
	private static String[] genesArray;
	private static double[][] scores;
	

	
	//initialisierungs-methode; erzeugt leere (!) HashMaps: patientData, patientsPos, genesPos, status
	public static void initializePatientData(){
		patientData = new HashMap<String,HashMap<String,Double>>();
		patientsPos = new HashMap<String,Integer>();
		genesPos = new HashMap<String,Integer>();
		status = new HashMap<String,Boolean>();
	}
	
	
	// fuegt einen eintrag (gen + score) bei einem patienten hinzu (= baut patientData auf)
	//erzeugt dabei auch gleich die HashMaps patientsPos, genesPos und status
	public static void addEntry(String patientId, String gene, double score, boolean stat){
		//HashMap<String,Integer> patientsPos ggf erweitern
		if(!patientsPos.containsKey(patientId)){
			patientsPos.put(patientId, patientsPos.size());
		}
		//HashMap<String,Integer> genesPos ggf erweitern
		if(!genesPos.containsKey(gene)){
			genesPos.put(gene, genesPos.size());
		}
		//HashMap<String,Integer> status ggf erweitern
		if(!status.containsKey(patientId)){
			status.put(patientId, stat);
		}
		else if(status.get(patientId)!=stat){
			System.out.println("Same patient id in cases and controls: "+patientId);
		}
		
		
		//falls patient bisher noch nicht in patientData enthalten, dann neu hinzufuegen:
		if(!patientData.containsKey(patientId)){
			HashMap<String,Double> genes = new HashMap<String,Double>();
			genes.put(gene, score);
			patientData.put(patientId, genes);
		}
		else{
			if(!patientData.get(patientId).containsKey(gene)){
				patientData.get(patientId).put(gene, score);	
			}
			else if(!patientData.get(patientId).get(gene).equals(score)){
				System.out.println("There is already a different score for patient "+patientId+" and gene "+gene);
			}
		}
	}
	
	
	//wandelt patientData-HashMap in matrix um (erzeugt Arrays: patientsArray, genesArray, scores)
	//dafuer noch patientsPos und genesPos noetig
	public static void hashmapToMatrix(){
		patientsArray = new String[patientsPos.size()];
		genesArray = new String[genesPos.size()];
		scores = new double[patientsPos.size()][genesPos.size()];
		
		//Gen-Array befuellen:
		for (String gene : genesPos.keySet()){
			genesArray[genesPos.get(gene)] = gene;
		}
		
		//durch patientData iterieren und dabei die patienten- und score-Arrays befuellen:
		for (String patientId : patientData.keySet()){ //alle patienten durchgehen
			//patientId in patientsList abspeichern
			patientsArray[patientsPos.get(patientId)] = patientId;
			//jetzt alle gene dieses patienten durchgehen und scores abspeichern
			for (String gene : patientData.get(patientId).keySet()){
				scores[patientsPos.get(patientId)][genesPos.get(gene)] = patientData.get(patientId).get(gene);
			}
		}
	}
	

	
	
	

	
	//getter:
	public static String[] getPatients(){
		return patientsArray;
	}
	public static String[] getGenes(){
		return genesArray;
	}
	public static double[][] getScores(){
		return scores;
	}
	public static HashMap<String,Boolean> getStatus(){
		return status;
	}
	


}
