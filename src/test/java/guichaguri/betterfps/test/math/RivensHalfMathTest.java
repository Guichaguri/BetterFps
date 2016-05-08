package guichaguri.betterfps.test.math;

import guichaguri.betterfps.math.RivensHalfMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Guilherme Chaguri
 */
public class RivensHalfMathTest {

    @Test
    public void testSine() {
        float sine = RivensHalfMath.sin(1);

        Assert.assertEquals(0.8414F, sine, 0.005F);
    }

    @Test
    public void testCosine() {
        float cosine = RivensHalfMath.cos(1);

        Assert.assertEquals(0.5403F, cosine, 0.005F);
    }

}
