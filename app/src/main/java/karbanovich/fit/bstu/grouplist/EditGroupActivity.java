package karbanovich.fit.bstu.grouplist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditGroupActivity extends AppCompatActivity {

    //View
    private EditText groupID;
    private EditText faculty;
    private EditText course;
    private EditText name;
    private Button edit;
    private Button delete;


    //Data
    private Group selectedGroup;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private String oldID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        binding();
        setListeners();
    }

    private void binding() {
        Intent intent = getIntent();
        selectedGroup = (Group) intent.getSerializableExtra("group");

        groupID = findViewById(R.id.edtGroupID);
        faculty = findViewById(R.id.edtFaculty);
        course = findViewById(R.id.edtCourse);
        name = findViewById(R.id.edtGroupName);
        edit = findViewById(R.id.btnEditGroup);
        delete = findViewById(R.id.btnDeleteGroup);

        dbHelper = new DBHelper(getApplicationContext());
        db = dbHelper.getReadableDatabase();

        groupID.setText(selectedGroup.getGroupID());
        faculty.setText(selectedGroup.getFaculty());
        course.setText(String.valueOf(selectedGroup.getCourse()));
        name.setText(selectedGroup.getName());

        oldID = selectedGroup.getGroupID();
    }

    private void setListeners() {
        edit.setOnClickListener(view -> {
            String groupID = this.groupID.getText().toString();
            String faculty = this.faculty.getText().toString();
            String course = this.course.getText().toString();
            String name = this.name.getText().toString();

            if(!checkInt(course) || !validation(groupID, faculty, Integer.parseInt(course), name))
                return;

            try {
                Group group = new Group(groupID, faculty, Integer.parseInt(course), name, " ");

                if(dbHelper.updateGroup(db, group, oldID) != -1) {
                    Toast.makeText(this, "Группа успешно изменена", Toast.LENGTH_SHORT).show();
                    selectedGroup = group;
                    oldID = selectedGroup.getGroupID();
                }
            } catch(Exception e) {
                Toast.makeText(this, "Ошибка изменения", Toast.LENGTH_SHORT).show();
            }
        });

        delete.setOnClickListener(view -> {
            dbHelper.deleteGroup(db, selectedGroup.getGroupID());
            startActivity(new Intent(this, GroupsActivity.class));
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
        startActivity(new Intent(this, GroupsActivity.class));
    }
}