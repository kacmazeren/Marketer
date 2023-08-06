package com.example.marketer10;

import android.database.sqlite.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
@RunWith(RobolectricTestRunner.class)
@Config(packageName="com.example.marketer10")
public class DatabaseHelperTest {
    private DatabaseHelper databaseHelper;

    @Before
    public void setUp() {
        databaseHelper = new DatabaseHelper(RuntimeEnvironment.application);
    }

    @Test
    public void testIsMemberExists() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        assertFalse(databaseHelper.isMemberExists("asdad"));
    }
}