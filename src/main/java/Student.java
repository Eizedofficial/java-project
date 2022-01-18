import java.util.ArrayList;

public class Student extends People {
    private int databaseId;
    private final int totalMark;
    private final String group;
    private final ArrayList<Chapter> chapters = new ArrayList<>();
    private String city;
    private Integer VkId;

    public void setDatabaseId(int id) {
        this.databaseId = id;
    }

    public int getDatabaseId() {
        return databaseId;
    }

    public Student(String name, String group, int totalMark) {
        super(name);
        this.group = group;
        this.totalMark = totalMark;
    }

    public ArrayList<Chapter> getChapters() {
        return chapters;
    }

    public void addChapter(Chapter chapter) {
        this.chapters.add(chapter);
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void foundInVK(int VkId) {
        this.VkId = VkId;
    }

    public Integer getVkId() {
        return VkId;
    }

    public String getCity() {
        return city;
    }

    public String getGroup() {
        return group;
    }

    public int getTotalMark() {
        return totalMark;
    }
}

class People {
    final private String name;

    public People(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
