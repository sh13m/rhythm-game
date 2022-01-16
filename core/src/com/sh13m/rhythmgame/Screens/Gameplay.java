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
import com.sh13m.rhythmgame.RhythmGame;
import com.sh13m.rhythmgame.Tools.SongInput;
import com.sh13m.rhythmgame.Tools.SongReader;
import com.sh13m.rhythmgame.Tools.TextUtil;

import java.util.Iterator;

public class Gameplay implements Screen {
    // gameplay settings
    private static final int R_HEIGHT = 30;
    private static final int COMBO_HEIGHT = 300;
    public static final int SCROLL_SPEED = 900;
    private static final float GLOBAL_DELAY = 3;
    private static final float SCROLL_OFFSET = (480f - R_HEIGHT) / SCROLL_SPEED;
    public static float COL1_X = RhythmGame.V_WIDTH / 2 - 128;
    public static float COL2_X = RhythmGame.V_WIDTH / 2 - 64;
    public static float COL3_X = RhythmGame.V_WIDTH / 2;
    public static float COL4_X = RhythmGame.V_WIDTH / 2 + 64;

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

    // rectangles
    private final Rectangle receptor1;
    private final Rectangle receptor2;
    private final Rectangle receptor3;
    private final Rectangle receptor4;

    // song data
    private final Music music;
    private final SongReader sr;
    private boolean START = false;

    // temp other
    private int combo;
    private final Timer delayedMusicStart;
    private final Timer delayedNoteStart;


    public Gameplay(RhythmGame game) {
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
        bg = new Texture(Gdx.files.internal("Songs/LN/bg.jpg"));


        // set up rectangles
        receptor1 = new Rectangle(COL1_X, R_HEIGHT,64,64);
        receptor2 = new Rectangle(COL2_X, R_HEIGHT,64,64);
        receptor3 = new Rectangle(COL3_X, R_HEIGHT,64,64);
        receptor4 = new Rectangle(COL4_X, R_HEIGHT,64,64);

        // set up song data
        music = Gdx.audio.newMusic(Gdx.files.internal("Songs/LN/Bluenation.mp3"));
        music.setVolume(0.5f);
        sr = new SongReader();
        delayedMusicStart = new Timer();
        delayedMusicStart.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                music.play();
            }
        }, GLOBAL_DELAY + sr.offset);
        delayedNoteStart = new Timer();
        delayedNoteStart.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                START = true;
            }
        }, GLOBAL_DELAY - SCROLL_OFFSET);

        // temp other set up
        combo = 0;
    }

    @Override
    public void show() {
        game.font.getData().setScale(0.5f);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(cam.combined);
        game.batch.begin();
        game.batch.draw(bg, 0,0,640,480);
        game.batch.draw(stage, RhythmGame.V_WIDTH / 2 - stage.getWidth() / 2, 0);
        game.batch.draw(receptors_img, RhythmGame.V_WIDTH / 2 - receptors_img.getRegionWidth() / 2 , R_HEIGHT);
        drawInput();
        drawNotes();
        game.font.draw(game.batch, String.valueOf(combo), RhythmGame.V_WIDTH / 2 - TextUtil.getTextWidth(game.font, String.valueOf(combo)) / 2, COMBO_HEIGHT);
        game.ltext.draw(game.batch, "GAMEPLAY", 5, 20);
        game.batch.end();
    }

    private void update(float delta) {
        handleInput();
        if (!sr.songEnded && START) {
            sr.readMeasure(delta);
            sr.addHoldBars(delta);
            updateNotes();
        }

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

    private void updateNotes() {
        // update tap notes
        for (Iterator<Rectangle> iter = sr.tap_notes.iterator(); iter.hasNext(); ) {
            Rectangle note = iter.next();
            note.y -= SCROLL_SPEED * Gdx.graphics.getDeltaTime();
            // remove notes if they fall off screen
            if (note.y + 64 < 0) {
                iter.remove();
                combo = 0;
            }
            // remove notes if they are successfully hit
            if (note.overlaps(receptor1) && SongInput.receptor1JustPressed()) {
                iter.remove();
                combo++;
            }
            if (note.overlaps(receptor2) && SongInput.receptor2JustPressed()) {
                iter.remove();
                combo++;
            }
            if (note.overlaps(receptor3) && SongInput.receptor3JustPressed()) {
                iter.remove();
                combo++;
            }
            if (note.overlaps(receptor4) && SongInput.receptor4JustPressed()) {
                iter.remove();
                combo++;
            }
        }
        // update hold note heads
        for (Iterator<Rectangle> iter = sr.hold_notes_start.iterator(); iter.hasNext(); ) {
            Rectangle note = iter.next();
            note.y -= SCROLL_SPEED * Gdx.graphics.getDeltaTime();
            if (note.y + 64 < 0) {
                iter.remove();
                combo = 0;
            }
            if (note.overlaps(receptor1) && SongInput.receptor1JustPressed()) {
                iter.remove();
                combo++;
            }
            if (note.overlaps(receptor2) && SongInput.receptor2JustPressed()) {
                iter.remove();
                combo++;
            }
            if (note.overlaps(receptor3) && SongInput.receptor3JustPressed()) {
                iter.remove();
                combo++;
            }
            if (note.overlaps(receptor4) && SongInput.receptor4JustPressed()) {
                iter.remove();
                combo++;
            }
        }
        // update hold bars
        for (Iterator<Rectangle> iter = sr.hold_bars.iterator(); iter.hasNext(); ) {
            Rectangle bar = iter.next();
            bar.y -= SCROLL_SPEED * Gdx.graphics.getDeltaTime();
            if (bar.y + 64 < 0) iter.remove();

        }
    }

    private void drawNotes() {
        // hold bars
        for (Rectangle bar : sr.hold_bars) {
            game.batch.draw(hold_bar_img, bar.x, bar.y);
        }
        // tap notes
        for (Rectangle note : sr.tap_notes) {
            if (note.x == COL1_X) game.batch.draw(note_1, note.x, note.y);
            else if (note.x == COL2_X) game.batch.draw(note_2, note.x, note.y);
            else if (note.x == COL3_X) game.batch.draw(note_3, note.x, note.y);
            else if (note.x == COL4_X) game.batch.draw(note_4, note.x, note.y);
        }
        // hold note heads
        for (Rectangle note : sr.hold_notes_start) {
            if (note.x == COL1_X) game.batch.draw(note_1, note.x, note.y);
            else if (note.x == COL2_X) game.batch.draw(note_2, note.x, note.y);
            else if (note.x == COL3_X) game.batch.draw(note_3, note.x, note.y);
            else if (note.x == COL4_X) game.batch.draw(note_4, note.x, note.y);
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
