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

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;




public class MyBenchmark {
	
	@State(Scope.Benchmark)
    public static class SerialState {
		@Param({"5"})
	    public int k;
		public KnnClassifier knn;
		
        @Setup(Level.Trial)
        public void doSetup() {
        	System.out.println("----------------[SetUp]----------------");
        	knn = new KnnClassifier(k, 7526883, 1742866, 500);

        }

        @TearDown(Level.Trial)
        public void doTearDown() {
            System.out.println("----------------[TearDown]----------------");
        }
    	
	    @Benchmark 
	    @Fork(value=1)
	    @Warmup(iterations = 3) 
	    @BenchmarkMode(Mode.Throughput)
	    @OutputTimeUnit(TimeUnit.MINUTES)
	    public void testSerialVersion(SerialState Serial_state) {
		
	    	Serial_state.knn.predict();
				
	    }
	}
	
	@State(Scope.Benchmark)
    public static class MutexState {
		@Param({ "5"})
	    public int k;
		public MutexKnnClassifier knn;
		
        @Setup(Level.Trial)
        public void doSetup() {
        	System.out.println("----------------[SetUp]----------------");
        	knn = new MutexKnnClassifier(k, 7526883, 1742866, 500, 2);

        }

        @TearDown(Level.Trial)
        public void doTearDown() {
            System.out.println("----------------[TearDown]----------------");
        }
    	



	    @Benchmark 
	    @Fork(value=1)
	    @Warmup(iterations = 3) 
	    @BenchmarkMode(Mode.Throughput)
	    @OutputTimeUnit(TimeUnit.MINUTES)
	    public void testMutexVersion(MutexState Mutex_state) {
		
	    	Mutex_state.knn.predict();
				
	    }
    
	    @State(Scope.Benchmark)
	    public static class AtomicState {
			@Param({ "5"})
		    public int k;
			public AtomicKnnClassifier knn;
			
	        @Setup(Level.Trial)
	        public void doSetup() {
	        	System.out.println("----------------[SetUp]----------------");
	        	knn = new AtomicKnnClassifier(k, 7526883, 1742866, 500, 2);

	        }

	        @TearDown(Level.Trial)
	        public void doTearDown() {
	            System.out.println("----------------[TearDown]----------------");
	        }
	    	



		    @Benchmark 
		    @Fork(value=1)
		    @Warmup(iterations = 3) 
		    @BenchmarkMode(Mode.Throughput)
		    @OutputTimeUnit(TimeUnit.MINUTES)
		    public void testAtomicVersion(AtomicState Atomic_state) {
			
		    	Atomic_state.knn.predict();
					
		}
	
	}
	
    
    }

}
