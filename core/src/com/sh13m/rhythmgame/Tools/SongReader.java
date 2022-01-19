package com.sh13m.rhythmgame.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.sh13m.rhythmgame.Objects.Bar;
import com.sh13m.rhythmgame.Objects.End;
import com.sh13m.rhythmgame.Objects.Head;
import com.sh13m.rhythmgame.Objects.TapNote;
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

    private boolean lastMeasure;

    public boolean isCreatingCol1Bar;
    public boolean isCreatingCol2Bar;
    public boolean isCreatingCol3Bar;
    public boolean isCreatingCol4Bar;
    private static Bar col1Bar;
    private static Bar col2Bar;
    private static Bar col3Bar;
    private static Bar col4Bar;


    private int currentLine;

    private final float measureLength;
    private float incrementLength;

    private final Array<Character> col1, col2, col3, col4;

    public final Array<TapNote> activeTapNotes = new Array<>();
    public final Pool<TapNote> tapNotePool = new Pool<TapNote>() {
        @Override
        protected TapNote newObject() {
            return new TapNote();
        }
    };
    public final Array<Head> activeHeads = new Array<>();
    public final Pool<Head> headPool = new Pool<Head>() {
        @Override
        protected Head newObject() {
            return new Head();
        }
    };
    public final Array<Bar> activeBars = new Array<>();
    public final Pool<Bar> barPool = new Pool<Bar>() {
        @Override
        protected Bar newObject() {
            return new Bar();
        }
    };
    public final Array<End> activeEnds = new Array<>();
    public final Pool<End> endPool = new Pool<End>() {
        @Override
        protected End newObject() {
            return new End();
        }
    };

    public SongReader(int level) {
        songFile = Gdx.files.internal("Songs/" + level + "/map.sm");
        fileLines = songFile.readString().split("\\r?\\n");
        getNoteDataStart();
        getOffset();
        getBPM();

        col1 = new Array<>();
        col2 = new Array<>();
        col3 = new Array<>();
        col4 = new Array<>();

        isCreatingCol1Bar = false;
        isCreatingCol2Bar = false;
        isCreatingCol3Bar = false;
        isCreatingCol4Bar = false;

        lastMeasure = false;
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
            if (fileLines[currentLine].contains(",") || fileLines[currentLine].contains(";")) {
                if (fileLines[currentLine].contains(";")) lastMeasure = true;
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

    public void parseMeasure() { // reads entire song now
        float startHeight = 480;

        while (!lastMeasure) {
            readMeasure();
            for (int i=0; i < noteType; ++i) {
                createTapNotes(i, startHeight);
                createHoldHeads(i, startHeight);
                createHoldEnds(i, startHeight);
                createBar();
            }
            startHeight += measureLength;
        }
    }

    private void createTapNotes(int i, float startHeight) {
        // creates an adds a note to its corresponding array
        if (col1.get(i).equals('1')) {
            TapNote note = tapNotePool.obtain();
            note.init(Gameplay.COL1_X, startHeight + i * incrementLength,64,64);
            activeTapNotes.add(note);
        }
        if (col2.get(i).equals('1')) {
            TapNote note = tapNotePool.obtain();
            note.init(Gameplay.COL2_X, startHeight + i * incrementLength,64,64);
            activeTapNotes.add(note);
        }
        if (col3.get(i).equals('1')) {
            TapNote note = tapNotePool.obtain();
            note.init(Gameplay.COL3_X, startHeight + i * incrementLength,64,64);
            activeTapNotes.add(note);
        }
        if (col4.get(i).equals('1')) {
            TapNote note = tapNotePool.obtain();
            note.init(Gameplay.COL4_X, startHeight + i * incrementLength,64,64);
            activeTapNotes.add(note);
        }
    }

    private void createHoldHeads(int i, float startHeight) {
        if (col1.get(i).equals('2') || col1.get(i).equals('4')) {
            Head note = headPool.obtain();
            note.init(Gameplay.COL1_X, startHeight + i * incrementLength,64,64);
            activeHeads.add(note);
            col1Bar = barPool.obtain();
            col1Bar.init(Gameplay.COL1_X, startHeight + 32 + i * incrementLength, 64, 0);
            isCreatingCol1Bar = true;
        }
        if (col2.get(i).equals('2') || col2.get(i).equals('4')) {
            Head note = headPool.obtain();
            note.init(Gameplay.COL2_X, startHeight + i * incrementLength,64,64);
            activeHeads.add(note);
            col2Bar = barPool.obtain();
            col2Bar.init(Gameplay.COL2_X, startHeight + 32 + i * incrementLength, 64, 0);
            isCreatingCol2Bar = true;
        }
        if (col3.get(i).equals('2') || col3.get(i).equals('4')) {
            Head note = headPool.obtain();
            note.init(Gameplay.COL3_X, startHeight + i * incrementLength,64,64);
            activeHeads.add(note);
            col3Bar = barPool.obtain();
            col3Bar.init(Gameplay.COL3_X, startHeight + 32 + i * incrementLength, 64, 0);
            isCreatingCol3Bar = true;
        }
        if (col4.get(i).equals('2') || col4.get(i).equals('4')) {
            Head note = headPool.obtain();
            note.init(Gameplay.COL4_X, startHeight + i * incrementLength,64,64);
            activeHeads.add(note);
            col4Bar = barPool.obtain();
            col4Bar.init(Gameplay.COL4_X, startHeight + 32 + i * incrementLength, 64, 0);
            isCreatingCol4Bar = true;
        }
    }

    private void createBar() {
        // create bars between tape note heads and ends
        if (isCreatingCol1Bar) {
            col1Bar.setHeight(col1Bar.getHeight() + incrementLength);
        }
        if (isCreatingCol2Bar) {
            col2Bar.setHeight(col2Bar.getHeight() + incrementLength);
        }
        if (isCreatingCol3Bar) {
            col3Bar.setHeight(col3Bar.getHeight() + incrementLength);
        }
        if (isCreatingCol4Bar) {
            col4Bar.setHeight(col4Bar.getHeight() + incrementLength);
        }
    }

    private void createHoldEnds(int i, float startHeight) {
        if (col1.get(i).equals('3')) {
            End end = endPool.obtain();
            end.init(Gameplay.COL1_X, startHeight + i * incrementLength,64, incrementLength);
            activeEnds.add(end);
            isCreatingCol1Bar = false;
            activeBars.add(col1Bar);
        }
        if (col2.get(i).equals('3')) {
            End end = endPool.obtain();
            end.init(Gameplay.COL2_X, startHeight + i * incrementLength,64, incrementLength);
            activeEnds.add(end);
            isCreatingCol2Bar = false;
            activeBars.add(col2Bar);
        }
        if (col3.get(i).equals('3')) {
            End end = endPool.obtain();
            end.init(Gameplay.COL3_X, startHeight + i * incrementLength,64, incrementLength);
            activeEnds.add(end);
            isCreatingCol3Bar = false;
            activeBars.add(col3Bar);
        }
        if (col4.get(i).equals('3')) {
            End end = endPool.obtain();
            end.init(Gameplay.COL4_X, startHeight + i * incrementLength,64, incrementLength);
            activeEnds.add(end);
            isCreatingCol4Bar = false;
            activeBars.add(col4Bar);
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

    public String getSongFileName() {
        String name = "";
        for (String line : fileLines) {
            if (line.contains("#MUSIC:")) {
                name = line.substring(7, line.length()-1);
                break;
            }
        }
        return name;
    }
}
