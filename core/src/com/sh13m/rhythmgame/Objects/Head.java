package com.sh13m.rhythmgame.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.sh13m.rhythmgame.Screens.Gameplay;
import com.sh13m.rhythmgame.Tools.NoteLogic;
import com.sh13m.rhythmgame.Tools.SongInput;

public class Head implements Pool.Poolable {
    public Rectangle head;
    public boolean alive;
    public boolean comboBreak;
    public boolean comboAdd;
    public boolean missed;
    public boolean isHit;
    public boolean isHeld;

    public Head() {
        this.head = new Rectangle();
        this.alive = false;
        this.comboBreak = false;
        this.comboAdd = false;
        this.missed = false;
        this.isHit = false;
        this.isHeld = false;
    }

    public void init(float x, float y, float width, float height) {
        head.set(x, y, width, height);
        alive = true;
        comboBreak = false;
        comboAdd = false;
        missed = false;
        isHit = false;
        isHeld = false;
    }

    public float getX() {
        return head.x;
    }

    public float getY() {
        return head.y;
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
                       Rectangle receptor3, Rectangle receptor4) {
        if (!isHeld) head.y -= Gameplay.SCROLL_SPEED * Gdx.graphics.getDeltaTime();
        else if (isHeld) head.y = Gameplay.R_HEIGHT;
        // remove notes if they fall off-screen
        if (head.y + 64 < 0) {
            alive = false;
            comboBreak = true;
            missed = true;
        }
        // notes hit successfully
        if (head.overlaps(receptor1) && SongInput.receptor1JustPressed()) {
            comboAdd = true;
            isHit = true;
            isHeld = true;
            head.y = Gameplay.R_HEIGHT;
            NoteLogic.col1HoldEnd = false;
        }
        if (head.overlaps(receptor2) && SongInput.receptor2JustPressed()) {
            comboAdd = true;
            isHit = true;
            isHeld = true;
            head.y = Gameplay.R_HEIGHT;
            NoteLogic.col2HoldEnd = false;
        }
        if (head.overlaps(receptor3) && SongInput.receptor3JustPressed()) {
            comboAdd = true;
            isHit = true;
            isHeld = true;
            head.y = Gameplay.R_HEIGHT;
            NoteLogic.col3HoldEnd = false;
        }
        if (head.overlaps(receptor4) && SongInput.receptor4JustPressed()) {
            comboAdd = true;
            isHit = true;
            isHeld = true;
            head.y = Gameplay.R_HEIGHT;
            NoteLogic.col4HoldEnd = false;
        }
        // note let go too early
        if (head.overlaps(receptor1) && isHeld && !SongInput.receptor1Pressed()) {
            comboBreak = true;
            isHeld = false;
        }
        if (head.overlaps(receptor2) && isHeld && !SongInput.receptor2Pressed()) {
            comboBreak = true;
            isHeld = false;
        }
        if (head.overlaps(receptor3) && isHeld && !SongInput.receptor3Pressed()) {
            comboBreak = true;
            isHeld = false;
        }
        if (head.overlaps(receptor4) && isHeld && !SongInput.receptor4Pressed()) {
            comboBreak = true;
            isHeld = false;
        }
        // note held all the way till end
        if (head.x == Gameplay.COL1_X && NoteLogic.col1HoldEnd && isHeld) {
            alive = false;
            NoteLogic.col1HoldEnd = false;
        }
        if (head.x == Gameplay.COL2_X && NoteLogic.col2HoldEnd && isHeld) {
            alive = false;
            NoteLogic.col2HoldEnd = false;
        }
        if (head.x == Gameplay.COL3_X && NoteLogic.col3HoldEnd && isHeld) {
            alive = false;
            NoteLogic.col3HoldEnd = false;
        }
        if (head.x == Gameplay.COL4_X && NoteLogic.col4HoldEnd && isHeld) {
            alive = false;
            NoteLogic.col4HoldEnd = false;
        }
    }
}
