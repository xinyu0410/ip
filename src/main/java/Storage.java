import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Reads and writes Xue tasks in a stable, machine-readable text format. */
public class Storage {
    private final Path filePath;

    /** Creates storage backed by the usual {@code data/duke.txt} file. */
    public Storage() {
        this(Path.of("data", "duke.txt"));
    }

    /** Creates storage backed by the specified file, useful for tests. */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /** Loads all valid saved tasks, returning an empty list for a missing file. */
    public List<Task> load() {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        try {
            List<Task> tasks = new ArrayList<>();
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                String[] fields = line.split(" \\| ", -1);
                if (fields.length < 3 || !(fields[0].equals("T") || fields[0].equals("D")
                        || fields[0].equals("E"))) {
                    continue;
                }
                boolean done = fields[1].equals("1");
                String from = fields[0].equals("E") ? fields[3] : null;
                String to = fields[0].equals("T") ? null : fields[fields[0].equals("E") ? 4 : 3];
                tasks.add(new Task(fields[0], fields[2], from, to, done));
            }
            return tasks;
        } catch (IOException | RuntimeException e) {
            throw new XueException("I could not read your saved tasks.");
        }
    }

    /** Saves the current tasks and creates the parent folder when necessary. */
    public void save(Task[] tasks, int taskCount) {
        try {
            Files.createDirectories(filePath.getParent());
            List<String> lines = new ArrayList<>();
            for (int i = 0; i < taskCount; i++) {
                Task task = tasks[i];
                StringBuilder line = new StringBuilder(task.getType())
                        .append(" | ").append(task.isDone() ? "1" : "0")
                        .append(" | ").append(task.getDescription());
                if ("D".equals(task.getType())) {
                    line.append(" | ").append(task.getTo());
                } else if ("E".equals(task.getType())) {
                    line.append(" | ").append(task.getFrom()).append(" | ").append(task.getTo());
                }
                lines.add(line.toString());
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new XueException("I could not save your tasks.");
        }
    }
}
