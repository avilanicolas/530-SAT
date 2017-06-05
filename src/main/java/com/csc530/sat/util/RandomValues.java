package com.csc530.sat.util;

import java.util.Random;

import com.csc530.sat.type.IntegerDDType;

public class RandomValues {
   private static Random random = new Random();
   static {
      random.setSeed(System.currentTimeMillis());
   }
   
   public static IntegerDDType randomIntger() {
      return IntegerDDType.create(random.nextInt());
   }

}
