package com.csc530.sat;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.csc530.sat.branch.DecisionDiagramBranches;
import com.csc530.sat.condition.DDCondition;
import com.csc530.sat.type.DDType;
import com.google.common.collect.ImmutableMap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A decision diagram representing a boolean equation. This diagram attempts to
 * simplify its structure when it can by removing variables from the diagram
 */
@RequiredArgsConstructor(
      access = AccessLevel.PRIVATE)
@SuppressWarnings({ "rawtypes", "unchecked" })
@Getter
public class DecisionDiagramNode<T> implements DecisionDiagram {
   private final SMTVariable<T> primaryVariable;
   private final LinkedHashSet<SMTVariable<?>> variables;
   private final DecisionDiagramBranches<T> branches;

   public static DecisionDiagramNode of(final SMTVariable variable,
         final DDCondition condition) {
      LinkedHashSet<SMTVariable<?>> vars = new LinkedHashSet<>();
      vars.add(variable);
      return of(variable, vars, condition);
   }

   public static DecisionDiagramNode of(final SMTVariable variable,
         final LinkedHashSet<SMTVariable<?>> variables, final DDCondition condition) {
      return new DecisionDiagramNode(variable, variables,
            DecisionDiagramBranches.builder().condition(condition)
                  .trueBranch(DecisionDiagramLeaf.SATISFIABLE)
                  .falseBranch(DecisionDiagramLeaf.UNSATISFIABLE).build());
   }

   private enum DecisionDiagramOperation {
      OR {
         @Override
         public DDCondition operate(DDCondition first, DDCondition second) {
            return first.or(second);
         }
      },
      AND {
         @Override
         public DDCondition operate(DDCondition first, DDCondition second) {
            return first.and(second);
         }
      };

      public abstract DDCondition operate(DDCondition first, DDCondition second);
   }

   @Override
   public DecisionDiagram or(final DecisionDiagram diagram) {
      return merge(diagram, (d1, d2) -> d1.or(d2), DecisionDiagramOperation.OR);
   }

   @Override
   public DecisionDiagram and(final DecisionDiagram diagram) {
      return merge(diagram, (d1, d2) -> d1.and(d2), DecisionDiagramOperation.AND);
   }

   private DecisionDiagram merge(final DecisionDiagram diagram,
         final BiFunction<DecisionDiagram, DecisionDiagram, DecisionDiagram> merge,
         final DecisionDiagramOperation operation) {
      if (diagram instanceof DecisionDiagramLeaf) {
         return merge.apply(diagram, this);
      }

      DecisionDiagramNode otherNode = (DecisionDiagramNode) diagram;
      LinkedHashSet<SMTVariable<?>> vars = new LinkedHashSet<>();
      vars.addAll(variables);
      vars.addAll(otherNode.variables);
      //
      // if (primaryVariable.equals(otherNode.primaryVariable)) {
      // DecisionDiagram mergedTrue = merge.apply(branches.getTrueBranch(),
      // otherNode.getBranches().getTrueBranch().assume(primaryVariable,
      // branches.getCondition().satisifier()));
      // DecisionDiagram mergedFalse = merge.apply(branches.getFalseBranch(),
      // otherNode.getBranches().getFalseBranch().assume(primaryVariable,
      // branches.getCondition().unSatisifier()));
      //
      // if (canSimplify(mergedTrue, mergedFalse)) {
      // return mergedTrue;
      // }
      //
      // return new DecisionDiagramNode(primaryVariable, vars,
      // branches.toBuilder()
      // .condition(operation.operate(branches.getCondition(),
      // otherNode.getBranches().getCondition()))
      // .trueBranch(mergedTrue)
      // .falseBranch(mergedFalse)
      // .build());
      // }

      DecisionDiagram mergedTrue = merge.apply(branches.getTrueBranch(),
            otherNode.getBranches().getTrueBranch().assume(primaryVariable,
                  branches.getCondition().satisifier()));
      DecisionDiagram mergedFalse = merge.apply(branches.getFalseBranch(),
            otherNode.getBranches().getFalseBranch().assume(primaryVariable,
                  branches.getCondition().unSatisifier()));

      if (canSimplify(mergedTrue, mergedFalse)) {
         return mergedTrue;
      }

      return new DecisionDiagramNode(primaryVariable, vars,
            branches.toBuilder()
                  .condition(operation.operate(branches.getCondition(),
                        otherNode.getBranches().getCondition()))
                  .trueBranch(mergedTrue)
                  .falseBranch(mergedFalse)
                  .build());
   }

   @Override
   public DecisionDiagram not() {
      return new DecisionDiagramNode(primaryVariable, variables, branches.not());
   }

   @Override
   public boolean satisfies(final Map<SMTVariable, DDType> assignment) {
      // This tree cannot possibly be satisfied if the assignment is empty
      if (assignment.isEmpty()) {
         return false;
      }

      // Or if it doesn't contain this node's variable!
      if (!assignment.containsKey(primaryVariable)) {
         return false;
      }

      DecisionDiagram branch = branches.getBranch(assignment.get(primaryVariable));

      // If the assignment does contain the variable, remove it from the
      // assignment
      // and recursively check the branch children
      Map<SMTVariable, DDType> reducedAssignment = assignment.keySet().stream()
            .filter(k -> !primaryVariable.equals(k))
            .collect(Collectors.toMap(k -> k, assignment::get));

      return branch.satisfies(reducedAssignment);
   }

   @Override
   public DecisionDiagram assume(final SMTVariable variable, final DDType value) {
      if (!variables.contains(variable)) {
         return this;
      }

      if (primaryVariable.equals(variable)) {
         return branches.getBranch(value);
      }

      DecisionDiagram trueBranchAssumed = branches.getTrueBranch().assume(variable,
            value);
      DecisionDiagram falsedBranchAssumed = branches.getFalseBranch().assume(variable,
            value);
      if (canSimplify(trueBranchAssumed, falsedBranchAssumed)) {
         return trueBranchAssumed;
      }
      LinkedHashSet<SMTVariable<?>> vars = new LinkedHashSet<>();
      variables.stream()
            .filter(v -> !v.equals(variable))
            .forEach(vars::add);

      return new DecisionDiagramNode(primaryVariable, vars,
            branches.toBuilder()
                  .trueBranch(trueBranchAssumed)
                  .falseBranch(falsedBranchAssumed)
                  .build());
   }

   private boolean canSimplify(final DecisionDiagram d1, final DecisionDiagram d2) {
      return d1 instanceof DecisionDiagramLeaf && d2 instanceof DecisionDiagramLeaf
            && d1 == d2;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      }

      if (o == null || !(o instanceof DecisionDiagramNode)) {
         return false;
      }

      DecisionDiagramNode other = (DecisionDiagramNode) o;
      return primaryVariable.equals(other.primaryVariable)
            && variables.equals(other.variables)
            && branches.getTrueBranch().equals(other.getBranches().getTrueBranch())
            && branches.getFalseBranch().equals(other.getBranches().getFalseBranch());
   }

   /**
   * Provides a JSON string representation to easily use in debugging.
   */
   @Override
   public String toString() {
      return String.format("{\"%s\": {\"true\": %s, \"false\": %s}}", primaryVariable,
            branches.getTrueBranch(), branches.getTrueBranch());
   }

   @Override
   public Stream<Map<SMTVariable, DDType>> satisifyAll() {
      Stream.Builder<Map<SMTVariable, DDType>> builder = Stream.builder();
      if (branches.getTrueBranch() instanceof DecisionDiagramLeaf) {
         if (((DecisionDiagramLeaf) branches.getTrueBranch())
               .equals(DecisionDiagramLeaf.SATISFIABLE)) {
            builder.add(
                  ImmutableMap.of(primaryVariable, branches.getCondition().satisifier()));

         }

      } else {
         branches.getTrueBranch().satisifyAll()
               .forEach(sat -> builder.add(ImmutableMap.<SMTVariable, DDType>builder()
                     .put(primaryVariable, branches.getCondition().satisifier())
                     .putAll(sat).build()));
      }
      if (branches.getFalseBranch() instanceof DecisionDiagramLeaf) {
         if (((DecisionDiagramLeaf) branches.getFalseBranch())
               .equals(DecisionDiagramLeaf.SATISFIABLE)) {
            builder.add(ImmutableMap.of(primaryVariable,
                  branches.getCondition().unSatisifier()));
         }

      } else {
         branches.getFalseBranch().satisifyAll()
               .forEach(sat -> builder.add(ImmutableMap.<SMTVariable, DDType>builder()
                     .put(primaryVariable, branches.getCondition().unSatisifier())
                     .putAll(sat).build()));
      }
      return builder.build();
   }
}