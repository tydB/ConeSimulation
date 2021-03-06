import java.util.*;

public class Area
{

	private Soil[][] area;
	private Metapop metapop;
	private int dimension;
	private int yearCones = 0;
	private int conesEaten = 0;
	private int amountOfTrees = 0;
	private int iterations = 100;
	private int yearSeeds = 0;
	private double minSquirrel = 74.0;
	private double maxSquirrel = 89.0;
	private int minSeedsPerCone = 40;
	private int maxSeedsPerCone = 80;
	private int coneTreshold = 1000;


	//input the dimension in meter square
	//Area constructor, takes in the information needed simulate a year
	public Area(Soil[][] inputArea,Metapop inputMeta,int inputDimension,int inputYearCones,int inputConesEaten,int inputAmountOfTrees,
				 int inputIterations, int inputConeTreshold, double inputMin, double inputMax)
	{
		metapop = inputMeta;
		area = inputArea;
		dimension = inputDimension;
		yearCones = inputYearCones;
		conesEaten = inputConesEaten;
		amountOfTrees = inputAmountOfTrees;
		iterations = inputIterations;
		coneTreshold = inputConeTreshold;
		minSquirrel = inputMin;
		maxSquirrel = inputMax;
	}

	//Area constructor use to simulate the run of one single year several times
	public Area(int dimension, Metapop metapop,double minSquirrel, double maxSquirrel, int coneTreshold, int minSeedsPerCone, int maxSeedsPerCone)
	{
		this.dimension = dimension/((new Soil()).getDimension());
		area = new Soil[this.dimension][this.dimension];
		this.minSquirrel = minSquirrel;
		this.maxSquirrel = maxSquirrel;
		this.minSeedsPerCone = minSeedsPerCone;
		this.maxSeedsPerCone = maxSeedsPerCone;
		this.coneTreshold = coneTreshold;
		initiate();
		this.metapop = metapop;
	}

	//Function use to initiate the area array with new soil
	private void initiate()
	{
		for(int i = 0; i < dimension; i++)
		{
			for(int j = 0; j < dimension; j++)
			{
				area[i][j] = new Soil();
			}
		}
	}

	//public function use to set the given amount of pine trees into the array, the trees are placed randomly
	public void setPineTrees(int amount)
	{
		amountOfTrees = amount;
		Random random = new Random();
		for(int i = 0; i < amount; i++)
		{
			int x = random.nextInt(dimension);
			int y = random.nextInt(dimension);

			while(area[x][y].containsPine())
			{
				x = random.nextInt(dimension);
				y = random.nextInt(dimension);
			}
			area[x][y] = new LimberPine(100);
		}
	}

	//public function use to simulate one year of time in the area
	//takes in a boolean value to decide whether or not the year is a mast year
	public void Year(boolean mast)
	{
		checkProgress();
		int tempCones = 0;
		int tempConesEaten = 0;
		int seedlings = 0;
		// int caches = 0;
		// int seeds = 0;

		for(int i = 0; i < iterations; i++)
		{
			// System.out.println("asda");
			Area tempArea = new Area(area,metapop,dimension,yearCones,conesEaten,
				amountOfTrees, iterations, coneTreshold,minSquirrel,maxSquirrel);
			tempArea.runYear(mast);
			tempCones += tempArea.getYearCones();
			tempConesEaten += tempArea.getYearConesEaten();
			// seedlings += tempArea.getSeedlingCount();
			// caches += tempArea.getCacheCount();
		}

		yearCones = (int)(tempCones/iterations);
		conesEaten = (int)(tempConesEaten/iterations);
		// caches = (int)(caches/iterations);
		// seedlings = (int)(seedlings/iterations);
		// if(seedlings <)
		if((yearCones-conesEaten) >= coneTreshold)
		{
			nutCrackerBehavior((double)((double)(tempConesEaten)/(double)(tempCones)));
		}
		eatCaches();
	}

	//function for cone production and other for a year in a given area
	//takes in a boolean value to decide whether or not is a mast year
	public void runYear(boolean mast)
	{
		// incrementAge();
		yearCones = 0;
		if(mast)
		{	
			for(int i = 0; i < dimension; i++)
			{
				for(int j = 0; j < dimension; j++)
				{
					if(area[i][j].containsPine())
					{
						area[i][j].produceCones(mast,metapop);
						yearCones += area[i][j].amountCones();
					}
				}
			}
			double totalPercentage = percentange(45.0, 60.0)/100;
			double squirellPercentage = percentange(40.0, (totalPercentage*100))/100;
			conesEaten = (int)(yearCones * totalPercentage);
			// System.out.println(yearCones);
		}
		else
		{
			for(int i = 0; i < dimension; i++)
			{
				for(int j = 0; j < dimension; j++)
				{
					if(area[i][j].containsPine())
					{
						area[i][j].produceCones(mast,metapop);
						yearCones += area[i][j].amountCones();
					}
				}
			}
			double totalPercentage = percentange(minSquirrel, maxSquirrel)/100;
			conesEaten = (int)(yearCones * totalPercentage);
		}
	}

	//private function use to simulate the nutcracker behavior when taking seeds and spreading them
	//takes in the percentage of cones taken by the squirrells to calculate the amount of cones left for the nutcrackers
	private void nutCrackerBehavior(double squirrellPercentage)
	{
		int nutcrackerCones = (int)(yearCones*percentange(squirrellPercentage,90.0));
		// System.out.println(nutcrackerCones);
		for(int i =0; i < nutcrackerCones; i++)
		{
			// System.out.println(seeds());	
			yearSeeds += seeds(minSeedsPerCone,maxSeedsPerCone);
		}
		createCaches(yearSeeds);
	}

	//function use to simulate the creation of caches by the nutcrackers after they've collected the seeds
	//takes in how many seeds are to be divided into caches
	private void createCaches(int seeds)
	{
		Random random = new Random();
		random.setSeed(-1);
		while(seeds > 0)
		{
			int x = random.nextInt(dimension);
			int y = random.nextInt(dimension);
			
			int seed= seeds(1,5);
			// System.out.println(seed);
			double perc = percentange(0.0,100.0);
			if((!area[x][y].containsPine()) && (!area[x][y].isCache()) && (!area[x][y].isSeedling()))
			{
				area[x][y] = new Cache(seed);
			}
			seeds -= seed;
			// System.out.println(seeds);
		}	
	}

	//public function use to increment the age of all valid areas within the array
	public void incrementAge()
	{
		for(int i =0; i < area.length; i++)
		{
			for(int j =0; j < area[i].length; j++)
			{
				if(area[i][j].isCache() || area[i][j].containsPine() || area[i][j].isSeedling())
				{
					area[i][j].incrementAge();
				}
			}
		}
	}

	//function used to simulate the nutcrackers behavior when eating caches
	private void eatCaches()
	{
		for(int i =0; i < area.length; i++)
		{
			for(int j =0; j < area[i].length; j++)
			{
				if(area[i][j].isCache() && (percentange(0.0,100.0) <= 75.0))
				{
					int seedEaten = seeds(0,area[i][j].getSeed());
					area[i][j].nutCrackerFeed(seedEaten);
					if(!area[i][j].hasSeeds())
					{
						area[i][j] = new Soil();
					}
				}
			}
		}
	}

	//public function use to return the amount of cones produce per tree
	//returns each value in an integer array
	public int[] conesProducePerTree()
	{
		int cones[] = new int[getTreeCount()];
		int counter  = 0;
		for(int i = 0; i < dimension; i++)
		{
			for(int j = 0; j < dimension; j++)
			{
				if(area[i][j].containsPine())
				{
					cones[counter] = area[i][j].amountCones();
					counter++;
				}
			}
		}
		return cones;
	}


	//returns how many cones were left on a given year
	public int conesLeft()
	{
		return (yearCones-conesEaten);
	}

	//function use to randomly select a percentage within a range
	private double percentange(double min, double max)
	{
		Random random = new Random();
		return (min + (max - min) * random.nextDouble());
	}

	//function used to randomly calculate the amunt of seeds within a given range
	private int seeds(int min, int max)
	{
		Random random = new Random();
		return (random.nextInt((max - min) + 1) + min);
	}

	//function will print a representation of the area
	public String toString()
	{
		String temp = "dimension for each square is"+(new Soil()).dimension();
		temp += "\n";
		for(int i =0; i < area.length; i++)
		{
			for(int j=0; j < area[i].length; j++)
			{
				temp +="| "+area[i][j]+" ";
				// table[i][j] = new Block(input[counter]);
				// counter++;
			}
			temp += "|\n";
		}
		return temp;
	}

	//function will check the progress of the area
	//making changes as they are necessary
	//will change cache or seedling to soil
	//soil to cache
	//cache to seedling
	private void checkProgress()
	{
		for(int i =0; i < area.length; i++)
		{
			for(int j =0; j < area[i].length; j++)
			{
				if(area[i][j].isCache())
				{
					if(area[i][j].age() > 1)
					{
						if(area[i][j].germinated())
						{
							area[i][j]= new Seedling(area[i][j].getSeed());
						}
					}
					if(area[i][j].age() >= 2 )
					{
						area[i][j]= new Soil();
					}
				}
				if(area[i][j].isSeedling())
				{	
					if((area[i][j].age()%3 == 0) && (area[i][j].age() != 0)) 
					{
						if(!area[i][j].survived())
						{
							area[i][j]= new Soil();
						}		
					}	
					if(area[i][j].mature())
					{
						// System.out.println("asd");
						area[i][j] = new LimberPine(area[i][j].age());
					}
				}

			}
		}
	}

	//function will return the amount of trees present in the area
	public int getTreeCount()
	{
		int count = 0;
		for(int i = 0; i < area.length; ++i)
		{
			for(int j = 0; j < area[i].length; ++j)
			{
				if (area[i][j].containsPine())
				{
					++count;
				}
			}
		}
		return count;
	}

	//function will return the amount of caches currently in the area
	public int getCacheCount()
	{
		int count = 0;
		for(int i = 0; i < area.length; ++i)
		{
			for(int j = 0; j < area[i].length; ++j)
			{
				if (area[i][j].isCache())
				{
					++count;
				}
			}
		}
		return count;
	}

	//function will return the amount of current seedlings in the area
	public int getSeedlingCount()
	{
		int count = 0;
		for(int i = 0; i < area.length; ++i)
		{
			for(int j = 0; j < area[i].length; ++j)
			{
				if (area[i][j].isSeedling())
				{
					++count;
				}
			}
		}
		return count;
	}

	//function will return the total amount of cones created in the area for a given year
	public int getYearCones()
	{
		return yearCones;
	}

	//function will return the amount of cones eaten in an area in a given year
	public int getYearConesEaten()
	{
		return conesEaten;
	}

	//function set the cone treshold for nutcracker attraction to the value pass in as a parameter
	public void setConeThreshold(int val)
	{
		coneTreshold = val;
	}

	//function will set the min and max percentage of squirrel consumption to the given parameters
	public void setSquirrelMinMax(double min, double max)
	{
		minSquirrel = min;
		maxSquirrel = max;
	}
}