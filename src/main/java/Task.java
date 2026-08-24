/**
 * Represents a task in Xue's task list.
 */
public class Task {
    private final String type;
    private final String description;
    private final String from;
    private final String to;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the task description
     */
    public Task(String description) {
        this("T", description, null, null);
    }

    /** Creates a dated task with the supplied display details. */
    public Task(String type, String description, String from, String to) {
        if (description == null || description.trim().isEmpty()) {
            throw new XueException("The description of a todo cannot be empty. I cannot read your mind!");
        }
        this.type = type;
        this.description = description;
        this.from = from;
        this.to = to;
        this.isDone = false;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the status icon used when displaying this task.
     *
     * @return {@code X} for a completed task, otherwise a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns this task's description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /** Returns the one-letter task type shown in the list. */
    public String getType() {
        return type;
    }

    /** Returns the optional date/time suffix shown in the list. */
    public String getDateTimeDescription() {
        if ("D".equals(type)) {
            return " (by: " + to + ")";
        }
        if ("E".equals(type)) {
            return " (from: " + from + " to: " + to + ")";
        }
        return "";
    }
}
