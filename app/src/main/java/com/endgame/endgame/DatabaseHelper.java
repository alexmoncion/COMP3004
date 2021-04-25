package com.endgame.endgame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "EndGame.db";
    public static final String TABLE_NAME = "ScoreboardTable";
    public static final String COL_1 = "GAME_ID";    //unique identifier for that game session
    public static final String COL_2 = "SCENARIO";  //scenario name
    public static final String COL_3 = "TIME";      //time to completion
    public static final String COL_4 = "MOVES";     //number of moves


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table " + TABLE_NAME + " (GAME_ID INTEGER PRIMARY KEY AUTOINCREMENT, SCENARIO TEXT, TIME LONG, MOVES INT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String scenario, long time, int moves) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, scenario);
        contentValues.put(COL_3, time);
        contentValues.put(COL_4, moves);
        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getScoreboardData() {
        SQLiteDatabase db = this.getWritableDatabase();

        //natural join the minimum moves and minimum time for each scenario... 3005 champions!
        Cursor res = db.rawQuery("select Scenario, minmoves, mintime " +
                "from (select Scenario, min(moves) as minmoves " +
                "from ScoreboardTable group by Scenario) " +
                "natural join (select Scenario, min(time) as mintime " +
                "from ScoreboardTable group by Scenario)", null);
        return res;
    }
}
