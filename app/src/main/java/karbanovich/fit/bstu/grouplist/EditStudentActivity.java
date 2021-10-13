package karbanovich.fit.bstu.grouplist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class EditStudentActivity extends AppCompatActivity {

    //View
    private TextView studentID;
    private EditText studentName;
    private TextView currentGroup;
    private Spinner spinnerGroups;
    private Button edit;

    //Data
    private Student selectedStudent;
    private List<Group> groupsList;
    private String[] groups;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private String oldName;
    private String oldGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);

        binding();
        setListeners();
    }

    private void binding() {
        Intent intent = getIntent();
        selectedStudent = (Student) intent.getSerializableExtra("student");

        studentID = findViewById(R.id.edtStudID);
        studentName = findViewById(R.id.edtStudName);
        currentGroup = findViewById(R.id.currentGroup);
        spinnerGroups = findViewById(R.id.spinnerGroups);
        edit = findViewById(R.id.btnEditStudent);

        dbHelper = new DBHelper(getApplicationContext());
        db = dbHelper.getReadableDatabase();

        setGroups();
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, groups);
        spinnerGroups.setAdapter(groupAdapter);

        studentID.setText("ID студента: " + selectedStudent.getStudentID());
        studentName.setText(selectedStudent.getName());
        currentGroup.setText("ID группы: " + selectedStudent.getGroupID());

        oldName = selectedStudent.getName();
        oldGroup = selectedStudent.getGroupID();
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

    private void setListeners() {
        edit.setOnClickListener(view -> {
            String name = studentName.getText().toString();

            if(!validation(name))
                return;

            try {
                Student student = new Student(groupsList.get(spinnerGroups.getSelectedItemPosition()).getGroupID(), selectedStudent.getStudentID(), name);

                if(dbHelper.updateStudent(db, student, oldName, oldGroup) != -1)
                    Toast.makeText(this, "Студент успешно изменен", Toast.LENGTH_SHORT).show();
            } catch(Exception e) {
                Toast.makeText(this, "Ошибка изменения", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validation(String name) {
        if(name == null || name.trim().isEmpty()) {
            Toast.makeText(this, "Проверьте введенные данные", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.edit_student_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_appointHead:
                appointHead();
                return true;
            case R.id.action_deleteStudent:
                deleteStudent();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void appointHead() {
        dbHelper.updateGroupHead(db, selectedStudent);
        Toast.makeText(this, "Староста успешно назначен", Toast.LENGTH_SHORT).show();
    }

    private void deleteStudent() {
        dbHelper.deleteStudent(db, selectedStudent);
        startActivity(new Intent(this, GroupsActivity.class));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, GroupsActivity.class));
    }
}