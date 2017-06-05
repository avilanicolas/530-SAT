package com.csc530.sat.branch;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.csc530.sat.DecisionDiagram;
import com.csc530.sat.SMTVariable;
import com.csc530.sat.condition.DDCondition;
import com.csc530.sat.type.DDType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DecisionDiagramBranches<T> {
   private final SMTVariable<T> primaryVariable;
   private final DDCondition<T> condition;
   private final DecisionDiagram trueBranch;
   private final DecisionDiagram falseBranch;

   @Builder(toBuilder = true)
   public static <T> DecisionDiagramBranches<T> create(SMTVariable<T> primaryVariable,
         DDCondition<T> condition,
         DecisionDiagram trueBranch, DecisionDiagram falseBranch) {
      return new DecisionDiagramBranches<T>(primaryVariable, condition, trueBranch,
            falseBranch);
   }

   public DecisionDiagram getBranch(DDType<T> value) {
      return condition.satisifies(value) ? trueBranch : falseBranch;
   }

   public DecisionDiagramBranches<T> not() {
      return toBuilder()
            .condition(condition.not())
            .build();
   }
   
   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null) {
         return false;
      }
      
      if (!(o instanceof DecisionDiagramBranches)) {
         return false;
      }
      @SuppressWarnings("rawtypes")
      DecisionDiagramBranches other = (DecisionDiagramBranches) o;
      
      return new EqualsBuilder()
            .append(primaryVariable, other.primaryVariable)
            .append(condition, other.condition)
            .append(trueBranch, other.trueBranch)
            .append(falseBranch, other.falseBranch)
            .isEquals();
   }

}
