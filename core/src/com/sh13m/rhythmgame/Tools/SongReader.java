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

    private boolean measureParsed;
    public boolean songEnded;
    private boolean lastMeasure;

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
    public Array<Rectangle> hold_notes_end;

    public SongReader() {
        songFile = Gdx.files.internal("Songs/LN/yaseta - Bluenation (Penguinosity).sm");
        fileLines = songFile.readString().split("\\r?\\n");
        getNoteDataStart();
        getOffset();
        getBPM();

        tap_notes = new Array<>();
        hold_notes_start = new Array<>();
        hold_bars = new Array<>();
        hold_notes_end = new Array<>();
        col1 = new Array<>();
        col2 = new Array<>();
        col3 = new Array<>();
        col4 = new Array<>();

        isDrawingCol1Bar = false;
        isDrawingCol2Bar = false;
        isDrawingCol3Bar = false;
        isDrawingCol4Bar = false;

        measureParsed = false;
        lastMeasure = false;
        songEnded = false;

        measureTime = 1/(bpm / 60 / 4);
        measureLength = Gameplay.SCROLL_SPEED * measureTime;
        timeSinceStart = 0;

        noteType = 0;
    }

    private void readMeasure() {
        noteType = 0;
        col1.setSize(0);
        col2.setSize(0);
        col3.setSize(0);
        col4.setSize(0);
        while (true) {
            if (fileLines[currentLine].equals(",") || fileLines[currentLine].equals(";")) {
                if (fileLines[currentLine].equals(";")) lastMeasure = true;
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

    public void parseMeasure(float delta) {
        timeSinceStart += delta;

        if (!measureParsed) {
            readMeasure();
            for (int i=0; i < noteType; i++) {
                createTapNotes(i);
                createHoldNotes(i);
                createHoldNoteEnd(i);
                createBar(i);
            }
            measureParsed = true;
        }

        if (timeSinceStart >= measureTime) {
            if (lastMeasure) songEnded = true;
            else {
                measureParsed = false;
                timeSinceStart -= measureTime;
            }
        }
    }

    private void createTapNotes(int i) {
        // creates an adds a note to its corresponding array
        if (col1.get(i).equals('1')) {
            Rectangle note = new Rectangle(Gameplay.COL1_X, 480 + i * incrementLength,64,64);
            tap_notes.add(note);
        }
        if (col2.get(i).equals('1')) {
            Rectangle note = new Rectangle(Gameplay.COL2_X, 480 + i * incrementLength,64,64);
            tap_notes.add(note);
        }
        if (col3.get(i).equals('1')) {
            Rectangle note = new Rectangle(Gameplay.COL3_X, 480 + i * incrementLength,64,64);
            tap_notes.add(note);
        }
        if (col4.get(i).equals('1')) {
            Rectangle note = new Rectangle(Gameplay.COL4_X, 480 + i * incrementLength,64,64);
            tap_notes.add(note);
        }
    }

    private void createHoldNotes(int i) {
        // create the heads
        if (col1.get(i).equals('2')) {
            Rectangle note = new Rectangle(Gameplay.COL1_X, 480 + i * incrementLength,64,64);
            hold_notes_start.add(note);
            isDrawingCol1Bar = true;
        }
        if (col2.get(i).equals('2')) {
            Rectangle note = new Rectangle(Gameplay.COL2_X, 480 + i * incrementLength,64,64);
            hold_notes_start.add(note);
            isDrawingCol2Bar = true;
        }
        if (col3.get(i).equals('2')) {
            Rectangle note = new Rectangle(Gameplay.COL3_X, 480 + i * incrementLength,64,64);
            hold_notes_start.add(note);
            isDrawingCol3Bar = true;
        }
        if (col4.get(i).equals('2')) {
            Rectangle note = new Rectangle(Gameplay.COL4_X, 480 + i * incrementLength,64,64);
            hold_notes_start.add(note);
            isDrawingCol4Bar = true;
        }
    }

    private void createBar(int i) {
        // create bars between tape note heads and ends
        if (isDrawingCol1Bar) {
            int parts = 192 / noteType;
            float height = incrementLength*1.05f/parts;
            for (int x=0; x < parts; x++) {
                Rectangle bar = new Rectangle(Gameplay.COL1_X, 512 + i*incrementLength+ x*height, 64, height);
                hold_bars.add(bar);
            }
        }
        if (isDrawingCol2Bar) {
            int parts = 192 / noteType;
            float height = incrementLength*1.05f/parts;
            for (int x=0; x < parts; x++) {
                Rectangle bar = new Rectangle(Gameplay.COL2_X, 512 + i*incrementLength+ x*height, 64, height);
                hold_bars.add(bar);
            }
        }
        if (isDrawingCol3Bar) {
            int parts = 192 / noteType;
            float height = incrementLength*1.05f/parts;
            for (int x=0; x < parts; x++) {
                Rectangle bar = new Rectangle(Gameplay.COL3_X, 512 + i*incrementLength+ x*height, 64, height);
                hold_bars.add(bar);
            }
        }
        if (isDrawingCol4Bar) {
            int parts = 192 / noteType;
            float height = incrementLength*1.05f/parts;
            for (int x=0; x < parts; x++) {
                Rectangle bar = new Rectangle(Gameplay.COL4_X, 512 + i*incrementLength+ x*height, 64, height);
                hold_bars.add(bar);
            }
        }

    }

    private void createHoldNoteEnd(int i) {
        if (col1.get(i).equals('3')) {
            Rectangle end = new Rectangle(Gameplay.COL1_X, 512 + i * incrementLength,64, incrementLength*1.05f);
            hold_notes_end.add(end);
            isDrawingCol1Bar = false;
        }
        if (col2.get(i).equals('3')) {
            Rectangle end = new Rectangle(Gameplay.COL2_X, 512 + i * incrementLength,64, incrementLength);
            hold_notes_end.add(end);
            isDrawingCol2Bar = false;
        }
        if (col3.get(i).equals('3')) {
            Rectangle end = new Rectangle(Gameplay.COL3_X, 512 + i * incrementLength,64, incrementLength);
            hold_notes_end.add(end);
            isDrawingCol3Bar = false;
        }
        if (col4.get(i).equals('3')) {
            Rectangle end = new Rectangle(Gameplay.COL4_X, 512 + i * incrementLength,64, incrementLength);
            hold_notes_end.add(end);
            isDrawingCol4Bar = false;
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
