package com.sh13m.rhythmgame.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
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
    private static float HOLD_CHECK_PERIOD = 0.35f;

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

    // hold properties
    private boolean col1isHeld;
    private boolean col2isHeld;
    private boolean col3isHeld;
    private boolean col4isHeld;
    private boolean col1HoldMissed;
    private boolean col2HoldMissed;
    private boolean col3HoldMissed;
    private boolean col4HoldMissed;
    private boolean col1HoldComboBreak;
    private boolean col2HoldComboBreak;
    private boolean col3HoldComboBreak;
    private boolean col4HoldComboBreak;
    private float col1HoldCheckDelta;
    private float col2HoldCheckDelta;
    private float col3HoldCheckDelta;
    private float col4HoldCheckDelta;
    private Array<Rectangle> holdFX;

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
        bg = new Texture(Gdx.files.internal("Songs/7/bg.jpg"));

        // set up rectangles
        receptor1 = new Rectangle(COL1_X, R_HEIGHT,64,64);
        receptor2 = new Rectangle(COL2_X, R_HEIGHT,64,64);
        receptor3 = new Rectangle(COL3_X, R_HEIGHT,64,64);
        receptor4 = new Rectangle(COL4_X, R_HEIGHT,64,64);

        // set up hold properties
        col1isHeld = false;
        col2isHeld = false;
        col3isHeld = false;
        col4isHeld = false;
        col1HoldMissed = false;
        col2HoldMissed = false;
        col3HoldMissed = false;
        col4HoldMissed = false;
        col1HoldComboBreak = false;
        col2HoldComboBreak = false;
        col3HoldComboBreak = false;
        col4HoldComboBreak = false;
        col1HoldCheckDelta = 0;
        col2HoldCheckDelta = 0;
        col3HoldCheckDelta = 0;
        col4HoldCheckDelta = 0;
        holdFX = new Array<>();

        // set up song data
        music = Gdx.audio.newMusic(Gdx.files.internal("Songs/7/audio.mp3"));
        music.setVolume(0.3f);
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
        game.batch.setColor(1,1,1,0.2f);
        game.batch.draw(bg, RhythmGame.V_WIDTH/2 - (480*bg.getWidth()/bg.getHeight())/2,0,480*bg.getWidth()/bg.getHeight(),480);
        game.batch.setColor(1,1,1,1);
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
            sr.parseMeasure(delta);
            updateNotes(delta);
        }

    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new SongSelect(game));
            dispose();
        }

    }

    private void updateNotes(float delta) {
        // update tap notes
        for (Iterator<Rectangle> iter = sr.tap_notes.iterator(); iter.hasNext(); ) {
            Rectangle note = iter.next();
            note.y -= SCROLL_SPEED * Gdx.graphics.getDeltaTime();
            // remove notes if they fall off-screen
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
                // remove notes if they fall off-screen
                iter.remove();
                combo = 0;
            }
            // hold note head successfully hit
            if (note.overlaps(receptor1)) {
                col1HoldMissed = true;
                if (SongInput.receptor1JustPressed()) {
                    iter.remove();
                    combo++;
                    col1isHeld = true;
                    col1HoldMissed = false;
                    Rectangle noteFX = new Rectangle(COL1_X, R_HEIGHT, 64, 64);
                    holdFX.add(noteFX);
                }
            }
            if (note.overlaps(receptor2)) {
                col2HoldMissed = true;
                if (SongInput.receptor2JustPressed()) {
                    iter.remove();
                    combo++;
                    col2isHeld = true;
                    col2HoldMissed = false;
                    Rectangle noteFX = new Rectangle(COL2_X, R_HEIGHT, 64, 64);
                    holdFX.add(noteFX);
                }
            }
            if (note.overlaps(receptor3)) {
                col3HoldMissed = true;
                if (SongInput.receptor3JustPressed()) {
                    iter.remove();
                    combo++;
                    col3isHeld = true;
                    col3HoldMissed = false;
                    Rectangle noteFX = new Rectangle(COL3_X, R_HEIGHT, 64, 64);
                    holdFX.add(noteFX);
                }
            }
            if (note.overlaps(receptor4)) {
                col4HoldMissed = true;
                if (SongInput.receptor4JustPressed()) {
                    iter.remove();
                    combo++;
                    col4isHeld = true;
                    col4HoldMissed = false;
                    Rectangle noteFX = new Rectangle(COL4_X, R_HEIGHT, 64, 64);
                    holdFX.add(noteFX);
                }
            }
            // window to hit hold note has been missed
            if (note.y + 64 < R_HEIGHT) {
                if (note.x == COL1_X && !col1isHeld) {
                    col1isHeld = false;
                    col1HoldMissed = true;
                    col1HoldComboBreak = false;
                }
                if (note.x == COL2_X && !col2isHeld) {
                    col2isHeld = false;
                    col2HoldMissed = true;
                    col2HoldComboBreak = false;
                }
                if (note.x == COL3_X && !col3isHeld) {
                    col3isHeld = false;
                    col3HoldMissed = true;
                    col3HoldComboBreak = false;
                }
                if (note.x == COL4_X && !col4isHeld) {
                    col4isHeld = false;
                    col4HoldMissed = true;
                    col4HoldComboBreak = false;
                }
            }
        }
        // update hold bars
        for (Iterator<Rectangle> iter = sr.hold_bars.iterator(); iter.hasNext(); ) {
            try { // really jank fix but seems to work
                Rectangle bar = iter.next();
                bar.y -= SCROLL_SPEED * Gdx.graphics.getDeltaTime();
                // remove bars if they fall off-screen
                if (bar.y + bar.height < 0) iter.remove();

                // bar is held and not missed
                if (bar.x == COL1_X && !col1HoldMissed && bar.y < R_HEIGHT + 32) {
                    iter.remove();
                }
                if (bar.x == COL2_X && !col2HoldMissed &&  bar.y < R_HEIGHT + 32) {
                    iter.remove();
                }
                if (bar.x == COL3_X && !col3HoldMissed && bar.y < R_HEIGHT + 32) {
                    iter.remove();
                }
                if (bar.x == COL4_X && !col4HoldMissed && bar.y < R_HEIGHT + 32) {
                    iter.remove();
                }
            } catch (ArrayIndexOutOfBoundsException e){

            }
        }
        // check if hold note was let go too early every so often
        if (col1isHeld) {
            if (!SongInput.receptor1Pressed()) col1HoldCheckDelta += delta;
            else col1HoldCheckDelta = 0;
            if (col1HoldCheckDelta >= HOLD_CHECK_PERIOD) {
                if (!col1HoldMissed) {
                    col1HoldMissed = true;
                    if (!col1HoldComboBreak) {
                        combo = 0;
                        col1HoldComboBreak = true;
                    }
                }
                col1HoldCheckDelta -= HOLD_CHECK_PERIOD;
            }
        }
        if (col2isHeld) {
            if (!SongInput.receptor2Pressed()) col2HoldCheckDelta += delta;
            else col2HoldCheckDelta = 0;
            if (col2HoldCheckDelta >= HOLD_CHECK_PERIOD) {
                if (!col2HoldMissed) {
                    col2HoldMissed = true;
                    if (!col2HoldComboBreak) {
                        combo = 0;
                        col2HoldComboBreak = true;
                    }
                }
                col2HoldCheckDelta -= HOLD_CHECK_PERIOD;
            }
        }
        if (col3isHeld) {
            if (!SongInput.receptor3Pressed()) col3HoldCheckDelta += delta;
            else col3HoldCheckDelta = 0;
            if (col3HoldCheckDelta >= HOLD_CHECK_PERIOD) {
                if (!col3HoldMissed) {
                    col3HoldMissed = true;
                    if (!col3HoldComboBreak) {
                        combo = 0;
                        col3HoldComboBreak = true;
                    }
                }
                col3HoldCheckDelta -= HOLD_CHECK_PERIOD;
            }
        }
        if (col4isHeld) {
            if (!SongInput.receptor4Pressed()) col4HoldCheckDelta += delta;
            else col4HoldCheckDelta = 0;
            if (col4HoldCheckDelta >= HOLD_CHECK_PERIOD) {
                if (!col4HoldMissed) {
                    col4HoldMissed = true;
                    if (!col4HoldComboBreak) {
                        combo = 0;
                        col4HoldComboBreak = true;
                    }
                }
                col4HoldCheckDelta -= HOLD_CHECK_PERIOD;
            }
        }
        // update hold end
        for (Iterator<Rectangle> iter = sr.hold_notes_end.iterator(); iter.hasNext(); ) {
            Rectangle end = iter.next();
            end.y -= SCROLL_SPEED * Gdx.graphics.getDeltaTime();
            // remove bars if they fall off-screen
            if (end.y < R_HEIGHT) {
                iter.remove();
                if (end.x == COL1_X) {
                    col1isHeld = false;
                    col1HoldComboBreak = false;
                    col1HoldCheckDelta = 0;
                }
                if (end.x == COL2_X) {
                    col2isHeld = false;
                    col2HoldComboBreak = false;
                    col2HoldCheckDelta = 0;
                }
                if (end.x == COL3_X) {
                    col3isHeld = false;
                    col3HoldComboBreak = false;
                    col3HoldCheckDelta = 0;
                }
                if (end.x == COL4_X) {
                    col4isHeld = false;
                    col4HoldComboBreak = false;
                    col4HoldCheckDelta = 0;
                }
            }
        }
        // update holdFX
        for (Iterator<Rectangle> iter = holdFX.iterator(); iter.hasNext(); ) {
            Rectangle noteFX = iter.next();
            if (noteFX.y + 64 < 0) iter.remove();
            if (noteFX.x == COL1_X && (col1HoldComboBreak || noteFX.y + 64 < R_HEIGHT)) noteFX.y -= SCROLL_SPEED * Gdx.graphics.getDeltaTime();
            if (noteFX.x == COL2_X && (col2HoldComboBreak || noteFX.y + 64 < R_HEIGHT)) noteFX.y -= SCROLL_SPEED * Gdx.graphics.getDeltaTime();
            if (noteFX.x == COL3_X && (col3HoldComboBreak || noteFX.y + 64 < R_HEIGHT)) noteFX.y -= SCROLL_SPEED * Gdx.graphics.getDeltaTime();
            if (noteFX.x == COL4_X && (col4HoldComboBreak || noteFX.y + 64 < R_HEIGHT)) noteFX.y -= SCROLL_SPEED * Gdx.graphics.getDeltaTime();
            if (noteFX.x == COL1_X && !col1isHeld && !col1HoldMissed) iter.remove();
            if (noteFX.x == COL2_X && !col2isHeld && !col2HoldMissed) iter.remove();
            if (noteFX.x == COL3_X && !col3isHeld && !col3HoldMissed) iter.remove();
            if (noteFX.x == COL4_X && !col4isHeld && !col4HoldMissed) iter.remove();
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
        // hold bars
        for (Rectangle bar : sr.hold_bars) {
            game.batch.draw(hold_bar_img, bar.x, bar.y, bar.width, bar.height);
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
        // hold note FX
        for (Rectangle note : holdFX) {
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
