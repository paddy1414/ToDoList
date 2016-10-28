
package pdesigns.com.todolist.DbStuffs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple reminder database access helper class.
 * Defines the basic CRUD operations (Create, Read, Update, Delete)
 * for the example, and gives the ability to list all reminders as well as
 * retrieve or modify a specific reminder.
 *
 */
public class TaskDbHelper {

    //
    // Databsae Related Constants
    //
    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "todoList";
    private static final int DATABASE_VERSION = 4;

    public static final String TASK = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_DATE_TIME = "reminder_date_time";
    public static final String _ID = "_id";
    public static final String DONE = "done";


    private static final String TAG = "ReminderDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation SQL statement
     */
    private static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE + " ("
                    +_ID + " integer primary key autoincrement, "
                    + TASK + " text not null, "
                    + KEY_BODY + " text not null, "
                    + KEY_DATE_TIME + " text not null, "+ ""
                    + DONE + " text not null);";



    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public TaskDbHelper(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public TaskDbHelper open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new reminder using the title, body and reminder date time provided.
     * If the reminder is  successfully created return the new rowId
     * for that reminder, otherwise return a -1 to indicate failure.
     *
     * @param title the title of the reminder
     * @param body the body of the reminder
     * @param reminderDateTime the date and time the reminder should remind the user
     * @return rowId or -1 if failed
     */
    public long createReminder(String title, String body, String reminderDateTime) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(TASK, title);
        initialValues.put(KEY_BODY, body);
        initialValues.put(KEY_DATE_TIME, reminderDateTime);
        initialValues.put(DONE, "Not Done");

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the reminder with the given rowId
     *
     * @param rowId id of reminder to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteReminder(long rowId) {

        return mDb.delete(DATABASE_TABLE,_ID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all reminders in the database
     *
     * @return Cursor over all reminders
     */
    public Cursor fetchAllReminders() {

        return mDb.query(DATABASE_TABLE, new String[] {_ID, TASK,
                KEY_BODY, KEY_DATE_TIME, DONE}, null, null, null, null, null);
    }

    public Cursor fetchAllRemindersBydone() {

        return mDb.query(DATABASE_TABLE, new String[] {_ID, TASK,
                KEY_BODY, KEY_DATE_TIME, DONE}, null, null, null, null, DONE+" DESC") ;
    }

    /**
     * Return a Cursor positioned at the reminder that matches the given rowId
     *
     * @param rowId id of reminder to retrieve
     * @return Cursor positioned to matching reminder, if found
     * @throws SQLException if reminder could not be found/retrieved
     */
    public Cursor fetchReminder(long rowId) throws SQLException {

        Cursor mCursor =
                mDb.query(true, DATABASE_TABLE, new String[] {_ID,
                                TASK, KEY_BODY, KEY_DATE_TIME, DONE}, _ID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the reminder using the details provided. The reminder to be updated is
     * specified using the rowId, and it is altered to use the title, body and reminder date time
     * values passed in
     *
     * @param rowId id of reminder to update
     * @param title value to set reminder title to
     * @param body value to set reminder body to
     * @param reminderDateTime value to set the reminder time.
     * @return true if the reminder was successfully updated, false otherwise
     */
    public boolean updateReminder(long rowId, String title, String body, String reminderDateTime, String done) {
        ContentValues args = new ContentValues();
        args.put(TASK, title);
        args.put(KEY_BODY, body);
        args.put(KEY_DATE_TIME, reminderDateTime);
        args.put(DONE, done);

        return mDb.update(DATABASE_TABLE, args, _ID + "=" + rowId, null) > 0;
    }
}
