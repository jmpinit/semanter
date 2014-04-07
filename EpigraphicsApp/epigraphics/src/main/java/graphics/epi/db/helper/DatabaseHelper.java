package graphics.epi.db.helper;

import graphics.epi.db.model.Note;
import graphics.epi.db.model.Note;
import graphics.epi.db.model.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "noteManager";

    // Table Names
    private static final String TABLE_NOTE = "notes";
    private static final String TABLE_FRAGMENT = "fragments";
    private static final String TABLE_BOUND = "bounds";
    private static final String TABLE_NOTE_FRAGMENT = "note_fragments";
    private static final String TABLE_FRAGMENT_BOUND = "fragment_bounds";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_PATH = "path";

    // NOTES Table - column names
    private static final String KEY_CLASS = "class";
    private static final String KEY_SUBJECT = "subject";
    private static final String KEY_PLAINTEXT = "plaintext";
    private static final String KEY_KEYWORDS = "keywords";
    private static final String KEY_NAME = "name";
    private static final String KEY_WRITTEN_AT = "written_at";

    // FRAGMENT Table - column names

    // BOUNDS Table - column names
    private static final String KEY_LEFT = "left";
    private static final String KEY_RIGHT = "right";
    private static final String KEY_TOP = "top";
    private static final String KEY_BOTTOM = "bottom";

    // NOTE_FRAGMENTS  & FRAGMENT_BOUND Table - column names
    private static final String KEY_NOTE_ID = "note_id";
    private static final String KEY_BOUND_ID = "bound_id";
    private static final String KEY_FRAGMENT_ID = "fragment_id";


    // Table Create Statements
    // Note table create statement
    private static final String CREATE_TABLE_NOTE = "CREATE TABLE "
            + TABLE_NOTE + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_PATH + " TEXT,"
            + KEY_CLASS + " TEXT,"
            + KEY_SUBJECT + " TEXT,"
            + KEY_PLAINTEXT + " TEXT,"
            + KEY_KEYWORDS + " TEXT,"
            + KEY_NAME + " TEXT,"
            + KEY_CREATED_AT + " DATETIME,"
            + KEY_WRITTEN_AT + " DATETIME"
            + ")";
    private static final String CREATE_TABLE_FRAGMENT = "CREATE TABLE "
            + TABLE_FRAGMENT + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_PATH + " TEXT,"
            + KEY_CREATED_AT + " DATETIME"
            + ")";

    private static final String CREATE_TABLE_BOUND = "CREATE TABLE "
            + TABLE_BOUND + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_LEFT + " INTEGER,"
            + KEY_RIGHT + " INTEGER,"
            + KEY_TOP + " INTEGER,"
            + KEY_BOTTOM + " INTEGER,"
            + KEY_CREATED_AT + " DATETIME"
            + ")";
    // note_fragments table create statement
    private static final String CREATE_TABLE_NOTE_FRAGMENTS = "CREATE TABLE "
            + TABLE_NOTE_FRAGMENT + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_NOTE_ID + " INTEGER,"
            + KEY_FRAGMENT_ID + " INTEGER,"
            + KEY_CREATED_AT + " DATETIME" + ")";
    // fragment_bounds table create statement
    private static final String CREATE_TABLE_FRAGMENT_BOUNDS = "CREATE TABLE "
            + TABLE_FRAGMENT_BOUND + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_FRAGMENT_ID + " INTEGER,"
            + KEY_BOUND_ID + " INTEGER,"
            + KEY_CREATED_AT + " DATETIME" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_NOTE);
        db.execSQL(CREATE_TABLE_FRAGMENT);
        db.execSQL(CREATE_TABLE_BOUND);
        db.execSQL(CREATE_TABLE_NOTE_FRAGMENTS);
        db.execSQL(CREATE_TABLE_FRAGMENT_BOUNDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRAGMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOUND);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE_FRAGMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRAGMENT_BOUNDS);

        // create new tables
        onCreate(db);
    }

    // ------------------------ "todos" table methods ----------------//

    /*
     * Creating a todo
     */
    public long createNote(Note note, long[] fragment_ids) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PATH, note.getPath());
        values.put(KEY_CLASS, note.getClass());
        values.put(KEY_SUBJECT, note.getSubject());
        values.put(KEY_PLAINTEXT, note.getPlainText());
        values.put(KEY_KEYWORDS, note.getKeywords());
        values.put(KEY_NAME, note.getName());
        values.put(KEY_WRITTEN_AT, note.getWrittenAt());
        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        long note_id = db.insert(TABLE_NOTE, null, values);

        // insert fragment_ids
        for (long fragment_id : fragment_ids) {
            createNoteFragment(note_id, fragment_id);
        }

        return note_id;
    }

    /*
     * get single todo
     */
    public Note getNote(long note_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_NOTE + " WHERE "
                + KEY_ID + " = " + note_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Note td = new Note();

        td.setId(c.getInt( c.getColumnIndex(KEY_ID) ));
        td.setPath(c.getString( c.getColumnIndex(KEY_PATH) ));
        td.setClass(c.getString( c.getColumnIndex(KEY_CLASS) ));
        td.setSubject(c.getString( c.getColumnIndex(KEY_SUBJECT) ));
        td.setPlainText(c.getString( c.getColumnIndex(KEY_PLAINTEXT) ));
        td.setName(c.getString( c.getColumnIndex(KEY_NAME) ));
        td.setKeywords(c.getString( c.getColumnIndex(KEY_KEYWORDS) ));
        td.setWrittenAt(c.getString( c.getColumnIndex(KEY_WRITTEN_AT) ));
        td.setCreatedAt(c.getString( c.getColumnIndex(KEY_CREATED_AT) ));

        return td;
    }

    /**
     * getting all notes
     * */
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<Note>();
        String selectQuery = "SELECT  * FROM " + TABLE_NOTE;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Note td = new Note();
                td.setId(c.getInt( c.getColumnIndex(KEY_ID) ));
                td.setPath(c.getString( c.getColumnIndex(KEY_PATH) ));
                td.setClass(c.getString( c.getColumnIndex(KEY_CLASS) ));
                td.setSubject(c.getString( c.getColumnIndex(KEY_SUBJECT) ));
                td.setPlainText(c.getString( c.getColumnIndex(KEY_PLAINTEXT) ));
                td.setName(c.getString( c.getColumnIndex(KEY_NAME) ));
                td.setKeywords(c.getString( c.getColumnIndex(KEY_KEYWORDS) ));
                td.setWrittenAt(c.getString( c.getColumnIndex(KEY_WRITTEN_AT) ));
                td.setCreatedAt(c.getString( c.getColumnIndex(KEY_CREATED_AT) ));

                // adding to todo list
                notes.add(td);
            } while (c.moveToNext());
        }

        return notes;
    }

    /**
     * getting all todos under single tag
     * */
    // public List<Todo> getAllToDosByTag(String tag_name) {
    // 	List<Todo> todos = new ArrayList<Todo>();

    // 	String selectQuery = "SELECT  * FROM " + TABLE_TODO + " td, "
    // 			+ TABLE_TAG + " tg, " + TABLE_TODO_TAG + " tt WHERE tg."
    // 			+ KEY_TAG_NAME + " = '" + tag_name + "'" + " AND tg." + KEY_ID
    // 			+ " = " + "tt." + KEY_TAG_ID + " AND td." + KEY_ID + " = "
    // 			+ "tt." + KEY_TODO_ID;

    // 	Log.e(LOG, selectQuery);

    // 	SQLiteDatabase db = this.getReadableDatabase();
    // 	Cursor c = db.rawQuery(selectQuery, null);

    // 	// looping through all rows and adding to list
    // 	if (c.moveToFirst()) {
    // 		do {
    // 			Todo td = new Todo();
    // 			td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
    // 			td.setNote((c.getString(c.getColumnIndex(KEY_TODO))));
    // 			td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

    // 			// adding to todo list
    // 			todos.add(td);
    // 		} while (c.moveToNext());
    // 	}

    // 	return todos;
    // }

	/*
	 * getting note count
	 */
    public int getNoteCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NOTE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    /*
     * Updating a note
     */
    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PATH, note.getPath());
        values.put(KEY_CLASS, note.getClass());
        values.put(KEY_SUBJECT, note.getSubject());
        values.put(KEY_PLAINTEXT, note.getPlainText());
        values.put(KEY_KEYWORDS, note.getKeywords());
        values.put(KEY_NAME, note.getName());
        values.put(KEY_WRITTEN_AT, note.getWrittenAt());
        values.put(KEY_CREATED_AT, getDateTime());

        // updating row
        return db.update(TABLE_NOTE, values, KEY_ID + " = ?",
                new String[] { String.valueOf(note.getId()) });
    }

    /*
     * Deleting a note
     */
    public void deleteToDo(long note_id) {
        //this should probably also delete associated fragments?
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTE, KEY_ID + " = ?",
                new String[] { String.valueOf(note_id) });
    }

    // -------------------- "fragment" table methods ----------------//

    /*
     * Creating fragment
     */
    public long createFragment(Fragment fragment) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PATH, fragment.getPath());
        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        long fragment_id = db.insert(TABLE_FRAGMENT, null, values);

        return fragment_id;
    }

    /**
     * getting all fragments
     * */
    public List<Fragment> getAllFragments() {
        List<Fragment> fragments = new ArrayList<Fragment>();
        String selectQuery = "SELECT  * FROM " + TABLE_FRAGMENT;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Fragment t = new Fragment();
                t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                t.setPath(c.getString(c.getColumnIndex(KEY_PATH)));

                // adding to fragments list
                fragments.add(t);
            } while (c.moveToNext());
        }
        return fragments;
    }

    /**
     * getting all fragments associated with a given note
     * */
    public List<Fragment> getAllFragmentsForNote(Note note) {
        List<Fragment> fragments = new ArrayList<Fragment>();

        String selectQuery = "SELECT  * FROM " + TABLE_TODO + " td, "
                + TABLE_TAG + " tg, " + TABLE_TODO_TAG + " tt WHERE tg."
                + KEY_TAG_NAME + " = '" + tag_name + "'" + " AND tg." + KEY_ID
                + " = " + "tt." + KEY_TAG_ID + " AND td." + KEY_ID + " = "
                + "tt." + KEY_TODO_ID; //this query most def won't work, but it should be similar

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Fragment t = new Fragment();
                t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                t.setPath(c.getString(c.getColumnIndex(KEY_PATH)));

                // adding to fragments list
                fragments.add(t);
            } while (c.moveToNext());
        }
        return fragments;
    }


    /*
     * Updating a fragment
     */
    public int updateFragment(Fragment fragment) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PATH, fragment.getPath());

        // updating row
        return db.update(TABLE_FRAGMENT, values, KEY_ID + " = ?",
                new String[] { String.valueOf(fragment.getId()) });
    }

    /*
     * Deleting a fragment
     */
    public void deleteFragment(Fragment fragment) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_FRAGMENT, KEY_ID + " = ?",
                new String[] { String.valueOf(fragment.getId()) });
    }

    // -------------------- "bound" table methods ----------------//

    /*
     * Creating bound
     */
    public long createBound(Bound bound) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LEFT, bound.getLeft());
        values.put(KEY_RIGHT, bound.getRight());
        values.put(KEY_TOP, bound.getTop());
        values.put(KEY_BOTTOM, bound.getBottom());
        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        long bound_id = db.insert(TABLE_BOUND, null, values);

        return bound_id;
    }

    /**
     * getting all bounds
     * */
    public List<Bound> getAllBounds() {
        List<Bound> bounds = new ArrayList<Bound>();
        String selectQuery = "SELECT  * FROM " + TABLE_BOUND;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Bound t = new Bound();
                t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                t.setLeft(c.getInt((c.getColumnIndex(KEY_LEFT))));
                t.setRight(c.getInt((c.getColumnIndex(KEY_RIGHT))));
                t.setTop(c.getInt((c.getColumnIndex(KEY_TOP))));
                t.setBottom(c.getInt((c.getColumnIndex(KEY_BOTTOM))));

                // adding to bounds list
                bounds.add(t);
            } while (c.moveToNext());
        }
        return bounds;
    }

    /**
     * getting all bounds associated with a given fragment
     * */
    public List<Bound> getAllBoundsForFragment(Fragment fragment) {
        List<Bound> bounds = new ArrayList<Bound>();

        String selectQuery = "SELECT  * FROM " + TABLE_TODO + " td, "
                + TABLE_TAG + " tg, " + TABLE_TODO_TAG + " tt WHERE tg."
                + KEY_TAG_NAME + " = '" + tag_name + "'" + " AND tg." + KEY_ID
                + " = " + "tt." + KEY_TAG_ID + " AND td." + KEY_ID + " = "
                + "tt." + KEY_TODO_ID; //this query most def won't work, but it should be similar

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Bound t = new Bound();
                t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                t.setLeft(c.getInt((c.getColumnIndex(KEY_LEFT))));
                t.setRight(c.getInt((c.getColumnIndex(KEY_RIGHT))));
                t.setTop(c.getInt((c.getColumnIndex(KEY_TOP))));
                t.setBottom(c.getInt((c.getColumnIndex(KEY_BOTTOM))));

                // adding to bounds list
                bounds.add(t);
            } while (c.moveToNext());
        }
        return bounds;
    }


    /*
     * Updating a bound
     */
    public int updateBound(Bound bound) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LEFT, bound.getLeft());
        values.put(KEY_RIGHT, bound.getRight());
        values.put(KEY_TOP, bound.getTop());
        values.put(KEY_BOTTOM, bound.getBottom());

        // updating row
        return db.update(TABLE_BOUND, values, KEY_ID + " = ?",
                new String[] { String.valueOf(bound.getId()) });
    }

    /*
     * Deleting a bound
     */
    public void deleteBound(Bound bound) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_BOUND, KEY_ID + " = ?",
                new String[] { String.valueOf(bound.getId()) });
    }

    // ------------------------ "note_fragments" table methods ----------------//

    /*
     * Creating todo_tag
     */
    public long createNoteFragment(long note_id, long fragment_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTE_ID, note_id);
        values.put(KEY_FRAGMENT_ID, fragment_id);
        values.put(KEY_CREATED_AT, getDateTime());

        long id = db.insert(TABLE_NOTE_FRAGMENT, null, values);

        return id;
    }

    /*
     * Updating a todo tag
     */
    public int updateNoteFragment(long id, long fragment_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FRAGMENT_ID, fragment_id);

        // updating row
        return db.update(TABLE_NOTE, values, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    /*
     * Deleting a todo tag
     */
    public void deleteNoteFragment(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTE, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }
    // ------------------------ "fragment_bounds" table methods ----------------//

    /*
     * Creating todo_tag todo - > fragment tag -> bound
     */
    public long createFragmentBound(long fragment_id, long bound_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FRAGMENT_ID, fragment_id);
        values.put(KEY_BOUND_ID, bound_id);
        values.put(KEY_CREATED_AT, getDateTime());

        long id = db.insert(TABLE_FRAGMENT_BOUND, null, values);

        return id;
    }

    /*
     * Updating a todo tag
     */
    public int updateFragmentBound(long id, long bound_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BOUND_ID, bound_id);

        // updating row
        return db.update(TABLE_FRAGMENT, values, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    /*
     * Deleting a todo tag
     */
    public void deleteFragmentBound(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FRAGMENT, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    /**
     * get datetime
     * */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
