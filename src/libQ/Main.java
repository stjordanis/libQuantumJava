package libQ;

import java.math.BigInteger;
import java.rmi.UnexpectedException;

import exceptions.OperationNotPermittedException;
import libQ.gates.EGateTypes;
import libQ.gates.GateFactory;
import libQ.gates.IGate;
import libQ.register.QReg;

public class Main {

	public static void main(String[] args) throws OperationNotPermittedException, UnexpectedException {
		QReg reg = new QReg(new BigInteger("1"), 4);

		reg.quantum_print_qureg();
		IGate h = GateFactory.getInstance().getGate(EGateTypes.E_HadamardGate);
		//h.apply(reg, 0);
		//h.apply(reg, 3);
		//TODO: check why it is not working
		h.apply(reg, 2);
		reg.quantum_print_qureg();
		h.apply(reg, 2);
		reg.quantum_print_qureg();
		System.out.println(reg.measure());
		/*
		 * h.apply(reg, 3, 2, 0); reg.quantum_print_qureg();
		 * System.out.println(GateFactory.getInstance().getNrCNotGate());
		 */
	}

}
