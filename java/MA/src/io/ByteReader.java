package io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

 
public class ByteReader {
	
	private BufferedInputStream in;
	private String path;
	
	
	//initialize reader
	public ByteReader(String path){
		this.path=path;
		//Charset c = Charset.forName("UTF-8");
		try {
			in = new BufferedInputStream(new FileInputStream(path));
		} catch (FileNotFoundException e1) {
			
			System.out.println("Error: this file is not found: "+path);
			System.exit(1);
			//auto-generated:
			//e1.printStackTrace();
		}
	}
	

	// return one int value (return -1 in case of completely read file)
	public int readInt(){

		//Allocate buffer with 4byte = 32bit = Integer.SIZE
		byte[] ioBuf = new byte[4];       
		int bytesRead;
		try {
			if ((bytesRead = in.read(ioBuf)) != -1){
				//Little-endian:
				int x = java.nio.ByteBuffer.wrap(ioBuf).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
				return x;
			}
			else{ // file is completely read
				System.out.println("File is completely read: no further line");
				return -1;
			}	
		}
		catch (IOException e) {
			System.out.println("Error while reading from file "+path);
			System.exit(1);
		}
		System.out.println("Something went wrong: int cannot be read");
		return -2;
	}
	
	
	
	// return one float value
	private float readFloat(){

		//Allocate buffer with 4byte = 32bit = Integer.SIZE
		byte[] ioBuf = new byte[4];       
		int bytesRead;
		try {
			if ((bytesRead = in.read(ioBuf)) != -1){
				//Little-endian:
				float f = java.nio.ByteBuffer.wrap(ioBuf).order(java.nio.ByteOrder.LITTLE_ENDIAN).getFloat();
				return f;
			}
			else{ // file is completely read (not possible here?!)
				System.out.println("Something went wrong: no float more in file");
				return -1;
			}	
		}
		catch (IOException e) {
			System.out.println("Error while reading from file "+path);
			System.exit(1);
		}
		System.out.println("Something went wrong: float cannot be read");
		return -2;
	}


	// read one line = read "number" elements (int index + float value) and return a ByteLine object
	public ByteLine readElements(int number) {
		ByteLine bl = new ByteLine(number);
		
		for(int c=1; c<=number; c++){
			int i = this.readInt();
			float f = this.readFloat();
			bl.addElement(i, f);
			// test
			if(i==-1){
				System.out.println("Something went wrong: file is already completely read (no further elements");
			}
		}
		return bl;
	}
	
	

}
