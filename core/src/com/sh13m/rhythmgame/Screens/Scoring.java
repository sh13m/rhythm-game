package com.sh13m.rhythmgame.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.sh13m.rhythmgame.RhythmGame;

public class Scoring implements Screen {
    private RhythmGame game;
    private int MAX_COMBO;
    public float ACCURACY;
    public long SCORE;
    private int MAX_COUNT;
    private int PERFECT_COUNT;
    private int GREAT_COUNT;
    private int GOOD_COUNT;
    private int BAD_COUNT;
    private int MISS_COUNT;

    public Scoring (RhythmGame game, int MAX_COMBO, float ACCURACY, long SCORE,
                    int MAX_COUNT, int PERFECT_COUNT, int GREAT_COUNT,
                    int GOOD_COUNT, int BAD_COUNT, int MISS_COUNT) {
        this.game = game;
        this.MAX_COMBO = MAX_COMBO;
        this.ACCURACY = ACCURACY;
        this.SCORE = SCORE;
        this.MAX_COUNT = MAX_COUNT;
        this.PERFECT_COUNT = PERFECT_COUNT;
        this.GREAT_COUNT = GREAT_COUNT;
        this.GOOD_COUNT = GOOD_COUNT;
        this.BAD_COUNT = BAD_COUNT;
        this.MISS_COUNT = MISS_COUNT;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update();

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(game.cam.combined);
        game.batch.begin();
        game.batch.end();
    }

    private void update() {
        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new SongSelect(game));
            dispose();
        }
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
