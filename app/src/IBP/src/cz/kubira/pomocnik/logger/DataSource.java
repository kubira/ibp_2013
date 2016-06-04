package cz.kubira.pomocnik.logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import cz.kubira.pomocnik.around.Hotel;
import cz.kubira.pomocnik.around.Place;
import cz.kubira.pomocnik.around.Result;

public class DataSource {

    protected SQLiteDatabase database;
    protected SQLiteAdapter dbHelper;
    private String[] categoryColumns = {
    		"id_category", "name_category"
    };
    private String[] journeyColumns = {
    		"id_journey", "name_journey"
    };
    private String[] recordColumns = {
 			"id_record", "id_category", "code_currency",
 			"id_journey", "amount", "timestamp"
 	};
    private String[] placeColumns = {
    		"id_place", "type", "name", "address",
    		"phone", "city", "description", "stars",
    		"rating", "latitude", "longitude", "url"
    };

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public DataSource(Context context) {
        dbHelper = new SQLiteAdapter(context);
    }

    public void addBytes(long bytes) {
    	ContentValues values = new ContentValues();
    	values.put("bytes", bytes);
    	open();
    	database.insert("data", null, values);
    	close();
    }

    public long getBytes() {
    	long result = 0;

    	open();
    	Cursor c = database.rawQuery("SELECT SUM(bytes) FROM data;", null);
    	if (c.getCount() > 0) {
    		c.moveToFirst();
    		result = c.getLong(0);
    	}
    	close();

    	return result;
    }

    public void dropBytes() {
    	open();
    	database.rawQuery("DELETE FROM data;", null);
    	close();
    }

    public Category createCategory(String name_category) {
        ContentValues values = new ContentValues();
        values.put("name_category", name_category);
        long insertId = database.insert("category", null, values);
        Cursor cursor = database.query("category", categoryColumns,
        		"id_category" + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Category newCategory = cursorToCategory(cursor);
        cursor.close();

        return newCategory;
    }

    public Category updateCategory(long id_category, String name_category) {
        ContentValues values = new ContentValues();
        values.put("name_category", name_category);
        database.update("category", values, "id_category = " + id_category, null);
        Cursor cursor = database.query("category", categoryColumns,
        		"id_category" + " = " + id_category, null, null, null, null);
        cursor.moveToFirst();
        Category newCategory = cursorToCategory(cursor);
        cursor.close();

        return newCategory;
    }

    public void deleteCategory(Category category) {
        long id = category.getId();
        database.delete("category", "id_category" + " = " + id, null);
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<Category>();

        Cursor cursor = database.query("category", categoryColumns,
        		null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Category category = cursorToCategory(cursor);
            categories.add(category);
            cursor.moveToNext();
        }

        cursor.close();
        return categories;
    }

    private Category cursorToCategory(Cursor cursor) {
        Category category = new Category();
        category.setId(cursor.getLong(0));
        category.setName(cursor.getString(1));
        return category;
    }

    public Journey createJourney(String name_journey) {
        ContentValues values = new ContentValues();
        values.put("name_journey", name_journey);
        long insertId = database.insert("journey", null, values);
        Cursor cursor = database.query("journey", journeyColumns,
        		"id_journey" + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Journey newJourney = cursorToJourney(cursor);
        cursor.close();
        return newJourney;
    }

    public Journey updateJourney(long id_journey, String name_journey) {
        ContentValues values = new ContentValues();
        values.put("name_journey", name_journey);
        database.update("journey", values, "id_journey = " + id_journey, null);
        Cursor cursor = database.query("journey", journeyColumns,
        		"id_journey" + " = " + id_journey, null, null, null, null);
        cursor.moveToFirst();
        Journey newJourney = cursorToJourney(cursor);
        cursor.close();
        return newJourney;
    }

    public void deleteJourney(Journey journey) {
        long id = journey.getId();
        database.delete("journey", "id_journey" + " = " + id, null);
    }

    public List<Journey> getAllJournies() {
        List<Journey> journies = new ArrayList<Journey>();

        Cursor cursor = database.query("journey", journeyColumns,
        		null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
          Journey journey = cursorToJourney(cursor);
          journies.add(journey);
          cursor.moveToNext();
        }

        cursor.close();
        return journies;
    }

    private Journey cursorToJourney(Cursor cursor) {
	    Journey journey = new Journey();
	    journey.setId(cursor.getLong(0));
	    journey.setName(cursor.getString(1));
        return journey;
    }

 	public Record createRecord(long id_category, String code_currency,
 			long id_journey, double amount, long date, long time) {
 		GregorianCalendar cal = new GregorianCalendar();
 		GregorianCalendar c = new GregorianCalendar();
 		c.setTimeInMillis(date*1000);
 		cal.set(Calendar.YEAR, c.get(Calendar.YEAR));
 		cal.set(Calendar.MONTH, c.get(Calendar.MONTH));
 		cal.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
 		c.setTimeInMillis(time*1000);
 		cal.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
 		cal.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
 		ContentValues values = new ContentValues();
 		values.put("id_category", id_category);
 		values.put("code_currency", code_currency);
 		values.put("id_journey", id_journey);
 		values.put("amount", amount);
 		values.put("timestamp", (cal.getTimeInMillis()/1000));
 		long insertId = database.insert("record", null, values);
 		Cursor cursor = database.query("record", recordColumns,
 				"id_record" + " = " + insertId, null, null, null, null);
 		cursor.moveToFirst();
 		Record newRecord = cursorToRecord(cursor);
 		cursor.close();
 		return newRecord;
 	}

 	public Record updateRecord(long id_record, long id_category,
 			String code_currency, long id_journey, double amount,
 			long date, long time) {
 		GregorianCalendar cal = new GregorianCalendar();
 		GregorianCalendar c = new GregorianCalendar();
 		c.setTimeInMillis(date*1000);
 		cal.set(Calendar.YEAR, c.get(Calendar.YEAR));
 		cal.set(Calendar.MONTH, c.get(Calendar.MONTH));
 		cal.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
 		c.setTimeInMillis(time*1000);
 		cal.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
 		cal.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
 		ContentValues values = new ContentValues();
 		values.put("id_category", id_category);
 		values.put("code_currency", code_currency);
 		values.put("id_journey", id_journey);
 		values.put("amount", amount);
 		values.put("timestamp", (cal.getTimeInMillis()/1000));
 		database.update("record", values, "id_record = "+id_record, null);
 		Cursor cursor = database.query("record", recordColumns,
 				"id_record = "+id_record, null, null, null, null);
 		cursor.moveToFirst();
 		Record newRecord = cursorToRecord(cursor);
 		cursor.close();
 		return newRecord;
 	}

 	public void deleteRecord(Record record) {
 		long id = record.getId();
 		database.delete("record", "id_record" + " = " + id, null);
 	}

 	public List<Record> getAllRecords() {
 		List<Record> records = new ArrayList<Record>();

 		Cursor cursor = database.query("record", recordColumns,
 				null, null, null, null, null);

 		cursor.moveToFirst();
 		while (!cursor.isAfterLast()) {
 			Record record = cursorToRecord(cursor);
 			records.add(record);
 			cursor.moveToNext();
 		}

 		cursor.close();
 		return records;
 	}

 	protected Record cursorToRecord(Cursor cursor) {
 		Record record = new Record();
 		record.setId(cursor.getLong(0));
 		record.setCategory(cursor.getLong(1));
 		record.setCurrency(cursor.getString(2));
 		record.setJourney(cursor.getLong(3));
 		record.setAmount(cursor.getDouble(4));
 		record.setTimestamp(cursor.getLong(5));
 		return record;
 	}

 	public List<Record> getRecords(Cursor c) {
 		List<Record> records = new ArrayList<Record>();

 		c.moveToFirst();
 		while (!c.isAfterLast()) {
 			Record record = cursorToRecord(c);
 			records.add(record);
 			c.moveToNext();
 		}

 		c.close();
 		return records;
 	}

 	public List<RecordToView> getRecordsToView(Cursor c) {
 		List<RecordToView> recordsToView = new ArrayList<RecordToView>();

 		c.moveToFirst();
 		while (!c.isAfterLast()) {
 			Record record = cursorToRecord(c);
 			RecordToView r = new RecordToView();
 			r.setCategory(this.getNameById("category", "name_category", record.getCategory()));
 			r.setJourney(this.getNameById("journey", "name_journey", record.getJourney()));
 			r.setRecord(record);
 			recordsToView.add(r);
 			c.moveToNext();
 		}

 		c.close();
 		return recordsToView;
 	}

 	public boolean isInTable(String table, String column, String value) {
 		Cursor cursor = database.query(table, null, column + " = '" + value + "'",
 				null, null, null, null);
 		if (cursor.getCount() > 0) {
 			cursor.close();
 			return true;
 		} else {
 			cursor.close();
 			return false;
 		}
 	}

 	public boolean isInTable(String table, String column, String value, long id) {
 		Cursor cursor = database.query(table, null,  "id_"+ table + " != " + id + " AND " + column + " = '" + value + "'",
 				null, null, null, null);
 		if (cursor.getCount() > 0) {
 			cursor.close();
 			return true;
 		} else {
 			cursor.close();
 			return false;
 		}
 	}

 	public String getNameById(String table, String column, long id) {
 		Cursor cursor = database.rawQuery("select "+column+" from "+table
 				+" where id_"+table+" = "+id+";", null);

 		cursor.moveToFirst();
 		String s = cursor.getString(0);
 		cursor.close();
 		return s;
 	}

 	public Place createPlace(Hotel hotel) {
 		ContentValues values = new ContentValues();
 		values.put("type", "hotel");
 		values.put("name", hotel.getName());
 		values.put("address", hotel.getAddress());
 		values.put("phone", hotel.getPhone());
 		values.put("city", hotel.getCity());
 		values.put("description", hotel.getDescription());
 		values.put("stars", hotel.getStars());
 		values.put("rating", hotel.getRating());
 		values.put("longitude", hotel.getLongitude());
 		values.put("latitude", hotel.getLatitude());
 		values.put("url", hotel.getUrl());

 		long insertId = database.insert("place", null, values);
 		Cursor cursor = database.query("place", placeColumns,
 				"id_place" + " = " + insertId, null, null, null, null);
 		cursor.moveToFirst();
 		Place newPlace = cursorToPlace(cursor);
 		cursor.close();
 		return newPlace;
 	}

 	public Place createPlace(Result result) {
 		ContentValues values = new ContentValues();
 		values.put("type", result.getType());
 		values.put("name", result.getName());
 		values.put("address", result.getAddress());
 		values.put("longitude", result.getLongitude());
 		values.put("latitude", result.getLatitude());

 		long insertId = database.insert("place", null, values);
 		Cursor cursor = database.query("place", placeColumns,
 				"id_place" + " = " + insertId, null, null, null, null);
 		cursor.moveToFirst();
 		Place newPlace = cursorToPlace(cursor);
 		System.out.println("Place added to DB");
 		cursor.close();
 		return newPlace;
 	}

 	public void deletePlace(Place place) {
 		long id = place.getId();
 		database.delete("place", "id_place" + " = " + id, null);
 	}

 	public List<Place> getAllPlaces() {
 		List<Place> places = new ArrayList<Place>();

 		Cursor cursor = database.query("place", placeColumns,
 				null, null, null, null, null);

 		cursor.moveToFirst();
 		while (!cursor.isAfterLast()) {
 			Place place = cursorToPlace(cursor);
 			places.add(place);
 			cursor.moveToNext();
 		}

 		cursor.close();
 		return places;
 	}

 	protected Place cursorToPlace(Cursor cursor) {
 		Place place = new Place();
 		place.setId(cursor.getLong(0));
 		place.setType(cursor.getString(1));
 		place.setName(cursor.getString(2));
 		place.setAddress(cursor.getString(3));
 		place.setPhone(cursor.getString(4));
 		place.setCity(cursor.getString(5));
 		place.setDescription(cursor.getString(6));
 		place.setStars(cursor.getInt(7));
 		place.setRating(cursor.getDouble(8));
 		place.setLatitude(cursor.getDouble(9));
 		place.setLongitude(cursor.getDouble(10));
 		place.setUrl(cursor.getString(11));
 		return place;
 	}
}