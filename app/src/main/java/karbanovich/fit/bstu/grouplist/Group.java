package karbanovich.fit.bstu.grouplist;

import java.io.Serializable;

public class Group implements Serializable {

    private String groupID;
    private String faculty;
    private int course;
    private String name;
    private String head;

    public Group(String groupID, String faculty, int course, String name, String head) {
        this.groupID = groupID;
        this.faculty = faculty;
        this.course = course;
        this.name = name;
        this.head = head;
    }

    public Group() { }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }
}
