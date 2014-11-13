/*
 * Brett Koenig 
 * Date: 4/18/13
 * This class has the ability to take an input of a makefile and output the correct order in which each dependency needs to be compiled in order for the final target to compile.
 */
import java.util.ArrayList;


public class Builder
{
	// insert instance variables
	// insert constants (if any)
	private int numberNodes = 0;
	private class Node {
		String name = "";
		String[] dependency = new String[10];
		String target = "";
  }
	private Node[] nodeArray = new Node[100];

    /**
     * 
     * @param makefile the incoming file
     * @throws ParseException
     * @throws UnknownTargetException
     * @throws CycleDetectedException
     */
    public Builder(String makefile) throws ParseException,
            UnknownTargetException, CycleDetectedException {
    		if(makefile == null || makefile.length()==0)
    		{
    			throw new ParseException();
    		}
			int count = 0;
			int innerCount = 0;
			int mostInCount = 0;
			int marker = 0;
			String newLineDivide[] = makefile.split("\\n");
			numberNodes = newLineDivide.length;
			while(count < newLineDivide.length)
			{
				String colonDivide[]=newLineDivide[count].split(":");
				if(colonDivide.length!=3)
				{
					throw new ParseException();
				}
				if(newLineDivide.length ==1 && !colonDivide[1].isEmpty())
				{
					throw new UnknownTargetException();
				}

				nodeArray[count] = new Node();
	            int trimCount = 0;
	            while(colonDivide[0].charAt(trimCount)==' ')
	            {
	              trimCount++;
	            }
	            int trimBack = 0;
	            while(colonDivide[0].charAt((colonDivide[0].length()-1)-trimBack) == ' ')
	            {
	              trimBack++;
	            }
	            trimBack = colonDivide[0].length()-trimBack;
	            nodeArray[count].name=colonDivide[0].substring(trimCount, trimBack);
	            if(!colonDivide[1].isEmpty())
	            {
	            	while(colonDivide[1].charAt(innerCount)==' ')
	            	{
	            		innerCount++;
	            	}
	            	while(innerCount < colonDivide[1].length())
	            	{
	            		while(colonDivide[1].charAt(innerCount)!='.')
	            		{
	            			innerCount++;
	            		}
	            		while(((innerCount+1)!=colonDivide[1].length()) && (colonDivide[1].charAt(innerCount)!=' '))
	            		{
	            			innerCount++;
	            		}
	            		innerCount++;
	            		String test =colonDivide[1].substring(marker, innerCount);
	            		test = test.trim();
	            		nodeArray[count].dependency[mostInCount]=test;
	            		while(((innerCount)!=colonDivide[1].length()) &&colonDivide[1].charAt(innerCount)==' ')
	            		{
	            			innerCount++;
	            		}
	            		marker = innerCount;
	            		mostInCount++;
	            	}
	            }
				marker = 0;
				mostInCount = 0;
				innerCount=0;
				if(colonDivide[2].isEmpty())
				{
					throw new UnknownTargetException();
				}
	            int trimCountTwo = 0;
	            while(colonDivide[2].charAt(trimCountTwo)==' ')
	            {
	              trimCountTwo++;
	            }
	            int trimBackTwo = 0;
	            while(colonDivide[2].charAt((colonDivide[2].length()-1)-trimBackTwo) == ' ') 
	            {
	              trimBackTwo++;
	            }
	            trimBackTwo = colonDivide[2].length()-trimBackTwo;
	            int counter = 0;
	            while(counter < count)
	            {
	            	if(colonDivide[2].substring(trimCountTwo, trimBackTwo).equals(nodeArray[counter].target))
	            	{
	            		throw new ParseException();
	            	}
	            	counter++;
	            }
	            nodeArray[count].target=colonDivide[2].substring(trimCountTwo, trimBackTwo);
				count++;
			}

			int countOne =0; //iterates through nodeArray
			int countTwo =0; //iterates through dependencies
			int countThree =0; //iterates through nodeArray.name for each dependency
			boolean flagTwo = false;
			while(countOne < count) //while there is something in the nodeArray
			{
				if(nodeArray[countOne].dependency[0] == null) //if dependency[0] is null there are no dependencies for this nodeArray node
				{
					countOne++;
				}
				else
				{
					while((nodeArray[countOne].dependency[countTwo]!=null)) //there is a dependency
					{
							while((countThree < count)&&(!flagTwo)) //iterating through the names while the matching name to dependency has not been found
							{
								if(nodeArray[countOne].dependency[countTwo].equals(nodeArray[countThree].name))//if found equal to a name set flag to true
								{
									flagTwo = true;
								}
								countThree++;
							}
							if(!flagTwo)
							{
								throw new UnknownTargetException();
							}
							countThree=0;
							flagTwo = false;
							countTwo++;
					}
					countTwo = 0;
					countOne++;
				}
			}
			countOne = 0;
			int countFour = 0;
			String test;
    		while(countOne<count)
    		{
    			while(countTwo<=nodeArray[countOne].dependency.length && nodeArray[countOne].dependency[countTwo]!=null)
    			{
        			test = nodeArray[countOne].dependency[countTwo];
        			while((countThree < count) && (!nodeArray[countThree].name.equals(test)))
        			{
        				countThree++;
        			}
        			while(countFour<=nodeArray[countThree].dependency.length && nodeArray[countThree].dependency[countFour]!=null)
        			{
        				if(nodeArray[countThree].dependency[countFour].equals(nodeArray[countOne].name))
        				{
        					throw new CycleDetectedException();
        				}
        				countFour++;
        			}
        			countTwo++;
    			}
    			countOne++;
    		}
    }
    
	/**
     * 
     * @param targetName the target
     * @return result the made target
     */
    public ArrayList<String> makeTarget(String targetName) {
		ArrayList<String> targetorder = new ArrayList<String>();
		ArrayList<String> targetrecurse = new ArrayList<String>();
    	if(targetName == null)
    	{
    		return targetorder;
    	}
		int count = 0;
		int innerCount = 0;
		while(count < numberNodes && !nodeArray[count].name.equals(targetName))
		{
			count++;
		}
		if(count == numberNodes)
		{
    		return targetorder;
		}

		if(nodeArray[count].dependency[0]==null)
		{
			if(!targetorder.contains(nodeArray[count].target))
			{
				targetorder.add(nodeArray[count].target);
			}
		}
		else{
			while(nodeArray[count].dependency[innerCount]!=null && innerCount<nodeArray[count].dependency.length)
			{
				if(!nodeArray[count].dependency[innerCount].isEmpty())
				{
					targetrecurse = makeTarget(nodeArray[count].dependency[innerCount]);
					while(targetrecurse.size() > 0)
					{
						String item = targetrecurse.remove(0);
						if(!targetorder.contains(item))
						{
							targetorder.add(item);
						}
					}
				}
				innerCount++;

			}
			if(!targetorder.contains(nodeArray[count].target))
			{
				targetorder.add(nodeArray[count].target);
			}

		}
        return targetorder;
    }
}