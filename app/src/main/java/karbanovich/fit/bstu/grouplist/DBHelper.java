package karbanovich.fit.bstu.grouplist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "STUDENTSDB.db";
    private static final int SCHEMA = 1;
    private static final String GROUPS_TABLE = "GROUPS";
    private static final String STUDENTS_TABLE = "STUDENTS";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + GROUPS_TABLE + " ("
                 + "IDGROUP text primary key,                        "
                 + "FACULTY text not null,                           "
                 + "COURSE integer check (COURSE > 0 and COURSE < 7),"
                 + "NAME text not null,                              "
                 + "HEAD text                                      );"
        );
        db.execSQL("create table " + STUDENTS_TABLE + " ("
                + "IDGROUP text,                                                 "
                + "IDSTUDENT text primary key,                                   "
                + "NAME text not null,                                           "
                + " foreign key(IDGROUP) references " + GROUPS_TABLE + "(IDGROUP)"
                + " on delete cascade on update cascade                        );"
        );
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists " + STUDENTS_TABLE);
        db.execSQL("drop table if exists " + GROUPS_TABLE);
        onCreate(db);
    }

    public long insertGroup(SQLiteDatabase db, Group group) {
        ContentValues values = new ContentValues();

        values.put("IDGROUP", group.getGroupID());
        values.put("FACULTY", group.getFaculty());
        values.put("COURSE", group.getCourse());
        values.put("NAME", group.getName());
        values.put("HEAD", group.getHead());

        return db.insert(GROUPS_TABLE, null, values);
    }

    public long insertStudent(SQLiteDatabase db, Student student) {
        ContentValues values = new ContentValues();

        values.put("IDGROUP", student.getGroupID());
        values.put("IDSTUDENT", student.getStudentID());
        values.put("NAME", student.getName());

        return db.insert(STUDENTS_TABLE, null, values);
    }

    public Cursor getGroups(SQLiteDatabase db) {
        return db.rawQuery("select IDGROUP, FACULTY, COURSE, NAME, HEAD from " + GROUPS_TABLE + " order by FACULTY, NAME, COURSE;", null);
    }

    public Cursor getStudentsByGroup(SQLiteDatabase db, String groupID) {
        return db.rawQuery("select IDSTUDENT, NAME from " + STUDENTS_TABLE + " where IDGROUP = " + groupID + ";", null);
    }

    public void deleteStudent(SQLiteDatabase db, Student student) {
        checkHead(db, student);
        db.delete(STUDENTS_TABLE, "IDSTUDENT = ?", new String[] {student.getStudentID()});
    }

    public void updateGroupHead(SQLiteDatabase db, Student student) {
        ContentValues cv = new ContentValues();
        cv.put("HEAD", student.getName());
        db.update(GROUPS_TABLE, cv, "IDGROUP = ?", new String[] {student.getGroupID()});
    }

    public int updateStudent(SQLiteDatabase db, Student student, String oldName, String oldGroup) {
        checkHead(db, student, oldName, oldGroup);

        ContentValues cv = new ContentValues();
        cv.put("NAME", student.getName());
        cv.put("IDGROUP", student.getGroupID());

        return db.update(STUDENTS_TABLE, cv, "IDSTUDENT = ?", new String[] {student.getStudentID()});
    }

    private void checkHead(SQLiteDatabase db, Student student, String oldName, String oldGroup) {
        Cursor cursor = db.rawQuery("select HEAD from " + GROUPS_TABLE + " where IDGROUP = " + oldGroup + ";", null);

        cursor.moveToFirst();
        String headName = cursor.getString(cursor.getColumnIndexOrThrow("HEAD"));
        ContentValues cv = new ContentValues();

        if(headName.equals(oldName) && !student.getGroupID().equals(oldGroup)) {
            cv.put("HEAD", " ");
            db.update(GROUPS_TABLE, cv, "IDGROUP = ?", new String[]{oldGroup});
        } else if(headName.equals(oldName) && student.getGroupID().equals(oldGroup)) {
            cv.put("HEAD", student.getName());
            db.update(GROUPS_TABLE, cv, "IDGROUP = ?", new String[]{student.getGroupID()});
        }
    }

    private void checkHead(SQLiteDatabase db, Student student) {
        Cursor cursor = db.rawQuery("select HEAD from " + GROUPS_TABLE + " where IDGROUP = " + student.getGroupID() + ";", null);

        cursor.moveToFirst();
        String headName = cursor.getString(cursor.getColumnIndexOrThrow("HEAD"));
        ContentValues cv = new ContentValues();

        if(headName.equals(student.getName())) {
            cv.put("HEAD", " ");
            db.update(GROUPS_TABLE, cv, "IDGROUP = ?", new String[]{student.getGroupID()});
        }
    }

    public void deleteGroup(SQLiteDatabase db, String id) {
        db.delete(GROUPS_TABLE, "IDGROUP = ?", new String[] {id});
    }

    public int updateGroup(SQLiteDatabase db, Group group, String oldID) {
        ContentValues values = new ContentValues();

        values.put("IDGROUP", group.getGroupID());
        values.put("FACULTY", group.getFaculty());
        values.put("COURSE", group.getCourse());
        values.put("NAME", group.getName());
        values.put("HEAD", group.getHead());

        return db.update(GROUPS_TABLE, values, "IDGROUP = ?", new String[] {oldID});
    }
}
