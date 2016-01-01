package company.mahendraprophecy.LOCAL_DB_FILES;

import android.provider.BaseColumns;

/**
 * Created by Vinit on 08-11-2015.
 */
public class TABLE_DATA
{
    public TABLE_DATA()
    {

    }

    public static abstract class TABLE_INFO implements BaseColumns
    {
        public static final String CAT_ID="cat_id";
        public static final String NAME="name";
        public static final String EXPIRY="expiry";
        public static final String STATUS="status";

        public static final String TABLE_NAME="subscriptions";
        public static final String DATABASE_NAME="subscription_db";
    }
}
