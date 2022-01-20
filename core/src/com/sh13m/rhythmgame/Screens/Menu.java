package com.sh13m.rhythmgame.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sh13m.rhythmgame.RhythmGame;
import com.sh13m.rhythmgame.Tools.TextUtil;

public class Menu implements Screen {
    // render
    private final RhythmGame game;
    private final Viewport viewport;
    private final OrthographicCamera cam;
    private final Pixmap pm;
    private final Cursor cursor;

    // assets
    private final Texture logo;
    private float timeSinceClick;
    private boolean selected;

    private int selection;

    public Menu(RhythmGame game) {
        this.game = game;

        // set up assets
        logo = new Texture("Graphics/logo.png");
        timeSinceClick = 0;
        selected = false;

        // set up cam
        cam = new OrthographicCamera();
        cam.setToOrtho(false, RhythmGame.V_WIDTH, RhythmGame.V_HEIGHT);
        viewport = new FitViewport(RhythmGame.V_WIDTH, RhythmGame.V_HEIGHT);

        // sets cursor invisible
        pm = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        cursor = Gdx.graphics.newCursor(pm,0,0);
        Gdx.graphics.setCursor(cursor);

        selection = 0;
    }

    @Override
    public void show() {
        game.font.getData().setScale(0.7f);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.1f,0.1f,0.1f,1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(cam.combined);

        game.batch.begin();
        game.batch.draw(logo, RhythmGame.V_WIDTH / 2 - logo.getWidth() / 2, 300);
        game.smalltext.draw(game.batch, "*USE <UP/DOWN> ARROW KEYS TO NAVIGATE AND <ENTER> TO SELECT*", RhythmGame.V_WIDTH / 2 - TextUtil.getTextWidth(game.smalltext,"*USE <UP/DOWN> ARROW KEYS TO NAVIGATE AND <ENTER> TO SELECT*" ) / 2, 20);
        drawSelectionsText();
        game.batch.end();
    }

    private void update(float delta) {
        handleInput();
        if (selected) {
            timeSinceClick += delta;
            if (timeSinceClick > 0.3f) handleSelection();
        }
    }

    private void handleInput() {
        // cycles through selection
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) && !selected) {
            game.click.play(0.3f);
            selection++;
            if (selection > 3) selection = 0;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && !selected) {
            game.click.play(0.3f);
            selection--;
            if (selection < 0) selection = 3;
        }
        // runs selection
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && !selected) {
            game.click.play(0.3f);
            selected = true;
        }
    }

    private void drawSelectionsText() {
        // highlights text if currently selected
        if (selection == 0) {
            game.font.setColor(0,1,1,1);
            game.font.draw(game.batch, "PLAY", RhythmGame.V_WIDTH / 2 - TextUtil.getTextWidth(game.font,"PLAY") / 2, 280);
        } else {
            game.font.setColor(1,1,1,1);
            game.font.draw(game.batch, "PLAY", RhythmGame.V_WIDTH / 2 - TextUtil.getTextWidth(game.font,"PLAY") / 2, 280);
        }
        if (selection == 1) {
            game.font.setColor(0,1,1,1);
            game.font.draw(game.batch, "HELP", RhythmGame.V_WIDTH / 2 - TextUtil.getTextWidth(game.font,"HELP") / 2, 240);
        } else {
            game.font.setColor(1,1,1,1);
            game.font.draw(game.batch, "HELP", RhythmGame.V_WIDTH / 2 - TextUtil.getTextWidth(game.font,"HELP") / 2, 240);
        }
        if (selection == 2) {
            game.font.setColor(0,1,1,1);
            game.font.draw(game.batch, "RESET", RhythmGame.V_WIDTH / 2 - TextUtil.getTextWidth(game.font,"RESET") / 2, 200);
        } else {
            game.font.setColor(1,1,1,1);
            game.font.draw(game.batch, "RESET", RhythmGame.V_WIDTH / 2 - TextUtil.getTextWidth(game.font,"RESET") / 2, 200);
        }
        if (selection == 3) {
            game.font.setColor(0,1,1,1);
            game.font.draw(game.batch, "QUIT", RhythmGame.V_WIDTH / 2 - TextUtil.getTextWidth(game.font,"QUIT") / 2, 160);
        } else {
            game.font.setColor(1,1,1,1);
            game.font.draw(game.batch, "QUIT", RhythmGame.V_WIDTH / 2 - TextUtil.getTextWidth(game.font,"QUIT") / 2, 160);
        }
    }

    private void handleSelection() {
        switch (selection) {
            case 0:
                game.setScreen(new SongSelect(game));
                dispose();
                break;
            case 1:
                game.setScreen(new Help(game));
                dispose();
                break;
            case 2:
                game.setScreen(new Reset(game));
                dispose();
                break;
            case 3:
                dispose();
                Gdx.app.exit();
                break;
        }
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
        logo.dispose();
        pm.dispose();
        cursor.dispose();
    }
}
