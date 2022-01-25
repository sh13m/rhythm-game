package com.sh13m.rhythmgame.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.sh13m.rhythmgame.RhythmGame;
import com.sh13m.rhythmgame.Tools.TextUtil;

public class Help implements Screen {
    private final RhythmGame game;

    // fade transition
    private boolean FADE_IN;
    private boolean FADE_OUT;
    private float FADE_ALPHA;
    private boolean selected;
    private float timeSinceClick;

    public Help(RhythmGame game) {
        this.game = game;

        // fade transition setup
        FADE_IN = true;
        FADE_OUT = false;
        FADE_ALPHA = 1;
        timeSinceClick = 0;
        selected = false;
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
        drawHelpText();
        fade(delta);
        game.batch.end();
    }

    private void update(float delta) {
        handleInput();
        if (selected) {
            timeSinceClick += delta;
            if (timeSinceClick >= .5f) {
                game.setScreen(new Menu(game));
                dispose();
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !selected) {
            game.click.play(0.3f);
            FADE_OUT = true;
            selected = true;
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
        game.font.draw(game.batch, "keys on your keyboard. The <D> key would control the", 30,200);
        game.font.draw(game.batch, "leftmost receptor, <F> key the second leftmost receptor,", 30, 170);
        game.font.draw(game.batch, "and so on. If you are having a hard time passing levels, press", 30, 140);
        game.font.draw(game.batch, "<F1> in-game to enable no-fail mode (scores wont be saved).", 30, 110);
        game.font.getData().setScale(0.7f);
        game.font.setColor(0,1,1,1);
        game.font.draw(game.batch, "BACK", RhythmGame.V_WIDTH / 2f - TextUtil.getTextWidth(game.font, "BACK") / 2, 55);
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
