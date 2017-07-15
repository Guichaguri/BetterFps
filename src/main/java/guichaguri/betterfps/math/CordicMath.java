// This file (CordicMath.java) contains a derivative of Wikipedia article "CORDIC" (https://en.wikipedia.org/w/index.php?title=CORDIC&oldid=783476745
// ) under the terms of the license Attribution-ShareAlike 3.0 Unported (CC BY-SA 3.0) [https://creativecommons.org/licenses/by-sa/3.0/].
// It is adapted to Java and optimized.
package guichaguri.betterfps.math;

public class CordicMath {
	// Code to generate angles (Swift):
	// http://swift.sandbox.bluemix.net/#/repl/596b4b1a64a11a59e285e90e
	private static final double[] angles = { 0.78539816339744828, 0.46364760900080615, 0.24497866312686414,
			0.12435499454676144, 0.06241880999595735, 0.031239833430268277, 0.015623728620476831, 0.0078123410601011111,
			0.0039062301319669718, 0.0019531225164788188, 0.00097656218955931946, 0.00048828121119489829,
			0.00024414062014936177, 0.00012207031189367021, 6.1035156174208773e-05, 3.0517578115526096e-05,
			1.5258789061315762e-05, 7.62939453110197e-06, 3.8146972656064961e-06, 1.907348632810187e-06,
			9.5367431640596084e-07, 4.7683715820308884e-07, 2.3841857910155797e-07, 1.1920928955078068e-07,
			5.9604644775390552e-08, 2.9802322387695303e-08, 1.4901161193847655e-08, 7.4505805969238281e-09,
			3.7252902984619141e-09, 1.862645149230957e-09, 9.3132257461547852e-10, 4.6566128730773926e-10,
			2.3283064365386963e-10, 1.1641532182693481e-10, 5.8207660913467407e-11, 2.9103830456733704e-11,
			1.4551915228366852e-11, 7.2759576141834259e-12, 3.637978807091713e-12, 1.8189894035458565e-12,
			9.0949470177292824e-13 };
	private static final double[] Kvalues = { 0.70710678118654746, 0.63245553203367577, 0.61357199107789628,
			0.60883391251775243, 0.60764825625616825, 0.60735177014129604, 0.60727764409352614, 0.60725911229889284,
			0.60725447933256249, 0.60725332108987529, 0.60725303152913446, 0.60725295913894495, 0.60725294104139727,
			0.60725293651701029, 0.60725293538591352, 0.60725293510313938, 0.60725293503244582, 0.6072529350147724,
			0.60725293501035404, 0.60725293500924948, 0.60725293500897337, 0.60725293500890432, 0.60725293500888711,
			0.60725293500888278, 0.60725293500888167, 0.60725293500888144, 0.60725293500888144, 0.60725293500888144,
			0.60725293500888144, 0.60725293500888144, 0.60725293500888144, 0.60725293500888144, 0.60725293500888144,
			0.60725293500888144, 0.60725293500888144, 0.60725293500888144, 0.60725293500888144, 0.60725293500888144,
			0.60725293500888144, 0.60725293500888144, 0.60725293500888144 };
	private static final double[] negativePowersOfTwo = { 1.0, 0.5, 0.25, 0.125, 0.0625, 0.03125, 0.015625, 0.0078125,
			0.00390625, 0.001953125, 0.0009765625, 0.00048828125, 0.000244140625, 0.0001220703125, 6.103515625e-05,
			3.0517578125e-05, 1.52587890625e-05, 7.62939453125e-06, 3.814697265625e-06, 1.9073486328125e-06,
			9.5367431640625e-07, 4.76837158203125e-07, 2.384185791015625e-07, 1.1920928955078125e-07,
			5.9604644775390625e-08, 2.9802322387695312e-08, 1.4901161193847656e-08, 7.4505805969238281e-09,
			3.7252902984619141e-09, 1.862645149230957e-09, 9.3132257461547852e-10, 4.6566128730773926e-10,
			2.3283064365386963e-10, 1.1641532182693481e-10, 5.8207660913467407e-11, 2.9103830456733704e-11,
			1.4551915228366852e-11, 7.2759576141834259e-12, 3.637978807091713e-12, 1.8189894035458565e-12 };
	// Caching negative powers is faster than dividing. Proof: http://ideone.com/KuH9V8
	private static final float HalfPi = (float) (Math.PI / 2);
	private static final float Tau = (float) (Math.PI * 2);
	private static final float PiFloat = (float) (Math.PI);

	private static final float cordic(float beta, boolean isSin) {
		beta -= (long) (beta / Tau) * Tau;
		if (beta < -HalfPi || beta > HalfPi) {
			if (beta < 0)
				return -cordic(beta + PiFloat, isSin);
			else
				return -cordic(beta - PiFloat, isSin);
		}
		double[] v = { 1, 0 };
		int powerOfTwo = 0;
		double angle = angles[0];
		double sigma, factor;
		for (int j = 0; j < 30; j++) {
			sigma = beta < 0 ? -1 : 1;
			factor = sigma * negativePowersOfTwo[powerOfTwo];
			v = new double[] { v[0] - v[1] * factor, v[1] + v[0] * factor };
			beta -= sigma * angle;
			++powerOfTwo;
			angle = j + 1 >= angles.length ? angle / 2 : angles[j + 1];
		}
		return (float) (Kvalues[29] * (isSin ? v[1] : v[0]));
	}

	public static final float sin(float rad) {
		return cordic(rad, true);
	}

	public static final float cos(float rad) {
		return cordic(rad, false);
	}
}
