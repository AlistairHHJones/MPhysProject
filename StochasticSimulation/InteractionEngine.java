package StochasticSimulation;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*MPhysProject: Interaction engine class
 *Author: Alistair Jones
 *UNN: s1104852
 *Date Created: 21/9/2014
 *Last Updated: 27/11/2014
 */

public class InteractionEngine{

	//Data
	private Variable[] V;
	private Speaker[] S;
	private int T;
	private double sigma;
	private int numSteps;
	
	//Multithreading
	private final int threads;
	ExecutorService exec;
	@SuppressWarnings("rawtypes")
	Future[] ft;
	int[][] interactionList;
	
	
	//**************************************************
	
	
	//Constructor
	public InteractionEngine(Variable[] VIn, Speaker[] SIn, int TIn, int numStepsIn, double sigmaIn, int threadsIn) {
		this.V = VIn;
		this.S = SIn;
		this.T = TIn;
		this.sigma = sigmaIn;
		this.numSteps = numStepsIn/threadsIn;
		threads = threadsIn;
		exec = Executors.newFixedThreadPool(threads);
		ft = new Future[threads];
		
		//Speakers to interact
		interactionList = new int[threads][2];
	}
	
	
	//**************************************************
	
	
	//Print array to file
	public void printArray(String fileName, double[][] data, int dim1, int dim2) throws IOException{
		System.out.println("Printing to file...");
		File f = new File(fileName + ".txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		for(int t = 0; t < dim1; t++) {
			this.percentageOut(t, dim1);
			for (int i = 0; i < dim2; i++) {
				writer.write(data[t][i] + " ");
			}
			writer.newLine();
		}
		writer.close();
	}
	
	
	//**************************************************
	
	
	//Percentage log
	public void percentageOut(int t, int length) {
		double percent = ((double)t*100)/length;
		if (length>1000) {
			if (t%(length/1000) == 0) {
				System.out.println(percent + " %");
			}
		}
		else {
			System.out.println(percent + " %");
		}
	}
	
	
	//**************************************************
	
	
	//Synchronize threads
	public void synchronize() {
		
		//Synchronization of Threads
		try {
			for(int j = 0; j < threads; j++) {
				ft[j].get();
			}
		} catch (InterruptedException x) {
			System.out.println("InterruptedException Algorithm");
		} catch (ExecutionException x) {
			System.out.println("Execution Exception Algorithm");
		}
	}
	
	
	//**************************************************
	
	
	//Shutdown threads
	public void stopThreads(){
		exec.shutdown();
	}
	
	
	//**************************************************
	
	
	//Generate list of integers for speakers to interact
	public int[][] getInteractionList(){
		
		//Random number generator
		Random gen = new Random();
		
		//Integers to exclude from random number selection
		ArrayList<Integer> exclude = new ArrayList<Integer>(threads*2);
		
		//Speakers to interact
		int ii = 0;
		int jj = 0;
		
		for (int i = 0; i < threads; i++) {
			
			//Check for speaker selected before
			boolean overlapping = true;
			
			while(overlapping == true) {
				
				//Check geometry and choose speakers to interact accordingly
				if (Speaker.oneDLatticeCheck() == false) {
					ii = gen.nextInt(S.length);
					jj = gen.nextInt(S.length);
				}
				else {
					//Choose i to exclude 0 and S.length-1
					ii  = gen.nextInt(S.length-2) + 1;
					//Randomly select i+1,i or i-1 for 1dlattice
					jj  = ii - 1 + gen.nextInt(3);
				}
				
				//Check if ii and jj have already been used
				if (!exclude.contains(ii) && !exclude.contains(jj)) {
					exclude.add(ii);
					exclude.add(jj);
					overlapping = false;
				}
			}
			
			//Set speakers to select
			interactionList[i][0] = ii;
			interactionList[i][1] = jj;
			
			//Clear list for next iteraction
			exclude.clear();
		}
	
		return interactionList;
	}
	
	
	//**************************************************
	
	
	//Multithreaded interaction
	public void interact(int[][] interactionList, double sigmaIn){
		
		//Interact
		for(int i = 0; i < threads; i++) {
			ft[i] = exec.submit(new ThreadedInteraction(V, S, T, sigmaIn, interactionList[i]));
		}
		
	}
	
	
	//**************************************************
	
	
	//Iteratively interact through time and save evolution data to file
	public void plotTimeEvolution() throws IOException{
		
		//Data array
		double[][] data = new double[numSteps][3];
		
		//Iterate
		for(int t = 0; t < numSteps; t++) {
			
			//Percent
			this.percentageOut(t, numSteps);
			
			//Set t
			data[t][0] = t;
			
			//Interact
			this.interact(this.getInteractionList(), this.sigma);

			//Synchronize threads
			this.synchronize();

			//Set data points by averaging probabilities
			for (int v = 0; v < V[0].getNumVariants(); v++) {
				for (int ii = 0; ii < S.length; ii++) {
					data[t][v+1] = data[t][v+1] + V[0].getProbability(ii, v)/(S.length);
				}
			}
		}

		//Shutdown threads
		this.stopThreads();
		
		//Write data to file
		this.printArray("TimeEvolution", data, numSteps, 2);
	}
	
	
	//**************************************************
	
	
	//Plot evolution of distance between group probabilities
	public void plotDistanceEvolution(int numSteps, int T) throws IOException{
			
		//Data array
		double[][] data = new double[numSteps][3];
		
		//Arrays for initial probabilities
		double[] initialProbs1 = new double[V[0].getNumVariants()];
		initialProbs1[0] = 0.0;
		initialProbs1[1] = 1.0;
		double[] initialProbs2 = new double[V[0].getNumVariants()];
		initialProbs2[0] = 1.0;
		initialProbs2[1] = 0.0;
		
		int numK = 10;
		for (int k = 0; k < numK; k++) {
					
			//Set initial probabilities
			for (int i = 0; i < S.length; i++) {
				if (i < S.length/2) {
					V[0].setProbability(i, initialProbs1);
				}
				else {
					V[0].setProbability(i, initialProbs2);
				}
			}
			
			//Iterate
			for(int t = 0; t < numSteps; t++) {
				
				this.percentageOut(t, numSteps);
				
				//Set t
				data[t][0] = t;
				
				//Interact
				this.interact(this.getInteractionList(), this.sigma);
				
				//Synchronize threads
				this.synchronize();
				
				//Calculate distance between group probabilities
				double probA = 0;
				double probB = 0;
				for (int s = 0; s < S.length; s++) {
					if (s < S.length/2) {
						probA = probA + V[0].getProbability(s, 0)/(S.length/2);
					}
					else {
						probB = probB + V[0].getProbability(s, 0)/(S.length/2);
					}
				}
				double distance = Math.abs(probA - probB);
				data[t][1] = data[t][1] + distance/numK;
			}
			
		}
		//Shutdown threads
		this.stopThreads();
		
		//Write data to file
		this.printArray("DistanceEvolution" + this.sigma, data, numSteps, 2);
	}
	
	
	//**************************************************
	
	
	//Distance at the end of time evolution vs sigma
	public void plotDistanceVsSigma(int numSteps, int T, int numSigma, double a, double b) throws IOException{
		
		//Data array
		double[][] data = new double[numSigma][2];
		
		//Arrays for initial probabilities
		double[] initialProbs1 = new double[V[0].getNumVariants()];
		initialProbs1[0] = 0.0;
		initialProbs1[1] = 1.0;
		double[] initialProbs2 = new double[V[0].getNumVariants()];
		initialProbs2[0] = 1.0;
		initialProbs2[1] = 0.0; 
		
		//Calculate increment size
		double increment = (double)(b-a)/numSigma;
		
		int numK = 1;
		
		for (int k = 0; k<numK; k++) {
			
			//For increasing sigma
			for (int ss = 0; ss < numSigma; ss++) {
				
				this.percentageOut(ss, numSigma);
				
				//Sigma
				double sigma = a + ss*increment;
						
				for (int i = 0; i < S.length; i++) {
					if (i < S.length/2) {
						V[0].setProbability(i, initialProbs1);
					}
					else {
						V[0].setProbability(i, initialProbs2);
					}
				}
				
				//Iterate
				for(int t = 0; t < numSteps; t++) {
					
					//Interact
					this.interact(this.getInteractionList(), sigma);
	
					//Synchronize threads
					this.synchronize();
				}
	
				//Calculate distance between group probabilities
				double probA = 0;
				double probB = 0;
				for (int s = 0; s < S.length; s++) {
					if (s < S.length/2) {
						probA = probA + V[0].getProbability(s, 0)/(S.length/2);
					}
					else {
						probB = probB + V[0].getProbability(s, 0)/(S.length/2);
					}
				}
				double distance = Math.abs(probA - probB);
				data[ss][0] = data[ss][0] + sigma/numK;
				data[ss][1] = data[ss][1] + distance/numK;
			}
		}
		//Shutdown threads
		this.stopThreads();
		
		//Write data to file
		this.printArray("DistanceVsSigma", data, numSigma, 2);
	}

	
	//**************************************************
	
	
	//Create phase diagram of hs/hd vs sigma
	public void plotPhaseDiagram(int numSigma, double s1, double s2, int numH, double hs0, double hd0, double hs1, double hd1, double critDistance) throws IOException{
		
		//Data array
		double[][] data = new double[numSigma][2];
		
		//Calculate increment size
		double sIncrement = (s2-s1)/numSigma;
		double hsIncrement = (hs1-hs0)/numH;
		//double hdIncrement = ()/numH;
		
		//Initial hs and hd
		double hs = hs0;
		double hd = hd0;
	
		//Arrays for initial probabilities
		double[] initialProbs1 = new double[V[0].getNumVariants()];
		initialProbs1[0] = 0.0;
		initialProbs1[1] = 1.0;
		double[] initialProbs2 = new double[V[0].getNumVariants()];
		initialProbs2[0] = 1.0;
		initialProbs2[1] = 0.0; 
		
		for (int h = 0; h < numH; h++) {
			
			//Percent
			this.percentageOut(h, numH);
			
			if (hd0 == hd1) {
				hs = hs + hsIncrement;
			}
			else if (hs0 == hs1) {
				//hd = hd + hdIncrement;
			}
			Speaker.setHij(hs,hd);
			
			//For increasing sigma
			for (int ss = 0; ss < numSigma; ss++) {
						
				//Set initial probabilities
				for (int i = 0; i < S.length; i++) {
					if (i < S.length/2) {
						V[0].setProbability(i, initialProbs1);
					}
					else {
						V[0].setProbability(i, initialProbs2);
					}
				}
				
				//Sigma
				double sigma = s1 + ss*sIncrement;
			
				//Iterate
				for(int t = 0; t < numSteps; t++) {
					this.interact(this.getInteractionList(),sigma);

					//Synchronize threads
					this.synchronize();
				}
	
				//Calculate distance between group probabilities
				double probA = 0;
				double probB = 0;
				for (int s = 0; s < S.length; s++) {
					if (s < S.length/2) {
						probA = probA + V[0].getProbability(s, 0)/(S.length/2);
					}
					else {
						probB = probB + V[0].getProbability(s, 0)/(S.length/2);
					}
				}
				double distance = Math.abs(probA - probB);
				if (distance > critDistance) {
					data[h][0] = sigma;
					data[h][1] = hs/hd;
					break;
				}
			}
		}
		//Shutdown threads
		this.stopThreads();
		
		//Write data to file
		this.printArray("PhaseDiagram",data,numH,2);	
	}
	
	

	
	//**************************************************
	

	//Create plot of average time for 'fixation' to be reached for varying x0 (1 variable 2 variants)
	public void endTimePlot(int numX0s, int averageTimes) throws IOException{
		
		//Probability pre time evolution
		double[] x0 = new double[2];
		
		//Array for average time taken 
		double[][] data = new double[numX0s][2];
		
		//Average results by averageTimes runs
		for (int a = 0; a < averageTimes; a++) {
			
			//Report progress
			this.percentageOut(a, averageTimes);
		
			for (int j = 0; j < numX0s; j++) {
				
				//Set probability pre time evolution
				x0[0] = ((double)j)/(numX0s-1.0);
				x0[1] = 1.0 - x0[0];
				for (int i = 0; i < S.length; i++) {
					V[0].setProbability(i, x0);
				}
				
				//Iterate
				boolean fixation = false;
				int timeElapsed = 0;
				while(fixation == false) {
					//Interact
					this.interact(this.getInteractionList(),this.sigma);
					
					//Synchronize threads
					this.synchronize();
					
					//Check fixation
					for (int v = 0; v < 2; v++) {
						double averageProb = 0.0;
						for (int s = 0; s < S.length; s++) {
							averageProb = averageProb + V[0].getProbability(s, v)/S.length;
						}
						//System.out.println(averageProb);
						if(averageProb > 0.999999 || averageProb < 0.000001) {
							fixation = true;
						}
					}
					//Increase counter
					timeElapsed++;
				}
				
				//Reset probabilities
				for (int k = 0; k < V.length; k++) {
					V[k].resetProbabilities();
				}

				//Record
				data[j][0] = x0[0];
				data[j][1] = data[j][1] + timeElapsed/averageTimes;
				
			}
		}
		//Shutdown threads
		this.stopThreads();
		
		//Write data
		this.printArray("EndTimePlot", data, numX0s, 2);
	}
	

	
	//**************************************************
	
	
	//Create plot from n runs for time t for single or multi speakers (BROKEN TIME EVOLUTION)
	/*public void setTimePlot(int numSteps, int T, int numRuns, int i, int v, int variable, double binWidth, boolean multiSpeaker) throws IOException{
		
		//File to save data to
		File f = new File("setTimePlot.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		
		//Probability and bin ID
		double x;
		int bin;
		
		//Number of bins
		int numBins = (int)(1.0/binWidth);
		
		//Bins for x values
		int[] xBins = new int[numBins];
		
		for (int j = 0; j < numRuns; j++) {
			
			//Evolve time
			if (multiSpeaker == false) {
				//this.timeEvolution(numSteps, T);
			}
			else {
				//this.timeEvolution(numSteps, T);
			}
			
			//Get probability post time evolution
			x = V[variable].getProbability(i, v);
			
			//Reset probabilities
			for (int k = 0; k < V.length; k++) {
				V[k].resetProbabilities();
			}
			
			//Find bin
			bin = (int)(x/binWidth);
			
			//Add probability to bin
			try{
				xBins[bin]++;
			}
			catch (ArrayIndexOutOfBoundsException e) {
				xBins[bin-1]++;
			}
		}
		
		//Normalise and save data
		for (int j = 0; j < numBins; j++) {
			double d = ((double)xBins[j])/numRuns;
			String s = " " + d;
			writer.write(s);
			writer.newLine();
		}
		writer.close();
	}*/
	
	
}
