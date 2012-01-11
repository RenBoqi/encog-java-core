/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.ml.hmm.distributions;

import java.util.Arrays;
import java.util.Random;

import org.encog.mathutil.matrices.Matrix;
import org.encog.mathutil.matrices.MatrixMath;
import org.encog.mathutil.matrices.decomposition.CholeskyDecomposition;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.util.EngineArray;

public class ContinousDistribution
implements StateDistribution
{	
	final private int dimension;
	final private double[] mean;
	final private Matrix covariance;
	private Matrix covarianceL = null; 
	private Matrix covarianceInv = null;
	private double covarianceDet;
	private final static Random randomGenerator = new Random();
	private CholeskyDecomposition cd;

	public ContinousDistribution(int dimension)
	{
		if (dimension <= 0)
			throw new IllegalArgumentException();
		
		this.dimension = dimension;
		mean = new double[dimension];
		covariance = new Matrix(dimension,dimension);

	}
	
	public ContinousDistribution(double[] mean, double[][] covariance)
	{		
		dimension = covariance.length;		
		this.mean = EngineArray.arrayCopy(mean);
		this.covariance = new Matrix(covariance);
		update(covariance);
	}
		
	public double probability(MLDataPair o)
	{
		double[] v = o.getInputArray();
		Matrix vmm = Matrix.createColumnMatrix( EngineArray.subtract(v, mean) );
		Matrix t = MatrixMath.multiply(this.covarianceInv, vmm);
		double expArg = MatrixMath.multiply(MatrixMath.transpose(vmm),t).get(0,0) * -0.5;		
		return Math.exp(expArg) / (Math.pow(2.0 * Math.PI, ((double) dimension) / 2.0) * 
				Math.pow(this.covarianceDet, 0.5));
	}
	
	
	public MLDataPair generate()
	{
		double[] d = new double[dimension];
		
		for (int i = 0; i < dimension; i++)
			d[i] = randomGenerator.nextGaussian();
		
		double[] d2 = MatrixMath.multiply(this.covarianceL, d);
		return new BasicMLDataPair(new BasicMLData(EngineArray.add(d2, mean)));
	}
	
	public void fit(MLDataSet co)
	{
		double[] weights = new double[co.size()];
		Arrays.fill(weights, 1. / co.size());
		
		fit(co, weights);
	}
	
	public void fit(MLDataSet co, 
			double[] weights)
	{
		if (co.size()<1 || co.size() != weights.length)
			throw new IllegalArgumentException();
		
		// Compute mean
		double[] mean = new double[this.dimension];
		for (int r = 0; r < this.dimension; r++) {
			int i = 0;
			
			for (MLDataPair o : co)
				mean[r] += o.getInput().getData(r) * weights[i++];
		}
		
		// Compute covariance
		double[][] covariance = new double[this.dimension][this.dimension];
		int i = 0;
		for (MLDataPair o : co) {
			double[] obs = o.getInput().getData();
			double[] omm = new double[obs.length];
			
			for (int j = 0; j < obs.length; j++)
				omm[j] = obs[j] - mean[j];
			
			for (int r = 0; r < this.dimension; r++)
				for (int c = 0; c < this.dimension; c++)
					covariance[r][c] += omm[r] * omm[c] * weights[i];
			
			i++;
		}
		
		update(covariance);
	}
	
	
	public ContinousDistribution clone()
	{
		try {
			return (ContinousDistribution) super.clone();
		} catch(CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
	}

	public void update(double[][] covariance) {
		this.cd = new CholeskyDecomposition(new Matrix(covariance));
		this.covarianceL = cd.getL();
		this.covarianceInv = cd.inverseCholesky();
		this.covarianceDet = cd.getDeterminant();
	}
}
