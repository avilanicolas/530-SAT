package com.csc530.example.sudoku;

import java.util.HashSet;
import java.util.Set;

public class SudokuSmtlibGenerator {

    /* 2x2 sudoku puzzles
    private static final int BOX_SIZE = 2;
    private static final int[] ROWS = {1,2,3,4};
    private static final int[] COLS = {1,2,3,4};
    private static final int[] VALS = {1,2,3,4};
    // */

    /* 3x3 sudoku puzzles */
    private static final int BOX_SIZE = 3;
    private static final int[] ROWS = {1,2,3,4,5,6,7,8,9};
    private static final int[] COLS = {1,2,3,4,5,6,7,8,9};
    private static final int[] VALS = {1,2,3,4,5,6,7,8,9};
    // */

    // In puzzles, a zero indicates an unknown value

    /* 2x2
    private static final int[][] PUZZLE = {
            {2, 0, 0, 0, },
            {0, 0, 1, 0, },
            {0, 2, 0, 0, },
            {0, 0, 0, 4, },
    };
    // */

    /* 3x3 easy */
    private static final int[][] PUZZLE = {
            {0, 2, 0, 4, 5, 6, 7, 8, 9, },
            {4, 5, 7, 0, 8, 0, 2, 3, 6, },
            {6, 8, 9, 2, 3, 7, 0, 4, 0, },
            {0, 0, 5, 3, 6, 2, 9, 7, 4, },
            {2, 7, 4, 0, 9, 0, 6, 5, 3, },
            {3, 9, 6, 5, 7, 4, 8, 0, 0, },
            {0, 4, 0, 6, 1, 8, 3, 9, 7, },
            {7, 6, 1, 0, 4, 0, 5, 2, 8, },
            {9, 3, 8, 7, 2, 5, 0, 6, 0, },
    };
    // */

    /* 3x3 medium
    private static final int[][] PUZZLE = {
            {6, 0, 0, 0, 2, 0, 0, 0, 9, },
            {0, 1, 0, 3, 0, 7, 0, 5, 0, },
            {0, 0, 3, 0, 0, 0, 1, 0, 0, },
            {0, 9, 0, 0, 0, 0, 0, 2, 0, },
            {2, 0, 0, 8, 7, 5, 0, 0, 3, },
            {0, 0, 5, 0, 1, 0, 4, 0, 0, },
            {0, 7, 0, 0, 8, 0, 0, 9, 0, },
            {0, 0, 1, 0, 4, 0, 8, 0, 0, },
            {0, 0, 0, 2, 5, 9, 0, 0, 0, },
    };
    // */

    /* 3x3 very hard
    private static final int[][] PUZZLE = {
            {0, 0, 0, 2, 0, 0, 0, 8, 0, },
            {0, 5, 4, 0, 0, 0, 0, 0, 0, },
            {0, 0, 0, 0, 0, 0, 0, 0, 0, },
            {0, 0, 0, 6, 1, 3, 0, 0, 0, },
            {8, 0, 0, 0, 0, 0, 0, 5, 0, },
            {0, 0, 0, 0, 0, 4, 0, 0, 0, },
            {2, 0, 0, 7, 8, 0, 0, 0, 0, },
            {1, 0, 0, 0, 0, 0, 6, 0, 0, },
            {0, 0, 0, 0, 0, 0, 4, 0, 3, },
    };
    // */

    private Set<String> knownFalse;

    /**
     * Prints out a smt lib file which can solve sudoku puzzles.
     */
    public void printSudokuFile() {
        knownFalse = new HashSet<>();
        enableAssignments();
        setLogic("QF_UF");
        // Declare all the values
        for (int r : ROWS) {
            for (int c : COLS) {
                for (int v : VALS) {
                    declareBool(var(r, c, v));
                    if (PUZZLE[r - 1][c - 1] != 0) {
                        if (v == PUZZLE[r - 1][c - 1]) {
                            makeAssertion(var(r, c, v));
                        } else {
                            makeAssertion("(not " + var(r, c, v) + ")");
                            knownFalse.add(var(r, c, v));
                        }
                    }
                }
            }
        }

        for (int r : ROWS) {
            for (int c : COLS) {
                // square has at least one value
                StringBuilder sb = new StringBuilder();
                sb.append("( or ");
                for (int v : VALS) {
                    sb.append(var(r, c, v) + " ");
                }
                sb.append(")");
                makeAssertion(sb.toString());

                // square has no more than one value
                for (int v : VALS) {
                    for (int a : VALS) {
                        if (a != v) {
                            makeExclusionAssertion(var(r, c, v), var(r, c, a));
                        }
                    }
                }

                // row contains each value no more than once
                for (int v : VALS) {
                    for (int a : ROWS) {
                        if (a != r) {
                            makeExclusionAssertion(var(r, c, v), var(a, c, v));
                        }
                    }
                }

                // column contains each value no more than once
                for (int v : VALS) {
                    for (int a : COLS) {
                        if (a != c) {
                            makeExclusionAssertion(var(r, c, v), var(r, a, v));
                        }
                    }
                }

                // box contains each value no more than once
                for (int v : VALS) {
                    for (int x = 0; x < BOX_SIZE; x++) {
                        for (int y = 0; y < BOX_SIZE; y++) {
                            int r2 = x + BOX_SIZE * ((r - 1) / BOX_SIZE) + 1;
                            int c2 = y + BOX_SIZE * ((c - 1) / BOX_SIZE) + 1;
                            if (!(r == r2 && c == c2)) {
                                makeExclusionAssertion(var(r, c, v), var(r2, c2, v));
                            }
                        }
                    }
                }
            }
        }

        // Check Satisfiability
        check();
        // Print out assignments
        for (int r : ROWS) {
            for (int c : COLS) {
                System.out.print("(get-value (");
                for (int v : VALS) {
                    System.out.print(var(r, c, v) + " ");
                }
                System.out.println("))");
            }
        }
    }

    private String var(int row, int col, int val) {
        return "s" + row + col + val;
    }

    /** This is needed for the z3 theorem solver to be able to print values at the end */
    private void enableAssignments() {
        print("(set-option :produce-assignments true)");
    }

    private void setLogic(String logic) {
        print("(set-logic " + logic + ")");
    }

    private void declareBool(String name) {
        print("(declare-fun " + name + " () Bool)");
    }

    private void makeAssertion(String statement) {
        print("(assert " + statement + ")");
    }

    private void makeExclusionAssertion(String a, String b) {
        if (!knownFalse.contains(a) && !knownFalse.contains(b)) {
            print("(assert (not (and " + a + " " + b + ")))");
        }
    }

    private void check() {
        print("(check-sat)");
    }

    private void print(String s) {
        System.out.println(s);
    }

    public static void main(String[] args) {
        SudokuSmtlibGenerator sslg = new SudokuSmtlibGenerator();
        sslg.printSudokuFile();
    }
}
