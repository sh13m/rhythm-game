package com.sh13m.rhythmgame.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.sh13m.rhythmgame.Screens.Gameplay;

public class Bar implements Pool.Poolable {
    public Rectangle bar;
    public boolean alive;

    public Bar() {
        this.bar = new Rectangle();
        this.alive = false;
    }

    public void init(float x, float y, float width, float height) {
        bar.set(x, y, width, height);
        alive = true;
    }

    public float getX() {
        return bar.x;
    }

    public float getY() {
        return bar.y;
    }


    public void setHeight(float height) {
        bar.height = height;
    }

    public float getWidth() {
        return bar.width;
    }

    public float getHeight() {
        return bar.height;
    }

    @Override
    public void reset() {
        bar.set(0,0,0,0);
        alive = false;
    }

    public void update() {
        bar.y -= Gameplay.SCROLL_SPEED * Gdx.graphics.getDeltaTime();
        if (bar.y + bar.height < 0) alive = false;
    }
}
