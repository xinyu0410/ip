package xue;

import xue.model.Task;
import xue.model.TaskList;
import xue.storage.Storage;

import java.util.List;

/** Processes one Xue command and returns the user-facing response. */
public class CommandProcessor {
    private static final String DEADLINE_MARKER = "/by";
    private static final String EVENT_FROM_MARKER = "/from";
    private static final String EVENT_TO_MARKER = "/to";
    private final TaskList tasks;
    private final Storage storage;
    private final String loadError;

    /** Creates a processor and restores tasks saved on disk. */
    public CommandProcessor(Storage storage) {
        this.storage = storage;
        List<Task> savedTasks;
        String loadingError;
        try {
            savedTasks = storage.load();
            loadingError = null;
        } catch (XueException e) {
            savedTasks = List.of();
            loadingError = e.getMessage();
        }
        tasks = new TaskList(savedTasks);
        loadError = loadingError;
    }

    /** Returns the storage-load error, or {@code null} when loading succeeded. */
    public String getLoadError() {
        return loadError;
    }

    /** Processes a command and returns its response, or an error response. */
    public String process(String command) {
        if (command == null) {
            return "";
        }
        command = command.trim();
        try {
            if (command.equals("bye")) {
                return "Finally, you're leaving. Bye. Don't make me miss you.";
            } else if (command.equals("list")) {
                return "Here are your tasks. Yes, I did all the work for you:\n" + formatTasks(tasks.asList());
            } else if (command.equals("find") || command.startsWith("find ")) {
                String keyword = command.substring("find".length()).trim();
                if (keyword.isEmpty()) {
                    throw new XueException("Tell me what to find. I am not a mind reader.");
                }
                return "Here are the matching tasks in your list:\n" + formatTasks(tasks.find(keyword));
            } else if (command.equals("mark") || command.startsWith("mark ")) {
                int index = getTaskIndex(command, "mark");
                tasks.get(index).markAsDone();
                storage.save(tasks.asList());
                return "Fine, I've marked this task as done. Happy now?\n  [X] "
                        + tasks.get(index).getDescription();
            } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                int index = getTaskIndex(command, "unmark");
                tasks.get(index).markAsNotDone();
                storage.save(tasks.asList());
                return "There. I've undone it. Try to make up your mind next time:\n  [ ] "
                        + tasks.get(index).getDescription();
            } else if (command.equals("delete") || command.startsWith("delete ")) {
                int index = getTaskIndex(command, "delete");
                Task deletedTask = tasks.delete(index);
                storage.save(tasks.asList());
                return "Fine, I've removed this task:\n  [" + deletedTask.getType() + "]["
                        + deletedTask.getStatusIcon() + "] " + deletedTask.getDescription()
                        + deletedTask.getDateTimeDescription() + "\nNow you have " + tasks.size()
                        + " tasks in the list.";
            } else if (command.equals("todo") || command.startsWith("todo ")) {
                return addTask(new Task(command.substring(4).trim()));
            } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                String[] parts = splitDateCommand(command, "deadline", DEADLINE_MARKER);
                return addTask(new Task("D", parts[0], null, parts[1]));
            } else if (command.equals("event") || command.startsWith("event ")) {
                String[] parts = splitEventCommand(command);
                return addTask(new Task("E", parts[0], parts[1], parts[2]));
            }
            throw new XueException("I don't know what that means. Use a proper command next time.");
        } catch (XueException e) {
            return "OOPS!!! " + e.getMessage();
        }
    }

    private String addTask(Task task) {
        tasks.add(task);
        storage.save(tasks.asList());
        return "Got it. I've added this task: [" + task.getType() + "][ ] " + task.getDescription()
                + task.getDateTimeDescription() + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    private String formatTasks(List<Task> taskList) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < taskList.size(); i++) {
            Task task = taskList.get(i);
            result.append(i + 1).append(".[").append(task.getType()).append("][")
                    .append(task.getStatusIcon()).append("] ").append(task.getDescription())
                    .append(task.getDateTimeDescription());
            if (i < taskList.size() - 1) {
                result.append('\n');
            }
        }
        return result.toString();
    }

    private int getTaskIndex(String command, String action) {
        String taskNumber = command.substring(action.length()).trim();
        if (taskNumber.isEmpty()) {
            throw new XueException("Tell me which task to " + action + ". I am not a mind reader.");
        }
        if (!taskNumber.matches("\\d+")) {
            throw new XueException("That is not a valid task number. Numbers are not that complicated.");
        }
        try {
            int index = Integer.parseInt(taskNumber) - 1;
            if (index < 0 || index >= tasks.size()) {
                throw new XueException("That task number does not exist. Did you just invent it?");
            }
            return index;
        } catch (NumberFormatException e) {
            throw new XueException("That is not a valid task number. Numbers are not that complicated.");
        }
    }

    private String[] splitDateCommand(String command, String keyword, String marker) {
        String body = command.substring(keyword.length()).trim();
        int markerIndex = body.indexOf(marker);
        if (markerIndex < 0) {
            throw new XueException("A deadline needs a /by date. Please do the minimum.");
        }
        return new String[] {body.substring(0, markerIndex).trim(),
            body.substring(markerIndex + marker.length()).trim()};
    }

    private String[] splitEventCommand(String command) {
        String body = command.substring("event".length()).trim();
        int fromIndex = body.indexOf(EVENT_FROM_MARKER);
        int toIndex = body.indexOf(EVENT_TO_MARKER, fromIndex + EVENT_FROM_MARKER.length());
        if (fromIndex < 0 || toIndex < 0) {
            throw new XueException("An event needs /from and /to times. I cannot guess your schedule.");
        }
        return new String[] {body.substring(0, fromIndex).trim(),
            body.substring(fromIndex + EVENT_FROM_MARKER.length(), toIndex).trim(),
            body.substring(toIndex + EVENT_TO_MARKER.length()).trim()};
    }
}
