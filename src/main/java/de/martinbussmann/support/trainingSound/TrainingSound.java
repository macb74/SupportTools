package de.martinbussmann.support.trainingSound;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.StopWatch;

public class TrainingSound {
	
    private static String SOUNDFILE;
    private static int LOOPS;
    private static int START;
    private static int SCALE;
	
	public static void main( String[] args )
    {
    	Map<String, String> arguments = new HashMap<String, String>();
		arguments.put("LOOPS", "2");
		arguments.put("START", "10");
		arguments.put("SCALE", "3");
		arguments.put("SOUNDFILE", "start-beeps.wav");
    	new TrainingSound(arguments);
    }
	
    public TrainingSound(Map<String, String> arguments) {
    	SOUNDFILE = arguments.get("SOUNDFILE");
    	LOOPS = Integer.valueOf(arguments.get("LOOPS"));
    	START = Integer.valueOf(arguments.get("START"));
    	SCALE = Integer.valueOf(arguments.get("SCALE"));
		
    	boolean run = true;
		
		StopWatch stopWatch = new StopWatch();
		long lastPlay   = 0;
		long timeToPlay = START;
		long timeInSec  = 0;
		int loop        = 0;
		
		playSound();
		
		stopWatch.start();
        while(run) {
        	
        	timeInSec = stopWatch.getTime() / 1000;
        	
        	if((lastPlay != timeInSec) && timeInSec == timeToPlay) {
            	System.out.println("Time: " + timeInSec + " Len: " + START + " Scale: " + SCALE + " Loops: " + LOOPS );
        		playSound();
        		loop++;
        		if(loop == LOOPS) {
        			loop = 0;
        			START = START - SCALE;
        		}
        		
        		timeToPlay = timeToPlay + START;
        	}
        	
        	if (START <= 3) {
        		run = false;
        	}
        }
	}
	
	public void playSound() {
		new PlayWave(SOUNDFILE).start();	
	}
}
