# UI Test Plan

This file is the source of truth for the `test-ui` skill.

## Execution notes

- Run commands from the repository root.
- Use Java 25 for Java application or build commands.
- Feed each case's inputs through standard input exactly as written.
- Compare output exactly unless a case documents an allowed normalization.
- Run cases in listed order and stop immediately after the first failure.
- Before each case, remove `data/duke.txt` if it exists so cases are isolated.

## Test cases

<!-- Add cases using this structure. Keep the command and expected output reproducible. -->

### Test case 1: specific input errors

**Aim:** Verify that empty todo descriptions, unknown commands, and invalid task numbers produce specific errors and do not terminate Xue.

**Command:**

```text
javac -d out src/main/java/*.java; java -cp out Xue
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
javac -d out src/main/java/*.java; java -cp out Xue
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
javac -d out src/main/java/*.java; java -cp out Xue
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
javac -d out src/main/java/*.java; java -cp out Xue
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

**Aim:** Verify that todos, deadlines, and events are accepted and displayed with their type and date/time text unchanged.

**Command:**

```text
javac -d out src/main/java/*.java; java -cp out Xue
```

**Inputs:**

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

**Expected output:**

```text
The output must contain, in order:
Got it. I've added this task: [T][ ] borrow book
Got it. I've added this task: [D][ ] return book (by: Sunday)
Got it. I've added this task: [E][ ] project meeting (from: Mon 2pm to: 4pm)
1.[T][ ] borrow book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
Finally, you're leaving. Bye. Don't make me miss you.
```

### Test case 6: deleting tasks and preserving list numbering

**Aim:** Verify that a valid delete removes the selected task, shifts later tasks, and rejects invalid task numbers.

**Command:**

```text
javac -d out src/main/java/*.java; java -cp out Xue
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

### Test case 7: saving and loading tasks

**Aim:** Verify that tasks are saved after changes and restored with their type, status, description, and date/time.

**Command:**

```text
javac -d out src/main/java/*.java; java -cp out Xue
```

**Inputs:**

```text
todo read book
deadline return book /by 2026-09-01 1800
event team meeting /from 2026-09-02 1400 /to 2026-09-02 1500
mark 1
bye
```

Then run the same command again with:

```text
list
bye
```

**Expected output:**

```text
The second run must contain, in order:
1.[T][X] read book
2.[D][ ] return book (by: 2026-09-01 1800)
3.[E][ ] team meeting (from: 2026-09-02 1400 to: 2026-09-02 1500)
```
