package references;

import java.util.Arrays;

public class Test {

    public static void main(String[] args) {
//        System.out.println("Hello!");
//
//        Integer i = null;
//
//        Integer j = i << 3;
//
//        System.out.println(j);

        String s = "1,2,3,,,";
        String[] arr1 = s.split(",");//[1, 2, 3]
        System.out.println(Arrays.toString(arr1));
        String[] arr2 = s.split(",", 0);//[1, 2, 3]
        System.out.println(Arrays.toString(arr2));
        String[] arr3 = s.split(",", 1);//[1,2,3,,,]
        System.out.println(Arrays.toString(arr3));
        String[] arr4 = s.split(",", -1);//[1, 2, 3, , , ]
        System.out.println(Arrays.toString(arr4));


    }
}
