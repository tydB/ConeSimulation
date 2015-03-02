import java.io.*;
import java.util.Random;
public class Simulation
{
	public static void main(String args[])
	{
		int dimensions = 2000; 		// default 1Km
		int numOfTrees = 150;		// default fiddy
		String location = "PH";		// default PH
		int maxCones = 1000000000; 	// default no limit
		int years = 60;			// default one year
		boolean variable = false;	// default constant cycle
		int mastRate = 3;			// default 3 years
		int density = -1;
		String fileName = "Data.txt";
		boolean yearlyInfo = true;
		for (int i = 0; i < args.length; ++i)
		{
			args[i] = args[i].toLowerCase();
			switch (args[i])
			{
				case "-s":
					dimensions = 1000 * Integer.valueOf(args[++i]);
					break;
				case "-t":
					numOfTrees = Integer.valueOf(args[++i]);
					break;
				case "-l":
					if (!args[i + 1].equals("KP") && !args[i + 1].equals("PH"))
						System.out.println("Location is unknown: using default settings");
					else if (args[++i].equals("KP"))
						location = "KP";
					break;
				case "-m":
					maxCones = Integer.valueOf(args[++i]);
					break;
				case "-y":
					years = Integer.valueOf(args[++i]);
					// System.out.println("Years: " + years);
					break;
				case "-i":
					if (!args[i + 1].equals("fixed") && !args[i + 1].equals("var"))
						System.out.println("Mast year variability unknown: using defualts");
					else if(args[++i].equals("var"))
						variable = true;
					mastRate = Integer.valueOf(args[++i]);
					break;
				case "-d":
					density = Integer.valueOf(args[++i]);
					break;
				case "-o":
					if (args[i + 1].equals("year"))
						yearlyInfo = true;
					else if (args[++i].equals("tree"))
						yearlyInfo = false;
					else
						System.out.println("Unknown output type: using defualts");
					break;
				case "-f":
					fileName = args[++i];
					break;
				case "-h":
					printHelp();
					System.exit(0);
				default: break;
			}
		}
		if (density > 0)
			numOfTrees = (density * (dimensions / 1000));
		// print off current simulation parameters
		// add pound
		System.out.println("Simulation Parameters" + 
							"\n  Area:      " + (dimensions / 1000) + "km^2" +
							"\n  Trees:     " + numOfTrees +
							"\n  Years:     " + years + 
							"\n  MastCycle: " + mastRate +
							"\n  Location:  " + location +
							"");

		FileParser info = new FileParser("../Data/coneData.txt");
		Area area = new Area(dimensions, info.getDataByMetaPop(location, maxCones));
		info.close();
		area.setPineTrees(numOfTrees);
		try
		{
			PrintWriter writer = new PrintWriter(fileName, "UTF-8");
			if (yearlyInfo)
				writer.println("source\tyear\tmastYear\ttrees\tconesProd\tconesEaten\tconeEscape\tcaches\tseedlings");
			else
				writer.println("source\tconesProd");
			int lastMastYear = 0;
			boolean needReset = true;
			int currentMastRate = mastRate;
			Random rand = new Random();
			int index = 0;
			for(int i = 0; i < years; ++i)
			{
				// System.out.println("WOrking on year: " + (i + 1));
				String s = "";
				boolean isMastYear = false;
				if (variable && needReset)
				{
					// System.out.println("Reseting Mast Year: " + i + ", " + lastMastYear);
					// set mast year to average +- 1 year
					double r = rand.nextDouble();
					if (r < 0.75)
						currentMastRate = mastRate + 1;
					if (r > 0.25)
						currentMastRate = mastRate - 1;
					else
						currentMastRate = mastRate;
					needReset = false;
				}
				else if (i - lastMastYear == currentMastRate)
				{
					// System.out.println("Is mast year");
					lastMastYear = i + 1;
					isMastYear = true;
					s = "M";
					needReset = true;
				}

				area.Year(isMastYear);
				if (yearlyInfo) {
					// writer.println("\tsource\tyear\ttrees\tconesProd\tconesEaten\tcaches\tseedlings");
					// index
					writer.print((index++) + "\t");
					// source
					writer.print("Simulation" + "\t");
					// year
					writer.print((i + 1) + "\t");
					// isMastYear
					writer.print(isMastYear + "\t");
					// num of trees
					writer.print(area.getTreeCount() + "\t");
					// conesprod
					writer.print(area.getYearCones() + "\t");
					// cones eaten
					writer.print(area.getYearConesEaten() + "\t");
					// coneEscape
					writer.print(1 - ((double)area.getYearConesEaten() / (double)area.getYearCones()) + "\t");
					// caches
					writer.print(area.getCacheCount() + "\t");
					// seedlings
					writer.println(area.getSeedlingCount());
				}
				else
				{
					int[] numbers = area.conesProducePerTree();

					for(int j = 0; j < area.getTreeCount(); ++j)						// should get end value from area object
					{
						// writer.println("\tsource\tyear\ttrees\tconesProd\tconesEaten\tcaches\tseedlings");
						// index
						writer.print((index++) + "\t");
						// source
						writer.print("Simulation" + "\t");
						// year
						// writer.print((i + 1) + "\t");
						// num of trees
						// writer.print(area.getTreeCount() + "\t");
						// conesprod
						writer.print(numbers[j] + "\n");
						// cones eaten

						// caches
						// writer.print(area.getCacheCount() + "\t");
						// seedlings
						// writer.println(area.getSeedlingCount());
						// need to write num of trees, cones produced, cones eaten, caches, seedlings year num
					}
				}
			}
			writer.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public static void printHelp()
	{
		System.out.println("\nOption input\n   description\n");
		System.out.println("-S int --default 2km\n   the size of the simulated area in kilometers squared.\n");
		System.out.println("-T int --default 150\n   the number of trees in the simulated area\n");
		System.out.println("-L str --default PH\n   {PH|KP} determines which data to uses for cone production\n");
		System.out.println("-M int --default no limit\n   the max number of cones a tree can produce\n");
		System.out.println("-Y int --default 1\n   the total years the simulation will run for\n");
		System.out.println("-I str int --default fixed 3\n   either 'fixed' or 'var' and the average number of years between mast years\n");
		System.out.println("-D int --default not set\n   three density in trees/km^2\n");
		System.out.println("-O str --default year\n   {tree|year} tree gives info about trees. year gives info about each year\n");
		System.out.println("-F str --default Data.txt\n   file name including suffix\n");
	}
}