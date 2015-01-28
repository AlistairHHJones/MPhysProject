package StochasticSimulation;

import java.io.*;

/*MPhysProject: Test class for single speaker network
 *Author: Alistair Jones
 *UNN: s1104852
 *Date Created: 21/9/2014
 *Last Updated: 16/10/2014
 */

public class Test {
	
	public static void main(String[] args) throws IOException{

		//New variable
		int numVariants = 2;
		int numSpeakers = 1;
		Variable[] V = new Variable[1];
		V[0] = new Variable("Var1",numVariants,numSpeakers);
		
		//Weights matrix
		double[][] H = new double[numSpeakers][numSpeakers];
		H[0][0] = 0.0;
		
		//New speakers
		Speaker[] S = new Speaker[numSpeakers];
		S[0] = new Speaker(0,H);
		//S[1] = new Speaker(1,H);

		//Parameters
		int T = 1;
		int numSteps = 3000000;
		//int numH = 10;
		int threads = 1;//Runtime.getRuntime().availableProcessors();
		
		//Interact
		InteractionEngine e = new InteractionEngine(V,S, T, numSteps, 0.00, threads);
		
		//double binWidth = 0.01;
		//.setTimePlot(numSteps , T, numRuns, i, v, variable, binWidth, false);
		e.endTimePlot(10,10);

		//Exit gracefully
		System.exit(0);
	}

}
