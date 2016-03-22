package main;

import io.FileInputReader;

public class Main {

	public static void main(String[] args) {
		String pathIn = "C:/Users/Carolin/Documents/Studium/2_Master/Masterarbeit/Data/Schizophrenie/example.vcf";
		
		FileInputReader reader = new FileInputReader(pathIn);
		String line = reader.read();
		System.out.println(line);
		reader.closer();

	}

}
