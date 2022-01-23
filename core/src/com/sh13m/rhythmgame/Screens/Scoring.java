package com.sh13m.rhythmgame.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.sh13m.rhythmgame.RhythmGame;

public class Scoring implements Screen {
    private final RhythmGame game;

    // stats
    private final int MAX_COMBO;
    public float ACCURACY;
    public long SCORE;
    private final int MAX_COUNT;
    private final int PERFECT_COUNT;
    private final int GREAT_COUNT;
    private final int GOOD_COUNT;
    private final int BAD_COUNT;
    private final int MISS_COUNT;
    private final boolean failed;
    private final Texture rank;

    // textures
    private final Texture bg;
    private final Texture judgement;
    private final TextureRegion MISS;
    private final TextureRegion BAD;
    private final TextureRegion GOOD;
    private final TextureRegion GREAT;
    private final TextureRegion PERFECT;
    private final TextureRegion MAX;
    private final Texture scoreBox;

    public Scoring (RhythmGame game, int level, boolean failed,
                    int MAX_COMBO, float ACCURACY, long SCORE,
                    int MAX_COUNT, int PERFECT_COUNT, int GREAT_COUNT,
                    int GOOD_COUNT, int BAD_COUNT, int MISS_COUNT) {
        this.game = game;

        // get stats
        this.MAX_COMBO = MAX_COMBO;
        this.ACCURACY = ACCURACY;
        this.SCORE = SCORE;
        this.MAX_COUNT = MAX_COUNT;
        this.PERFECT_COUNT = PERFECT_COUNT;
        this.GREAT_COUNT = GREAT_COUNT;
        this.GOOD_COUNT = GOOD_COUNT;
        this.BAD_COUNT = BAD_COUNT;
        this.MISS_COUNT = MISS_COUNT;
        this.failed = failed;
        rank = getRank();

        // set up textures
        bg = new Texture(Gdx.files.internal("Songs/" + level + "/bg.jpg"));
        judgement = new Texture(Gdx.files.internal("Graphics/Modern Nore 1x6.png"));
        MISS = new TextureRegion(judgement,0,170,256,34);
        BAD = new TextureRegion(judgement,0,136,256,34);
        GOOD = new TextureRegion(judgement,0,102,256,34);
        GREAT = new TextureRegion(judgement,0,68,256,34);
        PERFECT = new TextureRegion(judgement,0,34,256,34);
        MAX = new TextureRegion(judgement,0,0,256,34);
        scoreBox = new Texture(Gdx.files.internal("Graphics/stage.png"));

        // read scores and save highscores
        Array<Long> scores = new Array<>();
        FileHandle scorefile = Gdx.files.local("scores.txt");
        String[] temp = scorefile.readString().split("\\r?\\n");
        for (String s : temp) {
            scores.add(Long.parseLong(s));
        }
        long currentHighScore = scores.get(level - 1);
        if (SCORE > currentHighScore) scores.set(level-1, SCORE);
        scorefile.write(false);
        for (int i = 0; i < scores.size; ++i) {
            scorefile.writeString(String.valueOf(scores.get(i)), true);
            if (i != scores.size-1) scorefile.writeString("\n", true);
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update();

        Gdx.gl.glClearColor(0.07f,0.07f,0.07f,1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(game.cam.combined);
        game.batch.begin();
        drawBackground();
        drawScoring();
        game.batch.end();
    }

    private void update() {
        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new SongSelect(game));
            dispose();
        }
    }

    private void drawBackground() {
        game.batch.setColor(1,1,1,0.2f);
        game.batch.draw(bg, RhythmGame.V_WIDTH/2f - (480f*bg.getWidth()/bg.getHeight())/2f,0,480f*bg.getWidth()/bg.getHeight(),480);
        game.batch.setColor(1,1,1,1);
    }

    private void drawScoring() {
        game.batch.draw(scoreBox, 30, 30, 250, 420);
        game.batch.draw(rank, 350, RhythmGame.V_HEIGHT/2f - rank.getHeight()/2f);

        game.smalltext.getData().setScale(0.6f);
        game.smalltext.draw(game.batch, "SCORE", 50, 425);
        game.smalltext.draw(game.batch, "MAX COMBO", 50, 95);
        game.smalltext.draw(game.batch, "ACCURACY", 180, 95);
        game.font.getData().setScale(.7f);
        game.font.draw(game.batch, String.valueOf(SCORE), 50, 410);
        game.font.getData().setScale(.55f);
        game.font.draw(game.batch, String.valueOf(MAX_COMBO), 50, 75);
        game.font.draw(game.batch, String.format("%.1f%c", ACCURACY, '%'), 180, 75);

        game.batch.draw(MAX, 35, 330, MAX.getRegionWidth()*.5f, MAX.getRegionHeight()*.5f);
        game.batch.draw(PERFECT, 23, 290, PERFECT.getRegionWidth()*.5f, PERFECT.getRegionHeight()*.5f);
        game.batch.draw(GREAT, 13, 250, GREAT.getRegionWidth()*.5f, GREAT.getRegionHeight()*.5f);
        game.batch.draw(GOOD, 6, 210, GOOD.getRegionWidth()*.5f, GOOD.getRegionHeight()*.5f);
        game.batch.draw(BAD, -1, 170, BAD.getRegionWidth()*.5f, BAD.getRegionHeight()*.5f);
        game.batch.draw(MISS, 25, 130, MISS.getRegionWidth()*.5f, MISS.getRegionHeight()*.5f);
        game.font.getData().setScale(0.4f);
        game.font.draw(game.batch, "x " + MAX_COUNT, 180, 348);
        game.font.draw(game.batch, "x " + PERFECT_COUNT, 180, 308);
        game.font.draw(game.batch, "x " + GREAT_COUNT, 180, 268);
        game.font.draw(game.batch, "x " + GOOD_COUNT, 180, 228);
        game.font.draw(game.batch, "x " + BAD_COUNT, 180, 188);
        game.font.draw(game.batch, "x " + MISS_COUNT, 180, 148);

        game.smalltext.getData().setScale(0.7f);
        game.smalltext.draw(game.batch, "PRESS <ESC> TO GO BACK TO SONG MENU", 5, 15);
    }

    private Texture getRank() {
        Texture rank;
        if (failed) {
            rank = new Texture(Gdx.files.internal("Graphics/rank_F.png"));
        } else if (MISS_COUNT == 0) {
            rank = new Texture(Gdx.files.internal("Graphics/rank_S+.png"));
        } else if (ACCURACY >= 95f) {
            rank = new Texture(Gdx.files.internal("Graphics/rank_S.png"));
        } else if (ACCURACY >= 90f) {
            rank = new Texture(Gdx.files.internal("Graphics/rank_A.png"));
        } else if (ACCURACY >= 80f) {
            rank = new Texture(Gdx.files.internal("Graphics/rank_B.png"));
        } else if (ACCURACY >= 70f) {
            rank = new Texture(Gdx.files.internal("Graphics/rank_C.png"));
        } else {
            rank = new Texture(Gdx.files.internal("Graphics/rank_D.png"));
        }
        return rank;
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
        bg.dispose();
        judgement.dispose();
    }
}
