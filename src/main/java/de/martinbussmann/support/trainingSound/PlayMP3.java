package de.martinbussmann.support.trainingSound;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class PlayMP3 implements Runnable {

	private Player mp3Player;
	private Thread playerThread;
	
	public PlayMP3(String sfile){
    	FileInputStream fi;
		try {
			File f = new File(sfile);
			if (f.isFile() && f.canRead()) {
				fi = new FileInputStream(sfile);
				mp3Player = new Player(fi);
			    playerThread = new Thread(this);
			    playerThread.start();
			}
		} catch (JavaLayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Override
	public void run(){

		try {
			mp3Player.play();
		} catch (JavaLayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public boolean getStatus() {
		return playerThread.isAlive();
	}

}