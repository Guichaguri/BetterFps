package guichaguri.betterfps.test.math;

import guichaguri.betterfps.math.RivensMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Guilherme Chaguri
 */
public class RivensMathTest {

    @Test
    public void testSine() {
        float sine = RivensMath.sin(1);

        Assert.assertEquals(0.8414F, sine, 0.005F);
    }

    @Test
    public void testCosine() {
        float cosine = RivensMath.cos(1);

        Assert.assertEquals(0.5403F, cosine, 0.005F);
    }

}
