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
	

	// return one int value
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
				return -1;
			}	
		}
		catch (IOException e) {
			System.out.println("Error while reading from file "+path);
			System.exit(1);
		}
		return -2;
	}
	
	
	
	// return one float value
	public float readFloat(){

		//Allocate buffer with 4byte = 32bit = Integer.SIZE
		byte[] ioBuf = new byte[4];       
		int bytesRead;
		try {
			if ((bytesRead = in.read(ioBuf)) != -1){
				//Little-endian:
				float f = java.nio.ByteBuffer.wrap(ioBuf).order(java.nio.ByteOrder.LITTLE_ENDIAN).getFloat();
				return f;
			}
			else{ // file is completely read
				return -1;
			}	
		}
		catch (IOException e) {
			System.out.println("Error while reading from file "+path);
			System.exit(1);
		}
		return -2;
	}
	
	

}
