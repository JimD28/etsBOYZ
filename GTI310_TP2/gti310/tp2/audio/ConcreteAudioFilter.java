package gti310.tp2.audio;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import gti310.tp2.io.FileSink;
import gti310.tp2.io.FileSource;

public class ConcreteAudioFilter implements AudioFilter {

	//Creation des variables pour le fichier stereo et le fichier mono
	public FileSource stereo;
	public FileSink mono;
		
	byte[] convertIntTo4Bytes(long value) {
	    return new byte[] { 
	    	(byte)value,
	    	(byte)(value >> 8),
	    	(byte)(value >> 16),
	        (byte)(value >> 24)
	    };
	}
	byte[] convertIntTo2Bytes(long value) {
	    return new byte[] { 
	    	(byte)value,
	    	(byte)(value >> 8)  	
	    };
	}
	
	int convert4BytesToInt(byte[] bytes) {
	    return bytes[0]  | (bytes[1] & 0xFF) << 8 | (bytes[2] & 0xFF) << 16 | (bytes[3] & 0xFF << 24);
	}
	
	int convert2BytesToInt(byte[] bytes) {
	    return bytes[0]  | (bytes[1] & 0xFF) << 8;
	}
	
	public ConcreteAudioFilter(String stereoLocation, String monoLocation) {
		try {
			stereo = new FileSource(stereoLocation);
			mono = new FileSink(monoLocation);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void process() {

		//Creation 
		byte[] chunkID = stereo.pop(4);
		byte[] chunkSize = stereo.pop(4);
		byte[] format = stereo.pop(4);
		byte[] subChunk1Id = stereo.pop(4);
		byte[] subChunk1Size = stereo.pop(4);
		byte[] audioFormat = stereo.pop(2);
		byte[] numChannels = stereo.pop(2);
		byte[] sampleRate = stereo.pop(4);
		byte[] byteRate = stereo.pop(4);
		byte[] blockAlign = stereo.pop(2);
		byte[] bitsPerSample = stereo.pop(2);
		byte[] subChunk2Id = stereo.pop(4);
		byte[] subChunk2Size = stereo.pop(4);
		
		long fileDataSize = convert4BytesToInt(subChunk2Size);
		//Complexite 0(1)
		long monoFileDataSize = fileDataSize/2;

		boolean has2Channels = 1 == convert2BytesToInt(numChannels);
		
		if(has2Channels) {
			System.out.println("The file is not in stereo, a mono file won't be created");
		} else {
			System.out.println("The file is in stereo, a mono file will be created");

			// Modification de parties du header pour que �a soit en mono
			byte[] monoChunkSize = convertIntTo4Bytes((fileDataSize/2) + 36);			
			byte[] monoNumChannels = convertIntTo2Bytes(1);			
			byte[] monoByteRate = convertIntTo4Bytes(convert4BytesToInt(sampleRate) * (convert2BytesToInt(bitsPerSample)/8));
			byte[] monoBlockAlign = convertIntTo2Bytes((convert2BytesToInt(bitsPerSample)/8));
			byte[] monoSubChunk2Size = convertIntTo4Bytes(fileDataSize/2);
			
			//Creation du header du fichier mono
			mono.push(chunkID);
			mono.push(monoChunkSize); //Nouveau ChunkSize du fichier mono
			mono.push(format);
			mono.push(subChunk1Id);
			mono.push(subChunk1Size);
			mono.push(audioFormat);
			mono.push(monoNumChannels); //Nouveau NumChannels du fichier mono
			mono.push(sampleRate);
			mono.push(monoByteRate); //Nouveau ByteRate du fichier mono
			mono.push(monoBlockAlign); //Nouveau BlockAlign du fichier mono
			mono.push(bitsPerSample);
			mono.push(subChunk2Id);
			mono.push(monoSubChunk2Size); //Nouveau SubChunk2Size du fichier mono
						
			/* Diviser le data en deux pour passer de stereo a mono.
			 * Sauvegarde 2 bytes dans left side.
			 * Sauvegarde 2 bytes dans rightside.
			 * Calcul de la moyenne des 2 valeurs et la sauvegarder dans average.
			 * Mettre cette moyenne des 2 valeurs dans 2 bytes et le mettre dans le mono file.
			 * Recommencer jusqu'a remplir le file mono, donc la moitie de la taille du stereo file.
			*/
			// Complexite O(N)
			for (int i = 0; i < (monoFileDataSize) - 1; i= i+2) {
				long leftSide = ByteBuffer.wrap(stereo.pop(2)).order(ByteOrder.LITTLE_ENDIAN).getShort();
				long rightSide = ByteBuffer.wrap(stereo.pop(2)).order(ByteOrder.LITTLE_ENDIAN).getShort();
				long average = (short) (( leftSide + rightSide)/2);
				mono.push(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) average).array());
			}
		}
		// Fermer le file mono et le file stereo
		mono.close();
		stereo.close();
	}

}
