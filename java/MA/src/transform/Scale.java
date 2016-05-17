package transform;

import io.FileInputReader;
import io.FileOutputWriter;

public class Scale {

	// for columns
	private static double[] mean;
	private static double[] sd;
	private static int count;
	
	// for columns
	private static void initialize(int length) {
		mean = new double[length];
		sd = new double[length];
		count = 0;
	}

	
	// run method for columns
	public static void scaleColumns(String pathIn, String pathOut) {
		FileInputReader reader = new FileInputReader(pathIn);
		
		String line = reader.read(); //ignore header
		initialize(line.split("\t").length-2); // number of columns to scale
	
		while((line=reader.read())!=null){
			String[] l = line.split("\t");
			count++;
			for (int pos=2; pos<l.length; pos++){
				double n = Double.parseDouble(l[pos]);
				mean[pos-2] += n;
				sd[pos-2] += n*n;
			}
		}
		reader.closer();
		
		//calc actual means and sd:
		for (int pos=0; pos<mean.length; pos++){
			mean[pos] /= count;
			sd[pos] = Math.sqrt((sd[pos]/count) - (mean[pos]*mean[pos]));
		}
		
		//write output
		writeOutCol(pathIn, pathOut);
		
	}
	
	//write output for scale columns
	private static void writeOutCol(String pathIn, String pathOut) {
		FileInputReader reader = new FileInputReader(pathIn);
		FileOutputWriter writer = new FileOutputWriter(pathOut);
		
		String line = reader.read(); // header
		writer.write(line+"\n");
		
		while((line=reader.read())!=null){
			String[] l = line.split("\t");
			writer.write(l[0]+"\t"+l[1]);
			for (int pos=2; pos<l.length; pos++){
				double n = Double.parseDouble(l[pos]);
				double z = (n-mean[pos-2])/sd[pos-2];
				if(Double.isNaN(z)){
					z=0.0;
				}
				writer.write("\t"+z);
			}
			writer.write("\n");
		}
		reader.closer();
		writer.closer();
		
	}
	
	
	
	

	// run method for rows
	public static void scaleRows(String pathIn, String pathOut) {
		FileInputReader reader = new FileInputReader(pathIn);
		FileOutputWriter writer = new FileOutputWriter(pathOut);
		
		String line = reader.read(); // header
		writer.write(line+"\n");
		int c = line.split("\t").length-2;
		
		while((line=reader.read())!=null){
			String[] l = line.split("\t");
			writer.write(l[0]+"\t"+l[1]);
			
			double m = 0;
			double s = 0;
			
			for (int pos=2; pos<l.length; pos++){
				double n = Double.parseDouble(l[pos]);
				m += n;
				s += n*n;
			}
			
			//calc actual means and sd:
			m /= c;
			s = Math.sqrt((s/c) - (m*m));
			
			for (int pos=2; pos<l.length; pos++){
				double n = Double.parseDouble(l[pos]);
				double z = (n-m)/s;
				if(Double.isNaN(z)){
					z=0.0;
				}
				writer.write("\t"+z);
			}
			writer.write("\n");
		}
		reader.closer();
		writer.closer();
		
	}


}
