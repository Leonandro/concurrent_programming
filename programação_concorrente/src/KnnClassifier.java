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
		SortedMap <Float, Integer> sortedX = new TreeMap <Float, Integer>();
		SortedMap <Float, Integer> sortedIndexes = new TreeMap <Float, Integer> ();

		int index = 0;
		
		int instancePredicted = 0;
		for(double [] lineToPredict : data) {
			
			for(double [] lineToCompare : this.trainData) {
	
				sortedX.put(this.calculateEuclidianDistance(lineToPredict, lineToCompare), index);
				index++;
			}
			
			index = 0;
			
			int aux_index = 0;
			for(Entry<Float, Integer> entry: sortedX.entrySet()) {
				sortedIndexes.put(entry.getKey(), entry.getValue());
				aux_index++;
				if(aux_index >= this.k) break;
			}
			
			this.testDataTargetList[instancePredicted] = mode(sortedIndexes.values(), this.k);
			instancePredicted++;
			sortedX.clear();
			System.out.println("line: " +  instancePredicted + " - [" + (float)instancePredicted/20 + "]");
		}
			
		return this.testDataTargetList;
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
