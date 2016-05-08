package guichaguri.betterfps.test.math;

import guichaguri.betterfps.math.TaylorMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Guilherme Chaguri
 */
public class TaylorMathTest {

    @Test
    public void testSine() {
        float sine = TaylorMath.sin(1);

        Assert.assertEquals(0.8414F, sine, 0.005F);
    }

    @Test
    public void testCosine() {
        float cosine = TaylorMath.cos(1);

        Assert.assertEquals(0.5403F, cosine, 0.005F);
    }

}
