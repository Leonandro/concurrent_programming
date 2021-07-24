import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;;

public class MutexKnnClassifier {
	private int k; 
	private double [][] trainData;	
	private int [] trainDataTargetList;
	private int [] testDataTargetList;
	private ReentrantLock mutex;
	private Thread[] threads;
	private int n_threads;
	
	public MutexKnnClassifier(int n_neighbors, int n_instances_train, int n_instances_test, int n_threads) {

		this.k = n_neighbors;
		this.trainData = new double [n_instances_train][9];
		
		this.trainDataTargetList = new int [n_instances_train];
		this.testDataTargetList = new int [n_instances_test];
		
		this.n_threads = n_threads;
		threads = new Thread[n_threads];
		this.mutex = new ReentrantLock ();
	}
	
	public void fit(double [][] dataset, int [] targetList) {
		this.trainDataTargetList = targetList;
		this.trainData = dataset;
		
	}
	
	
	public int [] predict (double [] [] data) {
		int N_PARTITION_SIZE = (this.testDataTargetList.length / this.n_threads);
		// 20 / 3 = 6,667 => 6
		// 0..5
		// 6..11 
		// 12..17 
	
		for(int i=0; i<this.n_threads; i++) {
			int init = (N_PARTITION_SIZE*i);
			int end = N_PARTITION_SIZE*(i+1);
			
			threads[i] = new Thread(new Runnable() {
				public void run() {
					MutexKnnClassifier.this.predictSplited(init, end, data);	
				}
			});
		}
			
		
		for(Thread t : threads) {
			t.start();
		}
		

		// To stop execution only when all threads ends their execution
		for(Thread t : threads) {		
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return this.testDataTargetList;
	}
	
	public void predictSplited (int startingIndex, int finalIndex, double [][] data) {
		int N_PARTITION_SIZE = (this.testDataTargetList.length / this.n_threads);
		
		//The last partition have to represent all the rest of the data [P1, P2,...Pn + {the rest}]
		if(finalIndex == N_PARTITION_SIZE*(this.n_threads)) finalIndex = this.testDataTargetList.length;
		
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
			
			
			this.mutex.lock();
//			this.smartInsertion(instancePredicted, kInstances);
			this.testDataTargetList[i] = mode(kInstances.keySet(), this.k);
			this.mutex.unlock();
			
			
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
