import java.util.Scanner;

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
        String separator = "________________________________________________________________________________";
        String banner = "██   ██  ██   ██  ███████\n"
                + " ██ ██   ██   ██  ██\n"
                + "  ███    ██   ██  █████\n"
                + " ██ ██   ██   ██  ██\n"
                + "██   ██   █████   ███████";
        Task[] todo = new Task[100];
        int counter = 0;

        System.out.println(separator);
        System.out.println(banner);
        System.out.println("Hello, I'm Xue. Try not to make this difficult.");
        System.out.println("What do you want? I have work to do.");

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(separator);

            if (command.equals("bye")) {
                System.out.println("Finally, you're leaving. Bye. Don't make me miss you.");
                System.out.println(separator);
                break;
            }

            try {
                if (command.equals("list")) {
                    System.out.println("Here are your tasks. Yes, I did all the work for you:");
                    for (int i = 0; i < counter; i++) {
                        System.out.println((i + 1) + ".[" + todo[i].getType() + "]["
                                + todo[i].getStatusIcon() + "] " + todo[i].getDescription()
                                + todo[i].getDateTimeDescription());
                    }
                } else if (command.equals("mark") || command.startsWith("mark ")) {
                    int index = getTaskIndex(command, "mark", counter);
                    todo[index].markAsDone();
                    System.out.println("Fine, I've marked this task as done. Happy now?");
                    System.out.println("  [X] " + todo[index].getDescription());
                } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                    int index = getTaskIndex(command, "unmark", counter);
                    todo[index].markAsNotDone();
                    System.out.println("There. I've undone it. Try to make up your mind next time:");
                    System.out.println("  [ ] " + todo[index].getDescription());
                } else if (command.equals("delete") || command.startsWith("delete ")) {
                    int index = getTaskIndex(command, "delete", counter);
                    Task deletedTask = todo[index];
                    counter = deleteTask(todo, counter, index);
                    System.out.println("Fine, I've removed this task:");
                    System.out.println("  [" + deletedTask.getType() + "][" + deletedTask.getStatusIcon() + "] "
                            + deletedTask.getDescription() + deletedTask.getDateTimeDescription());
                    System.out.println("Now you have " + counter + " tasks in the list.");
                } else if (command.equals("todo") || command.startsWith("todo ")) {
                    counter = addTask(todo, counter, new Task(command.substring(4).trim()), "todo");
                } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                    String[] parts = splitDateCommand(command, "deadline", "/by");
                    counter = addTask(todo, counter, new Task("D", parts[0], null, parts[1]), "deadline");
                } else if (command.equals("event") || command.startsWith("event ")) {
                    String[] parts = splitEventCommand(command);
                    counter = addTask(todo, counter, new Task("E", parts[0], parts[1], parts[2]), "event");
                } else {
                    throw new XueException("I don't know what that means. Use a proper command next time.");
                }
            } catch (XueException e) {
                System.out.println("OOPS!!! " + e.getMessage());
            }
            System.out.println(separator);
        }
    }

    /** Removes a task and shifts later tasks so list numbering remains continuous. */
    private static int deleteTask(Task[] tasks, int taskCount, int index) {
        for (int i = index; i < taskCount - 1; i++) {
            tasks[i] = tasks[i + 1];
        }
        tasks[taskCount - 1] = null;
        return taskCount - 1;
    }

    /** Adds a task to the list and prints the confirmation message. */
    private static int addTask(Task[] tasks, int taskCount, Task task, String command) throws XueException {
        if (taskCount == tasks.length) {
            throw new XueException("Your task list is full. I refuse to carry any more of your tasks!");
        }
        tasks[taskCount] = task;
        System.out.println("Got it. I've added this task: [" + task.getType() + "][ ] "
                + task.getDescription() + task.getDateTimeDescription());
        System.out.println("Now you have " + (taskCount + 1) + " tasks in the list.");
        return taskCount + 1;
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
