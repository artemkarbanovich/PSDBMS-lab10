package karbanovich.fit.bstu.grouplist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class GroupsActivity extends AppCompatActivity {

    //View
    private Spinner groups;
    private ListView studentsList;
    private TextView groupHead;

    //Data
    CustomListAdapter customListAdapter;
    private String[] groupsStr;
    private ArrayList<Group> groupsList;
    private ArrayList<Student> studList;
    private DBHelper dbHelper;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        binding();
        setListeners();
    }

    private void binding() {
        groups = (Spinner) findViewById(R.id.groups);
        studentsList = (ListView) findViewById(R.id.studentsList);
        groupHead = (TextView) findViewById(R.id.groupHead);

        dbHelper = new DBHelper(getApplicationContext());
        db = dbHelper.getReadableDatabase();

        setGroups();
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, groupsStr);
        groups.setAdapter(groupAdapter);

        setStudents();
        customListAdapter = new CustomListAdapter(this, studList);
        studentsList.setAdapter(customListAdapter);
    }

    private void setListeners() {
        groups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                customListAdapter.updateStudentList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }

    private void setGroups() {
        groupsList = new ArrayList<>();
        Cursor cursor = dbHelper.getGroups(db);

        if(cursor.getCount() == 0) {
            groupsStr = new String[] {" "};
            return;
        }
        groupsStr = new String[cursor.getCount()];
        int i = 0;

        while(cursor.moveToNext()) {
            Group group = new Group();

            group.setGroupID(cursor.getString(cursor.getColumnIndexOrThrow("IDGROUP")));
            group.setFaculty(cursor.getString(cursor.getColumnIndexOrThrow("FACULTY")));
            group.setCourse(cursor.getInt(cursor.getColumnIndexOrThrow("COURSE")));
            group.setName(cursor.getString(cursor.getColumnIndexOrThrow("NAME")));
            group.setHead(cursor.getString(cursor.getColumnIndexOrThrow("HEAD")));

            groupsList.add(i, group);
            groupsStr[i++] = group.getGroupID() + " " + group.getFaculty() + " " + group.getName() + "-" + group.getCourse();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.groups_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_editGroup:
                editGroup();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editGroup() {
        Group group = groupsList.get(groups.getSelectedItemPosition());
        Intent intent = new Intent(this, EditGroupActivity.class);
        intent.putExtra("group", group);
        startActivity(intent);
    }

    private void setStudents() {
        if(groupsList.size() == 0) {
            studList = new ArrayList<>();
            return;
        }

        Cursor cursor = dbHelper.getStudentsByGroup(db, groupsList.get(groups.getSelectedItemPosition()).getGroupID());
        studList = new ArrayList<>();

        while(cursor.moveToNext()) {
            Student student = new Student();

            student.setStudentID(cursor.getString(cursor.getColumnIndexOrThrow("IDSTUDENT")));
            student.setName(cursor.getString(cursor.getColumnIndexOrThrow("NAME")));

            studList.add(student);
        }

        if(groupsList.get(groups.getSelectedItemPosition()).getHead().equals(" "))
            groupHead.setText("Староста: -");
        else
            groupHead.setText("Староста: " + groupsList.get(groups.getSelectedItemPosition()).getHead());
    }

    public class CustomListAdapter extends BaseAdapter {

        private ArrayList<Student> students;
        private Context context;


        public CustomListAdapter(Context context, ArrayList<Student> students) {
            this.context = context;
            this.students = students;
        }

        @Override
        public int getCount() {return students.size();}

        @Override
        public Object getItem(int i) {return null;}

        @Override
        public long getItemId(int i) {return 0;}

        public void updateStudentList() {
            setStudents();
            students.clear();
            students.addAll(studList);
            this.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View view = getLayoutInflater().inflate(R.layout.student_item, null);

            TextView itemID = (TextView) view.findViewById(R.id.itemID);
            TextView itemName = (TextView) view.findViewById(R.id.itemName);

            itemID.setText("ID: " + students.get(position).getStudentID());
            itemName.setText(students.get(position).getName());

            view.setOnClickListener(v -> {
                Intent intent = new Intent(GroupsActivity.this, EditStudentActivity.class);
                students.get(position).setGroupID(groupsList.get(groups.getSelectedItemPosition()).getGroupID());
                intent.putExtra("student", students.get(position));
                startActivity(intent);
            });

            return view;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}