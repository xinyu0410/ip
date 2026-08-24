# UI Test Plan

This file is the source of truth for the `test-ui` skill.

## Execution notes

- Run commands from the repository root.
- Use Java 25 for Java application or build commands.
- Feed each case's inputs through standard input exactly as written.
- Compare output exactly unless a case documents an allowed normalization.
- Run cases in listed order and stop immediately after the first failure.

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
