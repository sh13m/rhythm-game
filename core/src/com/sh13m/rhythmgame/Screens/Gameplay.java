package com.sh13m.rhythmgame.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.sh13m.rhythmgame.Objects.Bar;
import com.sh13m.rhythmgame.Objects.End;
import com.sh13m.rhythmgame.Objects.Head;
import com.sh13m.rhythmgame.Objects.TapNote;
import com.sh13m.rhythmgame.RhythmGame;
import com.sh13m.rhythmgame.Tools.NoteLogic;
import com.sh13m.rhythmgame.Tools.SongInput;
import com.sh13m.rhythmgame.Tools.SongReader;
import com.sh13m.rhythmgame.Tools.TextUtil;

public class Gameplay implements Screen {
    private final RhythmGame game;

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
    private boolean NO_FAIL;
    private boolean SONG_OVER;
    private boolean GO_SCORE;

    // textures
    private final Texture note_img;
    private final Texture hold_bar_img;
    private final Texture end_img;
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
    private final Texture stage_left;
    private final Texture stage_right;
    private final Texture bg;
    private final Texture judgement;
    private final TextureRegion MISS;
    private final TextureRegion BAD;
    private final TextureRegion GOOD;
    private final TextureRegion GREAT;
    private final TextureRegion PERFECT;
    private final TextureRegion MAX;
    private final Texture health_bar_img;

    // rectangles
    private final Rectangle receptor1;
    private final Rectangle receptor2;
    private final Rectangle receptor3;
    private final Rectangle receptor4;

    // song data
    private final Music music;
    private final SongReader sr;
    private final NoteLogic nl;
    private final int level;

    // timers
    private final Timer delayedMusicStart;
    private final Timer delayedNoteStart;
    private final Timer songEnd;
    private final Timer goScoring;
    private float transition;

    public Gameplay(RhythmGame game, int level) {
        this.game = game;
        SONG_OVER = false;
        GO_SCORE = false;
        NO_FAIL = false;

        // set up textures
        note_img = new Texture(Gdx.files.internal("Graphics/notes.png"));
        hold_bar_img = new Texture(Gdx.files.internal("Graphics/hold.png"));
        end_img = new Texture(Gdx.files.internal("Graphics/end.png"));
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
        stage_left = new Texture(Gdx.files.internal("Graphics/stage_left.png"));
        stage_right = new Texture(Gdx.files.internal("Graphics/stage_right.png"));
        judgement = new Texture(Gdx.files.internal("Graphics/Modern Nore 1x6.png"));
        MISS = new TextureRegion(judgement,0,170,256,34);
        BAD = new TextureRegion(judgement,0,136,256,34);
        GOOD = new TextureRegion(judgement,0,102,256,34);
        GREAT = new TextureRegion(judgement,0,68,256,34);
        PERFECT = new TextureRegion(judgement,0,34,256,34);
        MAX = new TextureRegion(judgement,0,0,256,34);
        health_bar_img = new Texture(Gdx.files.internal("Graphics/health_bar.png"));

        // set up rectangles
        receptor1 = new Rectangle(COL1_X, R_HEIGHT,64,64);
        receptor2 = new Rectangle(COL2_X, R_HEIGHT,64,64);
        receptor3 = new Rectangle(COL3_X, R_HEIGHT,64,64);
        receptor4 = new Rectangle(COL4_X, R_HEIGHT,64,64);

        // set up song data
        this.level = level;
        nl = new NoteLogic();
        sr = new SongReader(level);
        music = Gdx.audio.newMusic(Gdx.files.internal("Songs/" + level + "/" + sr.getSongFileName()));
        bg = new Texture(Gdx.files.internal("Songs/" + level + "/bg.jpg"));
        music.setVolume(0.2f);
        float musicBuffer = -1;
        if (sr.offset < musicBuffer) musicBuffer = sr.offset*-1;
        else musicBuffer = 0;

        // start r setup timers
        delayedMusicStart = Timer.instance();
        delayedMusicStart.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                music.play();
            }
        }, GLOBAL_DELAY + musicBuffer + sr.offset);
        delayedNoteStart = Timer.instance();
        delayedNoteStart.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                sr.parseMeasure();
            }
        }, GLOBAL_DELAY + musicBuffer - SCROLL_OFFSET);
        songEnd = Timer.instance();
        songEnd.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                SONG_OVER = true;
            }
        },GLOBAL_DELAY + musicBuffer - SCROLL_OFFSET + sr.songTime);
        goScoring = Timer.instance();
        goScoring.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                GO_SCORE = true;
            }
        }, GLOBAL_DELAY + musicBuffer - SCROLL_OFFSET + sr.songTime + 5);
        transition = 0;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(game.cam.combined);
        game.batch.begin();
        drawBackground();
        drawPlayField();
        drawInput();
        drawNotes();
        drawCombo();
        drawJudgement();
        drawStats();
        game.batch.end();
    }

    private void update(float delta) {
        handleInput();
        if (nl.HEALTH > 0 || NO_FAIL) nl.updateNotes(sr, receptor1, receptor2, receptor3, receptor4);
        if (nl.HEALTH <= 0 && !NO_FAIL) {
            transition += delta;
            if (music.isPlaying()) music.stop();
            if (transition >= 3) {
                game.setScreen(new Scoring(game, level, true, NO_FAIL,
                        nl.MAX_COMBO, nl.ACCURACY, nl.SCORE,
                        nl.MAX_COUNT, nl.PERFECT_COUNT, nl.GREAT_COUNT,
                        nl.GOOD_COUNT, nl.BAD_COUNT, nl.MISS_COUNT));
                dispose();;
            }
        }
        if (GO_SCORE) {
            game.setScreen(new Scoring(game, level, false, NO_FAIL,
                    nl.MAX_COMBO, nl.ACCURACY, nl.SCORE,
                    nl.MAX_COUNT, nl.PERFECT_COUNT, nl.GREAT_COUNT,
                    nl.GOOD_COUNT, nl.BAD_COUNT, nl.MISS_COUNT));
            dispose();
        }
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (SONG_OVER) {
                game.setScreen(new Scoring(game, level, false, NO_FAIL,
                        nl.MAX_COMBO, nl.ACCURACY, nl.SCORE,
                        nl.MAX_COUNT, nl.PERFECT_COUNT, nl.GREAT_COUNT,
                        nl.GOOD_COUNT, nl.BAD_COUNT, nl.MISS_COUNT));
                dispose();
            } else {
                game.setScreen(new SongSelect(game, level));
                dispose();
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            NO_FAIL = true;
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
        // ends
        for (End end : sr.activeEnds) {
            game.batch.draw(end_img, end.getX(), end.getY());
        }
        // hold heads
        for (Head head : sr.activeHeads) {
            if (head.getX() == COL1_X) game.batch.draw(note_1, head.getX(), head.getY());
            else if (head.getX() == COL2_X) game.batch.draw(note_2, head.getX(), head.getY());
            else if (head.getX() == COL3_X) game.batch.draw(note_3, head.getX(), head.getY());
            else if (head.getX() == COL4_X) game.batch.draw(note_4, head.getX(), head.getY());
        }
    }

    private void drawBackground() {game.batch.setColor(1,1,1,0.2f);
        game.batch.draw(bg, RhythmGame.V_WIDTH/2f - (480f*bg.getWidth()/bg.getHeight())/2f,0,480f*bg.getWidth()/bg.getHeight(),480);
        game.batch.setColor(1,1,1,1);

    }

    private void drawPlayField() {
        game.batch.draw(stage, RhythmGame.V_WIDTH / 2f - stage.getWidth() / 2f, 0);
        game.batch.draw(stage_left, RhythmGame.V_WIDTH / 2f - 149, 0);
        game.batch.draw(stage_right, RhythmGame.V_WIDTH/ 2f + 136, 0);
        game.batch.draw(receptors_img, RhythmGame.V_WIDTH / 2f - receptors_img.getRegionWidth() / 2f , R_HEIGHT);
    }

    private void drawJudgement() {
        switch (nl.JUDGEMENT) {
            case "MISS":
                game.batch.draw(MISS, RhythmGame.V_WIDTH / 2f - judgement.getWidth() / 2f * .7f, J_HEIGHT, MISS.getRegionWidth() * .7f, MISS.getRegionHeight() * .7f);
                break;
            case "BAD":
                game.batch.draw(BAD, RhythmGame.V_WIDTH / 2f - judgement.getWidth() / 2f * .7f, J_HEIGHT, MISS.getRegionWidth() * .7f, MISS.getRegionHeight() * .7f);
                break;
            case "GOOD":
                game.batch.draw(GOOD, RhythmGame.V_WIDTH / 2f - judgement.getWidth() / 2f * .7f, J_HEIGHT, MISS.getRegionWidth() * .7f, MISS.getRegionHeight() * .7f);
                break;
            case "GREAT":
                game.batch.draw(GREAT, RhythmGame.V_WIDTH / 2f - judgement.getWidth() / 2f * .7f, J_HEIGHT, MISS.getRegionWidth() * .7f, MISS.getRegionHeight() * .7f);
                break;
            case "PERFECT":
                game.batch.draw(PERFECT, RhythmGame.V_WIDTH / 2f - judgement.getWidth() / 2f * .7f, J_HEIGHT, MISS.getRegionWidth() * .7f, MISS.getRegionHeight() * .7f);
                break;
            case "MAX":
                game.batch.draw(MAX, RhythmGame.V_WIDTH / 2f - judgement.getWidth() / 2f * .7f, J_HEIGHT, MISS.getRegionWidth() * .7f, MISS.getRegionHeight() * .7f);
                break;
        }
    }

    private void drawCombo() {
        game.font.getData().setScale(0.5f);
        game.font.draw(game.batch, String.valueOf(nl.COMBO), RhythmGame.V_WIDTH / 2f - TextUtil.getTextWidth(game.font, String.valueOf(nl.COMBO)) / 2, COMBO_HEIGHT);
    }

    private void drawStats() {
        game.font.getData().setScale(0.4f);
        game.font.draw(game.batch, String.valueOf(nl.SCORE), 10, 470);
        game.font.draw(game.batch, String.format("%.1f%c", nl.ACCURACY, '%'), 10, 450);
        if (NO_FAIL) game.font.draw(game.batch, "NO-FAIL ON", 10, 430);
        game.batch.draw(health_bar_img, RhythmGame.V_WIDTH / 2f + 145, 7, 8, nl.HEALTH*2.2f);
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
        note_img.dispose();
        hold_bar_img.dispose();
        end_img.dispose();
        bg.dispose();
        music.dispose();
        stage.dispose();
        stage_left.dispose();
        stage_right.dispose();
        delayedMusicStart.clear();
        delayedNoteStart.clear();
        songEnd.clear();
        goScoring.clear();
        health_bar_img.dispose();
        judgement.dispose();
    }
}
