package com.csc530.sat.condition;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.csc530.sat.type.DDType;
import com.csc530.sat.type.IntegerDDType;
import com.csc530.sat.util.RandomValues;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class IntegerNEQCondition implements DDCondition<Integer> {
   private final Integer eqTo;
   
   public static IntegerNEQCondition create(int eqTo) {
      return new IntegerNEQCondition(eqTo);
   }

   @Override
   public boolean satisifies(DDType<Integer> value) {
      return !value.getValue().equals(eqTo);
   }

   @Override
   public DDType<Integer> satisifier() {
      DDType<Integer> r = RandomValues.randomIntger();
      while(r.getValue().equals(eqTo)) {
         r = RandomValues.randomIntger();
      }
      return r;
   }

   @Override
   public DDType<Integer> unSatisifier() {
      return IntegerDDType.create(eqTo);
   }

   @Override
   public DDCondition<Integer> not() {
      return IntegerEQCondition.create(eqTo);
   }

   @Override
   public String toString() {
      return eqTo.toString();
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      }

      if (o == null || !(o instanceof IntegerNEQCondition)) {
         return false;
      }

      IntegerNEQCondition other = (IntegerNEQCondition) o;
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
