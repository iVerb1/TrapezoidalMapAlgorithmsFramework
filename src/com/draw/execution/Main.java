package com.draw.execution;

import java.util.LinkedHashMap;
import java.util.Map;

import com.create.PolygonBuilder;
import com.draw.algorithms.Algorithm;
import com.draw.algorithms.divideandconquer.DivideAndConquerAlgorithm;
import com.draw.algorithms.incrementalconstruction.IncrementalConstructionAlgorithm;
import com.draw.algorithms.sweepline.SweeplineAlgorithmV2;
import com.draw.view.View;

/**
 * @author ABF Ampt
 *
 */
public class Main
{

	private static boolean batchMode = true;
	public static Map<String, Algorithm> algorithms = setAlgorithms();

	/**
	 * Set the algorithms that may be used
	 * The algorithms are displayed in the algorithm menu
	 */
	private static Map<String, Algorithm> setAlgorithms()
	{
		Map<String, Algorithm> algorithms = new LinkedHashMap<String, Algorithm>();

		algorithms.put("Sweepline", new SweeplineAlgorithmV2());
		algorithms.put("Divide and Conquer", new DivideAndConquerAlgorithm());
		algorithms.put("Incremental Construction - Randomized", new IncrementalConstructionAlgorithm(IncrementalConstructionAlgorithm.MODE.random, false));		
		algorithms.put("Incremental Construction - Clockwise order", new IncrementalConstructionAlgorithm(IncrementalConstructionAlgorithm.MODE.insertion, false));
		algorithms.put("Incremental Construction - Clockwise order - Semi Random", new IncrementalConstructionAlgorithm(IncrementalConstructionAlgorithm.MODE.insertion, true));
		algorithms.put("Incremental Construction - Leftmost segment first", new IncrementalConstructionAlgorithm(IncrementalConstructionAlgorithm.MODE.left_to_right, false));
		algorithms.put("Incremental Construction - Leftmost segment first - Semi Random", new IncrementalConstructionAlgorithm(IncrementalConstructionAlgorithm.MODE.left_to_right, true));
		algorithms.put("Incremental Construction - Widest segment first", new IncrementalConstructionAlgorithm(IncrementalConstructionAlgorithm.MODE.widest_first, false));
		algorithms.put("Incremental Construction - Widest segment first - Semi Random", new IncrementalConstructionAlgorithm(IncrementalConstructionAlgorithm.MODE.widest_first, true));
		//algorithms.put("Sweepline", new SweeplineAlgorithm());
		//algorithms.put("Example", new ExampleAlgorithm(false));

		return algorithms;
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[])
	{
		
		if (batchMode)
		{
			new BatchModeExecution().executeAlgorithms();
		}
		else
		{
			/* Create and display the form */
			java.awt.EventQueue.invokeLater(new Runnable()
			{
				public void run()
				{
					new View().setVisible(true);
				}
			});
		}
	}
}
