package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class FileUtilities {
	
	public static String[][] readInMatrix(String path){
		List<String> content = readLines(path);
		
		int size = content.get(0).split(",").length;
		String[][]result = new String[size][size];
		
		for(int i=0; i<result.length; i++){
			System.out.println(i);
			String [] parts = content.remove(0).split(",");
			result[i] = parts;
		}
		return result;
	}
	
	public static HashMap<Integer,LinkedList<String[]>>readInKSZFrequency(String path){
		HashMap<Integer,LinkedList<String[]>> frequency = new HashMap<Integer,LinkedList<String[]>>();
		
		List<String> content = readLines(path);
		content.remove(0);
		content.remove(0);

		for(String line : content){
			String [] parts = line.split("\t");
			int id = Integer.valueOf(parts[0]);
			String[]newSymptom = new String[2];
			newSymptom[0]=parts[1];
			newSymptom[1]=parts[2];
			LinkedList<String[]> symptoms = new LinkedList<String[]>();
			if(frequency.containsKey(id)){
				symptoms = frequency.get(id);
				symptoms.add(newSymptom);
			}
			else{
				symptoms.add(newSymptom);
			}
			frequency.put(id, symptoms);
		}
		
		return frequency;
	}
	
	public static LinkedList<Integer>readInQuery(String path){
		LinkedList<Integer> symptoms = new LinkedList<Integer>();
		
		List<String> content = readLines(path);
		content.remove(0);
		content.remove(0);
		
		for(String line : content){
			symptoms.add(Integer.valueOf(line));
		}
		
		return symptoms;
	}

	public static LinkedList<Integer> readInSymptoms(String path){
		LinkedList<Integer> symptoms = new LinkedList<Integer>();

		List<String> content = readLines(path);
		content.remove(0);
		content.remove(0);

		for(String line : content){
			String [] parts = line.split("\\s");
			int id = Integer.valueOf(parts[0]);
			symptoms.add(id);
		}
		return symptoms;
	}

	public static HashMap<Integer,LinkedList<Integer>> readInKSZ(String path){
		HashMap<Integer,LinkedList<Integer>> ksz = new HashMap<Integer,LinkedList<Integer>>();

		List<String> content = readLines(path);
		content.remove(0);
		content.remove(0);

		for(String line : content){
			String [] parts = line.split("\\s");
			int id = Integer.valueOf(parts[0]);
			LinkedList<Integer> symptoms = new LinkedList<Integer>();
			if(ksz.containsKey(id)){
				symptoms = ksz.get(id);
				symptoms.add(Integer.valueOf(parts[1]));
			}
			else{
				symptoms.add(Integer.valueOf(parts[1]));
			}
			ksz.put(id, symptoms);
		}

		return ksz;
	}

	public static int[][] readInOntology(String path){

		List<String> content = readLines(path);
		int[][] ontologyArray = new int[content.size()-2][2];
		content.remove(0);
		content.remove(0);

		int position = 0;
		for(String line : content){
			String [] parts = line.split("\\s");
			int idChild = Integer.valueOf(parts[0]);
			int idParent = Integer.valueOf(parts[1]);
			ontologyArray[position][0]=idChild;
			ontologyArray[position][1]=idParent;
			position++;
		}
		return ontologyArray;

	}














	/**
	 * to write content into a given file
	 * @param path
	 * @param content
	 */
	public static void writeString(String path, String content) {
		//Open file
		File file = new File(path);

		//Create File if necessary
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				System.err.println(ex);
				System.exit(1);
			}
		}

		//Error if not writable
		if (!file.canWrite()) {
			System.err.println(file + " could not be written to!");
			System.exit(1);
		}

		//Write lines
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(content);
			writer.close();
		} catch (IOException ex) {
			System.err.println(ex);
			System.exit(1);
		}

	}

	/**
	 * to write content into a given file without deleting its current content
	 * @param path
	 * @param content
	 */
	public static void writeStringToExistingFile(String path, String content) {
		//Open file
		File file = new File(path);

		//Create File if necessary
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				System.err.println(ex);
				System.exit(1);
			}
		}

		//Error if not writable
		if (!file.canWrite()) {
			System.err.println(file + " could not be written to!");
			System.exit(1);
		}

		//Write lines
		try {
			FileWriter writer = new FileWriter(file,true);
			writer.write(content);
			writer.close();
		} catch (IOException ex) {
			System.err.println(ex);
			System.exit(1);
		}

	}

	private static List<String> readLines(String path){
		ArrayList<String> lines = new ArrayList<String>();

		//open file
		File file = new File(path);

		//Error if not readable
		if (!file.canRead()) {
			System.err.println("File " + file.getAbsolutePath() + " could not be read!");
			System.exit(1);
		}

		BufferedReader inputStream = null;
		//Return lines
		try {
			inputStream = new BufferedReader(new FileReader(file));
			String line;
			while((line = inputStream.readLine()) != null)  {
				lines.add(line);
			}

			inputStream.close();
		} catch (FileNotFoundException ex) {
			System.err.println(file.getAbsolutePath() + " not found!");
			System.exit(1);
		} catch (IOException ex) {
			System.err.println(ex);
			System.exit(1);
		}

		return lines;
	}


}

