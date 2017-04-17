package com.avilan.sat;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode
public class SATVariable {
   private final String symbol;

   @Override
   public String toString() {
      return symbol;
   }
}