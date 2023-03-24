package ccw.serviceinnovation.common.util.random;

import java.util.Random;

/**
 * @author 陈翔
 */
public class RandomUtil {

    public static Integer getIndex(int start,int end){
        Random rand = new Random();
        return rand.nextInt(end)+ start;
    }
}
