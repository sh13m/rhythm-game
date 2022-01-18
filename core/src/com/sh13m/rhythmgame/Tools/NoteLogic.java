package com.sh13m.rhythmgame.Tools;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.sh13m.rhythmgame.Objects.TapNote;
import com.sh13m.rhythmgame.Screens.Gameplay;

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
