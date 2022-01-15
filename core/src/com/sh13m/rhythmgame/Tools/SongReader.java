package com.sh13m.rhythmgame.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.sh13m.rhythmgame.RhythmGame;

public class SongReader {
    private static final float HOLD_BAR_TIME_INCREMENT = 0.01f;

    private final FileHandle song_file;
    private final String[] file_lines;

    public float offset;
    private float bpm;
    private int note_type;

    private float time_since_start;
    private float increment_time;
    private float measure_time;
    private float time_since_last_bar;

    private boolean first_notes_created;
    private boolean measure_parsed;
    public boolean song_ended;

    public boolean col1_held;
    public boolean col2_held;
    public boolean col3_held;
    public boolean col4_held;
    public boolean col1_hold_missed;
    public boolean col2_hold_missed;
    public boolean col3_hold_missed;
    public boolean col4_hold_missed;

    private int current_line;
    private int current_measure_position;

    private Array<Character> col1, col2, col3, col4;
    public Array<Rectangle> tap_notes;
    public Array<Rectangle> hold_notes_start;
    public Array<Rectangle> hold_bars;
    public Array<Rectangle> hold_notes_end;

    public SongReader() {
        song_file = Gdx.files.internal("Songs/der wald (Wh1teh)/derwald.sm");
        file_lines = song_file.readString().split("\\r?\\n");
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

        col1_held = false;
        col2_held = false;
        col3_held = false;
        col4_held = false;


        first_notes_created = false;
        measure_parsed = false;
        song_ended = false;

        measure_time = 1/(bpm / 60 / 4);
        current_measure_position = 0;
        time_since_start = 0;
        time_since_last_bar = 0;
        note_type = 0;
    }

    private void parseMeasure() {
        note_type = 0;
        col1.setSize(0);
        col2.setSize(0);
        col3.setSize(0);
        col4.setSize(0);
        while (true) {
            if (file_lines[current_line].equals(",") || file_lines[current_line].equals(";")) {
                if (file_lines[current_line].equals(";")) song_ended = true;
                current_line++;
                break;
            }
            note_type++;
            col1.add(file_lines[current_line].charAt(0));
            col2.add(file_lines[current_line].charAt(1));
            col3.add(file_lines[current_line].charAt(2));
            col4.add(file_lines[current_line].charAt(3));
            current_line++;
        }
        increment_time = measure_time / note_type;
    }

    public void readMeasure(float delta) {
        time_since_start += delta;
        if (!measure_parsed) {
            parseMeasure();
            measure_parsed = true;
        }
        if (!first_notes_created) {
            addNotes();
            first_notes_created = true;
        }
        if (time_since_start >= increment_time) {
            addNotes();
            time_since_start -= increment_time;
        }
        if (current_measure_position == note_type) {
            measure_parsed = false;
            current_measure_position = 0;
        }
    }

    private void addNotes() {
        // create tap notes
        createNotes('1', tap_notes);
        // create hold notes
        createNotes('2', hold_notes_start);
        createNotes('3', hold_notes_end);
        current_measure_position++;
    }

    private void createNotes(char n, Array<Rectangle> arr) {
        // creates an adds a note to its corresponding array
        if (col1.get(current_measure_position).equals(n)) {
            Rectangle note = new Rectangle(RhythmGame.V_WIDTH / 2 - 128, 480,64,64);
            arr.add(note);
            if (n == '2' || n == '4') col1_held = true;
            if (n == '3') {
                col1_held = false;
                col1_hold_missed = false;
            }
        }
        if (col2.get(current_measure_position).equals(n)) {
            Rectangle note = new Rectangle(RhythmGame.V_WIDTH / 2 - 64, 480,64,64);
            arr.add(note);
            if (n == '2' || n == '4') col2_held = true;
            if (n == '3') {
                col2_held = false;
                col2_hold_missed = false;
            }
        }
        if (col3.get(current_measure_position).equals(n)) {
            Rectangle note = new Rectangle(RhythmGame.V_WIDTH / 2, 480,64,64);
            arr.add(note);
            if (n == '2' || n == '4') col3_held = true;
            if (n == '3') {
                col3_held = false;
                col3_hold_missed = false;
            }
        }
        if (col4.get(current_measure_position).equals(n)) {
            Rectangle note = new Rectangle(RhythmGame.V_WIDTH / 2 + 64, 480,64,64);
            arr.add(note);
            if (n == '2' || n == '4') col4_held = true;
            if (n == '3') {
                col4_held = false;
                col4_hold_missed = false;
            }
        }
    }

    public void addHoldBars(float delta) {
        time_since_last_bar += delta;
        if (time_since_last_bar >= HOLD_BAR_TIME_INCREMENT) {
            if (col1_held) {
                Rectangle bar = new Rectangle(RhythmGame.V_WIDTH / 2 - 128, 512,64,24);
                hold_bars.add(bar);
            }
            if (col2_held) {
                Rectangle bar = new Rectangle(RhythmGame.V_WIDTH / 2 - 64, 512,64,24);
                hold_bars.add(bar);
            }
            if (col3_held) {
                Rectangle bar = new Rectangle(RhythmGame.V_WIDTH / 2, 512,64,24);
                hold_bars.add(bar);
            }
            if (col4_held) {
                Rectangle bar = new Rectangle(RhythmGame.V_WIDTH / 2 + 64, 512,64,24);
                hold_bars.add(bar);
            }
            time_since_last_bar = 0;
        }

    }

    private void getNoteDataStart() {
        for (int i=0; i < file_lines.length; i++) {
            if (file_lines[i].length() == 4
                    && (file_lines[i].charAt(file_lines[i].length() - 1) != ';'
                    || file_lines[i].charAt(file_lines[i].length() - 1) != ':')) {
                current_line = i;
                break;
            }
        }
    }

    private void getOffset() {
        for (String line : file_lines) {
            if (line.contains("#OFFSET:")) {
                offset = Float.parseFloat(line.substring(8, line.length()-1));
                break;
            }
        }
    }

    private void getBPM() { // only works with maps with constant bpm currently
        for (String line : file_lines) {
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
