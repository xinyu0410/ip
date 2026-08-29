import java.util.ArrayList;
import java.util.List;

/** Stores and manages the tasks known to Xue. */
public class TaskList {
    private static final int MAX_TASKS = 100;
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() { this.tasks = new ArrayList<>(); }

    /** Creates a task list containing saved tasks. */
    public TaskList(List<Task> savedTasks) { this.tasks = new ArrayList<>(savedTasks); }

    /** Returns the number of tasks. */
    public int size() { return tasks.size(); }

    /** Returns the task at a zero-based index. */
    public Task get(int index) { return tasks.get(index); }

    /** Adds a task, enforcing Xue's task limit. */
    public void add(Task task) {
        if (tasks.size() == MAX_TASKS) {
            throw new XueException("Your task list is full. I refuse to carry any more of your tasks!");
        }
        tasks.add(task);
    }

    /** Removes and returns a task at a zero-based index. */
    public Task delete(int index) { return tasks.remove(index); }

    /** Returns a copy for storage without exposing internal state. */
    public List<Task> asList() { return new ArrayList<>(tasks); }
}
