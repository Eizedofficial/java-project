import java.util.ArrayList;

public class Chapter {
    private final String name;
    private int tasksCounter = 1;
    private int startPos = 0;
    private int totalMark;
    private ArrayList<Task> tasks = new ArrayList<>();

    public Chapter(String name, int startPos) {
        this.startPos = startPos;
        this.name = name;
    }

    public Chapter(String name) {
        this.name = name;
    }

    public int getTotalMark() {
        return this.totalMark;
    }

    public Chapter(Chapter chapter) {
        this.name = chapter.name;
        this.startPos = chapter.startPos;
        this.tasksCounter = chapter.tasksCounter;
        this.totalMark = chapter.totalMark;

        for(Task task : chapter.getTasks()) {
            this.addTask(task);
        }
    }

    public String getName() {
        return name;
    }

    public void incrementTasksCounter() {
        this.tasksCounter++;
    }

    public void addTask(String name, int position) {
        tasks.add(new Task(name, position));
    }

    public void addTask(Task task) {
        tasks.add(new Task(task));
    }

    public ArrayList<Task> getTasks() {
        return this.tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public int countTasks() {
        return tasksCounter;
    }

    public void setTotalMark(int totalMark) {
        this.totalMark = totalMark;
    }

    public int getStartPos() {
        return startPos;
    }
}

class Task {
    private final String name;
    private int mark;
    private int pointerPosition;

    public Task(String name, int pointerPosition) {
        this.name = name;
        this.pointerPosition = pointerPosition;
    }

    public Task(String name) {
        this.name = name;
    }

    public Task(Task task) {
        this.name = task.name;
        this.mark = task.mark;
        this.pointerPosition = task.pointerPosition;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public String getName() {
        return this.name;
    }

    public int getMark() {
        return this.mark;
    }

    public int getPointerPosition() {
        return pointerPosition;
    }
}
