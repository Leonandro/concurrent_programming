import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.Map;  
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Comparator;
import java.lang.Math;



public class KnnClassifier {
	
	private int k; 
	private double [][] trainData;	
	private int []  trainDataTargetList;
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
		
		//System.out.println("[TRAIN_SIZE]: " + dataset.length());
		//System.out.println("[1ST_TRAIN_SIZE]: " + dataset[0].length);
	}
	
	public int [] predict (double [][] data) {
		HashMap <Integer, Float> distances = new HashMap <Integer, Float>();
		HashMap <Integer, Float> sortedDistances = new HashMap <Integer, Float>();
		Set <Integer> sortedClasses = sortedDistances.keySet();
		int index = 0;
		
		int instancePredicted = 0;
		for(double [] lineToPredict : data) {
			
			for(double [] lineToCompare : this.trainData) {
				distances.put(index, this.calculateEuclidianDistance(lineToPredict, lineToCompare));
				index++;
			}
			index = 0;
			sortedDistances = this.sortByValue(distances);
			sortedClasses = sortedDistances.keySet();
			distances.clear();
			this.testDataTargetList[instancePredicted] = trainDataTargetList[mode(sortedClasses, this.k)];
			instancePredicted++;
			//System.out.println("[MODE]: " + mode(sortedClasses, this.k) + " [TRAIN]: " + this.trainDataTargetList.get(mode(sortedClasses, this.k)));
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
	

	 private HashMap<Integer, Float> sortByValue(HashMap<Integer, Float> unsortedDistances)
	    {
		 HashMap<Integer, Float> sortedDistances = new HashMap<Integer, Float>(); 
		 int indexToBeSwaped = -1;
		 
		 for(int indexOnUnsorted : unsortedDistances.keySet()) {
			 indexToBeSwaped = -1;
			 
			 // Caso o hash ordenado ainda não tenha um número mínimo de instancias
			 if(sortedDistances.size() <= this.k) {
				 sortedDistances.put(indexOnUnsorted, unsortedDistances.get(indexOnUnsorted));
			 }
			 
			 else {
				 
				 for(int indexOnSorted : sortedDistances.keySet()) {
					 
					 if(sortedDistances.get(indexOnSorted) > unsortedDistances.get(indexOnUnsorted)) {
						 
						 if(indexToBeSwaped == -1) { 
							 indexToBeSwaped = indexOnSorted;
						 }
						 
						 else {
							 
							 if(sortedDistances.get(indexToBeSwaped) > sortedDistances.get(indexOnSorted)) {
								 indexToBeSwaped = indexOnSorted;
							 }
						 }
						 
					 }
				 }
				 
				 if(indexToBeSwaped != -1) {
					 sortedDistances.remove(indexToBeSwaped);
					 sortedDistances.put(indexOnUnsorted, unsortedDistances.get(indexOnUnsorted));
				 }
				 
			 }
			 
			
		 }
		 
//	        // Create a list from elements of HashMap
//	        List<Map.Entry<Integer, Float> > list =
//	               new LinkedList<Map.Entry<Integer, Float> >(hm.entrySet());
//	 
//	        // Sort the list
//	        Collections.sort(list, new Comparator<Map.Entry<Integer, Float> >() {
//	            public int compare(Map.Entry<Integer, Float> o1,
//	                               Map.Entry<Integer, Float> o2)
//	            {
//	                return (o1.getValue()).compareTo(o2.getValue());
//	            }
//	        });
//	         
//	        // put data from sorted list to hashmap
//	        HashMap<Integer, Float> temp = new LinkedHashMap<Integer, Float>();
//	        for (Map.Entry<Integer, Float> aa : list) {
//	            temp.put(aa.getKey(), aa.getValue());
//	        }
//	        return temp;
		 
		 	return sortedDistances;
	    }
 	 
	 private int mode(Set <Integer> list, int k) {
		    int maxValue = 0; 
		    int maxCount = 0;
		    
		    int n = list.size();
		    Object [] a = new Object[n];
		    a = list.toArray();
		    
		    for (int i = 0; i <k; i++) {
		        int count = 0;
		        for (int j = 0; j < a.length; ++j) {
		            if (a[j] == a[i]) ++count;
		        }
		        if (count > maxCount) {
		            maxCount = count;
		            maxValue = (int) a[i];
		        }
		    }

		    return maxValue;
	 }

}
