package main;


import java.util.*;

public class CollectionsDemo {
   public static void main(String[] args) {
   // create array list object
   List numbers = new ArrayList();
      
   // populate the list
   for (int i = 0; i < 15; i++) {
   numbers.add(i);
   }

   System.out.println("Before : "+Arrays.toString(numbers.toArray()));
      
   // rotate the list at distance 10
   Collections.rotate(numbers, numbers.size() -5);

   System.out.println("After : "+Arrays.toString(numbers.toArray()));
   }
}