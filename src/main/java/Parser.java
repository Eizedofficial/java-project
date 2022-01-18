import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Parser {
    private static BufferedReader reader = null;

    public static ArrayList<Student> parseReport(String file) {
        ArrayList<Chapter> chapters = null;
        ArrayList<Student> students = null;

        try {
            Parser.reader = new BufferedReader(new FileReader(file));
        } catch (Exception e) {
            System.out.printf("Unable to read file. Got error: %s", e.getMessage());
            System.exit(0);
        }

        try {
            chapters = Parser.parseChapters(reader.readLine(), reader.readLine());
            Parser.reader.readLine();
            students = Parser.parseStudents(chapters);
        } catch (Exception e) {
            System.out.printf("Unable to parse file. Got error: %s", e.getMessage());
            System.exit(0);
        }

        return students;
    }

    private static ArrayList<Student> parseStudents(ArrayList<Chapter> chaptersSchema) throws IOException {
        ArrayList<Student> students = new ArrayList<>();
        String studentLine = Parser.reader.readLine();

        while(studentLine != null) {
            String[] studentStats = studentLine.split(";");
            Student student = new Student(studentStats[0], studentStats[1], Integer.parseInt(studentStats[2]));
            for(Chapter chapterSchema : chaptersSchema) {
                Chapter chapter = new Chapter(chapterSchema);
                chapter.setTotalMark(Integer.parseInt(studentStats[chapter.getStartPos()]));
                ArrayList<Task> tasks = new ArrayList<>();
                boolean firstTask = true;

                for (Task task : chapter.getTasks()) {
                    if(firstTask) {
                        firstTask = false;
                        continue;
                    }

                    task.setMark(Integer.parseInt(studentStats[task.getPointerPosition()]));
                    tasks.add(task);
                }

                chapter.setTasks(tasks);
                student.addChapter(chapter);
            }

            students.add(student);
            studentLine = Parser.reader.readLine();
        }

        return students;
    }

    private static ArrayList<Chapter> parseChapters(String chaptersLine, String tasksLine) {
        String[] chapterTitles = chaptersLine.split(";");
        ArrayList<Chapter> chapters = new ArrayList<>();

        Chapter currentChapter = new Chapter(chapterTitles[3], 3);
        for (int pointer = 4; pointer < chapterTitles.length; pointer++) {
            String chapterName = chapterTitles[pointer];

            if (!Objects.equals(chapterName, "")) {
                chapters.add(currentChapter);
                currentChapter = new Chapter(chapterName, pointer);
            } else {
                currentChapter.incrementTasksCounter();
            }
        }

        chapters.add(currentChapter);
        return Parser.parseTasks(chapters, tasksLine);
    }

    private static ArrayList<Chapter> parseTasks(ArrayList<Chapter> chapters, String line) {
        String[] tasksNames = line.split(";");
        int pointer = 3;
        int chaptersCounter = 1;

        for (Chapter chapter : chapters) {
            for (int taskInChapter = 0; taskInChapter < chapter.countTasks(); taskInChapter++) {
                chapter.addTask(tasksNames[pointer], pointer++);
            }
            chaptersCounter++;
            if (chaptersCounter == chapters.size() + 1) {
                chapter.addTask(tasksNames[tasksNames.length - 1], tasksNames.length - 1);
            }
        }

        return chapters;
    }
}

