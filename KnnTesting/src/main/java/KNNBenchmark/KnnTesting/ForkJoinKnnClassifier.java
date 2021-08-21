package KNNBenchmark.KnnTesting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class ForkJoinKnnClassifier {
	private int k; 
	private double [][] trainData;	
	private double [][] testData;
	private int [] trainDataTargetList;
	private int [] expectedTestTargetList;
	private int MAX_INSTANCES_OF_TEST;
	private AtomicIntegerArray testDataTargetList;
	private int n_threads;

	
	public ForkJoinKnnClassifier(int n_neighbors, int n_instances_train, int n_instances_test, int MAX_INSTANCES_OF_TEST, int n_threads) {
		System.out.println("KNN start ---- [Loading the files]");
		CSVReader trainReader = new CSVReader("/home/leonandro/Codes/java/programação_concorrente/datasets/diabetes.csv", 7526883);
		CSVReader testReader = new CSVReader("/home/leonandro/Codes/java/programação_concorrente/datasets/diabetes_328mb.csv", 1742866);
		
		this.k = n_neighbors;
		this.trainData = trainReader.load();
		this.trainDataTargetList = trainReader.getOutcomes();
		trainReader.clear();
		
		System.out.println("KNN update ---- [Train data loaded]");
		this.testData = testReader.load();
		this.expectedTestTargetList = testReader.getOutcomes();
		testReader.clear();
		
		System.out.println("KNN update ---- [Test data loaded]");
		
		this.MAX_INSTANCES_OF_TEST = MAX_INSTANCES_OF_TEST;
		this.n_threads = n_threads;
		
		
		
		this.testDataTargetList = new AtomicIntegerArray (MAX_INSTANCES_OF_TEST);
	}
	
	public class PredictTask extends RecursiveAction {

	    long partitionSize;
	    long N_PARTITION_SIZE;
	    int startinIndex;
	    int finalIndex;

	    public PredictTask(long n, long b, int init, int end) {
	        this.partitionSize = n;
	        this.N_PARTITION_SIZE = b;
	        this.startinIndex = init;
	        this.finalIndex = end;
	    }

	    @Override
	    protected void compute() {
	        if (this.partitionSize <= this.N_PARTITION_SIZE) {
	           predictSplited(this.startinIndex, this.finalIndex, ForkJoinKnnClassifier.this.testData);
	        }
	        else {
	        	
	        	//TODO maybe change the implementation of this part, to fit better the n_threads, and not only dividing in 2 parts
	        	PredictTask f1 = new PredictTask(partitionSize/2, N_PARTITION_SIZE, this.startinIndex, this.finalIndex/2);
	        	PredictTask f2 = new PredictTask(partitionSize/2, N_PARTITION_SIZE, this.finalIndex/2, this.finalIndex);
		        f1.fork();
		        f2.compute();
		        f1.join();
	        }
	    }

	}

	
	
	public int [] predict () throws InterruptedException, ExecutionException {
		int N_PARTITION_SIZE = (this.MAX_INSTANCES_OF_TEST / this.n_threads);
		
		ForkJoinPool pool = new ForkJoinPool();
		PredictTask task = new PredictTask(this.MAX_INSTANCES_OF_TEST, N_PARTITION_SIZE, 0, this.MAX_INSTANCES_OF_TEST);
		pool.invoke(task);
		
		int [] returnList = new int [this.testDataTargetList.length()];
		
		for(int i=0; i<this.testDataTargetList.length(); i++) {
			returnList[i] = this.testDataTargetList.get(i);
		}
		
		return returnList;
	}
	

	public void predictSplited (int startingIndex, int finalIndex, double [][] data) {
		int N_PARTITION_SIZE = (this.MAX_INSTANCES_OF_TEST / this.n_threads);
	
		//The last partition have to represent all the rest of the data [P1, P2,...Pn + {the rest}]
		//if(finalIndex == N_PARTITION_SIZE*(this.n_threads)) finalIndex = this.MAX_INSTANCES_OF_TEST;
		
		HashMap <Integer, Float> kInstances = new HashMap <Integer, Float>();
		
		int thisLittleMF = -1;
		for(int i=startingIndex; i<finalIndex; i++) {
			
			for(int j = 0; j < this.trainDataTargetList.length; j++) {
				float distanceToLineInTrain = this.calculateEuclidianDistance(data[i], this.trainData[j]);
				
				if(kInstances.size() < this.k) {
					kInstances.put(j, distanceToLineInTrain);
				}
				
				else {
					thisLittleMF = this.isSmallerThanSomeone(distanceToLineInTrain, kInstances); 
					if(thisLittleMF != -1) {
						kInstances.remove(thisLittleMF);
						kInstances.put(j, distanceToLineInTrain);
					}
				}
							
			}
			
			

			this.testDataTargetList.set(i, mode(kInstances.keySet(), this.k));
			
			kInstances.clear();
		}
	}
	
	private int isSmallerThanSomeone (float value, HashMap<Integer, Float> instances) {
		int smallerValueIndex = -1;
		float smallerValue = 9999999;
		for(int a : instances.keySet()) {
			if(value < instances.get(a)) {
				if(value < smallerValue) {
					smallerValueIndex = a;
					smallerValue = instances.get(a);
				}
			}
		}
		
		return smallerValueIndex;
	}
	
	
	private Float calculateEuclidianDistance(double [] a, double [] b) {
		double sum = 0f;
		
		//colocando -1 apenas para exluir a coluna de outcomes
		for(int i=0; i< (a.length-1) ; i++) {
			sum += (a[i] - b[i]) * (a[i] - b[i]);
		}	
		return (float)Math.sqrt(sum);
	}
	


 	 
	 private int mode(Collection <Integer> list, int k) {
		    int maxValue = 0; 
		    int maxCount = 0;
		    
		    int n = list.size();
		    Object [] a = new Object[n];
		    a = list.toArray();
		    
		    for (int i = 0; i <k; i++) {
		    	//System.out.print(a[i] + " - ");
		        int count = 0;
		        for (int j = 0; j < a.length; ++j) {
		            if (this.trainDataTargetList[(int)a[j]] == this.trainDataTargetList[(int)a[i]]) ++count;
		        }
		        if (count > maxCount) {
		            maxCount = count;
		            maxValue = this.trainDataTargetList [(int)a[i]];
		        }
		    }

		    return maxValue;
	 }
	 
	 public float getAccuracy () {
			int numberOfHits = 0;
			
			for (int i = 0; i<this.MAX_INSTANCES_OF_TEST; i++) {
				if(this.testDataTargetList.get(i) == this.expectedTestTargetList[i]) {
					numberOfHits++;
				}
			}
			//System.out.println((float)numberOfHits / this.MAX_INSTANCES_OF_TEST);
			return (float)numberOfHits / this.MAX_INSTANCES_OF_TEST;
		}
}
