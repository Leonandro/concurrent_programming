package ufrn;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.stream.IntStream;

public class ParallelStreamKnnClassifier {
	private int k; 
	private double [][] trainData;	
	private int [] trainDataTargetList;
	private double [][] testData;
	private int [] testDataTargetList;
	private int [] expectedTestTargetList;
	private int MAX_INSTANCES_OF_TEST;


	
	public ParallelStreamKnnClassifier(int n_neighbors, int n_instances_train, int n_instances_test, int MAX_INSTANCES_OF_TEST) {
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
		this.testDataTargetList = new int [MAX_INSTANCES_OF_TEST];
	}
	
	

	public int [] predict () {
		
		IntStream.range(0, this.MAX_INSTANCES_OF_TEST).parallel().forEach(i -> {	
			HashMap <Integer, Float> kInstances  = new HashMap <Integer, Float>();
			
			IntStream.range(0, this.trainDataTargetList.length).forEach(j -> {	
				int thisLittleMF = -1;

				float distanceToLineInTrain = this.calculateEuclidianDistance(this.testData[i], this.trainData[j]);
				
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
							
			});
			
			this.testDataTargetList[i] = mode(kInstances.keySet(), this.k);
			kInstances.clear();

		});
			
		return this.testDataTargetList;
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
			if(this.testDataTargetList[i] == this.expectedTestTargetList[i]) {
				numberOfHits++;
			}
		}
		//System.out.println((float)numberOfHits / this.MAX_INSTANCES_OF_TEST);
		return (float)numberOfHits / this.MAX_INSTANCES_OF_TEST;
	}
	
	public int [] getExpectedAnswers () {
		 int returnArray [] = new int [this.MAX_INSTANCES_OF_TEST];
		 for(int i=0; i<this.MAX_INSTANCES_OF_TEST; i++) {
			 returnArray[i] = this.expectedTestTargetList[i];
		 }
		 return returnArray;
	 }
}
