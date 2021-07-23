import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class AtomicKnnClassifier {
	private int k; 
	private double [][] trainData;	
	private int [] trainDataTargetList;
	private AtomicIntegerArray testDataTargetList;
	private SortedMap <Float, Integer> temporaryIndexBuffer;
	private int syncSortingSignal;
	private Thread[] threads;

	
	public AtomicKnnClassifier(int n_neighbors, int n_instances_train, int n_instances_test) {

		this.k = n_neighbors;
		this.trainData = new double [n_instances_train][9];
		
		
		this.trainDataTargetList = new int [n_instances_train];
		this.testDataTargetList = new AtomicIntegerArray (n_instances_test);
		
		
		this.temporaryIndexBuffer = new TreeMap <Float, Integer>();
		this.syncSortingSignal = 0;
		
		threads = new Thread[2];

	}
	
	public void fit(double [][] dataset, int [] targetList) {
		this.trainDataTargetList = targetList;
		this.trainData = dataset;
		
	}
	
	
	public int [] predict (double [] [] data) {
		
		int FIRST_THREAD_INIT = 0;
		int SECOND_THREAD_INIT = this.trainDataTargetList.length/2 + 1;
	
			
			
		threads[0] = new Thread(new Runnable() {
			public void run() {
				System.out.println("Chamano a split na thread 1");
				AtomicKnnClassifier.this.predictSplited(FIRST_THREAD_INIT, data);	
			}
		});
		
		threads[1] = new Thread(new Runnable() {
			public void run() {
				System.out.println("Chamano a split na thread 2");
				AtomicKnnClassifier.this.predictSplited(SECOND_THREAD_INIT, data);	
			}
		});
	
		threads[0].start();
		threads[1].start();
		// To stop execution only when all threads ends their execution
		for(Thread t : threads) {		
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		int [] returnList = new int [this.testDataTargetList.length()];
		
		for(int i=0; i<this.testDataTargetList.length(); i++) {
			returnList[i] = this.testDataTargetList.get(i);
		}
		
		return returnList;
	}
	
	public void predictSplited (int startingIndex, double [][] data) {
		
		int finalIndex = this.trainDataTargetList.length;
		if(startingIndex == 0) finalIndex = trainDataTargetList.length/2;
		
		SortedMap <Float, Integer> sortedIndexes = new TreeMap <Float, Integer> ();
		
		SortedMap <Float, Integer> sortedX = new TreeMap <Float, Integer>();

		int index = 0;
		
		int instancePredicted = 0;
		for(double [] lineToPredict : data) {
			
			for(int j = startingIndex; j < finalIndex; j++) {
				
				sortedX.put(this.calculateEuclidianDistance(lineToPredict, this.trainData[j]), index);
				index++;
			}
			
			index = 0;
			
			int aux_index = 0;
			for(Entry<Float, Integer> entry: sortedX.entrySet()) {
				sortedIndexes.put(entry.getKey(), entry.getValue());
				aux_index++;
				if(aux_index >= this.k) break;
			}
			
			
			this.smartInsertion(instancePredicted, sortedIndexes);
			
			//this.testDataTargetList[instancePredicted] = mode(sortedIndexes.values(), this.k);
			instancePredicted++;
			sortedX.clear();
			sortedIndexes.clear();
			System.out.println("line: " +  instancePredicted + " - [" + (float)instancePredicted/20 + "]");
		}
			
//		return this.testDataTargetList;
	}
	
	private void smartInsertion(int recptorIndex, SortedMap <Float, Integer> indexes) {
		if(this.syncSortingSignal == 0) {
			
			this.testDataTargetList.set(recptorIndex, mode(indexes.values(), this.k)); 
	
			for(Entry<Float, Integer> entry : indexes.entrySet()) {
				this.temporaryIndexBuffer.put(entry.getKey(), entry.getValue());
			}

			this.syncSortingSignal = 1;
		}
		
		else {
			
			for(Entry<Float, Integer> entry : indexes.entrySet()) {
				this.temporaryIndexBuffer.put(entry.getKey(), entry.getValue());
			}
			//System.out.println(this.temporaryIndexBuffer);
//			System.out.println("signal = 1");
//			System.out.print(this.testDataTargetList.get(recptorIndex) + " ");
			
			this.testDataTargetList.set(recptorIndex, mode(this.temporaryIndexBuffer.values(), this.k));
			
			this.syncSortingSignal = 0;
			
			this.temporaryIndexBuffer.clear();
			
			
		}
		
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
