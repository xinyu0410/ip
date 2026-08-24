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

### Test case 1: <short name>

**Aim:** <what behavior this verifies>

**Command:**

```text
<command to run>
```

**Inputs:**

```text
<console input, one line per interaction>
```

**Expected output:**

```text
<complete expected stdout/stderr output>
```
