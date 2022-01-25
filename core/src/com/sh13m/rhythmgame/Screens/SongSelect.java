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

    // fade transition
    private boolean FADE_IN;
    private boolean FADE_OUT;
    private float FADE_ALPHA;
    private boolean selected;
    private boolean goback;
    private float timeSinceClick;

    private int selection;
    private final String[] scores;
    private final String[] names;

    public SongSelect(RhythmGame game, int level) {
        this.game = game;
        if (!this.game.menuTheme.isPlaying()) this.game.menuTheme.play();

        // fade transition setup
        FADE_IN = true;
        FADE_OUT = false;
        FADE_ALPHA = 1;
        timeSinceClick = 0;
        selected = false;
        goback = false;


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
        update(delta);

        Gdx.gl.glClearColor(0.05f,0.05f,0.05f,1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(game.cam.combined);
        game.batch.begin();
        drawSongs();
        game.smalltext.draw(game.batch, "<UP/DOWN> TO CHANGE SONGS <ENTER> TO PLAY", RhythmGame.V_WIDTH/2f - TextUtil.getTextWidth(game.smalltext, "<UP/DOWN> TO CHANGE SONGS <ENTER> TO PLAY")/2f, 20);
        fade(delta);
        game.batch.end();
    }

    private void update(float delta) {
        handleInput();
        if (selected) {
            timeSinceClick += delta;
            if (timeSinceClick >= .5f) {
                if (goback) {
                    game.setScreen(new Menu(game));
                    dispose();
                } else {
                    game.setScreen(new Gameplay(game, selection));
                    game.menuTheme.stop();
                    dispose();
                }
            }
        }
    }

    private void fade(float delta) {
        if (FADE_OUT) {
            FADE_ALPHA += delta*10;
            if (FADE_ALPHA >= 1) {
                FADE_ALPHA = 1;
            }
        } else if (FADE_IN) {
            FADE_ALPHA -= delta*2;
            if (FADE_ALPHA <= 0) {
                FADE_ALPHA = 0;
                FADE_IN = false;
            }
        }
        game.batch.setColor(1,1,1, FADE_ALPHA);
        game.batch.draw(game.fade, 0, 0, RhythmGame.V_WIDTH, RhythmGame.V_HEIGHT);
        game.batch.setColor(1,1,1,1);
    }

    private void handleInput() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selection++;
            game.click.play(.3f);
            if (selection > 10) selection = 1;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selection--;
            game.click.play(.3f);
            if (selection < 1) selection = 10;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !selected) {
            game.click.play(.3f);
            FADE_OUT = true;
            selected = true;
            goback = true;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && !selected) {
            game.click.play(.3f);
            FADE_OUT = true;
            selected = true;
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
