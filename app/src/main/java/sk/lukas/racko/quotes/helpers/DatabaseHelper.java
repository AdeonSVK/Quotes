package sk.lukas.racko.quotes.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import sk.lukas.racko.quotes.models.Quote;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "quotes.db";
    public DatabaseHelper(Context context) {
        super(context, DB_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS quotes " +
                        "(id integer PRIMARY KEY, " +
                        "gender text," +
                        "firstName text," +
                        "lastName text, " +
                        "quote text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    // get random quote from DB
    public Quote getRandomQuote() throws CursorIndexOutOfBoundsException {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c =  db.rawQuery( "SELECT * FROM quotes ORDER BY RANDOM() LIMIT 1", null );
        c.moveToFirst();
        Quote q = new Quote(
                c.getInt(c.getColumnIndex("id")),
                c.getString(c.getColumnIndex("firstName")),
                c.getString(c.getColumnIndex("lastName")),
                c.getString(c.getColumnIndex("gender")),
                c.getString(c.getColumnIndex("quote")));

        return q;
    }

    // add quote to DB
    public boolean addQuote (String firstName, String lastName, String gender, String quote) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("firstName", firstName);
        cv.put("lastName", lastName);
        cv.put("gender", gender);
        cv.put("quote", quote);
        db.insert("quotes", null, cv);
        return true;
    }

    // get number of qutes in DB
    public int getQuotesCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, "quotes");
    }

    // get a list of all quotes in DB
    public ArrayList<Quote> getQuotes() {
        ArrayList<Quote> quotes = new ArrayList<Quote>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c =  db.rawQuery( "select * from quotes", null );
        c.moveToFirst();

        while(c.isAfterLast() == false){
            quotes.add(new Quote(
                    c.getInt(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("firstName")),
                    c.getString(c.getColumnIndex("lastName")),
                    c.getString(c.getColumnIndex("gender")),
                    c.getString(c.getColumnIndex("quote"))));
            c.moveToNext();
        }
        return quotes;
    }

    // delete quote with given ID
    public int deleteQuote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("quotes", "id = ? ", new String[]{Integer.toString(id)});
    }

    // delete all quotes from DB
    public void deleteAllQuotes () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("quotes", "id > 0 ", null);
    }
}
