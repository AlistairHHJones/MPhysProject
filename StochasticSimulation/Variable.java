/*MPhysProject: Variable class
 *Author: Alistair Jones
 *UNN: s1104852
 *Date Created: 20/9/2014
 *Last Updated: 21/9/2014
 */

public class Variable {
	 
	//Number of variants
	private int numVariants;
	
	//Label
	private String Label;
	
	//Probabilities that speakers speak variants
	private double[][] x;
	
	//Number of tokens produced by speakers
	private int[][] n;
	
	//Number of speakers
	int numSpeakers;
	
	//Intrinsic replicator weights
	double[][] S;
	
	
	//**************************************************
	
	
	//Constructor
	public Variable(String LabelIn, int numVariantsIn, int numSpeakersIn) {
		this.numVariants = numVariantsIn;
		this.Label = LabelIn;
		this.numSpeakers = numSpeakersIn;
		this.x = new double[this.numSpeakers][this.numVariants];
		this.n = new int[this.numSpeakers][this.numVariants];
		this.S = new double[this.numSpeakers][this.numVariants];
		
		//Fill initial probabilities, token numbers and intrinsic replicator weights
		for (int i = 0; i < this.numSpeakers; i++){
			for (int v = 0; v < this.numVariants; v++){
				this.x[i][v] = 1.0/this.numVariants;
				this.n[i][v] = 0;
				this.S[i][v] = 1.0/this.numVariants;
			}
		}
	}
	
	
	//**************************************************

	
	//Get label
	public String getLabel() {
		return this.Label;
	}

	
	//**************************************************

	
	//Get number of variants of variable
	public int getNumVariants() {
		return this.numVariants;
	}

	
	//**************************************************

	
	//Get number of speakers
	public int getNumSpeakers() {
		return this.numSpeakers;
	}

	
	//**************************************************

	
	//Get (i,v)th element of probability matrix
	public double getProbability(int i, int v) {
		return this.x[i][v];
	}

	
	//**************************************************

	
	//Update (i,v)th element of probabilities matrix
	public void setProbability(int i, double[] newProb) {	
		for (int v = 0; v < this.numVariants; v++) {
			this.x[i][v] = newProb[v];
		}
	}

	
	//**************************************************

	
	//Add token (i,v)
	public void addToken(int i, int v) {
		this.n[i][v]++;
	}

	
	//**************************************************

	
	//Get token number (i,v)
	public int getNumTokens(int i, int v) {
		return this.n[i][v];
	}

	
	//**************************************************

	
	//Reset token numbers for rows i and j
	public void resetNumTokens(int i, int j) {
		for(int v = 0; v < this.numVariants; v++) {
			this.n[i][v] = 0;
			this.n[j][v] = 0;
		}
	}

	
	//**************************************************

	
	//Reset probabilities
	public void resetProbabilities() {
		for(int i = 0; i < this.numSpeakers; i++) {
			for(int v = 0; v < this.numVariants; v++) {
				this.x[i][v] = 1.0/this.numVariants;
			}
		}
	}
	
	//**************************************************
	
	//Get replicator weights
	public double[] getReplicatorWeights(int i) {
		return this.S[i];
	}
	
	//**************************************************
	
	//Set replicator weights
	public void setReplicatorWeights(double[] Si, int i) {
		this.S[i] = Si;
	}
	
}
