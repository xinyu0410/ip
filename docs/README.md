# Xue User Guide

Xue is a task manager that supports todos, deadlines, events, and session-only undo/redo.

// Product screenshot goes here

// Product intro goes here

## Adding deadlines

Add a deadline with `deadline <description> /by <date>`.

// Give examples of usage

Example: `deadline submit report /by Friday`

Xue adds the deadline and displays it in the task list.

```
Got it. I've added this task: [D][ ] submit report (by: Friday)
```

## Undoing and redoing commands

Use `undo` to reverse the most recent successful task-changing command:

```text
todo read book
undo
```

Undo displays the current task list and starts with:

```text
Undo completed. Be alert next time.
```

Use `redo` to reapply the most recently undone command. Undo and redo history lasts only for the current session and stores up to 50 changes. Starting a new task-changing command clears redo history.

// Feature details
