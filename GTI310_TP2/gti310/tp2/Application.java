package gti310.tp2;

import gti310.tp2.audio.ConcreteAudioFilter;
import gti310.tp2.audio.SNRFilter;


public class Application {

	/**
	 * Launch the application
	 * @param args This parameter is ignored
	 */
	public static void main(String args[]) {
		if(args[0].equals("StereoMono")){
			System.out.println("Stereo to mono project");
			ConcreteAudioFilter concreteFilter = new ConcreteAudioFilter(args[1], args[2]);
			concreteFilter.process();
		}
		else if(args[0].equals("SNR")) {
			System.out.println("SNR project");
			SNRFilter snrFilter = new SNRFilter();
			int i=2;
			while(i<10)
			{
				
				snrFilter.storeFiles(args[1], args[i]);
				snrFilter.process();
				i++;
			}
		}
	}
}
