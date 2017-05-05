package com.avilan.sat;

import static com.csc530.sat.DecisionDiagramLeaf.SATISFIABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.csc530.sat.DecisionDiagram;
import com.csc530.sat.DecisionDiagramNode;
import com.csc530.sat.SATVariable;
import com.google.common.collect.ImmutableMap;

public class DecisionDiagramTest {

   private static final SATVariable X = new SATVariable("x");
   private static final SATVariable Y = new SATVariable("y");
   private static final SATVariable Z = new SATVariable("z");
   private static final SATVariable W = new SATVariable("w");

   private static final SATVariable A = new SATVariable("a");
   private static final SATVariable B = new SATVariable("b");
   private static final SATVariable C = new SATVariable("c");
   private static final SATVariable D = new SATVariable("d");
   private static final SATVariable E = new SATVariable("e");

   @Test
   public void testUnion() {
      DecisionDiagramNode a = DecisionDiagramNode.of(A);
      DecisionDiagramNode b = DecisionDiagramNode.of(B);
      DecisionDiagramNode c = DecisionDiagramNode.of(C);
      DecisionDiagramNode d = DecisionDiagramNode.of(D);
      DecisionDiagramNode e = DecisionDiagramNode.of(E);
//      System.out.println(xor(a, xor(b, c)));
      System.out.println(xor(a, xor(b, xor(c, xor(d, e)))));

      DecisionDiagramNode xDiagram = DecisionDiagramNode.of(X);
      DecisionDiagramNode yDiagram = DecisionDiagramNode.of(Y);
      DecisionDiagramNode zDiagram = DecisionDiagramNode.of(Z);

      assertEquals(SATISFIABLE, xDiagram.or(SATISFIABLE));
      assertEquals(xDiagram, xDiagram.or(xDiagram));
      assertEquals(SATISFIABLE, xDiagram.or(xDiagram.not()));
      assertEquals(xDiagram.or(yDiagram), xDiagram.or(yDiagram.and(xDiagram.not())));


      DecisionDiagram specialDiagram = zDiagram.and(zDiagram.not());

      DecisionDiagram xor = xDiagram.and(yDiagram.not())
         .or(yDiagram.and(xDiagram.not()));

//      System.out.println(xor(xDiagram, xor(yDiagram, specialDiagram)));
//      System.out.println(xor(yDiagram, specialDiagram));
      SATVariable isItRainingVar = new SATVariable("isItRaining?");
      SATVariable iHaveAnUmbrellaVar = new SATVariable("iHaveAnUmbrella");

      DecisionDiagram isItRaining = DecisionDiagramNode.of(isItRainingVar);
      DecisionDiagram iHaveAnUmbrella = DecisionDiagramNode.of(iHaveAnUmbrellaVar);

      System.out.println(isItRaining.not().or(iHaveAnUmbrella));
   }

   private DecisionDiagram xor(DecisionDiagram a, DecisionDiagram b) {
      return a.and(b.not()).or(b.and(a.not()));
   }

   @Test
   public void testContains() {
      DecisionDiagramNode xDiagram = DecisionDiagramNode.of(X);
      DecisionDiagramNode yDiagram = DecisionDiagramNode.of(Y);

      DecisionDiagram xor = xDiagram.and(yDiagram.not())
            .or(yDiagram.and(xDiagram.not()));

      assertTrue(xor.satisfies(assignment(true, false, false)));
      assertTrue(xor.satisfies(assignment(false, true, false)));
      assertFalse(xor.satisfies(assignment(false, false, true)));
      assertFalse(xor.satisfies(assignment(true, true, true)));
   }

   private Map<SATVariable, Boolean> assignment(final boolean x, final boolean y,
         final boolean z) {
      return ImmutableMap.of(X, x, Y, y, Z, z, W, false);
   }
}