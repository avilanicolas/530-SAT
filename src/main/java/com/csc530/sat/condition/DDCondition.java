package com.csc530.sat.condition;

import com.csc530.sat.type.DDType;

public interface DDCondition<T> {
   public boolean satisifies(DDType<T> value);
   public DDType<T> satisifier();
   public DDType<T> unSatisifier();

   public DDCondition<T> not();
   
   public DDCondition<T> or(DDCondition<T> other);

   public DDCondition<T> and(DDCondition<T> other);
}
