package guichaguri.betterfps.math;

/**
 * @author Guilherme Chaguri
 */

public class TaylorOptimizedMath {
	private static final float BF_SIN_TO_COS = (float) (Math.PI * 0.5);
	private static final double TAU = Math.PI * 2;

	public static float sin(float rad) {
		long n = (long) (rad / TAU);
		double x = rad - n * TAU;
	  
      if( x > BF_SIN_TO_COS )
        x = Math.PI - x;
      
      if( x <= 0.174 ){ // 10 degs- 0 based, 100% precise http://www.emathhelp.net/calculators/calculus-1/taylor-and-maclaurin-series-calculator/?f=sin+%28x%29&p=5+pi+%2F36&n=3
		double x2 = x * x;
		double x3 = x2 * x;

		return (float) (x - x3 * 0.16666666666666666666666666666667 + x2 * x3 * 0.00833333333333333333333333333333);
      } else if ( x <= 0.352 ) { //350 = stable 20 degs - 15 based, 100% precise https://www.compilejava.net
        x -= 0.26179938779914943653855361527329;
        double x2 = x * x;
        double x3 = x2 * x;
        double x4 = x3 * x;
        
        return (float) (0.2588190451025207623488988376240483 + 0.9659258262890682867497431997288974 * x
                       - 0.1294095225512603811744494188120242 * x2 - 0.1609876377148447144582905332881496 * x3 + x4 * 0.01078412687927169843120411823433535 + x4 * x * 0.008049381885742235722914526664407478);
      } else if ( x < 0.39 ) { // 21 degs, last digit +- 2
        x -= 0.3665191429188092111539750613826087;
        double x2 = x * x;
        double x3 = x2 * x;
      	return (float) (0.3583679495453002734841377894134668 + x * 0.9335804264972017489900430631395707 - x2 * 0.1791839747726501367420688947067334 - x3 * 0.1555967377495336248316738438565951 + x3 * x * 0.01493199789772084472850574122556112);
      } else if ( x <= 0.52 ) { //min: 0.35555 // 30 degs - 24 based, last digit +- 2 http://keisan.casio.com/calculator
        x -= 0.4188790204786390984616857844372671;
        double x2 = x * x;
        double x3 = x2 * x;
        double x4 = x3 * x;
        double x5 = x4 * x;
        double x6 = x5 * x;
        //double x7 = x6 * x;
        //double x8 = x7 * x;
        return (float) (0.4067366430758002077539859903414976 + x * 0.9135454576426008955021275719853172 - x2 * 0.2033683215379001038769929951707488 - x3 * 0.1522575762737668159170212619975529
                       + x4 * 0.01694736012815834198974941626422907 + x5 * 0.007612878813688340795851063099877643 - x6 * 5.649120042719447329916472088076356e-4 - x6 * x * 1.812590193735319237107395976161344e-4 /*+ /*x8 * 1.008771436199901308913655730013635e-5 + x8 * x * 2.517486380187943384871383300224088E-7*/);
      } else if ( x <= 0.705 ) { // 40 degs - 35 based last digit +- 1
        x -= 0.6108652381980153519232917689710144;
        double x2 = x * x;
        double x3 = x2 * x;
        double x4 = x3 * x;
        return (float) (0.5735764363510460961080319128261579 + x * 0.8191520442889917896844883859168434 - x2 * 0.2867882181755230480540159564130789 - x3 * 0.1365253407148319649474147309861406 + x4 * 0.02389901818129358733783466303442324 + x4 * x * 0.006826267035741598247370736549307029 /*+ x4 * x4 * 1.422560606029380198680634704429955e-5*/);
      } else if ( x <= 0.873 ) { // 50 degs - 45 based, last digit +- 1
        x -= 0.7853981633974483096156608458198757;
        double x2 = x * x;
        double x3 = x2 * x;
        double x4 = x3 * x;
        return (float) (0.707106781186547524400844362104849 * (x + 1 - x2 / 2) - 0.1178511301977579207334740603508082 * x3 + x4 * 0.02946278254943948018336851508770204 + x4 * x * 0.005892556509887896036673703017540409);
      } else if ( x <= 0.96 ) { // 55 degs - 52.5 based, last digit +- 1
        x -= 0.9162978572970230278849376534565217;
        double x2 = x * x;
        return (float) (0.7933533402912351645797769615012993 + 0.608761429008720639416097542898164 * x - 0.3966766701456175822898884807506496 * x2 - 0.1014602381681201065693495904830273 * x2 * x);
      } else if ( x <= 1.13 ) { // 65 degs - 60 based, last digit +- 2
        x -= 1.047197551196597746154214461093168;
        double x2 = x * x;
        double x3 = x2 * x;
        double x4 = x3 * x;
        double x5 = x4 * x;
        double x6 = x5 * x;
        return (float) (0.8660254037844386467637231707529362 + x/2 - x2 * 0.4330127018922193233818615853764681 - x3/12 + x4 * 0.03608439182435161028182179878137234 /*+ x5 / 12*/ - 0.001202813060811720342727393292712411 * x6 - x6 * x / 10080);
      } else if ( x <= 1.265 ) { // 72.5 degs - 67.5 based, last digit +- 1
        x -= 1.265363707695888943269675807154244;
        double x2 = x * x;
        double x3 = x2 * x;
        double x4 = x3 * x;
        return (float) (0.9537169507482269211438470646002574 + x * 0.3007057995042731216225471359310734 - x2 * 0.4768584753741134605719235323001287 - x3 * 0.05011763325071218693709118932184557 + x4 * 0.03973820628117612171432696102501073 - x4 * x * x * 0.001324606876039204057144232034167024);
      } else if ( x <= 1.395 ) { // 80 degs - 75 based, 100% precise
        x -= 1.30899693899574718269276807636646;
        double x2 = x * x;
        double x3 = x2 * x;
        double x4 = x3 * x;
        return (float) (0.9659258262890682867497431997288974 + x * 0.2588190451025207623488988376240483 - x2 * 0.4829629131445341433748715998644487 - x3 * 0.04313650751708679372481647293734139 + 0.04024690942871117861457263332203739 * x4);
      } else { // 80 - 90 (1.57 rad) degs - 85 based, 100% precise
        x -= 1.483529864195180140385137153215321;
       	double x2 = x * x;
        double x3 = x2 * x;
        double x4 = x3 * x;
        return (float) (0.996194698091745532295010402473888 + x * 0.0871557427476581735580642708374736 - x2 * 0.498097349045872766147505201236944 - x3 * 0.01452595712460969559301071180624559 + x4 * 0.04150811242048939717895876676974534);
      }
	}

	public static float cos(float rad) {
		return sin(BF_SIN_TO_COS + rad);
	}
}
