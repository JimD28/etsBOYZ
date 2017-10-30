package gti310.tp2.audio;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

import gti310.tp2.io.FileSource;

public class SNRFilter implements AudioFilter{
	private FileSource originalFile;
	private FileSource comparedFile;
	private byte[] dataSizeOriginal;
	double SNRTotal;
	long upperDataTotal;
	long bottomDataTotal;
	ArrayList<Double> tableauSNR = new ArrayList<Double>();

	@Override
	public void process() {
		originalFile.pop(40);
		comparedFile.pop(44);
		dataSizeOriginal = originalFile.pop(4);
		int originalDataSizeInt = ByteBuffer.wrap(dataSizeOriginal).order(ByteOrder.LITTLE_ENDIAN).getInt();
		
		
		
		upperDataTotal = 0;
		bottomDataTotal = 0;
		for(int i = 0;i < originalDataSizeInt; i++)
		{
			int upperData = 0;
			int bottomData = 0;
			
			int originalSample =  originalFile.pop(1)[0] & 0xFF;
			int comparedSample =  comparedFile.pop(1)[0] & 0xFF;
			
			upperData = originalSample * originalSample;
			bottomData = (int) Math.pow((originalSample - comparedSample), 2);
			
			
			upperDataTotal += upperData;
			bottomDataTotal += bottomData;
		

		}
		
		SNRTotal = (double) 10*Math.log10(upperDataTotal/bottomDataTotal);
		tableauSNR.add(SNRTotal);
		//System.out.println();
		
		
		if(tableauSNR.size() == 8)
		{
			System.out.println(insertionSort(tableauSNR));
		}
		
	}
	
	
	
	
	
	public static ArrayList<Double> insertionSort(ArrayList<Double> tableauSNRUnsorted)
	{
		double temp;
		for(int i = 1; i < tableauSNRUnsorted.size();i++)
		{
			for(int j = i; j > 0; j--)
			{
				if(tableauSNRUnsorted.get(j) < tableauSNRUnsorted.get(j-1))
				{
					temp = tableauSNRUnsorted.get(j);
					tableauSNRUnsorted.set(j, tableauSNRUnsorted.get(j-1));
					tableauSNRUnsorted.set(j-1, temp);
				}
			}
		}
		return tableauSNRUnsorted;
	}
	
	public void storeFiles(String originalLocation, String comparedLocation)
	{
		try {
		originalFile = new FileSource(originalLocation);
		comparedFile = new FileSource(comparedLocation);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
