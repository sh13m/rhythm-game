package com.sh13m.rhythmgame.Tools;

import com.badlogic.gdx.math.Rectangle;
import com.sh13m.rhythmgame.Objects.Bar;
import com.sh13m.rhythmgame.Objects.End;
import com.sh13m.rhythmgame.Objects.Head;
import com.sh13m.rhythmgame.Objects.TapNote;
import com.sh13m.rhythmgame.Screens.Gameplay;

public class NoteLogic {
    private static float HOLD_CHECK_PERIOD = 0.15f;
    public int COMBO;
    public String JUDGEMENT;

    public static boolean col1HoldEnd = false;
    public static boolean col2HoldEnd = false;
    public static boolean col3HoldEnd = false;
    public static boolean col4HoldEnd = false;

    public NoteLogic() {
        COMBO = 0;
        JUDGEMENT = "NONE";
    }

    public void updateNotes(SongReader sr,
                            Rectangle receptor1, Rectangle receptor2,
                            Rectangle receptor3, Rectangle receptor4) {
        updateTapNotes(sr, receptor1, receptor2, receptor3, receptor4);
        updateHolds(sr, receptor1, receptor2, receptor3, receptor4);
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
            if (note.isHit) getJudgement(note.getY());
            if (!note.alive) {
                sr.activeTapNotes.removeIndex(i);
                sr.tapNotePool.free(note);
            }
        }
    }

    private void updateHolds(SongReader sr,
                             Rectangle receptor1, Rectangle receptor2,
                             Rectangle receptor3, Rectangle receptor4) {

        End end;
        int eLen = sr.activeEnds.size;
        for (int i=eLen; --i >= 0; ) {
            end = sr.activeEnds.get(i);
            end.update(receptor1, receptor2, receptor3, receptor4);

            if (!end.alive) {
                sr.activeEnds.removeIndex(i);
                sr.endPool.free(end);
            }
        }
        Head head;
        int hLen = sr.activeHeads.size;
        for (int j=hLen; --j >= 0; ) {
            head = sr.activeHeads.get(j);
            head.update(receptor1, receptor2, receptor3, receptor4);

            if (!head.alive) {
                sr.activeHeads.removeIndex(j);
                sr.headPool.free(head);
            }
        }
        Bar bar;
        int bLen = sr.activeBars.size;
        for (int k=bLen; --k >= 0; ) {
            bar = sr.activeBars.get(k);
            bar.update();

            if (!bar.alive) {
                sr.activeBars.removeIndex(k);
                sr.barPool.free(bar);
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
