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


	
	public KnnClassifier(int n_neighbors, int n_instances_train, int n_instances_test) {
		this.k = n_neighbors;
		this.trainData = new double [n_instances_train][9];
		this.testData = new double [n_instances_test][9];
		
		this.trainDataTargetList = new int [n_instances_train];
		this.testDataTargetList = new int [n_instances_test];
	}
	
	public void fit(double [][] dataset, int [] targetList) {
		this.trainDataTargetList = targetList;
		this.trainData = dataset;
		
	}
	
	public int [] predict (double [][] data) {
		HashMap <Integer, Float> kInstances = new HashMap <Integer, Float>();

		int index = 0;
		
		int instancePredicted = 0;
		int thisLittleMF = -1;
		for(double [] lineToPredict : data) {
			
			for(int j = 0; j < this.trainDataTargetList.length; j++) {
				float distanceToLineInTrain = this.calculateEuclidianDistance(lineToPredict, this.trainData[j]);
				
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
			//System.out.println("Size of [SortedX]: " + sortedX.size() + " / Size of [hue]: " + kInstances.size() + "[PREDICT] - " + this.testDataTargetList[instancePredicted-1]);
			kInstances.clear();


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
}
