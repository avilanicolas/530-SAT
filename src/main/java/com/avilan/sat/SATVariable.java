package com.avilan.sat;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SATVariable {
   private final String symbol;

   @Override
   public String toString() {
      return symbol;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      }

      if (o == null || !(o instanceof SATVariable)) {
         return false;
      }

      SATVariable other = (SATVariable) o;
      return symbol.equals(other.symbol);
   }

   @Override
   public int hashCode() {
      return symbol.hashCode();
   }
}