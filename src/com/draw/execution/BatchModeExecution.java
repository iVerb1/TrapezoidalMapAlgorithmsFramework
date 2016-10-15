package com.draw.execution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.draw.algorithms.Algorithm;
import com.draw.util.CircularList;
import com.draw.util.geometry.Point;
import com.draw.util.geometry.Trapezoid;

/**
 * @author ABF Ampt
 *
 */
public class BatchModeExecution
{

	/******************************
	 *           Fields           *
	 ******************************/

	/******************************
	 *        Constructors        *
	 ******************************/

	/**
	 * BatchModeExecution default constructor
	 */
	public BatchModeExecution()
	{
	}

	/******************************
	 *       Business Logic       *
	 ******************************/

	/**
	 * Execute the the algorithms in config.conf
	 */
	public void executeAlgorithms()
	{
		File confFile = new File("src/config.conf");
		//Algorithm algorithm;

		if (!confFile.exists())
		{
			System.out.println("Missing config.conf, no default executed");
			return;
		}

		Scanner scanner = null;
		String algorithmName = "";
		try
		{
			// create new Polygon
			scanner = new Scanner(confFile);

			if (scanner.hasNextLine())
			{
				algorithmName = scanner.nextLine().trim();
				if (!Main.algorithms.containsKey(algorithmName))
				{
					throw new IllegalArgumentException(
							"The algorithm in the config.conf file is not known");
				}
				//algorithm = Main.algorithms.get(algorithmName);
			}
			else
				throw new IllegalArgumentException(
						"config.conf is empty, no default executed");

			Publisher publisher = new Publisher()
			{
				@Override
				public void print(Object obj)
				{
					//System.out.print(obj);
				}

				@Override
				public void println(Object obj)
				{
					//System.out.println(obj);
				}

				@Override
				public void printStackTrace(Exception e)
				{
					//e.printStackTrace();
				}
			};
			
			List<Algorithm> algorithms = new ArrayList<Algorithm>();
			algorithms.add(Main.algorithms.get("Sweepline"));
			algorithms.add(Main.algorithms.get("Divide and Conquer"));
			algorithms.add(Main.algorithms.get("Incremental Construction - Randomized"));
			algorithms.add(Main.algorithms.get("Incremental Construction - Clockwise order - Semi Random"));
			algorithms.add(Main.algorithms.get("Incremental Construction - Leftmost segment first - Semi Random"));
			algorithms.add(Main.algorithms.get("Incremental Construction - Widest segment first - Semi Random"));

			if (scanner.hasNextLine())
			{
				do
				{
					String filePath = "";
					//System.out.println("-----#####  Start  #####-----");
					boolean success = true;
					File file = null;
					try{
					filePath = scanner.nextLine().trim();

					file = new File(filePath);
					if (!file.exists())
					{
						throw new IllegalArgumentException(
								"File in the config.conf file is not known");
					}
					}
					catch(Exception e)
					{
					}
					//System.out.println("File:       " + file.getAbsolutePath());
					Set<Trapezoid> result = new HashSet<Trapezoid>();
					ExecutionEnvironment e = new ExecutionEnvironment();
					CircularList<Point> polygon = e.readInput(file);

					System.out.print("random " + file.getName());
					System.out.print(" & ");
					System.out.print(polygon.size());
					for(Algorithm algorithm : algorithms)
					{
						success = true;
					try{
					result = e.executeAlgorithm(algorithm, publisher, polygon);
					//System.out.println("-----##### Results #####-----");
					//System.out.println("Points:     " + polygon.size());
					//System.out.println("Trapezoids: " + result.size());
					//System.out.println("Memory:     " + String.format("%.3f",
						//	e.getExecutionMemory() / (1024d * 1024d)) + " MB");
					//System.out.println("Time:       " + String.format("%.3f",
						//	e.getExecutionTime() / 1000.0) + " sec");

					}
					catch (Exception bro)
					{
						success = false;
						//System.err.println("Could not execute " + algorithmName + " for intput " + filePath);
					}
					//System.out.print(result.size());
					//System.out.print(" & ");
					//System.out.print(String.format("%.3f", e.getExecutionMemory() / 1024d / 1024d));
					System.out.print(" & ");
					if(success)
					{
						System.out.print(String.format("%.3f", e.getExecutionTime() / 1000d / 1000d));
					}
					else
						System.out.print("-");
					//System.out.print(" & ");
					//System.out.print(success ? "yes" : "no");
					//System.out.println("-----#####  Stop   #####-----");
					}
					System.out.println("\\\\ \\hline");
				} while (scanner.hasNextLine());
			}
			else
				throw new IllegalArgumentException(
						"Cannot open this config.conf file, not the right format");
		}
		catch (Exception ex)
		{
			System.err.println(ex.getMessage());
			PrintWriter writer = null;
			try
			{
				writer = new PrintWriter(confFile);
				writer.print("");
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			finally
			{
				writer.close();
			}
		}
		finally
		{
			scanner.close();
		}
	}

	/******************************
	 *      Getters & Setters     *
	 ******************************/
}
