package autotest.gionee.automonkeypowertest.util.sqlite;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

import autotest.gionee.automonkeypowertest.util.Constants;

class DatabaseContext extends ContextWrapper {

    public DatabaseContext(Context base) {
        super(base);
    }

    @Override
    public File getDatabasePath(String name) {
        String dbFile = Constants.FILE_PATH + DatabaseHelper.DATABASE_NAME;
        if (!dbFile.endsWith(".db")) {
            dbFile += ".db";
        }
        File result = new File(dbFile);
        if (!result.getParentFile().exists()) {
            result.getParentFile().mkdirs();
        }
        return result;
    }

    /*
     * this softVersion is called for android devices >= api-11. thank to @damccull
     * for fixing this.
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode,
                                               SQLiteDatabase.CursorFactory factory,
                                               DatabaseErrorHandler errorHandler) {
        return openOrCreateDatabase(name, mode, factory);
    }

    /* this softVersion is called for android devices < api-11 */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode,
                                               SQLiteDatabase.CursorFactory factory) {
        return SQLiteDatabase.openOrCreateDatabase(
                getDatabasePath(name), null);

    }
}
