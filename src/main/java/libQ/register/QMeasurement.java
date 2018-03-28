package libQ.register;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.complex.Complex;

/**
 * 
 * @author Gustavo Banegas
 *
 */
public class QMeasurement {

	public static BigInteger measure(QReg reg) {
		double r;
		int i;

		/* Get a random number between 0 and 1 */

		r = quantumRandom();

		for (i = 0; i < reg.getSize(); i++) {
			/*
			 * If the random number is less than the probability of the given base state -
			 * r, return the base state as the result. Otherwise, continue with the next
			 * base state.
			 */

			r -= QMeasurement.quantumProbabilityInline(reg.getAmplitude().get(i));
			if (0 >= r)
				return reg.getState().get(i);
		}
		return BigInteger.ZERO;
	}

	public static void deleteRegAndPreserveHash(QReg reg) {
		// TODO Auto-generated method stub

	}

	public static void measureStateAndCollapse(int position, BigInteger value, QReg reg) {
		int i, j, k;
		int size = 0;
		double d = 0;
		BigInteger lpat = BigInteger.ZERO, rpat = BigInteger.ZERO, pos2;

		pos2 = BigInteger.ONE.shiftLeft(position);

		/*
		 * Eradicate all amplitudes of base states which have been ruled out by the
		 * measurement and get the norm of the new register
		 */

		for (i = 0; i < reg.getSize(); i++) {
			BigInteger tmp = reg.getState().get(i).and(pos2);

			if (((tmp.compareTo(BigInteger.ZERO) != 0) && (value.compareTo(BigInteger.ZERO) != 0))
					|| ((tmp.compareTo(BigInteger.ZERO) == 0) && (value.compareTo(BigInteger.ZERO) == 0))) {
				d += quantumProbabilityInline(reg.getAmplitude().get(i));
				size++;
			}
		}

		/*
		 * Determine the numbers of the new base states and norm the quantum register
		 */
		List<BigInteger> nState = new ArrayList<>();
		List<Complex> nAmplitude = new ArrayList<>();
		for (i = 0, j = 0; i < reg.getSize(); i++) {
			BigInteger tmp = reg.getState().get(i).and(pos2);

			if (((tmp.compareTo(BigInteger.ZERO) != 0) && (value.compareTo(BigInteger.ZERO) != 0))
					|| ((tmp.compareTo(BigInteger.ZERO) == 0) && (value.compareTo(BigInteger.ZERO) == 0))) {
				for (k = 0, rpat = BigInteger.ZERO; k < position; k++) {
					BigInteger tmp_1 = BigInteger.ONE.shiftLeft(k);
					rpat = rpat.add(tmp_1);
				}

				rpat = rpat.and(reg.getState().get(i));

				for (k = 63, lpat = BigInteger.ZERO; k > position; k--) {
					BigInteger tmp_lpat = BigInteger.ONE.shiftLeft(k);
					lpat = lpat.add(tmp_lpat);
				}

				lpat = lpat.and(reg.getState().get(i));

				BigInteger lapt_shift = BigInteger.ONE.shiftRight(lpat.intValue());
				nState.add(j, lapt_shift.or(rpat));
				Complex n = (reg.getAmplitude().get(i).multiply(Complex.ONE)).subtract(new Complex(Math.sqrt(d)));
				nAmplitude.add(j, n);

				// out.state[j] = (lpat >> 1) | rpat;
				// out.amplitude[j] = reg.amplitude[i] * 1 / (float) sqrt(d);

				j++;
			}
		}
		reg.setSize(size);
		reg.setWidth(reg.getWidth() - 1);
		reg.setState(nState);
		reg.setAmplitude(nAmplitude);

	}

	public static double quantumProbabilityInline(Complex complex) {
		double result = (complex.getReal() * complex.getReal()) + (complex.getImaginary() * complex.getImaginary());
		return result;
	}

	private static double quantumRandom() {
		Random r = new Random();
		return r.nextDouble();
	}

	public static BigInteger measureQBit(QReg qReg, int position) {
		int i;
		BigInteger result = BigInteger.ZERO;
		double pa = 0, r;
		BigInteger pos2;

		pos2 = BigInteger.ONE.shiftLeft(position);

		/* Sum up the probability for 0 being the result */

		for (i = 0; i < qReg.getSize(); i++) {
			BigInteger tmp = qReg.getState().get(i).and(pos2);
			if ((tmp.compareTo(BigInteger.ZERO) == 0)) {
				pa += QMeasurement.quantumProbabilityInline(qReg.getAmplitude().get(i));
			}
		}

		/*
		 * Compare the probability for 0 with a random number and determine the result
		 * of the measurement
		 */

		r = quantumRandom();

		if (r > pa)
			result = BigInteger.ONE;

		QMeasurement.measureStateAndCollapse(position, result, qReg);

		QMeasurement.deleteRegAndPreserveHash(qReg);

		return result;
	}

}
