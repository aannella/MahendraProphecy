package company.mahendraprophecy.LOCAL_DB_FILES;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Vinit on 08-11-2015.
 */
public class DATABASE_OPERATIONS extends SQLiteOpenHelper
{
    public static final int db_version=1;
    public String CREATE_TABLE="CREATE TABLE if not exists "+TABLE_DATA.TABLE_INFO.TABLE_NAME+"("+ TABLE_DATA.TABLE_INFO.CAT_ID+" TEXT,"+ TABLE_DATA.TABLE_INFO.NAME+" TEXT,"+ TABLE_DATA.TABLE_INFO.EXPIRY+" TEXT,"+ TABLE_DATA.TABLE_INFO.STATUS+" TEXT );";



    public DATABASE_OPERATIONS(Context context)
    {
        super(context, TABLE_DATA.TABLE_INFO.DATABASE_NAME, null, db_version);
        onCreate(getWritableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE);
            //deleteInformation(sqLiteDatabase);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2)
    {

    }

    public void putInformation(DATABASE_OPERATIONS D_OP,String cat_id,String name,String expiry,String status)
    {
        SQLiteDatabase sqLiteDatabase=D_OP.getReadableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(TABLE_DATA.TABLE_INFO.CAT_ID,cat_id);
        contentValues.put(TABLE_DATA.TABLE_INFO.NAME,name);
        contentValues.put(TABLE_DATA.TABLE_INFO.EXPIRY,expiry);
        contentValues.put(TABLE_DATA.TABLE_INFO.STATUS,status);
        sqLiteDatabase.insert(TABLE_DATA.TABLE_INFO.TABLE_NAME,null,contentValues);
    }

    public void deleteInformation(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.delete(TABLE_DATA.TABLE_INFO.TABLE_NAME, null, null);
    }

    public Cursor getCatInformation(DATABASE_OPERATIONS D_OP,String cat)
    {
        SQLiteDatabase sqLiteDatabase=D_OP.getReadableDatabase();
        String[] columns=new String[]{TABLE_DATA.TABLE_INFO.CAT_ID,TABLE_DATA.TABLE_INFO.NAME,TABLE_DATA.TABLE_INFO.EXPIRY,TABLE_DATA.TABLE_INFO.STATUS};
        //Cursor cursor=sqLiteDatabase.query(TABLE_DATA.TABLE_INFO.TABLE_NAME,columns,null,null,null,null,null);
        Cursor cursor=sqLiteDatabase.rawQuery("SELECT  "+ TABLE_DATA.TABLE_INFO.STATUS+","+ TABLE_DATA.TABLE_INFO.EXPIRY+","+ TABLE_DATA.TABLE_INFO.NAME+" FROM "+ TABLE_DATA.TABLE_INFO.TABLE_NAME+" WHERE "+ TABLE_DATA.TABLE_INFO.CAT_ID+"="+cat,null);
        return cursor;
    }

    public Cursor getInformation(DATABASE_OPERATIONS D_OP)
    {
        SQLiteDatabase sqLiteDatabase=D_OP.getReadableDatabase();
        String[] columns=new String[]{TABLE_DATA.TABLE_INFO.CAT_ID,TABLE_DATA.TABLE_INFO.NAME,TABLE_DATA.TABLE_INFO.EXPIRY,TABLE_DATA.TABLE_INFO.STATUS};
        Cursor cursor=sqLiteDatabase.query(TABLE_DATA.TABLE_INFO.TABLE_NAME,columns,null,null,null,null,null);
        //Cursor cursor=sqLiteDatabase.rawQuery("SELECT  "+ TABLE_DATA.TABLE_INFO.STATUS+","+ TABLE_DATA.TABLE_INFO.EXPIRY+","+ TABLE_DATA.TABLE_INFO.NAME+" FROM "+ TABLE_DATA.TABLE_INFO.TABLE_NAME+" WHERE "+ TABLE_DATA.TABLE_INFO.CAT_ID+"="+cat,null);
        return cursor;
    }

}
