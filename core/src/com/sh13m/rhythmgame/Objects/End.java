package com.sh13m.rhythmgame.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.sh13m.rhythmgame.Screens.Gameplay;

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

    public void update(Head head, Bar bar) {
        end.y -= Gameplay.SCROLL_SPEED * Gdx.graphics.getDeltaTime();
        // hold successfully done
        if (end.overlaps(head.getRect()) && head.isHeld) {
            alive = false;
        }
        // hold missed
        if (end.y + end.height < 0) {
            alive = false;
        }
    }
}
