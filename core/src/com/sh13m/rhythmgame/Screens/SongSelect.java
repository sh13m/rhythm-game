package com.sh13m.rhythmgame.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sh13m.rhythmgame.RhythmGame;

public class SongSelect implements Screen {
    private final RhythmGame game;
    private final Viewport viewport;
    private final OrthographicCamera cam;

    private int selection;

    public SongSelect(RhythmGame game) {
        this.game = game;

        cam = new OrthographicCamera();
        cam.setToOrtho(false, RhythmGame.V_WIDTH, RhythmGame.V_HEIGHT);
        viewport = new FitViewport(RhythmGame.V_WIDTH, RhythmGame.V_HEIGHT);

        if (!this.game.menuTheme.isPlaying()) this.game.menuTheme.play();

        selection = 1;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.1f,0.1f,0.1f,1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(cam.combined);
        game.batch.begin();
        game.smalltext.draw(game.batch, "SONG SELECT", 5, 20);
        game.font.draw(game.batch, String.valueOf(selection), 40, 300);
        game.batch.end();
    }

    private void update(float delta) {
        handleInput();
    }

    private void handleInput() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selection++;
            if (selection > 9) selection = 1;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selection--;
            if (selection < 1) selection = 9;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new Menu(game));
            dispose();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new Gameplay(game, selection));
            game.menuTheme.stop();
            dispose();
        }
    }

    private void drawSongs() {

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
