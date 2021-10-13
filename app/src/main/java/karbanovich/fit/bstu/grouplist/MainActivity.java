package karbanovich.fit.bstu.grouplist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    //View
    private Button addGroup;
    private Button addStudent;
    private Button groups;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding();
        setListeners();
    }

    private void binding() {
        addGroup = findViewById(R.id.btnAddGroup);
        addStudent = findViewById(R.id.btnAddStudent);
        groups = findViewById(R.id.btnGroups);
    }

    private void setListeners() {
        addGroup.setOnClickListener(view ->{
            startActivity(new Intent(this, AddGroupActivity.class));
        });
        addStudent.setOnClickListener(view -> {
            startActivity(new Intent(this, AddStudentActivity.class));
        });
        groups.setOnClickListener(view -> {
            startActivity(new Intent(this, GroupsActivity.class));
        });
    }
}