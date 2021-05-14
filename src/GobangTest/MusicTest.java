package GobangTest;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class MusicTest {
	private static Clip bgm;
	private static AudioInputStream ais;
	MusicTest()
	{
		try {
			bgm=AudioSystem.getClip();
			InputStream is=MusicTest.class.getClassLoader().getResourceAsStream("sound/飘向北方.wav");
			ais=AudioSystem.getAudioInputStream(is);
			bgm.open(ais);
		} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
	}
	public static void play_Music()
	{
		bgm.start();
		bgm.loop(Clip.LOOP_CONTINUOUSLY);
	}
	public static void Stop_music()
	{
		if(ais!=null)
			bgm.close();
	}
}
	
