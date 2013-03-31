package com.rohit.challenge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MazeTraversal {
	
	class MazeNode
	{
		int x;
		int y;
		
		/**
		 * Constructor
		 * @param xcood x coordinate
		 * @param ycood y coordinate
		 */
		public MazeNode(int xcood, int ycood)
		{
			x=xcood;
			y=ycood;
		}
	}
	
	/**
	 * Variables
	 */
	ArrayList<MazeNode> visitedNodes = new ArrayList<MazeTraversal.MazeNode>();
	final static String baseURL = "http://challenge.flipboard.com/m";
	final Pattern nodePattern = Pattern.compile("\\(.*?\\)");
	String mazeURL;
	boolean success = false;
	
	/**
	 * Extracts the common element from the source URL
	 * 
	 * @param url Source URL 
	 */
	void setMazeURL(String url)
	{
		mazeURL = url.substring(0, url.indexOf("&x"));
	}
	
	/**
	 * Given the URLbody (eg: "end:false/(9,10),(8,11)") that is returned, this function returns the nodes that it contains 
	 * 
	 * @param URLbody
	 * @return nextNodes An ArrayList of nodes
	 */
	public ArrayList<MazeNode> getNextNodes(String URLbody)
	{
		ArrayList<MazeNode> nextNodes = new ArrayList<MazeNode>();
		Matcher matcher = nodePattern.matcher(URLbody);
		
		while (matcher.find()) {
		      String temp = matcher.group();
		      temp = temp.replace("(", "");
		      temp = temp.replace(")", "");
		      String tuple[] = temp.split(",");
		      nextNodes.add(new MazeNode(Integer.parseInt(tuple[0]), Integer.parseInt(tuple[1])));
		    }
		
		return nextNodes;
	}
	
	/**
	 * Checks if the program has reached the end of the maze
	 * 
	 * @param urlBody (eg : "end:false/(9,10),(8,11)")
	 * @return <code>true</code> Reached the end
	 * 		   <code>false</code> Not reached the end yet
	 */
	public boolean checkStatus(String urlBody)
	{
		String status = urlBody.substring((urlBody.indexOf(":")+1), urlBody.indexOf("/"));
		if(status.equalsIgnoreCase("false"))
			return false;
		else if(status.equalsIgnoreCase("true"))
			return true;
		else
		{
			System.out.println("ERROR : The URL bosy does not contain a valid status");
			return false;
		}
	}
	
	/**
	 * Takes x and y coordinates and returns the corresponding URL
	 * 
	 * @param x x coordinate
	 * @param y x coordinate
	 * @return
	 */
	String constructURL(int x, int y)
	{
		return(mazeURL+"&x="+String.valueOf(x)+"&y="+String.valueOf(y));
	}
	
	/**
	 * 
	 * @param node
	 */
	boolean checkCycle(MazeNode mn)
	{
		for(MazeNode itr : visitedNodes)
		{
			if((itr.x == mn.x)&&(itr.y == mn.y))
			{
				return true;
			}
		}
		return false;
	}
	
	void printVisitedNodes()
	{
		StringBuilder builder = new StringBuilder();
		for(MazeNode mn : visitedNodes)
		{
			builder.append("("+mn.x+","+mn.y+"), ");
		}
		System.out.println(builder.toString());
	}
	
	void taverseMaze(MazeNode node)
	{
		if(success == false)
		{
			printVisitedNodes();
			String URL = constructURL(node.x, node.y);
			ArrayList<MazeNode> nextNodes;
			try
			{
				URLConnection con = new URL(URL).openConnection();
				con.connect();
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
				String urlBody = in.readLine();
				in.close();
				if(checkStatus(urlBody) == false)
				{
					nextNodes = getNextNodes(urlBody);
					for(MazeNode itr : nextNodes)
					{
						if(checkCycle(itr))
						{						
							continue;
						}
						else
						{
							visitedNodes.add(itr);
							taverseMaze(itr);
						}
					}
				}
				else
				{
					System.out.println("Reached the end of the maze " + urlBody);
					System.out.println("URL: " + URL);
					success = true;
				}
				in.close();
				
			}
			catch(MalformedURLException mue)
			{
				System.out.println("ERROR : URL not valid");
			}
			catch(IOException ioe)
			{
				System.out.println("ERROR : IO Exception has occurred");
			}
		}
		
	}
	
	void initMaze()
	{
		ArrayList<MazeNode> nextNodes;
		try
		{
			if(mazeURL == null)
			{
				URLConnection con = new URL(baseURL).openConnection();
				con.connect();
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
				setMazeURL(con.getURL().toString());
				String urlBody = in.readLine();		
				System.out.println("Starting the Maze Traversal");
				visitedNodes.add(new MazeNode(0, 0));
				if(checkStatus(urlBody) == false)
				{
					nextNodes = getNextNodes(urlBody);
					for(MazeNode itr : nextNodes)
					{
						if(checkCycle(itr))
						{
							//Already visited this node
							continue;
						}
						else
						{
							visitedNodes.add(itr);
							taverseMaze(itr);
						}
					}
				}
				else
				{
					System.out.println("Reached the end of the maze " + urlBody);
					success = true;
				}
				in.close();
			}
		}
		catch(MalformedURLException mue)
		{
			System.out.println("ERROR : URL is not valid");
		}
		catch(IOException ioe)
		{
			System.out.println("ERROR : IO Exception");
		}
	}
	
	
	public static void main(String args[])
	{
		MazeTraversal maze = new MazeTraversal();
		maze.initMaze();
		
	}
}
