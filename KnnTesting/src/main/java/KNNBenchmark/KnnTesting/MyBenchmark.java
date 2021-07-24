/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package KNNBenchmark.KnnTesting;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import java.util.concurrent.TimeUnit;




public class MyBenchmark {
	

 
	@State(Scope.Thread)
	@OutputTimeUnit(TimeUnit.SECONDS)
    public static class MyState {
		@Param({ "5", "7", "10"})
	    public int k;
		
        @Setup(Level.Trial)
        public void doSetup() {
        	System.out.println("-----------------[SetUp]-----------------");
        	trainReader.load();
    		testReader.load();
            test = testReader.getTrain();
            TEST_ANSWERS = testReader.getOutcomes();
        }

        @TearDown(Level.Trial)
        public void doTearDown() {
        	trainReader.clear();
    		testReader.clear();
            System.out.println("----------------[TearDown]----------------");
        }
    	public CSVReader trainReader = new CSVReader("/home/leonandro/Codes/java/programação_concorrente/datasets/diabetes.csv", 7526883);
		public CSVReader testReader = new CSVReader("/home/leonandro/Codes/java/programação_concorrente/datasets/test_diabetes.csv", 20);
		public double [][] test = new double [20][9]; 
		public int [] TEST_ANSWERS = new int [20];
		public int [] KNN_ANSWERS = new int [20];
	}



    @Benchmark 
    @Warmup(iterations = 3) 
    @BenchmarkMode(Mode.Throughput) 
    public void testAtomicVersion(MyState state) {
	
		AtomicKnnClassifier knn = new AtomicKnnClassifier(state.k, 7526883, 20, 2);
		
		knn.fit(state.trainReader.getTrain(), state.trainReader.getOutcomes());
		
		state.KNN_ANSWERS = knn.predict(state.test);
		

    }

}
