package com.sh13m.rhythmgame.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.sh13m.rhythmgame.RhythmGame;
import com.sh13m.rhythmgame.Tools.TextUtil;

public class Reset implements Screen {
    // render
    private final RhythmGame game;

    // fade transition
    private boolean FADE_IN;
    private boolean FADE_OUT;
    private float FADE_ALPHA;
    private boolean selected;
    private float timeSinceClick;

    private int selection;

    public Reset(RhythmGame game) {
        this.game = game;

        // fade transition setup
        FADE_IN = true;
        FADE_OUT = false;
        FADE_ALPHA = 1;
        timeSinceClick = 0;
        selected = false;

        selection = 0;
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
        drawResetText();
        drawSelectionText();
        fade(delta);
        game.batch.end();
    }
    private void update(float delta) {
        handleInput();
        if (selected) {
            timeSinceClick += delta;
            if (timeSinceClick >= .5f) {
                handleSelection();
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !selected) {
            game.click.play(.3f);
            FADE_OUT = true;
            selected = true;
            selection = 0;
        }
        // Cycles between yes or no
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            game.click.play(0.3f);
            selection++;
            if (selection > 1) selection = 0;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            game.click.play(0.3f);
            selection--;
            if (selection < 0) selection = 1;
        }
        // runs selection
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && !selected) {
            game.click.play(0.3f);
            FADE_OUT = true;
            selected = true;
        }
    }

    private void drawResetText() {
        game.font.getData().setScale(0.7f);
        game.font.setColor(1,1,1,1);
        game.font.draw(game.batch, "all scores will be cleared", RhythmGame.V_WIDTH / 2f - TextUtil.getTextWidth(game.font, "All scores will be cleared") / 2, 380);
        game.font.draw(game.batch, "do you wish to proceed?", RhythmGame.V_WIDTH / 2f - TextUtil.getTextWidth(game.font, "do you wish to proceed?") / 2, 330);
    }

    private void drawSelectionText() {
        // highlights text if currently selected
        if (selection == 0) {
            game.font.setColor(0,1,1,1);
            game.font.draw(game.batch, "NO", RhythmGame.V_WIDTH / 2f - TextUtil.getTextWidth(game.font,"NO") / 2, 230);
        } else {
            game.font.setColor(1,1,1,1);
            game.font.draw(game.batch, "NO", RhythmGame.V_WIDTH / 2f - TextUtil.getTextWidth(game.font,"NO") / 2, 230);
        }
        if (selection == 1) {
            game.font.setColor(0,1,1,1);
            game.font.draw(game.batch, "YES", RhythmGame.V_WIDTH / 2f - TextUtil.getTextWidth(game.font,"YES") / 2, 170);
        } else {
            game.font.setColor(1,1,1,1);
            game.font.draw(game.batch, "YES", RhythmGame.V_WIDTH / 2f - TextUtil.getTextWidth(game.font,"YES") / 2, 170);
        }
    }

    private void handleSelection() {
        switch (selection) {
            case 0:
                game.setScreen(new Menu(game));
                dispose();
                break;
            case 1:
                clearData();
                game.setScreen(new ResetDone(game));
                dispose();
                break;
        }
    }

    private void clearData() {

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
