package ufrn;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.*;

import java.util.Map;  
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.lang.Math;



public class SparkKnnClassifier {
	
	private int k; 
	static private double [][] trainData;	
	static private int [] trainDataTargetList;
	static private double [][] testData;
	static private int [] testDataTargetList;
	private int [] expectedTestTargetList;
	private int MAX_INSTANCES_OF_TEST;

	
	public SparkKnnClassifier(int n_neighbors, int n_instances_train, int n_instances_test, int MAX_INSTANCES_OF_TEST) {
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

		List<Integer> l = new ArrayList<>(this.MAX_INSTANCES_OF_TEST);
		for (int i = 0; i < this.MAX_INSTANCES_OF_TEST; i++) {
		  l.add(i);
		}
		
		int trainLength = trainDataTargetList.length;
		int k_ = this.k;
		
		SparkConf conf = new SparkConf().setAppName("nomeApp").setMaster("local[2]");
    	JavaSparkContext sc = new JavaSparkContext(conf);
		JavaRDD<Integer> linesToBePredicted = sc.parallelize(l);
		linesToBePredicted.foreach(i-> {
			int thisLittleMF = -1;
			for(int j = 0; j < trainLength; j++) { 
				float distanceToLineInTrain = 200;  
				
				//{IMPLEMENTAÇÃO EXPLCITRA DA FUNÇÃO calculateEuclidianDistance()}
				double sum = 0f;
				
				for(int l_index=0; l_index < 9; l_index++) {
					sum += (testData[i][l_index] - trainData[j][l_index]) * (testData[i][l_index] - trainData[j][l_index]);
				}	
				//Fim da implementação
				
				distanceToLineInTrain =  (float)Math.sqrt(sum);
				
				if(kInstances.size() < k_) { // this.k
					kInstances.put(j, distanceToLineInTrain);
				}
				
				else {
					
					// {IMPLEMENTAÇÃO EXPLCITRA DA FUNÇÃO isSmallerThanSomeone()}
					int smallerValueIndex = -1;
					float smallerValue = 9999999;
					for(int a : kInstances.keySet()) {
						if(distanceToLineInTrain < kInstances.get(a)) {
							if(distanceToLineInTrain < smallerValue) {
								smallerValueIndex = a;
								smallerValue = kInstances.get(a);
							}
						}
					}
					//Fim da implementação
					
					thisLittleMF = smallerValueIndex;
					
					if(thisLittleMF != -1) {
						kInstances.remove(thisLittleMF);
						kInstances.put(j, distanceToLineInTrain);
					}
				}
							
			}
			
			// {IMPLEMENTAÇÃO EXPLICITA DA FUNÇÃO mode()}
			int maxValue = 0; 
		    int maxCount = 0;
		    
		    int n = kInstances.keySet().size();
		    Object [] a = new Object[n];
		    a = kInstances.keySet().toArray();
		    
		    for (int mode_index = 0; mode_index  < k_ ; mode_index++) {
		    	//System.out.print(a[i] + " - ");
		        int count = 0;
		        for (int j_mode = 0; j_mode < a.length; ++j_mode) {
		            if (trainDataTargetList[(int)a[j_mode]] == trainDataTargetList[(int)a[mode_index]]) ++count;
		        }
		        if (count > maxCount) {
		            maxCount = count;
		            maxValue = trainDataTargetList[(int)a[mode_index]];
		        }
		    }
		    
		    testDataTargetList[i] = maxValue;
		    
			kInstances.clear();
		});
		
    	sc.close();
		return testDataTargetList;
	}
	
	
	public float getAccuracy () {
		int numberOfHits = 0;
		
		for (int i = 0; i<this.MAX_INSTANCES_OF_TEST; i++) {
			if(testDataTargetList[i] == this.expectedTestTargetList[i]) {
				numberOfHits++;
			}
		}
		//System.out.println((float)numberOfHits / this.MAX_INSTANCES_OF_TEST);
		return (float)numberOfHits / this.MAX_INSTANCES_OF_TEST;
	}
}
