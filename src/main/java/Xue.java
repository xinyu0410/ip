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
        System.out.println("Hello! I'm Xue.");
        System.out.println("What can I do for you?");

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(separator);

            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(separator);
                break;
            }

            if (command.equals("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < counter; i++) {
                    System.out.println((i + 1) + ".[" + todo[i].getStatusIcon() + "] "
                            + todo[i].getDescription());
                }
                System.out.println(separator);
                continue;
            }

            if (command.startsWith("mark ")) {
                String taskNumber = command.substring(5).trim();
                try {
                    int index = Integer.parseInt(taskNumber) - 1;
                    if (index >= 0 && index < counter) {
                        todo[index].markAsDone();
                        System.out.println("Nice! I've marked this task as done:");
                        System.out.println("  [X] " + todo[index].getDescription());
                    } else {
                        System.out.println("That task number does not exist.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please provide a valid task number.");
                }
                System.out.println(separator);
                continue;
            }

            if (command.startsWith("unmark ")) {
                String taskNumber = command.substring(7).trim();
                try {
                    int index = Integer.parseInt(taskNumber) - 1;
                    if (index >= 0 && index < counter) {
                        todo[index].markAsNotDone();
                        System.out.println("OK, I've marked this task as not done yet:");
                        System.out.println("  [ ] " + todo[index].getDescription());
                    } else {
                        System.out.println("That task number does not exist.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please provide a valid task number.");
                }
                System.out.println(separator);
                continue;
            }

            todo[counter] = new Task(command);
            counter++;
            System.out.println("added: " + command);
            System.out.println(separator);
        }
    }
}
