package KNNBenchmark.KnnTesting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Map;  
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.lang.Math;



public class KnnClassifier {
	
	private int k; 
	private double [][] trainData;	
	private int [] trainDataTargetList;
	private double [][] testData;
	private int [] testDataTargetList;
	private int [] expectedTestTargetList;
	private int MAX_INSTANCES_OF_TEST;


	
	public KnnClassifier(int n_neighbors, int n_instances_train, int n_instances_test, int MAX_INSTANCES_OF_TEST) {
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
		HashMap <Integer, Float> kInstances = new HashMap <Integer, Float>();

		int index = 0;
		
		int instancePredicted = 0;
		int thisLittleMF = -1;
		for(int i=0; i<this.MAX_INSTANCES_OF_TEST; i++) {
			
			for(int j = 0; j < this.trainDataTargetList.length; j++) {
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
							
			}
			
			this.testDataTargetList[instancePredicted] = mode(kInstances.keySet(), this.k);
			instancePredicted++;
			kInstances.clear();
			System.out.println("Predict [" + instancePredicted + "] " + (float)100*instancePredicted/this.MAX_INSTANCES_OF_TEST + "%");

		}
			
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
}
