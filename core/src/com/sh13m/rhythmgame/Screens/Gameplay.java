package com.sh13m.rhythmgame.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sh13m.rhythmgame.Objects.Bar;
import com.sh13m.rhythmgame.Objects.Head;
import com.sh13m.rhythmgame.Objects.TapNote;
import com.sh13m.rhythmgame.RhythmGame;
import com.sh13m.rhythmgame.Tools.NoteLogic;
import com.sh13m.rhythmgame.Tools.SongInput;
import com.sh13m.rhythmgame.Tools.SongReader;
import com.sh13m.rhythmgame.Tools.TextUtil;

public class Gameplay implements Screen {
    // gameplay settings
    public static final int R_HEIGHT = 30;
    private static final int COMBO_HEIGHT = 300;
    private static final int J_HEIGHT = 240;
    public static final int SCROLL_SPEED = 900;
    private static final float GLOBAL_DELAY = 3;
    private static final float SCROLL_OFFSET = (480f - R_HEIGHT) / SCROLL_SPEED;
    public static float COL1_X = RhythmGame.V_WIDTH / 2f - 128;
    public static float COL2_X = RhythmGame.V_WIDTH / 2f - 64;
    public static float COL3_X = RhythmGame.V_WIDTH / 2f;
    public static float COL4_X = RhythmGame.V_WIDTH / 2f + 64;

    // render
    private final RhythmGame game;
    private final Viewport viewport;
    private final OrthographicCamera cam;
    private final Pixmap pm;
    private final Cursor cursor;

    // textures
    private final Texture note_img;
    private final Texture hold_bar_img;
    private final TextureRegion receptors_img;
    private final TextureRegion note_1;
    private final TextureRegion note_2;
    private final TextureRegion note_3;
    private final TextureRegion note_4;
    private final TextureRegion note_clicked_1;
    private final TextureRegion note_clicked_2;
    private final TextureRegion note_clicked_3;
    private final TextureRegion note_clicked_4;
    private final Texture stage;
    private final Texture bg;
    private final Texture Judgement;
    private final TextureRegion MISS;
    private final TextureRegion BAD;
    private final TextureRegion GOOD;
    private final TextureRegion GREAT;
    private final TextureRegion PERFECT;
    private final TextureRegion MAX;

    // rectangles
    private final Rectangle receptor1;
    private final Rectangle receptor2;
    private final Rectangle receptor3;
    private final Rectangle receptor4;

    // song data
    private final Music music;
    private final SongReader sr;

    // temp other
    private final Timer delayedMusicStart;
    private final Timer delayedNoteStart;
    private final NoteLogic nl;

    public Gameplay(RhythmGame game, int level) {
        this.game = game;
        // sets the cursor invisible
        pm = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        cursor = Gdx.graphics.newCursor(pm,0,0);
        Gdx.graphics.setCursor(cursor);

        // set up camera
        cam = new OrthographicCamera();
        cam.setToOrtho(false, RhythmGame.V_WIDTH, RhythmGame.V_HEIGHT);
        viewport = new FitViewport(RhythmGame.V_WIDTH, RhythmGame.V_HEIGHT);

        // set up textures
        note_img = new Texture(Gdx.files.internal("Graphics/notes.png"));
        hold_bar_img = new Texture(Gdx.files.internal("Graphics/hold.png"));
        receptors_img = new TextureRegion(note_img,0,64,256,64);
        note_1 = new TextureRegion(note_img, 0,0,64,64);
        note_2 = new TextureRegion(note_img, 64,0,64,64);
        note_3 = new TextureRegion(note_img, 128,0,64,64);
        note_4 = new TextureRegion(note_img,192,0,64,64);
        note_clicked_1 = new TextureRegion(note_img, 0,128,64,64);
        note_clicked_2 = new TextureRegion(note_img, 64,128,64,64);
        note_clicked_3 = new TextureRegion(note_img, 128,128,64,64);
        note_clicked_4 = new TextureRegion(note_img,192,128,64,64);
        stage = new Texture(Gdx.files.internal("Graphics/stage.png"));
        Judgement = new Texture(Gdx.files.internal("Graphics/Modern Nore 1x6.png"));
        MISS = new TextureRegion(Judgement,0,170,256,34);
        BAD = new TextureRegion(Judgement,0,136,256,34);
        GOOD = new TextureRegion(Judgement,0,102,256,34);
        GREAT = new TextureRegion(Judgement,0,68,256,34);
        PERFECT = new TextureRegion(Judgement,0,34,256,34);
        MAX = new TextureRegion(Judgement,0,0,256,34);


        // set up rectangles
        receptor1 = new Rectangle(COL1_X, R_HEIGHT,64,64);
        receptor2 = new Rectangle(COL2_X, R_HEIGHT,64,64);
        receptor3 = new Rectangle(COL3_X, R_HEIGHT,64,64);
        receptor4 = new Rectangle(COL4_X, R_HEIGHT,64,64);

        // set up song data
        nl = new NoteLogic();
        sr = new SongReader(level);
        music = Gdx.audio.newMusic(Gdx.files.internal("Songs/" + level + "/" + sr.getSongFileName()));
        bg = new Texture(Gdx.files.internal("Songs/" + level + "/bg.jpg"));
        music.setVolume(0.2f);
        float musicBuffer = -1;
        if (sr.offset < musicBuffer) musicBuffer = sr.offset*-1;
        else musicBuffer = 0;
        delayedMusicStart = new Timer();
        delayedMusicStart.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                music.play();
            }
        }, GLOBAL_DELAY + musicBuffer + sr.offset);
        delayedNoteStart = new Timer();
        delayedNoteStart.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                sr.parseMeasure();
            }
        }, GLOBAL_DELAY + musicBuffer - SCROLL_OFFSET);
    }

    @Override
    public void show() {
        game.font.getData().setScale(0.5f);
    }

    @Override
    public void render(float delta) {
        update();

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(cam.combined);
        game.batch.begin();
        game.batch.setColor(1,1,1,0.2f);
        game.batch.draw(bg, RhythmGame.V_WIDTH/2f - (480f*bg.getWidth()/bg.getHeight())/2f,0,480f*bg.getWidth()/bg.getHeight(),480);
        game.batch.setColor(1,1,1,1);
        game.batch.draw(stage, RhythmGame.V_WIDTH / 2f - stage.getWidth() / 2f, 0);
        game.batch.draw(receptors_img, RhythmGame.V_WIDTH / 2f - receptors_img.getRegionWidth() / 2f , R_HEIGHT);
        drawInput();
        drawNotes();
        drawJudgement();
        game.font.draw(game.batch, String.valueOf(nl.COMBO), RhythmGame.V_WIDTH / 2f - TextUtil.getTextWidth(game.font, String.valueOf(nl.COMBO)) / 2, COMBO_HEIGHT);
        game.smalltext.draw(game.batch, "GAMEPLAY", 5, 20);
        game.batch.end();
    }

    private void update() {
        handleInput();
        nl.updateNotes(sr, receptor1, receptor2, receptor3, receptor4);

    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new SongSelect(game));
            dispose();
        }

    }

    private void drawInput() {
        // lights up receptors if keys are pressed
        if (SongInput.receptor1Pressed()) {
            game.batch.draw(note_clicked_1, COL1_X, R_HEIGHT);
        }
        if (SongInput.receptor2Pressed()) {
            game.batch.draw(note_clicked_2, COL2_X, R_HEIGHT);
        }
        if (SongInput.receptor3Pressed()) {
            game.batch.draw(note_clicked_3, COL3_X, R_HEIGHT);
        }
        if (SongInput.receptor4Pressed()) {
            game.batch.draw(note_clicked_4, COL4_X, R_HEIGHT);
        }
    }

    private void drawNotes() {
        // tap notes
        for (TapNote note : sr.activeTapNotes) {
            if (note.getX() == COL1_X) game.batch.draw(note_1, note.getX(), note.getY());
            else if (note.getX() == COL2_X) game.batch.draw(note_2, note.getX(), note.getY());
            else if (note.getX() == COL3_X) game.batch.draw(note_3, note.getX(), note.getY());
            else if (note.getX() == COL4_X) game.batch.draw(note_4, note.getX(), note.getY());
        }
        // bars
        for (Bar bar : sr.activeBars) {
            game.batch.draw(hold_bar_img, bar.getX(), bar.getY(), bar.getWidth(), bar.getHeight());
        }
        // hold heads
        for (Head head : sr.activeHeads) {
            if (head.getX() == COL1_X) game.batch.draw(note_1, head.getX(), head.getY());
            else if (head.getX() == COL2_X) game.batch.draw(note_2, head.getX(), head.getY());
            else if (head.getX() == COL3_X) game.batch.draw(note_3, head.getX(), head.getY());
            else if (head.getX() == COL4_X) game.batch.draw(note_4, head.getX(), head.getY());
        }
    }

    private void drawJudgement() {
        switch (nl.JUDGEMENT) {
            case "MISS":
                game.batch.draw(MISS, RhythmGame.V_WIDTH / 2f - Judgement.getWidth() / 2f * .7f, J_HEIGHT, MISS.getRegionWidth() * .7f, MISS.getRegionHeight() * .7f);
                break;
            case "BAD":
                game.batch.draw(BAD, RhythmGame.V_WIDTH / 2f - Judgement.getWidth() / 2f * .7f, J_HEIGHT, MISS.getRegionWidth() * .7f, MISS.getRegionHeight() * .7f);
                break;
            case "GOOD":
                game.batch.draw(GOOD, RhythmGame.V_WIDTH / 2f - Judgement.getWidth() / 2f * .7f, J_HEIGHT, MISS.getRegionWidth() * .7f, MISS.getRegionHeight() * .7f);
                break;
            case "GREAT":
                game.batch.draw(GREAT, RhythmGame.V_WIDTH / 2f - Judgement.getWidth() / 2f * .7f, J_HEIGHT, MISS.getRegionWidth() * .7f, MISS.getRegionHeight() * .7f);
                break;
            case "PERFECT":
                game.batch.draw(PERFECT, RhythmGame.V_WIDTH / 2f - Judgement.getWidth() / 2f * .7f, J_HEIGHT, MISS.getRegionWidth() * .7f, MISS.getRegionHeight() * .7f);
                break;
            case "MAX":
                game.batch.draw(MAX, RhythmGame.V_WIDTH / 2f - Judgement.getWidth() / 2f * .7f, J_HEIGHT, MISS.getRegionWidth() * .7f, MISS.getRegionHeight() * .7f);
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
        note_img.dispose();
        hold_bar_img.dispose();
        bg.dispose();
        pm.dispose();
        cursor.dispose();
        music.dispose();
        stage.dispose();
        delayedMusicStart.clear();
        delayedNoteStart.clear();
    }
}
