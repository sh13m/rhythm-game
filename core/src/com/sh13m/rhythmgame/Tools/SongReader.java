package com.sh13m.rhythmgame.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.sh13m.rhythmgame.Screens.Gameplay;

// not all features of the sm file are implemented in this game
public class SongReader {
    private final FileHandle songFile;
    private final String[] fileLines;

    public float offset;
    private float bpm;
    private int noteType;

    public float timeSinceStart;
    public float measureTime;

    private boolean firstNotesCreated;
    private boolean measureParsed;
    public boolean songEnded;

    public boolean isDrawingCol1Bar;
    public boolean isDrawingCol2Bar;
    public boolean isDrawingCol3Bar;
    public boolean isDrawingCol4Bar;

    private int currentLine;

    private float measureLength;
    private float incrementLength;

    private Array<Character> col1, col2, col3, col4;
    public Array<Rectangle> tap_notes;
    public Array<Rectangle> hold_notes_start;
    public Array<Rectangle> hold_bars;

    public SongReader() {
        songFile = Gdx.files.internal("Songs/LN/yaseta - Bluenation (Penguinosity).sm");
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
        measureLength = Gameplay.SCROLL_SPEED * measureTime;
        timeSinceStart = 0;
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
        incrementLength = measureLength / noteType;
    }

    public void readMeasure(float delta) {
        timeSinceStart += delta;

        if (!measureParsed) {
            parseMeasure();
            for (int i=0; i < noteType; i++) {
                createNotes('1', tap_notes, i);
                createNotes('2', hold_notes_start, i);
            }
            measureParsed = true;
        }

        if (timeSinceStart >= measureTime) {
            measureParsed = false;
            timeSinceStart = 0;
        }
    }

    private void createNotes(char n, Array<Rectangle> arr, int i) {
        // creates an adds a note to its corresponding array
        if (col1.get(i).equals(n)) {
            Rectangle note = new Rectangle(Gameplay.COL1_X, 480 + i * incrementLength,64,64);
            arr.add(note);
        }
        if (col2.get(i).equals(n)) {
            Rectangle note = new Rectangle(Gameplay.COL2_X, 480 + i * incrementLength,64,64);
            arr.add(note);
        }
        if (col3.get(i).equals(n)) {
            Rectangle note = new Rectangle(Gameplay.COL3_X, 480 + i * incrementLength,64,64);
            arr.add(note);
        }
        if (col4.get(i).equals(n)) {
            Rectangle note = new Rectangle(Gameplay.COL4_X, 480 + i * incrementLength,64,64);
            arr.add(note);
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
