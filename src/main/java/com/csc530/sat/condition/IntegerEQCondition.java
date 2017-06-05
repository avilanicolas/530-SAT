package com.csc530.sat.condition;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.csc530.sat.type.DDType;
import com.csc530.sat.type.IntegerDDType;
import com.csc530.sat.util.RandomValues;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class IntegerEQCondition implements DDCondition<Integer> {
   private final Integer eqTo;
   
   public static IntegerEQCondition create(int eqTo) {
      return new IntegerEQCondition(eqTo);
   }

   @Override
   public boolean satisifies(DDType<Integer> value) {
      return value.getValue().equals(eqTo);
   }

   @Override
   public DDType<Integer> satisifier() {
      return IntegerDDType.create(eqTo);
   }

   @Override
   public DDType<Integer> unSatisifier() {
      DDType<Integer> r = RandomValues.randomIntger();
      while(r.getValue().equals(eqTo)) {
         r = RandomValues.randomIntger();
      }
      return r;
   }

   @Override
   public DDCondition<Integer> not() {
      return IntegerNEQCondition.create(eqTo);
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      }

      if (o == null || !(o instanceof IntegerEQCondition)) {
         return false;
      }

      IntegerEQCondition other = (IntegerEQCondition) o;
      return eqTo.equals(other.eqTo);
   }

   @Override
   public int hashCode() {
      return new HashCodeBuilder()
            .append(eqTo)
            .build()
            .hashCode();
   }
}
