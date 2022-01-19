package com.sh13m.rhythmgame.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.sh13m.rhythmgame.Screens.Gameplay;
import com.sh13m.rhythmgame.Tools.NoteLogic;

public class End implements Pool.Poolable {
    public Rectangle end;
    public boolean alive;

    public End() {
        this.end = new Rectangle();
        this.alive = false;
    }

    public void init(float x, float y, float width, float height) {
        end.set(x, y, width, height);
        this.alive = true;
    }

    public Rectangle getRect() {
        return end;
    }

    public float getX() {
        return end.x;
    }

    public float getY() {
        return end.y;
    }

    @Override
    public void reset() {
        end.set(0,0,0,0);
        alive = false;
    }

    public void update(Rectangle receptor1, Rectangle receptor2,
                       Rectangle receptor3, Rectangle receptor4) {
        end.y -= Gameplay.SCROLL_SPEED * Gdx.graphics.getDeltaTime();
        if (end.overlaps(receptor1) && end.y < Gameplay.R_HEIGHT + 30) NoteLogic.col1HoldEnd = true;
        if (end.overlaps(receptor2) && end.y < Gameplay.R_HEIGHT + 30) NoteLogic.col2HoldEnd = true;
        if (end.overlaps(receptor3) && end.y < Gameplay.R_HEIGHT + 30) NoteLogic.col3HoldEnd = true;
        if (end.overlaps(receptor4) && end.y < Gameplay.R_HEIGHT + 30) NoteLogic.col4HoldEnd = true;

    }
}
