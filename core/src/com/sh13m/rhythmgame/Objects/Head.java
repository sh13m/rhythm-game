package com.sh13m.rhythmgame.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.sh13m.rhythmgame.Screens.Gameplay;
import com.sh13m.rhythmgame.Tools.SongInput;

public class Head implements Pool.Poolable {
    public Rectangle head;
    public boolean alive;
    public boolean comboBreak;
    public boolean comboAdd;
    public boolean missed;
    public boolean isHit;
    public boolean isHeld;
    public boolean gotJudgement;

    public Head() {
        this.head = new Rectangle();
        this.alive = false;
        this.comboBreak = false;
        this.comboAdd = false;
        this.missed = false;
        this.isHit = false;
        this.isHeld = false;
        this.gotJudgement = false;
    }

    public void init(float x, float y, float width, float height) {
        head.set(x, y, width, height);
        alive = true;
        comboBreak = false;
        comboAdd = false;
        missed = false;
        isHit = false;
        isHeld = false;
        gotJudgement = false;
    }

    public float getX() {
        return head.x;
    }

    public float getY() {
        return head.y;
    }

    public Rectangle getRect() {
        return head;
    }

    @Override
    public void reset() {
        head.set(0,0,0,0);
        alive = false;
        comboBreak = false;
        comboAdd = false;
        missed = false;
        isHit = false;
        isHeld = false;
    }

    public void update(Rectangle receptor1, Rectangle receptor2,
                       Rectangle receptor3, Rectangle receptor4,
                       Bar bar, End end) {
        if (!isHeld) head.y -= Gameplay.SCROLL_SPEED * Gdx.graphics.getDeltaTime();
        else if (isHeld) head.y = Gameplay.R_HEIGHT;
        // break combo they fall off-screen
        if (head.y + 64 < 0 && !missed) {
            comboBreak = true;
            missed = true;
        }
        // notes hit successfully
        if (head.overlaps(receptor1) && SongInput.receptor1JustPressed()) {
            comboAdd = true;
            isHit = true;
            isHeld = true;
        } else if (head.overlaps(receptor2) && SongInput.receptor2JustPressed()) {
            comboAdd = true;
            isHit = true;
            isHeld = true;
        } else if (head.overlaps(receptor3) && SongInput.receptor3JustPressed()) {
            comboAdd = true;
            isHit = true;
            isHeld = true;
        } else if (head.overlaps(receptor4) && SongInput.receptor4JustPressed()) {
            comboAdd = true;
            isHit = true;
            isHeld = true;
        }
        // makes note stay on receptor while held
        if (gotJudgement) {
            isHit = false;
        }
        // note let go too early
        if (head.overlaps(receptor1) && isHeld && !SongInput.receptor1Pressed()) {
            isHeld = false;
        } else if (head.overlaps(receptor2) && isHeld && !SongInput.receptor2Pressed()) {
            isHeld = false;
        } else if (head.overlaps(receptor3) && isHeld && !SongInput.receptor3Pressed()) {
            isHeld = false;
        } else if (head.overlaps(receptor4) && isHeld && !SongInput.receptor4Pressed()) {
            isHeld = false;
        }

        if (!end.alive) {
            alive = false;
        }
    }
}
