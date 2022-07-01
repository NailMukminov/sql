package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Generator {

    public static List<Integer> doubleNumeralsMultiplesOf (int count, int bound) {
        List<Integer> lst = new ArrayList<Integer>();
        for (int i = 0; i < count; i++) {
            int num = new Random().nextInt(bound);
            if (num % 100 % 11 == 0)
            {
                boolean check = false;
                for (var el : lst) {
                    if (el == num) {
                        check = true;
                        break;
                    }
                }
                if (!check)
                    lst.add(num);
                else
                    i--;
            }
            else
                i--;
        }
        return lst;
    }
}
