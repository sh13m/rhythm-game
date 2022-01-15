package com.sh13m.rhythmgame.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sh13m.rhythmgame.RhythmGame;
import com.sh13m.rhythmgame.Tools.TextUtil;

public class Help implements Screen {
    // render
    private final RhythmGame game;
    private final Viewport viewport;
    private final OrthographicCamera cam;

    public Help(RhythmGame game) {
        this.game = game;

        // set up cam
        cam = new OrthographicCamera();
        cam.setToOrtho(false, RhythmGame.V_WIDTH, RhythmGame.V_HEIGHT);
        viewport = new FitViewport(RhythmGame.V_WIDTH, RhythmGame.V_HEIGHT);
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
        drawHelpText();
        game.ltext.draw(game.batch, "HELP", 5, 20);
        game.batch.end();
    }

    private void update(float delta) {
        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.click.play(0.5f);
            game.setScreen(new Menu(game));
            dispose();
        }
    }

    private void drawHelpText() {
        game.font.getData().setScale(.6f);
        game.font.setColor(1,1,1,1);
        game.font.draw(game.batch, "RHYTHM GAME", 30, 455);
        game.font.getData().setScale(.35f);
        game.font.draw(game.batch, "This game has 2 types of notes: tap notes and hold notes.", 30, 410);
        game.font.draw(game.batch, "Arrow-shaped notes will fall towards receptors from the", 30, 380);
        game.font.draw(game.batch, "top of the screen. Once a note reaches a receptor, click", 30, 350);
        game.font.draw(game.batch, "or hold the corresponding receptor to 'hit' the note. There", 30, 320);
        game.font.draw(game.batch, "is a health bar that will decrease when notes aren't successfully", 30, 290);
        game.font.draw(game.batch, "hit. Hitting notes successfully will replenish the health bar.", 30, 260);
        game.font.draw(game.batch, "Notes can be hit either by using the <Arrow> or the <DFJK>", 30,230);
        game.font.draw(game.batch, "keys keys on your keyboard. The <D> key would control the", 30,200);
        game.font.draw(game.batch, "leftmost receptor, <F> key the second leftmost receptor,", 30, 170);
        game.font.draw(game.batch, "and so on.", 30, 140);
        game.font.getData().setScale(0.7f);
        game.font.setColor(0,1,1,1);
        game.font.draw(game.batch, "BACK", RhythmGame.V_WIDTH / 2 - TextUtil.getTextWidth(game.font, "BACK") / 2, 75);
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
