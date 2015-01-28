package StochasticSimulation;

import java.io.*;


public class Test3_1dLattice {
	
	public static void main(String[] args) throws IOException{

		//New variable
		int numVariants = 2;
		int numSpeakers = 100;
		Variable[] V = new Variable[1];
		V[0] = new Variable("Var1",numVariants,numSpeakers);
		
		//Weights matrix
		double[][] H = new double[numSpeakers][numSpeakers];
		double HS = 0.1;
		double HD = 0;//HS/1000;
		for (int i = 0; i < numSpeakers; i++) {
			
			if (i < numSpeakers/2 - 1 || i >numSpeakers/2) {
				if (i < numSpeakers-1) {
					H[i][i+1] = HS;
				}
				if (i > 0) {
					H[i][i-1] = HS;
				}
			}
			else if (i == numSpeakers/2 - 1) {
				H[i][i+1] = HD;
				H[i][i-1] = HS;
			}
			else{
				H[i][i+1] = HS;
				H[i][i-1] = HD;
			}
		}
		
		//New speakers
		Speaker[] S = new Speaker[numSpeakers];
		for (int i = 0; i < numSpeakers; i++) {
			S[i] = new Speaker(i,H,"1dLattice");
		}

		//Interact
		double sigma = 0.0;
		int T = 5;
		int numSteps = 100000;
		int threads = 1;//Runtime.getRuntime().availableProcessors();
		InteractionEngine e = new InteractionEngine(V, S, T, numSteps, sigma, threads);
		
		//Set initial probabilities at ends (fixed)
		double[] prob1 = new double[2];
		prob1[0] = 0;
		prob1[1] = 1.0;
		double[] prob2 = new double[2];
		prob2[0] = 1.0;
		prob2[1] = 0;
		V[0].setProbability(0, prob1);
		V[0].setProbability(numSpeakers-1, prob2);
		
		int numK = 1;

		//Data array
		double[][] data = new double[numSpeakers][2];
		for (int k = 0; k < numK; k++) {
			
			//Interact
			for (int t = 0; t < numSteps; t++) {
				e.interact(e.getInteractionList(), sigma);
	
				//Synchronize threads
				e.synchronize();
				
				//Percentage
				e.percentageOut(t, numSteps);
			}

			//Write data array
			for(int i = 0; i < numSpeakers; i++) {
				data[i][0] = data[i][0] + V[0].getProbability(i, 0)/numK;
				data[i][1] = i;
			}
		}
		
		//Print array to file
		e.printArray("1DLattice",data,numSpeakers,1);
		//e.printArray("SpeakerWeights",H,numSpeakers,numSpeakers);
				
		//Exit gracefully
		System.exit(0);
	}
	
	
}
