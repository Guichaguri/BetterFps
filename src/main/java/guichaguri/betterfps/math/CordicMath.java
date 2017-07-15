// This file (CordicMath.java) contains a derivative of Wikipedia article "CORDIC" (Link to the webpage: https://en.wikipedia.org/w/index.php?title=CORDIC&oldid=783476745
// retrived 15 July 2017) under the terms of the license Attribution-ShareAlike 3.0 Unported (as known as CC BY-SA 3.0, it can be found
// here: https://creativecommons.org/licenses/by-sa/3.0/). It is adapted to Java and optimized.

public class CordicMath {
	// Code to generate angles (Swift):
  // var angles: [Double] = []
	// for i: Double in stride(from: 0, through: 32, by: 1) {
	// angles.append(atan(pow(2, -i)))
	// }
	// var Kvalues: [Double] = []
	// for i: Double in stride(from: 0, through: 32, by: 1) {
	// Kvalues.append(1/sqrt(abs(Double(1) + pow(2,-2 * i))))
	// if i > 0 {
	// Kvalues[Kvalues.count - 1] *= Kvalues[Kvalues.count - 2]
	// }
	// }
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
	private static final float HalfPi = (float) (Math.PI / 2);
	private static final float Tau = (float) (Math.PI * 2);

	private static final float cordic(float beta, int n, boolean isSin) {
		beta -= (long) (beta / Tau) * Tau;
		if (beta < -HalfPi || beta > HalfPi) {
			if (beta < 0)
				return -cordic(beta + (float) (Math.PI), n, isSin);
			else
				return -cordic(beta - (float) (Math.PI), n, isSin);
		}
		double Kn = Kvalues[n < Kvalues.length ? n - 1 : Kvalues.length - 1];
		double[] v = {1,0};
		double powerOfTwo = 1;
		double angle = angles[0];
		double sigma, factor;
		for (int j = 0; j < n; j++) {
			sigma = beta < 0 ? -1 : 1;
			factor = sigma * powerOfTwo;
			v = new double[]{v[0] - v[1] * factor, v[1] + v[0] * factor};
			beta -= sigma * angle;
			powerOfTwo /= 2;
			angle = j + 1 >= angles.length ? angle / 2 : angles[j + 1];
		}
		return (float) (isSin ?  v[1] * Kn : v[0] * Kn);
	}

	public static final float sin(float rad) {
		return cordic(rad, 30, true);
	}

	public static final float cos(float rad) {
		return cordic(rad, 30, false);
	}
}
