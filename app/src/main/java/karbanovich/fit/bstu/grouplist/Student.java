package karbanovich.fit.bstu.grouplist;

import java.io.Serializable;

public class Student implements Serializable {

    private String groupID;
    private String studentID;
    private String name;

    public Student(String groupID, String studentID, String name) {
        this.groupID = groupID;
        this.studentID = studentID;
        this.name = name;
    }

    public Student() { }


    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
