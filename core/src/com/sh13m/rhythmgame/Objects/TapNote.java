package com.sh13m.rhythmgame.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.sh13m.rhythmgame.Screens.Gameplay;
import com.sh13m.rhythmgame.Tools.SongInput;

public class TapNote implements Pool.Poolable{
    public Rectangle note;
    public boolean alive;
    public boolean comboBreak;
    public boolean comboAdd;
    public boolean missed;
    public boolean isHit;

    public TapNote() {
        this.note = new Rectangle();
        this.alive = false;
        this.comboBreak = false;
        this.comboAdd = false;
        this.missed = false;
        this.isHit = false;
    }

    public void init(float x, float y, float width, float height) {
        note.set(x, y, width, height);
        alive = true;
        comboBreak = false;
        comboAdd = false;
        missed = false;
        isHit = false;
    }

    public float getX() {
        return note.x;
    }

    public float getY() {
        return note.y;
    }

    @Override
    public void reset() {
        note.set(0,0,0,0);
        alive = false;
        comboBreak = false;
        comboAdd = false;
        missed = false;
        isHit = false;
    }

    public void update(Rectangle receptor1, Rectangle receptor2,
                       Rectangle receptor3, Rectangle receptor4) {
        note.y -= Gameplay.SCROLL_SPEED * Gdx.graphics.getDeltaTime();
        // remove notes if they fall off-screen
        if (note.y + 64 < 0) {
            alive = false;
            comboBreak = true;
            missed = true;
        }
        // remove notes if they are successfully hit
        if (note.overlaps(receptor1) && SongInput.receptor1JustPressed()) {
            alive = false;
            comboAdd = true;
            isHit = true;
        }
        if (note.overlaps(receptor2) && SongInput.receptor2JustPressed()) {
            alive = false;
            comboAdd = true;
            isHit = true;
        }
        if (note.overlaps(receptor3) && SongInput.receptor3JustPressed()) {
            alive = false;
            comboAdd = true;
            isHit = true;
        }
        if (note.overlaps(receptor4) && SongInput.receptor4JustPressed()) {
            alive = false;
            comboAdd = true;
            isHit = true;
        }
    }
}
