package gti310.tp2.audio;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;



import gti310.tp2.io.FileSource;

public class SNRFilter implements AudioFilter{
	private String comparedLocationString;
	private FileSource originalFile;
	private FileSource comparedFile;
	private byte[] dataSizeOriginal;
	double SNRTotal;
	long upperDataTotal;
	long bottomDataTotal;
	private String[][] tableauSNR = new String[2][8];

	@Override
	public void process(int compteur) {
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
		tableauSNR[0][compteur - 2] = comparedLocationString;
		tableauSNR[1][compteur - 2] = Double.toString(SNRTotal);
		
		//System.out.println();
		
		
		if(tableauSNR[1][7] != null)
		{
			insertionSort(tableauSNR);
			for (int j= 0; j < tableauSNR[0].length; j++) {

				System.out.println(tableauSNR[0][j]+ ": "+tableauSNR[1][j]);

			}
		}
		
	}
	
	
	
	
	
	public static void insertionSort(String[][] tableauSNRUnsorted)
	{
		double tempNumber;
		String tempKey;
		for(int i = 1; i < tableauSNRUnsorted[1].length;i++)
		{
			for(int j = i; j < tableauSNRUnsorted[1].length && j > 0; j--)
			{
				if(Double.parseDouble(tableauSNRUnsorted[1][j]) < Double.parseDouble(tableauSNRUnsorted[1][j-1]))
				{
					tempNumber = Double.parseDouble(tableauSNRUnsorted[1][j]);
					tableauSNRUnsorted[1][j] = tableauSNRUnsorted[1][j-1];
					tableauSNRUnsorted[1][j-1] = "" + tempNumber;
					
					tempKey = tableauSNRUnsorted[0][j];
					tableauSNRUnsorted[0][j] = tableauSNRUnsorted[0][j-1];
					tableauSNRUnsorted[0][j-1] = tempKey;

					
				}
			}
		}

	}
	
	public void storeFiles(String originalLocation, String comparedLocation)
	{
		try {
	    comparedLocationString = comparedLocation.substring(comparedLocation.indexOf("App2"));
		originalFile = new FileSource(originalLocation);
		comparedFile = new FileSource(comparedLocation);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}





	@Override
	public void process() {
		// TODO Auto-generated method stub
		
	}

}
