package com.sh13m.rhythmgame.Tools;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.sh13m.rhythmgame.Objects.Bar;
import com.sh13m.rhythmgame.Objects.End;
import com.sh13m.rhythmgame.Objects.Head;
import com.sh13m.rhythmgame.Objects.TapNote;
import com.sh13m.rhythmgame.Screens.Gameplay;

public class NoteLogic {
    public int COMBO;
    public int MAX_COMBO;
    public String JUDGEMENT;
    public int HEALTH;
    public float ACCURACY;
    public long SCORE;
    public int MAX_COUNT;
    public int PERFECT_COUNT;
    public int GREAT_COUNT;
    public int GOOD_COUNT;
    public int BAD_COUNT;
    public int MISS_COUNT;

    private final Array<Float> accuracyIndex;

    public NoteLogic() {
        COMBO = 0;
        MAX_COMBO = 0;
        JUDGEMENT = "NONE";
        HEALTH = 100;
        ACCURACY = 100;
        SCORE = 0;
        accuracyIndex = new Array<>();

        MAX_COUNT = 0;
        PERFECT_COUNT = 0;
        GREAT_COUNT = 0;
        GOOD_COUNT = 0;
        BAD_COUNT = 0;
        MISS_COUNT = 0;
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
            if (note.isHit) getJudgement(note.getY());
            if (note.missed) {
                JUDGEMENT = "MISS";
                MISS_COUNT++;
                updateAccuracy(0);
            }
            if (note.comboAdd) {
                COMBO++;
                if (COMBO > MAX_COMBO) MAX_COMBO = COMBO;
            }
            if (note.comboBreak) {
                COMBO = 0;
                HEALTH -= 15;
                if (HEALTH < 0) HEALTH = 0;
            }
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

            end.update(head);
            head.update(receptor1, receptor2, receptor3, receptor4, end);
            bar.update(head);


            if (head.comboBreak) {
                COMBO = 0;
                if (head.gotJudgement) {
                    JUDGEMENT = "BAD";
                    BAD_COUNT++;
                    updateAccuracy(60);
                    HEALTH -= 10;
                }
                else {
                    JUDGEMENT = "MISS";
                    MISS_COUNT++;
                    updateAccuracy(0);
                    HEALTH -= 15;
                }
                if (HEALTH < 0) HEALTH = 0;
                head.comboBreak = false;
            }
            if (head.isHit) {
                COMBO++;
                if (COMBO > MAX_COMBO) MAX_COMBO = COMBO;
                getJudgement(head.getY());
                head.gotJudgement = true;
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
        if (y < (float)Gameplay.R_HEIGHT - 54) {
            JUDGEMENT = "BAD";
            BAD_COUNT++;
            updateAccuracy(60);
            SCORE += 600;
            HEALTH += 1;
            if (HEALTH > 100) HEALTH = 100;
        }
        else if (y < (float)Gameplay.R_HEIGHT - 42) {
            JUDGEMENT = "GOOD";
            GOOD_COUNT++;
            updateAccuracy(70);
            SCORE += 700;
            HEALTH += 2;
            if (HEALTH > 100) HEALTH = 100;
        }
        else if (y < (float)Gameplay.R_HEIGHT - 28) {
            JUDGEMENT = "GREAT";
            GREAT_COUNT++;
            updateAccuracy(80);
            SCORE += 800;
            HEALTH += 3;
            if (HEALTH > 100) HEALTH = 100;
        }
        else if (y < (float)Gameplay.R_HEIGHT - 14) {
            JUDGEMENT = "PERFECT";
            PERFECT_COUNT++;
            updateAccuracy(100);
            SCORE += 900;
            HEALTH += 5;
            if (HEALTH > 100) HEALTH = 100;
        }
        else if (y < (float)Gameplay.R_HEIGHT + 14) {
            JUDGEMENT = "MAX";
            MAX_COUNT++;
            updateAccuracy(100);
            SCORE += 1000;
            HEALTH += 5;
            if (HEALTH > 100) HEALTH = 100;
        }
        else if (y < (float)Gameplay.R_HEIGHT + 28) {
            JUDGEMENT = "PERFECT";
            PERFECT_COUNT++;
            updateAccuracy(100);
            SCORE += 900;
            HEALTH += 5;
            if (HEALTH > 100) HEALTH = 100;
        }
        else if (y < (float)Gameplay.R_HEIGHT + 42) {
            JUDGEMENT = "GREAT";
            GREAT_COUNT++;
            updateAccuracy(80);
            SCORE += 800;
            HEALTH += 3;
            if (HEALTH > 100) HEALTH = 100;
        }
        else if (y < (float)Gameplay.R_HEIGHT + 54) {
            JUDGEMENT = "GOOD";
            GOOD_COUNT++;
            updateAccuracy(70);
            SCORE += 700;
            HEALTH += 2;
            if (HEALTH > 100) HEALTH = 100;
        }
        else if (y < (float)Gameplay.R_HEIGHT + 64) {
            JUDGEMENT = "BAD";
            BAD_COUNT++;
            updateAccuracy(60);
            SCORE += 600;
            HEALTH += 1;
            if (HEALTH > 100) HEALTH = 100;
        }
    }

    private void updateAccuracy(float acc) {
        accuracyIndex.add(acc);
        float sum = 0;
        for (float num : accuracyIndex) {
            sum += num;
        }
        ACCURACY = sum / accuracyIndex.size;
    }
}
