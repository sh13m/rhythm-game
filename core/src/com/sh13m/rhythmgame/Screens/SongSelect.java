package com.sh13m.rhythmgame.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL30;
import com.sh13m.rhythmgame.RhythmGame;
import com.sh13m.rhythmgame.Tools.TextUtil;

public class SongSelect implements Screen {
    private final RhythmGame game;

    private int selection;
    private final String[] scores;
    private final String[] names;

    public SongSelect(RhythmGame game, int level) {
        this.game = game;
        if (!this.game.menuTheme.isPlaying()) this.game.menuTheme.play();


        FileHandle scorefile = Gdx.files.internal("scores.txt");
        scores = scorefile.readString().split("\\r?\\n");
        FileHandle namefile = Gdx.files.internal("names.txt");
        names = namefile.readString().split("\\r?\\n");

        selection = level;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update();

        Gdx.gl.glClearColor(0.05f,0.05f,0.05f,1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(game.cam.combined);
        game.batch.begin();
        drawSongs();
        game.smalltext.draw(game.batch, "<UP/DOWN> TO CHANGE SONGS", RhythmGame.V_WIDTH/2f - TextUtil.getTextWidth(game.smalltext, "<UP/DOWN> TO CHANGE SONGS")/2f, 20);
        game.batch.end();
    }

    private void update() {
        handleInput();
    }

    private void handleInput() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selection++;
            if (selection > 10) selection = 1;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selection--;
            if (selection < 1) selection = 10;
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
        game.font.getData().setScale(0.8f);
        game.font.draw(game.batch, selection + ". " + names[selection-1], RhythmGame.V_WIDTH/2f - TextUtil.getTextWidth(game.font, selection + ". " + names[selection-1])/2f, 300);
        game.font.getData().setScale(0.6f);
        game.font.draw(game.batch, "HIGHSCORE: " + scores[selection-1], RhythmGame.V_WIDTH/2f - TextUtil.getTextWidth(game.font, "HIGHSCORE: " + scores[selection-1])/2f, 250);

    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height);
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
