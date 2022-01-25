package com.sh13m.rhythmgame.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.utils.Timer;
import com.sh13m.rhythmgame.RhythmGame;
import com.sh13m.rhythmgame.Tools.TextUtil;

public class ResetDone implements Screen {
    private RhythmGame game;
    private boolean GO_BACK;

    public ResetDone(RhythmGame game) {
        this.game = game;
        GO_BACK = false;
        Timer timer = Timer.instance();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                GO_BACK = true;
            }
        }, 3);

        FileHandle scorefile = Gdx.files.local("scores.txt");
        String[] temp = scorefile.readString().split("\\r?\\n");
        scorefile.write(false);
        for (int i = 0; i < temp.length; ++i) {
            scorefile.writeString(String.valueOf(0), true);
            if (i != temp.length-1) scorefile.writeString("\n", true);
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if (GO_BACK) {
            game.setScreen(new Menu(game));
            dispose();
        }

        Gdx.gl.glClearColor(0.05f,0.05f,0.05f,1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(game.cam.combined);
        game.batch.begin();
        game.font.setColor(1,1,1,1);
        game.font.draw(game.batch, "SCORES HAVE BEEN CLEARED", RhythmGame.V_WIDTH/2f - TextUtil.getTextWidth(game.font, "SCORES HAVE BEEN CLEARED")/2f, 300);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {

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
