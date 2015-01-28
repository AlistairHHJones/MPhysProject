import java.util.Random;

/*MPhysProject: Speaker class
 *Author: Alistair Jones
 *UNN: s1104852
 *Date Created: 20/9/2014
 *Last Updated: 27/11/2014
 */

public class Speaker{

	//Integer label
	private int i;
	
	//Boolean for 1d lattice
	static boolean oneDLattice = false;
	
	//Weights speakers ascribe to utterances of others
	static double[][] H;
	
	
	//**************************************************

	
	//Constructor
	public Speaker(int labelIn){
		this.i = labelIn;
	}
	
	
	//**************************************************

	//Constructor
	public Speaker(int labelIn, String Geometry){
		this.i = labelIn;
		
		if (Geometry == "1dLattice") {
			oneDLattice = true;
		}
	}

	
	//**************************************************

	
	//Constructor with HIn
	public Speaker(int labelIn, double[][] HIn){
		this.i = labelIn;
		H = HIn;
	}
	
	
	//**************************************************

	
	//Constructor with HIn
	public Speaker(int labelIn, double[][] HIn, String Geometry){
		this.i = labelIn;
		H = HIn;
		if (Geometry == "1dLattice") {
			oneDLattice = true;
		}
	}

	
	//**************************************************

	
	//Get label
	public int getLabel() {
		return this.i;
	}

	
	//**************************************************

	
	//Get (i,j)th element of H
	public static double getH(int i1, int j1){
		return H[i1][j1];
	}
	
	
	//**************************************************
	
	//Set elements of H
	public static void setHij(double HS, double HD) {
		for (int ii = 0; ii < H.length; ii++) {
			for (int jj = 0; jj < H.length; jj++) {
				if (ii != jj) {
					if (ii >= H.length/2 && jj >= H.length/2 || ii < H.length/2 && jj < H.length/2) {
						H[ii][jj] = HS;
						H[jj][ii] = HS;
					}
					else{
						H[ii][jj] = HD;
						H[jj][ii] = HD;
					}
				}
			}
		}
	}
	
	
	//**************************************************
	
	
	//Check oneDLattice
	public static boolean oneDLatticeCheck() {
		return oneDLattice;
	}

	
	//**************************************************

	
	//Randomly generate n tokens of variable V
	public void speak(int T, Variable V){
		
		//Random number generator
		Random gen = new Random();
		double a = 0;
		
		//Cumulative sum of x(i,v)'s
		double xiv = 0;
		
		//Generate token
		for (int j = 0; j < T; j++){
			a = gen.nextDouble();
			
			//If statements to select token to generate
			for (int v = 0; v < V.getNumVariants(); v++){

				if (xiv <= a && a < xiv + V.getProbability(i, v)) {
					V.addToken(i, v);
					break;
				}

				//Sum x(i,v)'s for if's
				xiv = xiv + V.getProbability(i, v);
			}
			xiv = 0;
		}
	}

	
	//**************************************************

	
	//Function fiv(u) for percieved frequency
	public double fiv(double u, double Siv) {
		double f = (1.0+Siv)*u;
		if (f>1) {
			return 1.0;
		}
		else {
			return f;
		}
	}

	
	//**************************************************

	
	//Perceived frequency of each variant
	public double[] perceive(int T, Speaker j, Variable V, double sigma) {
		
		//Array for perceived frequencies
		double[] yi = new double[V.getNumVariants()];
		
		//Intrinsic replicator weight
		double[] Si = V.getReplicatorWeights(this.i);
		double[] SiPrimed = new double[Si.length];
		
		//Gamma and sigma for SiPrimed
		double gamma = 0.01;
		
		//Sum of Hij*njv/T over j
		double sum = 0;
		
		//Calculate y values
		for (int v = 0; v < V.getNumVariants(); v++) {
			
			//Get n(i,v) and n(j,i)
			double niv = V.getNumTokens(this.i, v);
			double njv = V.getNumTokens(j.getLabel(), v);
			
			//Calculate SiPrimed[v]
			for (int jj = 0; jj < V.getNumSpeakers(); jj++) {
				sum = sum + H[i][jj]*V.getNumTokens(jj,v)/T;
			}
			SiPrimed[v] = (Si[v] + sigma*gamma*sum)/(1+gamma);
			
			//Perceived frequency of variant v
			yi[v] = this.fiv(niv/T,SiPrimed[v]) + H[this.i][j.getLabel()]*this.fiv(njv/T,SiPrimed[v]);
		}
		//Set SiPrimed
		V.setReplicatorWeights(SiPrimed, this.i);
		
		return yi;
	}

	
	//**************************************************

	
	//Modify variant probabilities based on perceived frequencies
	public void modify(Speaker j, double[] yi, Variable V) {
		
		//Lambda scales how much affect yiv has on xiv'
		double lambda = 0.01;
		
		//Normalisation Z such that the sum over v of xiv' = 1
		double Z = 1 + lambda*(1 + H[this.i][j.getLabel()]);
		
		//Array for new probabilities
		double[] xivPrimed = new double[V.getNumVariants()];
		
		//Calculate new (non-normalised) probabilities and normalisation
		for (int v = 0; v < V.getNumVariants(); v++) {
			double xiv = V.getProbability(this.i, v);
			xivPrimed[v] = (xiv + lambda*yi[v])/Z;
		}
		//Set probabilities
		V.setProbability(this.i, xivPrimed);
	}
}
