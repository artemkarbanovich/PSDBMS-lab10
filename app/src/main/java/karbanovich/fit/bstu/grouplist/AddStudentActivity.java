package karbanovich.fit.bstu.grouplist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddStudentActivity extends AppCompatActivity {

    //View
    private EditText studentID;
    private EditText name;
    private Spinner group;
    private Button save;

    //Data
    private List<Group> groupsList;
    private String[] groups;
    private DBHelper dbHelper;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        binding();
        setListeners();
    }

    private void binding() {
        studentID = findViewById(R.id.studentID);
        name = findViewById(R.id.studentName);
        group = findViewById(R.id.group);
        save = findViewById(R.id.btnSaveStudent);

        dbHelper = new DBHelper(getApplicationContext());
        db = dbHelper.getReadableDatabase();

        setGroups();
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, groups);
        group.setAdapter(groupAdapter);
    }

    private void setListeners() {
        save.setOnClickListener(view -> {
            String studentID = this.studentID.getText().toString();
            String name = this.name.getText().toString();

            if(!validation(studentID, name))
                return;

            try {
                Student student = new Student(groupsList.get(group.getSelectedItemPosition()).getGroupID(), studentID, name);

                if(dbHelper.insertStudent(db, student) != -1) {
                    Toast.makeText(this, "Студент успешно добавлен", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else
                    Toast.makeText(this, "Такой ID уже существует", Toast.LENGTH_SHORT).show();
            } catch(Exception e) {
                Toast.makeText(this, "Ошибка добавления", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setGroups() {
        groupsList = new ArrayList<>();
        Cursor cursor = dbHelper.getGroups(db);

        if(cursor.getCount() == 0) {
            groups = new String[] {" "};
            return;
        }
        groups = new String[cursor.getCount()];
        int i = 0;

        while(cursor.moveToNext()) {
            Group group = new Group();

            group.setGroupID(cursor.getString(cursor.getColumnIndexOrThrow("IDGROUP")));
            group.setFaculty(cursor.getString(cursor.getColumnIndexOrThrow("FACULTY")));
            group.setCourse(cursor.getInt(cursor.getColumnIndexOrThrow("COURSE")));
            group.setName(cursor.getString(cursor.getColumnIndexOrThrow("NAME")));

            groupsList.add(i, group);
            groups[i++] = group.getGroupID() + " " + group.getFaculty() + " " + group.getName() + "-" + group.getCourse();
        }
    }

    private void clearFields() {
        studentID.setText("");
        name.setText("");
    }

    private boolean validation(String studentID, String name) {
        if(studentID == null || studentID.isEmpty() || studentID.contains(" ") ||
           name == null || name.trim().length() < 5 ||
           groupsList.size() == 0) {
            Toast.makeText(this, "Проверьте введенные данные", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}