package com.csc530.sat.condition;

import java.util.function.Function;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.csc530.sat.type.BooleanDDType;
import com.csc530.sat.type.DDType;
import com.google.common.base.Preconditions;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder(
      toBuilder = true)
@AllArgsConstructor
public class BooleanCondition implements DDCondition<Boolean> {
   private Function<Boolean, Boolean> truthy;
   private Boolean satisifier;

   public static BooleanCondition isTrue() {
      return new BooleanCondition(b -> b, true);
   }

   @Override
   public boolean satisifies(DDType<Boolean> value) {
      Preconditions.checkState(truthy.apply(satisifier) == true, "Invalid state");
      return truthy.apply(value.getValue());
   }

   @Override
   public DDType<Boolean> satisifier() {
      return BooleanDDType.valueOf(satisifier);
   }

   @Override
   public DDType<Boolean> unSatisifier() {
      return BooleanDDType.valueOf(!satisifier);
   }

   @Override
   public DDCondition<Boolean> not() {
      return toBuilder()
            .truthy(b -> !truthy.apply(b))
            .satisifier(!satisifier)
            .build();
   }

   @Override
   public DDCondition<Boolean> or(DDCondition<Boolean> other) {
      return toBuilder()
            .truthy(b -> truthy.apply(b) || other.satisifies(BooleanDDType.valueOf(b)))
            .build();
   }

   @Override
   public DDCondition<Boolean> and(DDCondition<Boolean> other) {
      return toBuilder()
            .truthy(b -> truthy.apply(b) && other.satisifies(BooleanDDType.valueOf(b)))
            .satisifier(truthy.apply(satisifier)
                  && other.satisifies(BooleanDDType.valueOf(satisifier)) ? satisifier
                        : !satisifier)
            .build();
   }

   @Override
   public String toString() {
      return satisifier.toString();
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      }

      if (o == null || !(o instanceof BooleanCondition)) {
         return false;
      }

      BooleanCondition other = (BooleanCondition) o;
      if (truthy.apply(satisifier) != other.truthy.apply(satisifier)) {
         return false;
      }
      return new EqualsBuilder()
            .append(satisifier, other.satisifier)
            .isEquals();
   }

   @Override
   public int hashCode() {
      return new HashCodeBuilder()
            .append(satisifier)
            .build()
            .hashCode();
   }
}