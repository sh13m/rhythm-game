package com.sh13m.rhythmgame.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.sh13m.rhythmgame.Objects.TapNote;
import com.sh13m.rhythmgame.Screens.Gameplay;

import java.util.Iterator;

public class NoteLogic {
    private static float HOLD_CHECK_PERIOD = 0.15f;
    public int COMBO;
    public String JUDGEMENT;

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
    public Array<Rectangle> holdFX;

    public NoteLogic() {
        COMBO = 0;
        JUDGEMENT = "NONE";
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
    }

    public void updateNotes(float delta, SongReader sr,
                             Rectangle receptor1, Rectangle receptor2,
                             Rectangle receptor3, Rectangle receptor4) {
        updateTapNotes(sr, receptor1, receptor2, receptor3, receptor4);
        updateHoldNoteHeads(sr, receptor1, receptor2, receptor3, receptor4);
        updateHoldBars(delta, sr);
        updateHoldNoteEnds(sr);
        updateHoldFX();
    }

    private void updateTapNotes(SongReader sr,
                                Rectangle receptor1, Rectangle receptor2,
                                Rectangle receptor3, Rectangle receptor4) {
        TapNote note;
        int len = sr.activeTapNotes.size;
        for (int i=len; --i >= 0; ) {
            note = sr.activeTapNotes.get(i);
            note.update(receptor1, receptor2, receptor3, receptor4);
            if (note.comboAdd) COMBO++;
            if (note.comboBreak) COMBO = 0;
            if (note.missed) JUDGEMENT = "MISS";
            if (note.hit) getJudgement(note.getY());
            if (!note.alive) {
                sr.activeTapNotes.removeIndex(i);
                sr.tapNotePool.free(note);
            }
        }

    }

    private void updateHoldNoteHeads(SongReader sr,
                                     Rectangle receptor1, Rectangle receptor2,
                                     Rectangle receptor3, Rectangle receptor4) {
        for (Iterator<Rectangle> iter = sr.hold_notes_start.iterator(); iter.hasNext(); ) {
            Rectangle note = iter.next();
            note.y -= Gameplay.SCROLL_SPEED * Gdx.graphics.getDeltaTime();
            // remove notes if they fall off-screen
            if (note.y + 64 < 0) {
                iter.remove();
                COMBO = 0;
                JUDGEMENT = "MISS";
            }
            // hold note head successfully hit
            if (note.overlaps(receptor1)) {
                if (SongInput.receptor1JustPressed()) {
                    iter.remove();
                    getJudgement(note.y);
                    COMBO++;
                    col1isHeld = true;
                    col1HoldMissed = false;
                    Rectangle noteFX = new Rectangle(Gameplay.COL1_X, Gameplay.R_HEIGHT, 64, 64);
                    holdFX.add(noteFX);
                }
            }
            if (note.overlaps(receptor2)) {
                if (SongInput.receptor2JustPressed()) {
                    iter.remove();
                    getJudgement(note.y);
                    COMBO++;
                    col2isHeld = true;
                    col2HoldMissed = false;
                    Rectangle noteFX = new Rectangle(Gameplay.COL2_X, Gameplay.R_HEIGHT, 64, 64);
                    holdFX.add(noteFX);
                }
            }
            if (note.overlaps(receptor3)) {
                if (SongInput.receptor3JustPressed()) {
                    iter.remove();
                    getJudgement(note.y);
                    COMBO++;
                    col3isHeld = true;
                    col3HoldMissed = false;
                    Rectangle noteFX = new Rectangle(Gameplay.COL3_X, Gameplay.R_HEIGHT, 64, 64);
                    holdFX.add(noteFX);
                }
            }
            if (note.overlaps(receptor4)) {
                if (SongInput.receptor4JustPressed()) {
                    iter.remove();
                    getJudgement(note.y);
                    COMBO++;
                    col4isHeld = true;
                    col4HoldMissed = false;
                    Rectangle noteFX = new Rectangle(Gameplay.COL4_X, Gameplay.R_HEIGHT, 64, 64);
                    holdFX.add(noteFX);
                }
            }
            // window to hit hold note has been missed
            if (note.y + 64 < Gameplay.R_HEIGHT) {
                if (note.x == Gameplay.COL1_X && !col1isHeld) {
                    col1isHeld = false;
                    col1HoldMissed = true;
                    col1HoldComboBreak = false;
                }
                if (note.x == Gameplay.COL2_X && !col2isHeld) {
                    col2isHeld = false;
                    col2HoldMissed = true;
                    col2HoldComboBreak = false;
                }
                if (note.x == Gameplay.COL3_X && !col3isHeld) {
                    col3isHeld = false;
                    col3HoldMissed = true;
                    col3HoldComboBreak = false;
                }
                if (note.x == Gameplay.COL4_X && !col4isHeld) {
                    col4isHeld = false;
                    col4HoldMissed = true;
                    col4HoldComboBreak = false;
                }
            }
        }
    }

    private void updateHoldBars(float delta, SongReader sr) {
        for (Iterator<Rectangle> iter = sr.hold_bars.iterator(); iter.hasNext(); ) {
            try { // really jank fix but seems to work
                Rectangle bar = iter.next();
                bar.y -= Gameplay.SCROLL_SPEED * Gdx.graphics.getDeltaTime();
                // remove bars if they fall off-screen
                if (bar.y + bar.height < 0) iter.remove();

                // bar is held and not missed
                if (bar.x == Gameplay.COL1_X && !col1HoldMissed && bar.y < Gameplay.R_HEIGHT + 30) {
                    iter.remove();
                }
                if (bar.x == Gameplay.COL2_X && !col2HoldMissed &&  bar.y < Gameplay.R_HEIGHT + 30) {
                    iter.remove();
                }
                if (bar.x == Gameplay.COL3_X && !col3HoldMissed && bar.y < Gameplay.R_HEIGHT + 30) {
                    iter.remove();
                }
                if (bar.x == Gameplay.COL4_X && !col4HoldMissed && bar.y < Gameplay.R_HEIGHT + 30) {
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
                        COMBO = 0;
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
                        COMBO = 0;
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
                        COMBO = 0;
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
                        COMBO = 0;
                        col4HoldComboBreak = true;
                    }
                }
                col4HoldCheckDelta -= HOLD_CHECK_PERIOD;
            }
        }
    }

    private void updateHoldNoteEnds(SongReader sr) {
        for (Iterator<Rectangle> iter = sr.hold_notes_end.iterator(); iter.hasNext(); ) {
            Rectangle end = iter.next();
            end.y -= Gameplay.SCROLL_SPEED * Gdx.graphics.getDeltaTime();
            // remove bars if they fall off-screen
            if (end.y < Gameplay.R_HEIGHT) {
                iter.remove();
                if (end.x == Gameplay.COL1_X) {
                    col1isHeld = false;
                    col1HoldComboBreak = false;
                    col1HoldCheckDelta = 0;
                }
                if (end.x == Gameplay.COL2_X) {
                    col2isHeld = false;
                    col2HoldComboBreak = false;
                    col2HoldCheckDelta = 0;
                }
                if (end.x == Gameplay.COL3_X) {
                    col3isHeld = false;
                    col3HoldComboBreak = false;
                    col3HoldCheckDelta = 0;
                }
                if (end.x == Gameplay.COL4_X) {
                    col4isHeld = false;
                    col4HoldComboBreak = false;
                    col4HoldCheckDelta = 0;
                }
            }
        }
    }

    private void updateHoldFX() {
        for (Iterator<Rectangle> iter = holdFX.iterator(); iter.hasNext(); ) {
            Rectangle noteFX = iter.next();
            if (noteFX.y + 64 < 0) iter.remove();
            if (noteFX.x == Gameplay.COL1_X && (col1HoldComboBreak || noteFX.y < Gameplay.R_HEIGHT)) noteFX.y -= Gameplay.SCROLL_SPEED * Gdx.graphics.getDeltaTime();
            if (noteFX.x == Gameplay.COL2_X && (col2HoldComboBreak || noteFX.y < Gameplay.R_HEIGHT)) noteFX.y -= Gameplay.SCROLL_SPEED * Gdx.graphics.getDeltaTime();
            if (noteFX.x == Gameplay.COL3_X && (col3HoldComboBreak || noteFX.y < Gameplay.R_HEIGHT)) noteFX.y -= Gameplay.SCROLL_SPEED * Gdx.graphics.getDeltaTime();
            if (noteFX.x == Gameplay.COL4_X && (col4HoldComboBreak || noteFX.y < Gameplay.R_HEIGHT)) noteFX.y -= Gameplay.SCROLL_SPEED * Gdx.graphics.getDeltaTime();
            if (noteFX.x == Gameplay.COL1_X && !col1isHeld && !col1HoldMissed) iter.remove();
            if (noteFX.x == Gameplay.COL2_X && !col2isHeld && !col2HoldMissed) iter.remove();
            if (noteFX.x == Gameplay.COL3_X && !col3isHeld && !col3HoldMissed) iter.remove();
            if (noteFX.x == Gameplay.COL4_X && !col4isHeld && !col4HoldMissed) iter.remove();
        }
    }

    private void getJudgement(float y) {
        if (y < (float)Gameplay.R_HEIGHT - 54) JUDGEMENT = "BAD";
        else if (y < (float)Gameplay.R_HEIGHT - 42) JUDGEMENT = "GOOD";
        else if (y < (float)Gameplay.R_HEIGHT - 28) JUDGEMENT = "GREAT";
        else if (y < (float)Gameplay.R_HEIGHT - 14) JUDGEMENT = "PERFECT";
        else if (y < (float)Gameplay.R_HEIGHT + 14) JUDGEMENT = "MAX";
        else if (y < (float)Gameplay.R_HEIGHT + 28) JUDGEMENT = "PERFECT";
        else if (y < (float)Gameplay.R_HEIGHT + 42) JUDGEMENT = "GREAT";
        else if (y < (float)Gameplay.R_HEIGHT + 54) JUDGEMENT = "GOOD";
        else if (y < (float)Gameplay.R_HEIGHT + 64) JUDGEMENT = "BAD";
    }
}
