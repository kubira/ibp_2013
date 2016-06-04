package cz.kubira.pomocnik.logger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteAdapter extends SQLiteOpenHelper {

    protected static final String DATABASE_NAME = "assistant";
    private static final int DATABASE_VERSION = 1;

    protected static final String CREATE_CATEGORY_TABLE = "create table category ("
    		+ "id_category integer primary key autoincrement,"
    		+ "name_category text not null);";
    protected static final String CREATE_JOURNEY_TABLE = "create table journey ("
    		+ "id_journey integer primary key autoincrement,"
    		+ "name_journey text not null);";
    protected static final String CREATE_RECORD_TABLE = "create table record ("
    		+ "id_record integer primary key autoincrement,"
    		+ "id_category integer not null,"
    		+ "code_currency text not null,"
    		+ "id_journey integer not null,"
    		+ "amount real not null,"
    		+ "timestamp integer not null);";
    protected static final String CREATE_DATA_TABLE = "create table data ("
    		+ "bytes integer);";
    protected static final String CREATE_PLACE_TABLE = "create table place ("
    		+ "id_place integer primary key autoincrement,"
    		+ "type text not null,"
    		+ "name text not null,"
    		+ "address text not null,"
    		+ "phone text,"
    		+ "city text,"
    		+ "description text,"
    		+ "stars integer,"
    		+ "rating real,"
    		+ "latitude real not null,"
    		+ "longitude real not null,"
    		+ "url text);";

    public SQLiteAdapter(Context context) {
    	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
    	database.execSQL(CREATE_CATEGORY_TABLE);
    	database.execSQL(CREATE_JOURNEY_TABLE);
    	database.execSQL(CREATE_RECORD_TABLE);

    	database.execSQL(CREATE_DATA_TABLE);

    	database.execSQL(CREATE_PLACE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	Log.w(SQLiteAdapter.class.getName(),
    			"Upgrading database from version " + oldVersion + " to "
    			+ newVersion + ", which will destroy all old data");
    	db.execSQL("DROP TABLE IF EXISTS category;");
    	db.execSQL("DROP TABLE IF EXISTS journey;");
    	db.execSQL("DROP TABLE IF EXISTS record;");

    	db.execSQL("DROP TABLE IF EXISTS data;");

    	db.execSQL("DROP TABLE IF EXISTS place;");

    	onCreate(db);
    }

}