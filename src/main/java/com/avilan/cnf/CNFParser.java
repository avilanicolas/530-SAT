package com.avilan.cnf;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import com.avilan.sat.DecisionDiagram;
import com.avilan.sat.DecisionDiagramNode;
import com.avilan.sat.SATVariable;

/**
 * This class parses stat problem expressed in conjuctive normal form to a
 * decision diagram, see
 * {@linkplain https://en.wikipedia.org/wiki/Conjunctive_normal_form}
 * 
 * Specifically this uses the DIMACS-CNF file format that other sat solvers like
 * mini-sat use. see
 * {@linkplain https://www.dwheeler.com/essays/minisat-user-guide.html}
 */
public class CNFParser {
   private static final String COMMENT = "c";
   private static final String HEADER = "p";
   private static final String HEADER_ID = "cnf";
   private static final String VAR_PREFIX = "x";

   /**
    * The DIMACS-CNF format is pretty simple. Any line before the header must be
    * prefaced with c to indicate that it's a comment.
    * 
    * The header starts with "p cnf" and then list two numbers, the number of
    * variables and the number of clauses
    * 
    * A clause is a sequence of number terminated by 0 where each number
    * represents a boolean variable. If the number is negative it represents the
    * negation of said variable. Each clause then is a sequence of variables
    * or'ed together and the over all file is all the clauses and'ed together.
    * 
    * Thus: 1 -5 4 0 3 -1 0 means: (1 | not 5 | 4) and (3 | not 1)
    * 
    * from here it's fairly easy to see how to construct an arbitrary boolean
    * statement.
    * 
    * 
    * @param file
    *           a path to a cnf file
    * @return a decision diagram representing the sat problem described by this
    *         file
    * @throws IOException
    *            if the file cannot be read
    */
   public static DecisionDiagram fromFile(Path file) throws IOException {
      try (Scanner s = new Scanner(new BufferedInputStream(Files.newInputStream(file)))) {
         String token = s.next();
         while (token.equals(COMMENT)) {
            s.nextLine();
            token = s.next();
         }

         if (!token.equals(HEADER)) {
            throw new RuntimeException("Invalid cnf file. missing header");
         }

         token = s.next();
         if (!token.equals(HEADER_ID)) {
            throw new RuntimeException("Invalid cnf file. invalid header");
         }
         s.nextInt(); // num vars not used
         int numClauses = s.nextInt();

         DecisionDiagram topLevel = null;
         DecisionDiagram currentStatement = null;
         while (numClauses > 0) {
            int literal = s.nextInt();
            if (literal == 0) {
               if (topLevel == null) {
                  topLevel = currentStatement;
               } else if (currentStatement != null) {
                  topLevel = topLevel.and(currentStatement);
                  currentStatement = null;
               }
               numClauses--;
            } else {
               DecisionDiagram current = DecisionDiagramNode
                     .of(new SATVariable(VAR_PREFIX + Math.abs(literal)));
               if (literal < 0) {
                  current = current.not();
               }
               if (currentStatement == null) {
                  currentStatement = current;
               } else {
                  currentStatement = currentStatement.or(current);
               }
            }
         }

         return topLevel;
      }
   }

}
