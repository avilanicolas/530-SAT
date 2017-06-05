package com.csc530.sat;

import static com.csc530.sat.DecisionDiagramLeaf.SATISFIABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.csc530.sat.condition.BooleanCondition;
import com.csc530.sat.type.BooleanDDType;
import com.csc530.sat.type.DDType;
import com.google.common.collect.ImmutableMap;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class BooleanTheoryTest {
   private static final SMTVariable X = new SMTVariable("x", Boolean.class);
   private static final SMTVariable Y = new SMTVariable("y", Boolean.class);
   private static final SMTVariable Z = new SMTVariable("z", Boolean.class);
   private static final SMTVariable W = new SMTVariable("w", Boolean.class);

   @Test
   public void testUnion() {
      DecisionDiagramNode xDiagram = DecisionDiagramNode.of(X, BooleanCondition.isTrue());
      DecisionDiagramNode yDiagram = DecisionDiagramNode.of(Y, BooleanCondition.isTrue());

      assertEquals(SATISFIABLE, xDiagram.or(SATISFIABLE));
      assertEquals(xDiagram, xDiagram.or(xDiagram));
      assertEquals(SATISFIABLE, xDiagram.or(xDiagram.not()));
      assertEquals(xDiagram.or(yDiagram), xDiagram.or(yDiagram.and(xDiagram.not())));


      SMTVariable isItRainingVar = new SMTVariable("isItRaining?", Boolean.class);
      SMTVariable iHaveAnUmbrellaVar = new SMTVariable("iHaveAnUmbrella", Boolean.class);

      DecisionDiagram isItRaining = DecisionDiagramNode.of(isItRainingVar,
            BooleanCondition.isTrue());
      DecisionDiagram iHaveAnUmbrella = DecisionDiagramNode.of(iHaveAnUmbrellaVar,
            BooleanCondition.isTrue());

      System.out.println(isItRaining.not().or(iHaveAnUmbrella));
   }

   @Test
   public void testContains() {
      DecisionDiagramNode xDiagram = DecisionDiagramNode.of(X, BooleanCondition.isTrue());
      DecisionDiagramNode yDiagram = DecisionDiagramNode.of(Y, BooleanCondition.isTrue());

      DecisionDiagram xor = xDiagram.and(yDiagram.not())
            .or(yDiagram.and(xDiagram.not()));

      assertTrue(xor.satisfies(assignment(true, false, false)));
      assertTrue(xor.satisfies(assignment(false, true, false)));
      assertFalse(xor.satisfies(assignment(false, false, true)));
      assertFalse(xor.satisfies(assignment(true, true, true)));
   }

   private Map<SMTVariable, DDType> assignment(final boolean x, final boolean y,
         final boolean z) {
      return ImmutableMap.of(X, BooleanDDType.valueOf(x), Y, BooleanDDType.valueOf(y), Z,
            BooleanDDType.valueOf(z), W, BooleanDDType.valueOf(false));
   }
}