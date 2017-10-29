package gti310.tp2;

import gti310.tp2.audio.ConcreteAudioFilter;

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
	}
}
