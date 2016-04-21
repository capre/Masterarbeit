package io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileOutputWriter {
	
	private BufferedWriter writer;
	private String path;

	
	/**
	 * creates BufferedWriter writing to a file from path path
	 * @param path: path to file that should be written
	 */
	public FileOutputWriter(String path){
		this.path=path;
		Charset c = Charset.forName("UTF-8");
		try{
			writer = Files.newBufferedWriter(Paths.get(path),c);
		}
		catch(IOException e){
			System.out.println("Error while creating the writer for file "+path);
			System.exit(1);
		}
	}
	
	/**
	 * appends a string to the file
	 * @param text: string that should be written
	 */
	public void write(String text){
		try {
			writer.append(text);
		} catch (IOException e) {
			System.out.println("Error while writing to file "+path);
			System.exit(1);
		}
	}
	
	
	/**
	 * closes the writer
	 */
	public void closer(){
		try{
			writer.close();
		}
		catch(IOException e){
			System.out.println("Error while closing writer for file "+path);
			System.exit(1);
		}
	}


	
	
	
	
	
	
	//----------------------------------------

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

}
