package net.elshaarawy.movies.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by elshaarawy on 19-Apr-16.
 */
public class MoviesProvider extends ContentProvider {

    private MoviesDbHelper moviesDbHelper;

    //matcher types
    static final int POPULAR = 19;
    static final int RATED = 20;
    static final int POPULAR_ITEM = 21;
    static final int RATED_ITEM = 22;

    private static final String POPULAR_ID_SELECTION =
            MoviesContract.MoviesEntry.TABLE_NAME_POPULAR + "." + MoviesContract.MoviesEntry._ID + " = ? ";
    private static final String RATED_ID_SELECTION =
            MoviesContract.MoviesEntry.TABLE_NAME_RATED + "." + MoviesContract.MoviesEntry._ID + " = ? ";

    //my matcher of this content provider
    private static final UriMatcher matcher = myUriMatcher();

    private static UriMatcher myUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);//root uri code

        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.POPULAR_BATH, POPULAR);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.RATED_BATH, RATED);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.POPULAR_BATH + "/#", POPULAR_ITEM);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.RATED_BATH + "/#", RATED_ITEM);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        moviesDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = moviesDbHelper.getReadableDatabase();
        Cursor rCursor;

        switch (matcher.match(uri)) {
            case POPULAR: {
                rCursor = db.query(MoviesContract.MoviesEntry.TABLE_NAME_POPULAR,
                        projection,
                        selection,
                        selectionArgs,
                        null, null, sortOrder);
                break;
            }

            case RATED: {
                rCursor = db.query(MoviesContract.MoviesEntry.TABLE_NAME_RATED,
                        projection,
                        selection,
                        selectionArgs,
                        null, null, sortOrder);
                break;
            }
            case POPULAR_ITEM: {
                String[] _id = {uri.getLastPathSegment()};
                rCursor = db.query(MoviesContract.MoviesEntry.TABLE_NAME_POPULAR,
                        projection, POPULAR_ID_SELECTION, _id, null, null, sortOrder);
                break;
            }
            case RATED_ITEM:{
                String[] _id = {uri.getLastPathSegment()};
                rCursor = db.query(MoviesContract.MoviesEntry.TABLE_NAME_RATED,
                        projection, RATED_ID_SELECTION, _id, null, null, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("the following uri isn't supported: " + uri.toString());
        }
        rCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return rCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {


        switch (matcher.match(uri)) {
            case POPULAR:
                return MoviesContract.MoviesEntry.POPULAR_TYPE;
            case RATED:
                return MoviesContract.MoviesEntry.RATED_TYPE;

            case POPULAR_ITEM:
                return MoviesContract.MoviesEntry.POPULAR_ITEM_TYPE;
            case RATED_ITEM:
                return MoviesContract.MoviesEntry.RATED_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("the following uri isn't supported: " + uri.toString());
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = moviesDbHelper.getReadableDatabase();
        Uri rUri;

        switch (matcher.match(uri)) {
            case POPULAR: {
                Long _id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME_POPULAR, null, values);
                if (_id > 0) {
                    rUri = MoviesContract.MoviesEntry.append_idToURI(_id, true);
                } else {
                    throw new android.database.SQLException("insertion failure to " + uri.toString());
                }
                break;
            }
            case RATED: {
                Long _id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME_RATED, null, values);
                if (_id > 0) {
                    rUri = MoviesContract.MoviesEntry.append_idToURI(_id, false);
                } else {
                    throw new android.database.SQLException("insertion failure to " + uri.toString());
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("the following uri isn't supported: " + uri.toString());
        }

        //notify the registered Resolver about the change
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return rUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = moviesDbHelper.getReadableDatabase();
        int rDeleteCount;
        // to avoid deleting all rows due to silly error
        selection = selection == null ? "1" : selection;

        switch (matcher.match(uri)) {
            case POPULAR: {
                rDeleteCount = db.delete(MoviesContract.MoviesEntry.TABLE_NAME_POPULAR, selection, selectionArgs);
                break;
            }
            case RATED: {
                rDeleteCount = db.delete(MoviesContract.MoviesEntry.TABLE_NAME_RATED, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("the following uri isn't supported: " + uri.toString());
        }

        if (rDeleteCount != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        db.close();

        return rDeleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = moviesDbHelper.getReadableDatabase();
        int rUpdateCount;
        switch (matcher.match(uri)) {
            case POPULAR: {
                rUpdateCount = db.update(MoviesContract.MoviesEntry.TABLE_NAME_POPULAR, values, selection, selectionArgs);
                break;
            }
            case RATED: {
                rUpdateCount = db.update(MoviesContract.MoviesEntry.TABLE_NAME_RATED, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("the following uri isn't supported: " + uri.toString());
        }

        if (rUpdateCount != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        db.close();

        return rUpdateCount;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = moviesDbHelper.getReadableDatabase();

        switch (matcher.match(uri)) {
            case POPULAR: {
                return multiInsertion(db, MoviesContract.MoviesEntry.TABLE_NAME_POPULAR,
                        values, getContext().getContentResolver(), uri);
            }
            case RATED: {
                return multiInsertion(db, MoviesContract.MoviesEntry.TABLE_NAME_RATED,
                        values, getContext().getContentResolver(), uri);
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private static int multiInsertion(SQLiteDatabase db, String tableName, ContentValues[] values, ContentResolver resolver, Uri _uri) {
        int rCount = 0;
        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                Long _id = db.insert(tableName, null, value);
                if (_id != -1)
                    rCount++;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        resolver.notifyChange(_uri, null);
        return rCount;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        moviesDbHelper.close();
    }
}
