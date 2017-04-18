package com.avilan.sat;

import static com.avilan.sat.BinaryDecisionDiagramLeaf.TRUE;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class BinaryDecisionDiagramTest {

   private static final SATVariable X = new SATVariable("x");
   private static final SATVariable Y = new SATVariable("y");
   private static final SATVariable Z = new SATVariable("z");
   private static final SATVariable W = new SATVariable("w");

   @Test
   public void testUnion() {

      BinaryDecisionDiagramNode xDiagram = BinaryDecisionDiagramNode.of(X);
      BinaryDecisionDiagramNode yDiagram = BinaryDecisionDiagramNode.of(Y);

      assertEquals(TRUE, xDiagram.or(TRUE));
      assertEquals(xDiagram, xDiagram.or(xDiagram));
      assertEquals(TRUE, xDiagram.or(xDiagram.not()));
      System.out.println(xDiagram.or(yDiagram.and(xDiagram.not())));
      assertEquals(xDiagram.or(yDiagram), xDiagram.or(yDiagram.and(xDiagram.not())));
   }

   @Test
   public void testContains() {
      BinaryDecisionDiagramNode xDiagram = BinaryDecisionDiagramNode.of(X);
      BinaryDecisionDiagramNode yDiagram = BinaryDecisionDiagramNode.of(Y);

      BinaryDecisionDiagram xor = xDiagram.and(yDiagram.not())
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
