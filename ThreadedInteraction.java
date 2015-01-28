import java.util.Random;


public class ThreadedInteraction implements Runnable{

	//Data
	private Variable[] V;
	private Speaker[] S;
	private int T;
	private double sigma;
	private int[] interactionList;
	
	
	//**************************************************
	
	
	public ThreadedInteraction(Variable[] VIn, Speaker[] SIn, int TIn, double sigmaIn, int[] interactionListIn) {
		this.V = VIn;
		this.S = SIn;
		this.T = TIn;
		this.sigma = sigmaIn;
		this.interactionList = interactionListIn;
	}
	
	
	//**************************************************
	
	
	//Select two speakers and interact
	@Override
	//NOTE: Need to make sure the same speakers aren't selected each time period
	public void run() {
		
		//Random number generator
		Random gen = new Random();
		
		//OneDLattice check
		boolean oneDLattice = Speaker.oneDLatticeCheck();
		
		//Speakers
		int i = interactionList[0];
		int j = interactionList[1];
		
		//Select variable
		int variable = gen.nextInt(V.length);
		
		//Speaker speak
		S[i].speak(T, V[variable]);
		//Only speak if i!=j
		if (i != j) {
			S[j].speak(T, V[variable]);
		}
		
		//Construct perceived frequencies
		double[] y1 = S[i].perceive(T, S[j], V[variable], sigma);
	
		//Modify probabilities
		S[i].modify(S[j], y1, V[variable]);
		
		//Only modify j probability if j isn't an end point or if i != j
		if (oneDLattice == false && i != j || j > 0 && j < S.length-1 && i != j) {
			double[] y2 = S[j].perceive(T, S[i], V[variable], sigma);
			S[j].modify(S[i], y2, V[variable]);
		}
		
		//Reset token numbers
		V[variable].resetNumTokens(i, j);
	}
}
