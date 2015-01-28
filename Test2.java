import java.io.*;

/*MPhysProject: Test class for multi speaker network
 *Author: Alistair Jones
 *UNN: s1104852
 *Date Created: 9/10/2014
 *Last Updated: 27/11/2014
 */

public class Test2 {
	
	public static void main(String[] args) throws IOException{

		//New variable
		int numVariants = 2;
		int numSpeakers = 50;
		Variable[] V = new Variable[1];
		V[0] = new Variable("Var1",numVariants,numSpeakers);
		
		//Weights matrix
		double[][] H = new double[numSpeakers][numSpeakers];
		//double hs = 1.0;
		//double hd = 1.0*hs;
		//double lambda = 0.01;
		//*****
		double HS = 0.1;
		double HD = HS/1000;
		
		for (int i = 0; i < numSpeakers; i++) {
			for (int j = i; j < numSpeakers; j++) {
				if (i != j) {
					if (i >= numSpeakers/2 && j >= numSpeakers/2 || i < numSpeakers/2 && j < numSpeakers/2) {
						H[i][j] = HS;
						H[j][i] = HS;
					}
					else{
						H[i][j] = HD;
						H[j][i] = HD;
					}
				}
			}
		}
		
		//New speakers
		Speaker[] S = new Speaker[numSpeakers];
		for (int i = 0; i < numSpeakers; i++) {
			S[i] = new Speaker(i,H);
		}

		//Parameters
		int T = 3;
		int numSteps = 3000000;
		int numSigma = 5;
		//int numH = 10;
		int threads = Runtime.getRuntime().availableProcessors();
		
		//Interact
		InteractionEngine e = new InteractionEngine(V,S, T, numSteps, 0.1, threads);
		
		//e.endTimePlot("Hello", T, 50, 0, 100);
		e.plotDistanceEvolution(numSteps, T);
		//e.endTimePlot("",T,100);
		//e.plotDistanceVsSigma(numSteps, T, numSigma, 0.000, 0.01);
		//e.plotPhaseDiagram(numSigma, 0.0001, 0.01, numH, 0.01, 0.0001, 0.1, 0.0001, 0.7);
		
		//Exit gracefully
		System.exit(0);
	}

}

