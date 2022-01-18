import java.net.ConnectException;
import java.sql.*;
import java.util.ArrayList;

public class DataBaseManager {
    private static Connection conn;
    private static final String[] tables = new String[]{"students", "chapters", "tasks"};

    public static void storeStudentsToDB(ArrayList<Student> students) {
        try {
            DataBaseManager.connect();
        } catch (ConnectException e) {
            System.out.printf("Connection error: %s \n", e.getMessage());
            return;
        }

        try {
            DataBaseManager.clearTables();
        } catch (Exception e) {
            System.out.printf("Tables cleaning error: %s \n", e.getMessage());
            return;
        }

        try {
            DataBaseManager.insertStudentData(students);
        } catch (Exception e) {
            System.out.printf("Students inserting error: %s \n", e.getMessage());
        }

    }

    public static Student getStudentInfo(int id) throws ConnectException, SQLException {
        DataBaseManager.connect();
        Student student = null;

        String studentSql = String.format("SELECT * FROM students WHERE id = %s LIMIT 1", id);
        ResultSet studentData = DataBaseManager.executeQuery(studentSql);
        while(true) {
            assert studentData != null;
            if (!studentData.next()) break;
            student = new Student(studentData.getString("name"), studentData.getString("group"), studentData.getInt("total_mark"));
            student.setCity(studentData.getString("city"));
            student.foundInVK(studentData.getInt("vk_id"));
        }

        String chaptersSql = String.format("SELECT * FROM chapters WHERE student_id = %s", id);
        ResultSet chaptersData = DataBaseManager.executeQuery(chaptersSql);
        while(true) {
            assert chaptersData != null;
            if (!chaptersData.next()) break;
            String taskSql = "SELECT * FROM tasks WHERE chapter_id = " + chaptersData.getInt("id");

            Chapter chapter = new Chapter(chaptersData.getString("name"));
            chapter.setTotalMark(chaptersData.getInt("total_mark"));

            ResultSet tasksData = DataBaseManager.executeQuery(taskSql);
            while(true) {
                assert tasksData != null;
                if (!tasksData.next()) break;
                Task task = new Task(tasksData.getString("name"));
                task.setMark(tasksData.getInt("total_mark"));

                chapter.addTask(task);
            }

            assert student != null;
            student.addChapter(chapter);
        }

        return student;
    }

    public static int countStoredStudents() {
        try {
            DataBaseManager.connect();
            ResultSet response = DataBaseManager.executeQuery("SELECT COUNT(*) FROM students");

            assert response != null;
            return response.getInt("COUNT(*)");
        } catch (Exception e) {
            System.out.printf("DB connecting error: %s\n", e.getMessage());

            return 0;
        }
    }

    private static void insertStudentData(ArrayList<Student> students) throws SQLException, ConnectException {
        int counter = 1;
        int total = students.size();
        for (Student student : students) {
            System.out.printf("Processing: %s / %s (%s)\n", counter, total, student.getName());
            String studentsQuery = "INSERT INTO students ('name', 'city', 'vk_id', 'total_mark', 'group') VALUES " +
                    String.format("('%s', '%s', '%s', '%s', '%s')", student.getName(), student.getCity(), student.getVkId(), student.getTotalMark(), student.getGroup());
            DataBaseManager.executeQuery(studentsQuery);
            int studentId = DataBaseManager.getLastInsertedId("students");
            student.setDatabaseId(studentId);

            for (Chapter chapter : student.getChapters()) {
                String chapterQuery = "INSERT INTO chapters (student_id, name, total_mark) VALUES " +
                        String.format("('%s', '%s', '%s')", studentId, chapter.getName(), chapter.getTotalMark());
                DataBaseManager.executeQuery(chapterQuery);
                int chapterId = DataBaseManager.getLastInsertedId("chapters");

                StringBuilder tasksQuery = new StringBuilder("INSERT INTO tasks (chapter_id, total_mark, name) VALUES ");
                String delimiter = "";
                for (Task task : chapter.getTasks()) {
                    tasksQuery.append(String.format(delimiter + " ('%s', '%s', '%s')", chapterId, task.getMark(), task.getName()));
                    delimiter = ",";
                }
                DataBaseManager.executeQuery(tasksQuery.toString());
            }
            counter++;
        }
    }

    private static int getLastInsertedId(String table) throws SQLException, ConnectException {
        String query = "SELECT last_insert_rowid() FROM " + table;
        ResultSet res = DataBaseManager.executeQuery(query);
        assert res != null;

        return res.getInt("last_insert_rowid()");
    }

    public static ResultSet executeQuery(String query) throws SQLException, ConnectException {
        connect();
        Statement statement = DataBaseManager.conn.createStatement();
        if (statement.execute(query)) {
            return statement.getResultSet();
        } else {
            return null;
        }
    }

    private static void connect() throws ConnectException {
        if(DataBaseManager.conn == null) {
            try {
                Class.forName("org.sqlite.JDBC");
                DataBaseManager.conn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Xiaomi\\IdeaProjects\\BigBrother\\src\\java.db");
            } catch (Exception e) {
                throw new ConnectException(e.getMessage());
            }
        }
    }

    private static void clearTables() throws SQLException, ConnectException {
        for (String tableName : DataBaseManager.tables) {
            DataBaseManager.executeQuery("DELETE FROM " + tableName);
        }
    }
}
