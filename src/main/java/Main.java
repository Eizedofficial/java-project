import org.knowm.xchart.*;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Main {
    public static ArrayList<Student> students;

    public static void main(String[] args) {
        Router.start();
    }
}

class Router {
    public static void start() {
        listenForAction();
    }

    public static void listenForAction() {
        while (true) {
            System.out.println("Type action (help for list all actions):");
            Scanner scanner = new Scanner(System.in);
            String action = scanner.nextLine();
            boolean toStop = false;
            System.out.println();

            switch (action) {
                case "stop":
                    toStop = true;
                    break;
                case "help":
                    printActionMap();
                    break;
                case "status":
                    printStatusMessage();
                    break;
                case "parse":
                    startCSVParsing();
                    break;
                case "stats":
                    printStudentStats();
                    break;
                default:
                    System.out.println("Unknown action. Try again");
            }

            System.out.println();

            if (toStop) {
                break;
            }
        }
    }

    private static void printStudentStats() {
        while (true) {
            System.out.println("Insert name or surname or ID. Then choose ID of related student (or stop to return to menu)");
            Scanner scanner = new Scanner(System.in);
            String data = scanner.nextLine();

            if (Objects.equals(data, "stop")) {
                return;
            }

            if (data.matches("[0-9]+")) {
                printStudentInfo(Integer.parseInt(data));
                return;
            } else {
                try {
                    findStudent(data);
                } catch (Exception e) {
                    System.out.println("No students found" + e.getMessage());
                }
            }
        }
    }

    private static void findStudent(String name) throws SQLException, ConnectException {
        String query = String.format("SELECT id, name FROM students WHERE name LIKE '%%%s%%'", name);
        ResultSet res = DataBaseManager.executeQuery(query);
        while(true) {
            assert res != null;
            if (!res.next()) break;
            System.out.printf("[%s] %s\n", res.getInt("id"), res.getString("name"));
        }
    }

    private static void printStudentInfo(int studentId) {
        try {
            Student student = DataBaseManager.getStudentInfo(studentId);

            System.out.println(student.getName());
            if(student.getCity() != null) {
                System.out.println("\tCity: " + student.getCity());
            }
            if(student.getVkId() != null) {
                System.out.printf("\tVK Id: %s\n", student.getVkId());
            }

            for(Chapter chapter : student.getChapters()) {
                System.out.printf("\tChapter: '%s', (%s points):\n", chapter.getName(), chapter.getTotalMark());
                for(Task task : chapter.getTasks()) {
                    System.out.printf("\t\tTask: '%s' - %s points\n", task.getName(), task.getMark());
                }
            }
        } catch (Exception e) {
            System.out.println("Student querying error: " + e.getMessage());
        }
    }

    private static void printActionMap() {
        System.out.println(
                "status - shows the status of app (parsing progress and database storage progress)" +
                "\nstop - stops the app" +
                "\nparse - starts parsing a csv file" +
                "\nstats - shows the stats of student (run after parsing)"
        );
    }

    private static void startCSVParsing() {
        try {
            if (!checkFileExistence()) {
                waitForCSVInserted();
            } else {
                System.out.println("Parsing CSV file");
                Main.students = Parser.parseReport("src\\students.csv");

                System.out.println("Parsing VK data");
                VK.parseVK(Main.students);

                System.out.println("Storing students to database");
                DataBaseManager.storeStudentsToDB(Main.students);
            }
        } catch (Exception e) {
            System.out.printf("FileManager error: %s\n", e.getMessage());
        }
    }

    private static void waitForCSVInserted() throws IOException {
        while (true) {
            String path = new java.io.File("./src").getCanonicalPath();
            System.out.printf("CSV file not found.\nPut CSV file to %s and name it 'students.csv'\nType 'ok' when done, or 'stop' to return to menu\n", path);
            Scanner scanner = new Scanner(System.in);
            String action = scanner.nextLine();

            if (Objects.equals(action, "stop")) {
                return;
            }

            if (Objects.equals(action, "ok")) {
                startCSVParsing();
                break;
            }
        }
    }

    private static boolean checkFileExistence() throws IOException {
        String path = new java.io.File("./src/students.csv").getCanonicalPath();
        File file = new File(path);
        return file.exists();
    }

    private static void printStatusMessage() {
        boolean csvParsed = Main.students != null;

        int amountOfStoredStudents = DataBaseManager.countStoredStudents();
        boolean studentsStored = amountOfStoredStudents != 0;

        String outputMessage = "Csv parsed: %s\nStudents are stored in database : %s\n";
        if (studentsStored) {
            outputMessage += "Amount of stored students: %s\n";
            System.out.printf(outputMessage, csvParsed, studentsStored, amountOfStoredStudents);
        } else {
            System.out.printf(outputMessage, csvParsed, studentsStored);
        }
    }
}
