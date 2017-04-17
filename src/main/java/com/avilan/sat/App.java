package com.avilan.sat;

public class App {
   public static void main(String[] args) {
      SATVariable x = new SATVariable("x");
      BinaryDecisionDiagram x1 = BinaryDecisionDiagramNode.of(x);
      BinaryDecisionDiagram x2 = x1.not();

      SATVariable y = new SATVariable("y");
      BinaryDecisionDiagram y1 = BinaryDecisionDiagramNode.of(y);

      SATVariable z = new SATVariable("z");
      BinaryDecisionDiagram z1 = BinaryDecisionDiagramNode.of(z);
      // (and x y (not x))
      // x ? y ? T
      // .     : F
      // : F

      // (or (and x z) (and y x))
//      System.out.println(x1.and(z1).or(x1.and(y1)));
//      System.out.println(
//         x1.and(z1).or(y1.or(x1.not())).satisfies(ImmutableMap.of(
//            x, true,
//            z, true,
//            y, false)));
      BinaryDecisionDiagram y1Xorx1 = 
           y1.and(x1.not())
              .or(y1.not().and(x1));
//      System.out.println(
//            y1Xorx1.not().and(x1)
//            );
      BinaryDecisionDiagram x1Andz1 = x1.and(z1);
      BinaryDecisionDiagram special = 
            y1.and(x1Andz1);

      System.out.println(x1.or(y1Xorx1));
   }
}