# UI Test Plan

This file is the source of truth for the `test-ui` skill.

## Execution notes

- Run commands from the repository root.
- Use Java 25 for Java application or build commands.
- Feed each case's inputs through standard input exactly as written.
- Compare output exactly unless a case documents an allowed normalization.
- Run cases in listed order and stop immediately after the first failure.
- For test cases 1–6, start with no `data/duke.txt` file so saved state from another case does not affect the result.
- For GUI checks, run `gradlew run` from the repository root and use test case 10.

## Test cases

<!-- Add cases using this structure. Keep the command and expected output reproducible. -->

### Test case 1: specific input errors

**Aim:** Verify that empty todo descriptions, unknown commands, and invalid task numbers produce specific errors and do not terminate Xue.

**Command:**

```text
javac -d out (Get-ChildItem -Recurse src/main/java -Filter *.java); java -cp out xue.Xue
```

**Inputs:**

```text
todo
blah
mark abc
mark
mark 99
todo buy milk
mark 1
unmark 1
list
bye
```

**Expected output:**

```text
The output must contain, in order:
OOPS!!! The description of a todo cannot be empty. I cannot read your mind!
OOPS!!! I don't know what that means. Use a proper command next time.
OOPS!!! That is not a valid task number. Numbers are not that complicated.
OOPS!!! Tell me which task to mark. I am not a mind reader.
OOPS!!! That task number does not exist. Did you just invent it?
added: buy milk. One more thing for me to deal with.
Fine, I've marked this task as done. Happy now?
There. I've undone it. Try to make up your mind next time:
1.[ ] buy milk
Finally, you're leaving. Bye. Don't make me miss you.
```

### Test case 2: invalid commands do not corrupt task state

**Aim:** Verify that invalid additions and task-number commands leave existing tasks unchanged while valid commands still work afterward.

**Command:**

```text
javac -d out (Get-ChildItem -Recurse src/main/java -Filter *.java); java -cp out xue.Xue
```

**Inputs:**

```text
todo finish report
todo
list
mark 0
list
mark 1
mark 1 extra
list
unmark 1
list
bye
```

**Expected output:**

```text
The output must contain, in order:
added: finish report. One more thing for me to deal with.
OOPS!!! The description of a todo cannot be empty. I cannot read your mind!
1.[ ] finish report
OOPS!!! That task number does not exist. Did you just invent it?
1.[ ] finish report
Fine, I've marked this task as done. Happy now?
OOPS!!! That is not a valid task number. Numbers are not that complicated.
1.[X] finish report
There. I've undone it. Try to make up your mind next time:
1.[ ] finish report
Finally, you're leaving. Bye. Don't make me miss you.
```

### Test case 3: whitespace and malformed command edge cases

**Aim:** Verify that whitespace-only descriptions, malformed command names, and malformed task numbers are rejected without creating tasks.

**Command:**

```text
javac -d out (Get-ChildItem -Recurse src/main/java -Filter *.java); java -cp out xue.Xue
```

**Inputs:**

```text
   
todo    
Todo valid-looking task
todo   actual task
mark +1
unmark -1
list
bye now
bye
```

**Expected output:**

```text
The output must contain, in order:
OOPS!!! I don't know what that means. Use a proper command next time.
OOPS!!! The description of a todo cannot be empty. I cannot read your mind!
OOPS!!! I don't know what that means. Use a proper command next time.
added: actual task. One more thing for me to deal with.
OOPS!!! That is not a valid task number. Numbers are not that complicated.
OOPS!!! That task number does not exist. Did you just invent it?
1.[ ] actual task
OOPS!!! I don't know what that means. Use a proper command next time.
Finally, you're leaving. Bye. Don't make me miss you.
```

### Test case 4: multiple tasks and boundary indices

**Aim:** Verify that task numbering remains correct with multiple tasks and that only valid boundary indices change state.

**Command:**

```text
javac -d out (Get-ChildItem -Recurse src/main/java -Filter *.java); java -cp out xue.Xue
```

**Inputs:**

```text
todo first task
todo second task
todo third task
mark 3
list
unmark 2
list
mark 4
unmark 0
list
bye
```

**Expected output:**

```text
The output must contain, in order:
added: first task. One more thing for me to deal with.
added: second task. One more thing for me to deal with.
added: third task. One more thing for me to deal with.
Fine, I've marked this task as done. Happy now?
1.[ ] first task
2.[ ] second task
3.[X] third task
There. I've undone it. Try to make up your mind next time:
OOPS!!! That task number does not exist. Did you just invent it?
OOPS!!! That task number does not exist. Did you just invent it?
1.[ ] first task
2.[ ] second task
3.[X] third task
Finally, you're leaving. Bye. Don't make me miss you.
```

### Test case 5: task types and date/time text

**Aim:** Verify that todos, deadlines, and events are accepted, while supported numeric dates are parsed and displayed in a readable format.

**Command:**

```text
javac -d out (Get-ChildItem -Recurse src/main/java -Filter *.java); java -cp out xue.Xue
```

**Inputs:**

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
deadline submit form /by 2019-10-15
event conference /from 2019-10-15 0900 /to 2019-10-15 1800
list
bye
```

**Expected output:**

```text
The output must contain, in order:
Got it. I've added this task: [T][ ] borrow book
Got it. I've added this task: [D][ ] return book (by: Sunday)
Got it. I've added this task: [E][ ] project meeting (from: Mon 2pm to: 4pm)
Got it. I've added this task: [D][ ] submit form (by: Oct 15 2019)
Got it. I've added this task: [E][ ] conference (from: Oct 15 2019 09:00 AM to: Oct 15 2019 06:00 PM)
1.[T][ ] borrow book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
4.[D][ ] submit form (by: Oct 15 2019)
5.[E][ ] conference (from: Oct 15 2019 09:00 AM to: Oct 15 2019 06:00 PM)
Finally, you're leaving. Bye. Don't make me miss you.
```

### Test case 6: deleting tasks and preserving list numbering

**Aim:** Verify that a valid delete removes the selected task, shifts later tasks, and rejects invalid task numbers.

**Command:**

```text
javac -d out (Get-ChildItem -Recurse src/main/java -Filter *.java); java -cp out xue.Xue
```

**Inputs:**

```text
todo first task
deadline second task /by Friday
event third task /from Mon 2pm /to 4pm
delete 2
delete 9
list
bye
```

**Expected output:**

```text
The output must contain, in order:
Got it. I've added this task: [T][ ] first task
Got it. I've added this task: [D][ ] second task (by: Friday)
Got it. I've added this task: [E][ ] third task (from: Mon 2pm to: 4pm)
Fine, I've removed this task:
  [D][ ] second task (by: Friday)
Now you have 2 tasks in the list.
OOPS!!! That task number does not exist. Did you just invent it?
1.[T][ ] first task
2.[E][ ] third task (from: Mon 2pm to: 4pm)
Finally, you're leaving. Bye. Don't make me miss you.
```

### Test case 7: loading saved tasks

**Aim:** Verify that valid todo, deadline, and event records are loaded with their completion status and date/time details.

**Setup:** Create `data/duke.txt` containing:

```text
T | 1 | read book
D | 0 | return book | June 6th
E | 0 | project meeting | Aug 6th 2pm | Aug 6th 4pm
```

**Inputs:**

```text
list
bye
```

**Expected output:** The list must contain the three saved tasks, with the todo marked `[X]` and the saved date/time text unchanged.

### Test case 8: malformed saved records

**Aim:** Verify that malformed records are ignored while valid records still load.

**Setup:** Add malformed records with unknown types, invalid statuses, missing fields, and blank descriptions alongside one valid todo. Run `list` and verify that only the valid todo appears; Xue must not terminate.

### Test case 9: finding tasks by description keyword

**Aim:** Verify that `find` returns matching tasks in their original order, searches case-insensitively, and preserves task details.

**Command:**

```text
javac -d out (Get-ChildItem -Recurse src/main/java -Filter *.java); java -cp out xue.Xue
```

**Inputs:**

```text
todo read book
deadline return book /by June 6th
todo buy milk
find BOOK
bye
```

**Expected output:**

```text
The output must contain, in order:
Got it. I've added this task: [T][ ] read book
Got it. I've added this task: [D][ ] return book (by: June 6th)
Got it. I've added this task: [T][ ] buy milk
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: June 6th)
Finally, you're leaving. Bye. Don't make me miss you.
```

### Test case 10: JavaFX GUI interactions

**Aim:** Verify the GUI welcome message, command submission, error handling, existing commands, and safe closing.

**Command:**

```text
gradlew run
```

**Inputs and checks:**

1. Confirm the welcome message is visible when the window opens.
2. Confirm the bottom input area and **Send** button are visible.
3. Enter `todo read book` and click **Send**; confirm both the `You:` message and `Xue:` response appear.
4. Enter `todo buy milk` and press **Enter**; confirm both command and response appear.
5. Click **Send** with empty input; confirm no blank exchange is added.
6. Enter `not a command`; confirm an `OOPS!!!` response appears.
7. Try `list`, `find book`, `mark 1`, `unmark 1`, and `delete 1`.
8. Add enough messages to exceed the window height; confirm the conversation area scrolls while the input area remains visible at the bottom.
9. Close the window with its close button and confirm it exits without an error.

### Test case 11: undo and redo

**Aim:** Verify that undo reverses the latest successful mutation, redo reapplies it, and a new mutation clears redo history.

**Command:**

```text
javac -d out (Get-ChildItem -Recurse src/main/java -Filter *.java); java -cp out xue.Xue
```

**Inputs:**

```text
todo first task
todo second task
undo
redo
undo
todo replacement task
redo
undo
list
bye
```

**Expected output:**

The output must contain, in order:

```text
Undo completed. Be alert next time.
Redo completed.
Undo completed. Be alert next time.
OOPS!!! There is nothing to redo.
Undo completed. Be alert next time.
1.[T][ ] first task
Finally, you're leaving. Bye. Don't make me miss you.
```

### Test case 12: undo and redo validation

**Aim:** Verify that undo and redo reject arguments and report empty history correctly.

**Command:**

```text
javac -d out (Get-ChildItem -Recurse src/main/java -Filter *.java); java -cp out xue.Xue
```

**Inputs:**

```text
undo
redo
undo 1
redo 1
bye
```

**Expected output:**

The output must contain, in order:

```text
OOPS!!! Hey you need to DO before you can undo!
OOPS!!! There is nothing to redo.
OOPS!!! The undo command does not accept any arguments.
OOPS!!! The redo command does not accept any arguments.
Finally, you're leaving. Bye. Don't make me miss you.
```

