package com.sh13m.rhythmgame.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.sh13m.rhythmgame.Screens.Gameplay;

// not all features of the sm file are implemented in this game
public class SongReader {
    private static final float HOLD_BAR_TIME_INCREMENT = 5f / Gameplay.SCROLL_SPEED;

    private final FileHandle songFile;
    private final String[] fileLines;

    public float offset;
    private float bpm;
    private int noteType;

    private float timeSinceStart;
    private float incrementTime;
    public float measureTime;
    private float timeSinceLastBar;

    private boolean firstNotesCreated;
    private boolean measureParsed;
    public boolean songEnded;

    public boolean isDrawingCol1Bar;
    public boolean isDrawingCol2Bar;
    public boolean isDrawingCol3Bar;
    public boolean isDrawingCol4Bar;
    public boolean col1_hold_missed;
    public boolean col2_hold_missed;
    public boolean col3_hold_missed;
    public boolean col4_hold_missed;

    private int currentLine;
    private int currentMeasurePosition;

    private Array<Character> col1, col2, col3, col4;
    public Array<Rectangle> tap_notes;
    public Array<Rectangle> hold_notes_start;
    public Array<Rectangle> hold_bars;

    public SongReader() {
        songFile = Gdx.files.internal("Songs/LN/bug thief.sm");
        fileLines = songFile.readString().split("\\r?\\n");
        getNoteDataStart();
        getOffset();
        getBPM();

        tap_notes = new Array<>();
        hold_notes_start = new Array<>();
        hold_bars = new Array<>();
        col1 = new Array<>();
        col2 = new Array<>();
        col3 = new Array<>();
        col4 = new Array<>();

        isDrawingCol1Bar = false;
        isDrawingCol2Bar = false;
        isDrawingCol3Bar = false;
        isDrawingCol4Bar = false;

        firstNotesCreated = false;
        measureParsed = false;
        songEnded = false;

        measureTime = 1/(bpm / 60 / 4);
        currentMeasurePosition = 0;
        timeSinceStart = 0;
        timeSinceLastBar = 0;
        noteType = 0;
    }

    private void parseMeasure() {
        noteType = 0;
        col1.setSize(0);
        col2.setSize(0);
        col3.setSize(0);
        col4.setSize(0);
        while (true) {
            if (fileLines[currentLine].equals(",") || fileLines[currentLine].equals(";")) {
                if (fileLines[currentLine].equals(";")) songEnded = true;
                currentLine++;
                break;
            }
            noteType++;
            col1.add(fileLines[currentLine].charAt(0));
            col2.add(fileLines[currentLine].charAt(1));
            col3.add(fileLines[currentLine].charAt(2));
            col4.add(fileLines[currentLine].charAt(3));
            currentLine++;
        }
        incrementTime = measureTime / noteType;
    }

    public void readMeasure(float delta) {
        timeSinceStart += delta;

        if (!measureParsed) {
            parseMeasure();
            measureParsed = true;
        }

        if (!firstNotesCreated) {
            addNotes();
            currentMeasurePosition++;
            firstNotesCreated = true;
        }

        if (timeSinceStart >= incrementTime) {
            if (currentMeasurePosition == noteType) {
                measureParsed = false;
                currentMeasurePosition = 0;
                firstNotesCreated = false;
            } else {
                addNotes();
                currentMeasurePosition++;
            }
            timeSinceStart -= incrementTime;
        }
    }

    private void addNotes() {
        // create tap notes
        createNotes('1', tap_notes);
        // create hold notes
        createNotes('2', hold_notes_start);
        createHoldEnds('3');
    }

    private void createNotes(char n, Array<Rectangle> arr) {
        // creates an adds a note to its corresponding array
        if (col1.get(currentMeasurePosition).equals(n)) {
            Rectangle note = new Rectangle(Gameplay.COL1_X, 480,64,64);
            arr.add(note);
            if (n == '2' || n == '4') isDrawingCol1Bar = true;
        }
        if (col2.get(currentMeasurePosition).equals(n)) {
            Rectangle note = new Rectangle(Gameplay.COL2_X, 480,64,64);
            arr.add(note);
            if (n == '2' || n == '4') isDrawingCol2Bar = true;
        }
        if (col3.get(currentMeasurePosition).equals(n)) {
            Rectangle note = new Rectangle(Gameplay.COL3_X, 480,64,64);
            arr.add(note);
            if (n == '2' || n == '4') isDrawingCol3Bar = true;
        }
        if (col4.get(currentMeasurePosition).equals(n)) {
            Rectangle note = new Rectangle(Gameplay.COL4_X, 480,64,64);
            arr.add(note);
            if (n == '2' || n == '4') isDrawingCol4Bar = true;
        }
    }

    private void createHoldEnds(char n) {
        if (col1.get(currentMeasurePosition).equals(n)) {
            isDrawingCol1Bar = false;
        }
        if (col2.get(currentMeasurePosition).equals(n)) {
            isDrawingCol2Bar = false;
        }
        if (col3.get(currentMeasurePosition).equals(n)) {
            isDrawingCol3Bar = false;
        }
        if (col4.get(currentMeasurePosition).equals(n)) {
            isDrawingCol4Bar = false;
        }
    }

    public void addHoldBars(float delta) {
        timeSinceLastBar += delta;
        if (timeSinceLastBar >= HOLD_BAR_TIME_INCREMENT) {
            if (isDrawingCol1Bar) {
                Rectangle bar = new Rectangle(Gameplay.COL1_X, 512,64,16);
                hold_bars.add(bar);
            }
            if (isDrawingCol2Bar) {
                Rectangle bar = new Rectangle(Gameplay.COL2_X, 512,64,16);
                hold_bars.add(bar);
            }
            if (isDrawingCol3Bar) {
                Rectangle bar = new Rectangle(Gameplay.COL3_X, 512,64,16);
                hold_bars.add(bar);
            }
            if (isDrawingCol4Bar) {
                Rectangle bar = new Rectangle(Gameplay.COL4_X, 512,64,16);
                hold_bars.add(bar);
            }
            timeSinceLastBar = 0;
        }
    }


    private void getNoteDataStart() {
        for (int i = 0; i < fileLines.length; i++) {
            if (fileLines[i].length() == 4
                    && (fileLines[i].charAt(fileLines[i].length() - 1) != ';'
                    || fileLines[i].charAt(fileLines[i].length() - 1) != ':')) {
                currentLine = i;
                break;
            }
        }
    }

    private void getOffset() {
        for (String line : fileLines) {
            if (line.contains("#OFFSET:")) {
                offset = Float.parseFloat(line.substring(8, line.length()-1));
                break;
            }
        }
    }

    private void getBPM() { // only works with maps with constant bpm currently
        for (String line : fileLines) {
            if (line.contains("#BPMS:")) {
                char a = 0;
                while (a != '=') {
                    a = line.charAt(0);
                    line = new StringBuilder(line).deleteCharAt(0).toString();
                }
                bpm = Float.parseFloat(line.substring(0, line.length()-1));
                break;
            }
        }
    }
}
