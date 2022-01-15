package com.sh13m.rhythmgame.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.sh13m.rhythmgame.RhythmGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Rhythm Game";
		config.width = 640;
		config.height = 480;
		config.foregroundFPS = 240;
		config.backgroundFPS = 30;
		config.addIcon("iconL.png", Files.FileType.Internal); // Mac
		config.addIcon("iconM.png", Files.FileType.Internal); // Windows + Linux
		config.addIcon("iconS.png", Files.FileType.Internal); // Windows
		new LwjglApplication(new RhythmGame(), config);
	}
}
