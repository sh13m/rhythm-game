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

    private int selection;

    public Reset(RhythmGame game) {
        this.game = game;

        selection = 0;
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
        drawResetText();
        drawSelectionText();
        game.smalltext.draw(game.batch, "RESET SCORES", 5, 20);
        game.batch.end();
    }
    private void update() {
        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new Menu(game));
            dispose();
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.click.play(0.3f);
            handleSelection();
        }
    }

    private void drawResetText() {
        game.font.getData().setScale(0.7f);
        game.font.setColor(1,1,1,1);
        game.font.draw(game.batch, "all game data will be cleared", RhythmGame.V_WIDTH / 2f - TextUtil.getTextWidth(game.font, "All game data will be cleared") / 2, 380);
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
                game.setScreen(new Menu(game));
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
