package guichaguri.betterfps.test.math;

import guichaguri.betterfps.math.LibGDXMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Guilherme Chaguri
 */
public class LibGDXMathTest {

    @Test
    public void testSine() {
        float sine = LibGDXMath.sin(1);

        Assert.assertEquals(0.8414F, sine, 0.005F);
    }

    @Test
    public void testCosine() {
        float cosine = LibGDXMath.cos(1);

        Assert.assertEquals(0.5403F, cosine, 0.005F);
    }

}
