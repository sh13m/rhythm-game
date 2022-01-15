package com.sh13m.rhythmgame.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public abstract class SongInput {
    public static boolean receptor1JustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT);
    }

    public static boolean receptor2JustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.F) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN);
    }

    public static boolean receptor3JustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.J) || Gdx.input.isKeyJustPressed(Input.Keys.UP);
    }

    public static boolean receptor4JustPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.K) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT);
    }

    public static boolean receptor1Pressed() {
        return Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
    }

    public static boolean receptor2Pressed() {
        return Gdx.input.isKeyPressed(Input.Keys.F) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
    }

    public static boolean receptor3Pressed() {
        return Gdx.input.isKeyPressed(Input.Keys.J) || Gdx.input.isKeyPressed(Input.Keys.UP);
    }

    public static boolean receptor4Pressed() {
        return Gdx.input.isKeyPressed(Input.Keys.K) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
    }
}
