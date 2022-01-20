package com.sh13m.rhythmgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.sh13m.rhythmgame.Screens.Menu;

public class RhythmGame extends Game {
	private Graphics.DisplayMode displayMode;
	private boolean isFullScreen;
	public static final Integer V_WIDTH = 640;
	public static final Integer V_HEIGHT = 480;

	public ShapeRenderer shapeRenderer;
	public SpriteBatch batch;
	public BitmapFont font;
	public BitmapFont smalltext;

	public Music menuTheme;
	public Sound click;

	@Override
	public void create() {
		displayMode = Gdx.graphics.getDisplayMode();
		isFullScreen = false;

		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);

		font = new BitmapFont(
				Gdx.files.internal("Graphics/Bebas_Neue.fnt"),
				Gdx.files.internal("Graphics/Bebas_Neue_0.png"),
				false);
		smalltext = new BitmapFont();
		smalltext.getData().setScale(0.7f);
		click = Gdx.audio.newSound(Gdx.files.internal("SFX/click.ogg"));
		menuTheme = Gdx.audio.newMusic(Gdx.files.internal("SFX/Music title (loop).ogg"));
		menuTheme.setVolume(0.2f);
		menuTheme.setLooping(true);
		menuTheme.play();

		setScreen(new Menu(this));
	}

	@Override
	public void render() {
		handleInput();
		super.render();
	}

	private void handleInput() {
		if ((Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT)) && Gdx.input.isKeyPressed(Input.Keys.F4)) {
			Gdx.app.exit();
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
			if (isFullScreen) {
				Gdx.graphics.setUndecorated(false);
				Gdx.graphics.setWindowedMode(V_WIDTH, V_HEIGHT);
				isFullScreen = false;
			} else {
				Gdx.graphics.setUndecorated(true);
				Gdx.graphics.setWindowedMode(displayMode.width, displayMode.height);
				isFullScreen = true;
			}
		}
	}

	@Override
	public void dispose() {
		batch.dispose();
		shapeRenderer.dispose();
		font.dispose();
		smalltext.dispose();
		menuTheme.dispose();
		click.dispose();
	}
}
