package xue;

import xue.model.Task;
import xue.model.TaskList;
import xue.storage.Storage;
import xue.ui.Ui;

import java.util.List;

/**
 * The main entry point for Xue.
 */
public class Xue {
    /**
     * Starts Xue and processes commands until the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        TaskList tasks = new TaskList();
        Storage storage = new Storage();
        List<Task> savedTasks;
        try {
            savedTasks = storage.load();
        } catch (XueException e) {
            savedTasks = List.of();
            ui.showError(e.getMessage());
        }
        tasks = new TaskList(savedTasks);

        ui.showWelcome();

        String command;
        while ((command = ui.readCommand()) != null) {
            ui.showLine();

            if (command.equals("bye")) {
                System.out.println("Finally, you're leaving. Bye. Don't make me miss you.");
                ui.showLine();
                break;
            }

            try {
                if (command.equals("list")) {
                    System.out.println("Here are your tasks. Yes, I did all the work for you:");
                    printTasks(tasks.asList());
                } else if (command.equals("find") || command.startsWith("find ")) {
                    String keyword = command.substring("find".length()).trim();
                    if (keyword.isEmpty()) {
                        throw new XueException("Tell me what to find. I am not a mind reader.");
                    }
                    System.out.println("Here are the matching tasks in your list:");
                    printTasks(tasks.find(keyword));
                } else if (command.equals("mark") || command.startsWith("mark ")) {
                    int index = getTaskIndex(command, "mark", tasks.size());
                    tasks.get(index).markAsDone();
                    storage.save(tasks.asList());
                    System.out.println("Fine, I've marked this task as done. Happy now?");
                    System.out.println("  [X] " + tasks.get(index).getDescription());
                } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                    int index = getTaskIndex(command, "unmark", tasks.size());
                    tasks.get(index).markAsNotDone();
                    storage.save(tasks.asList());
                    System.out.println("There. I've undone it. Try to make up your mind next time:");
                    System.out.println("  [ ] " + tasks.get(index).getDescription());
                } else if (command.equals("delete") || command.startsWith("delete ")) {
                    int index = getTaskIndex(command, "delete", tasks.size());
                    Task deletedTask = tasks.delete(index);
                    storage.save(tasks.asList());
                    System.out.println("Fine, I've removed this task:");
                    System.out.println("  [" + deletedTask.getType() + "][" + deletedTask.getStatusIcon() + "] "
                            + deletedTask.getDescription() + deletedTask.getDateTimeDescription());
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                } else if (command.equals("todo") || command.startsWith("todo ")) {
                    addTask(tasks, new Task(command.substring(4).trim()));
                    storage.save(tasks.asList());
                } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                    String[] parts = splitDateCommand(command, "deadline", "/by");
                    addTask(tasks, new Task("D", parts[0], null, parts[1]));
                    storage.save(tasks.asList());
                } else if (command.equals("event") || command.startsWith("event ")) {
                    String[] parts = splitEventCommand(command);
                    addTask(tasks, new Task("E", parts[0], parts[1], parts[2]));
                    storage.save(tasks.asList());
                } else {
                    throw new XueException("I don't know what that means. Use a proper command next time.");
                }
            } catch (XueException e) {
                ui.showError(e.getMessage());
            }
            ui.showLine();
        }
    }

    /** Adds a task to the list and prints the confirmation message. */
    private static void addTask(TaskList tasks, Task task) throws XueException {
        tasks.add(task);
        System.out.println("Got it. I've added this task: [" + task.getType() + "][ ] "
                + task.getDescription() + task.getDateTimeDescription());
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
    }

    /** Prints tasks with one-based numbering for list and find commands. */
    private static void printTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            System.out.println((i + 1) + ".[" + task.getType() + "]["
                    + task.getStatusIcon() + "] " + task.getDescription()
                    + task.getDateTimeDescription());
        }
    }

    /** Splits a deadline command into its description and date/time. */
    private static String[] splitDateCommand(String command, String keyword, String marker) {
        String body = command.substring(keyword.length()).trim();
        int markerIndex = body.indexOf(marker);
        if (markerIndex < 0) {
            throw new XueException("A deadline needs a /by date. Please do the minimum.");
        }
        return new String[] {body.substring(0, markerIndex).trim(),
                body.substring(markerIndex + marker.length()).trim()};
    }

    /** Splits an event command into its description, start, and end. */
    private static String[] splitEventCommand(String command) {
        String body = command.substring("event".length()).trim();
        int fromIndex = body.indexOf("/from");
        int toIndex = body.indexOf("/to", fromIndex + 5);
        if (fromIndex < 0 || toIndex < 0) {
            throw new XueException("An event needs /from and /to times. I cannot guess your schedule.");
        }
        return new String[] {body.substring(0, fromIndex).trim(),
                body.substring(fromIndex + 5, toIndex).trim(),
                body.substring(toIndex + 3).trim()};
    }

    /** Parses and validates a task number for a mark or unmark command. */
    private static int getTaskIndex(String command, String action, int taskCount) throws XueException {
        String taskNumber = command.substring(action.length()).trim();
        if (taskNumber.isEmpty()) {
            throw new XueException("Tell me which task to " + action + ". I am not a mind reader.");
        }
        if (!taskNumber.matches("\\d+")) {
            throw new XueException("That is not a valid task number. Numbers are not that complicated.");
        }
        try {
            int index = Integer.parseInt(taskNumber) - 1;
            if (index < 0 || index >= taskCount) {
                throw new XueException("That task number does not exist. Did you just invent it?");
            }
            return index;
        } catch (NumberFormatException e) {
            throw new XueException("That is not a valid task number. Numbers are not that complicated.");
        }
    }
}
