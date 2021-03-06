package cn.edu.buaa.crypto.library.sake;

import it.unisa.dia.gas.jpbc.Element;
import cn.edu.buaa.crypto.util.Out;
import cn.edu.buaa.crypto.util.Timer;

public class TestSAKE {
	private final String[] ID;
	
	private final int timeToTest;
	private final int maxDepth;
	
	private double setupTime;
	private double[] keyGenTime;
	private double[] signTime;
	private double[] verificationTime;
	
	public TestSAKE(int timeToTest, int maxDepth){
		this.timeToTest = timeToTest;
		this.maxDepth = maxDepth;
		this.ID = new String[maxDepth];
		for (int i=0; i<this.ID.length; i++){
			this.ID[i] = "ID_" + (i+1);
		}
		
		this.keyGenTime = new double[this.maxDepth];
		this.signTime = new double[this.maxDepth];
		this.verificationTime = new double[this.maxDepth];
	}
	
	private void test_one_round(){
		Timer timer = new Timer();
		SAKE sake = new SAKE();
		
		Element r_A;
		Element r_B;
		
		timer.start(0);
		SAKEmsk msk  = sake.Setup();
		SAKEpp pp = sake.getPublicParameter();
		this.setupTime += timer.stop(0);
		
		r_B = pp.getPairing().getZr().newRandomElement().getImmutable();
		
		SAKEsk[] sk = new SAKEsk[this.maxDepth];
		timer.start(0);
		sk[0] = sake.KeyGen(pp, msk, ID[0]);
		this.keyGenTime[0] += timer.stop(0);
		
		for (int i=1; i<this.maxDepth; i++){
			timer.start(0);
			sk[i] = sake.Delegate(pp, sk[i-1], ID[i]);
			this.keyGenTime[i] += timer.stop(0);
		}
		
		SAKEsign[] sign = new SAKEsign[this.maxDepth];
		for (int i=0; i<this.maxDepth; i++){
			timer.start(0);
			r_A = pp.getPairing().getZr().newRandomElement().getImmutable();
			Element message = pp.getPairing().pairing(pp.get_g(), pp.get_g()).powZn(r_A).getImmutable();
			sign[i] = sake.Signing(pp, sk[i], message.toBytes());
			this.signTime[i] += timer.stop(0);
		}
		
		for (int i=0; i<this.maxDepth; i++){
			timer.start(0);
			sake.Verification(pp, sign[i]);
			Element e_g_g_r = pp.getPairing().getGT().newElementFromBytes(sign[i].get_m());
			e_g_g_r = e_g_g_r.powZn(r_B).getImmutable();
			this.verificationTime[i] += timer.stop(0);
		}
	}
	
	public void testBenchmark() {
		Out out = new Out("SAKE-benchmark-" + Timer.nowTime());
		for (int index = 0; index < this.timeToTest; index++) {
			test_one_round();
		}
		
		setupTime /= this.timeToTest;
		for (int i=0; i<keyGenTime.length; i++){
			keyGenTime[i] /= this.timeToTest;
		}
		for (int i=0; i<keyGenTime.length; i++){
			signTime[i] /= this.timeToTest;
		}
		for (int i=0; i<keyGenTime.length; i++){
			verificationTime[i] /= this.timeToTest;
		}
		out.println("Total Test Time = " + this.timeToTest + ", Max Depth = " + this.maxDepth);
		
		
		out.printf("Setup Time: %.2f\n", this.setupTime);
		out.print("KeyGen Time: ");
		for (int i=0; i<keyGenTime.length; i++){
			out.printf("%.2f\t", keyGenTime[i]);
		}
		out.println();
		out.print("Signing Time: ");
		for (int i=0; i<signTime.length; i++){
			out.printf("%.2f\t", signTime[i]);
		}
		out.println();
		out.print("Verification Time: ");
		for (int i=0; i<verificationTime.length; i++){
			out.printf("%.2f\t", verificationTime[i]);
		}
		out.println();
	}
	
	public static void main(String[] args){
		new TestSAKE(100, 10).testBenchmark();
	}
}
