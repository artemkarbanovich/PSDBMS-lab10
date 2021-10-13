package karbanovich.fit.bstu.grouplist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddGroupActivity extends AppCompatActivity {

    //View
    private EditText groupID;
    private EditText faculty;
    private EditText course;
    private EditText name;
    private Button save;

    //Data
    private DBHelper dbHelper;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        binding();
        setListeners();
    }

    private void binding() {
        groupID = findViewById(R.id.groupID);
        faculty = findViewById(R.id.faculty);
        course = findViewById(R.id.course);
        name = findViewById(R.id.groupName);
        save = findViewById(R.id.btnSaveGroup);

        dbHelper = new DBHelper(getApplicationContext());
        db = dbHelper.getReadableDatabase();
    }

    private void setListeners() {
        save.setOnClickListener(view -> {
            String groupID = this.groupID.getText().toString();
            String faculty = this.faculty.getText().toString();
            String course = this.course.getText().toString();
            String name = this.name.getText().toString();

            if(!checkInt(course) || !validation(groupID, faculty, Integer.parseInt(course), name))
                return;

            try {
                Group group = new Group(groupID, faculty, Integer.parseInt(course), name, " ");

                if(dbHelper.insertGroup(db, group) != -1) {
                    Toast.makeText(this, "Группа успешно добавлена", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else
                    Toast.makeText(this, "Такой ID уже существует", Toast.LENGTH_SHORT).show();
            } catch(Exception e) {
                Toast.makeText(this, "Ошибка добавления", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validation(String groupID, String faculty, int course, String name) {
        if(groupID == null || groupID.isEmpty() || groupID.contains(" ") ||
           faculty == null || faculty.trim().isEmpty() ||
           course < 1 || course > 6 ||
           name == null || name.trim().isEmpty()) {
            Toast.makeText(this, "Проверьте введенные данные", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void clearFields() {
        groupID.setText("");
        faculty.setText("");
        course.setText("");
        name.setText("");
    }

    private boolean checkInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}