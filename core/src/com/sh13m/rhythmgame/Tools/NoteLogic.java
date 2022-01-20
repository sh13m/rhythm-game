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

    // indexes must be lined up in all 3 arrays
    private void updateHolds(SongReader sr,
                             Rectangle receptor1, Rectangle receptor2,
                             Rectangle receptor3, Rectangle receptor4) {
        Head head;
        End end;
        Bar bar;
        int len = sr.activeBars.size;
        for (int i=len; --i >= 0; ) { // moves everything down
            head = sr.activeHeads.get(i);
            end = sr.activeEnds.get(i);
            bar = sr.activeBars.get(i);

            end.update(head, bar);
            head.update(receptor1, receptor2, receptor3, receptor4, bar, end);
            bar.update(head, end);


            if (head.comboBreak) {
                COMBO = 0;
                JUDGEMENT = "MISS";
            }
            if (head.isHit) {
                COMBO++;
                getJudgement(head.getY());
                head.isHit = false;
            }
            if (!head.alive) {
                sr.activeHeads.removeIndex(i);
                sr.headPool.free(head);
            }
            if (!end.alive) {
                sr.activeEnds.removeIndex(i);
                sr.endPool.free(end);
            }
            if (!bar.alive) {
                sr.activeBars.removeIndex(i);
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
