package client;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import resource.Factory;
import resource.Resource;

public class FactorySimulation {
	
	Factory mFactory;
	
	private ArrayList<FactoryObject> mFObjects;
	private ArrayList<FactoryWorker> mFWorkers;
	private FactoryNode mFNodes[][];
	private Map<String, FactoryNode> mFNodeMap;
	private FactoryTaskBoard mTaskBoard;
	private boolean isDone = false;
	
	private double totalTime = 0.0;
	
	private FactoryMailBox mMailBox;
	
	//instance constructor
	{
		mFObjects = new ArrayList<FactoryObject>();
		mFWorkers = new ArrayList<FactoryWorker>();
		mFNodeMap = new HashMap<String, FactoryNode>();
	}
	
	FactorySimulation(Factory inFactory, JTable inTable) {
		mFactory = inFactory;
		mFNodes = new FactoryNode[mFactory.getWidth()][mFactory.getHeight()];		
		Scanner reader = null;
		
		//Create the nodes of the factory
		for(int height = 0; height < mFactory.getHeight(); height++) {
			for(int width = 0; width < mFactory.getWidth(); width++) {
				mFNodes[width][height] = new FactoryNode(width,height);
				mFObjects.add(mFNodes[width][height]);
			}
		}
		
		//Link all of the nodes together
		for(FactoryNode[] nodes: mFNodes) {
			for(FactoryNode node : nodes) {
				int x = node.getX();
				int y = node.getY();
				if(x != 0) node.addNeighbor(mFNodes[x-1][y]);
				if(x != mFactory.getWidth()-1) node.addNeighbor(mFNodes[x+1][y]);
				if(y != 0) node.addNeighbor(mFNodes[x][y-1]);
				if(y != mFactory.getHeight()-1) node.addNeighbor(mFNodes[x][y+1]);
			}
		}
		
		//Create the resources
		for(Resource resource : mFactory.getResources()) {
			FactoryResource fr = new FactoryResource(resource);
			mFObjects.add(fr);
			mFNodes[fr.getX()][fr.getY()].setObject(fr);
			mFNodeMap.put(fr.getName(), mFNodes[fr.getX()][fr.getY()]);
		}
		
		//Create the task board
		Point taskBoardLocation = mFactory.getTaskBoardLocation();
		mTaskBoard = new FactoryTaskBoard(inTable,inFactory.getProducts(),taskBoardLocation.x,taskBoardLocation.y);
		mFObjects.add(mTaskBoard);
		mFNodes[taskBoardLocation.x][taskBoardLocation.y].setObject(mTaskBoard);
		mFNodeMap.put("Task Board", mFNodes[taskBoardLocation.x][taskBoardLocation.y]);
		
		try{
			reader = new Scanner(new File("Walls"));
			while(reader.hasNext()){
				int x = reader.nextInt();
				int y = reader.nextInt();
				String file = reader.next();
				FactoryWall fw = new FactoryWall(new Rectangle(x, y, 1, 1), file);
				mFObjects.add(fw);
				mFNodes[fw.getX()][fw.getY()].setObject(fw);
			}
		}
		catch(IOException ioe){
			System.out.println(ioe.getMessage());
		}
		finally{
			if(reader != null){
				reader.close();
			}
		}
		
		for(int i = 0; i < 6; i++){
			FactoryWall2 fw = new FactoryWall2(new Rectangle(i,9,1,1));
			mFObjects.add(fw);
			mFNodes[fw.getX()][fw.getY()].setObject(fw);
		}
		
		//Create the workers, set their first task to look at the task board
		for(int i = 0; i < mFactory.getNumberOfWorkers(); i++) {
			//Start each worker at the first node (upper left corner)
			//Note, may change this, but all factories have an upper left corner(0,0) so it makes sense
			FactoryWorker fw = new FactoryWorker(i, mFNodes[0][0], this);
			mFObjects.add(fw);
			mFWorkers.add(fw);
		}
		
		//Create mailbox
		mMailBox = new FactoryMailBox(mFactory.getResources());
		mFObjects.add(mMailBox);
		mFNodes[0][0].setObject(mMailBox);
		mFNodeMap.put("MailBox", mFNodes[0][0]);

		//Create some stockpersons
		for(int i = 0; i < 3; ++i){
			FactoryStockPerson sp = new FactoryStockPerson(i, mFNodes[0][0], this);
			mFObjects.add(sp);
			mFWorkers.add(sp);
		}
		
	}
	
	public void update(double deltaTime){
		
		totalTime += deltaTime;

		if(isDone){
			return;
		}
				
		//Update all the objects in the factor that need updating each tick
		for(FactoryObject object : mFWorkers) object.update(deltaTime);
		
		if(mTaskBoard.isDone()){
			isDone = true;
			
			//Done dialogue
			DecimalFormat threePlaces = new DecimalFormat(".###");
			JOptionPane.showMessageDialog(null, 
					"Total time: "+ threePlaces.format(totalTime)+ "s", 
					"Simulation Over!", 
					JOptionPane.INFORMATION_MESSAGE);
			
			String[] options = {"Both", "Items Only", "Resource Only"};
			
			int result = -1;
			
			result = JOptionPane.showOptionDialog(null, 
					"Would you like to save the Resources used, the completed items list, or both?", 
					"Save the files?", 
					JOptionPane.DEFAULT_OPTION, 
					JOptionPane.QUESTION_MESSAGE, 
					null, 
					options, 
					 options[2]);		
				
			System.out.println("Result: " + result );

			
			if(result == 0 || result == 2){
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				String outFileName = "reports/" + timestamp;
				outFileName = outFileName.replaceAll("[-:.]", "_");
				FileWriter fw = null;
				try {
					fw = new FileWriter(outFileName);
					for(FactoryObject object : mFObjects) {
						if(object instanceof FactoryReporter) {
							((FactoryReporter)object).report(fw);
						}
					}
				} catch (IOException ioe) {
					System.out.println("Error occurred writing to file: " + outFileName);
					System.out.println(ioe.getMessage());
				} finally {
					if (fw != null) {
						try {
							fw.close();
						} catch (IOException ioe) {
							System.out.println("IOE closing FileWriter: " + ioe.getMessage());
						}
					}
				}
			}
			
			if(result == 2 ){
				File file = new File("CompletedItems.txt");
				boolean a = file.delete();
				if(a)
					System.out.println("Deleted");
				else
					System.out.println("Not deleted");
			}
				
		}
	}

	
	public ArrayList<FactoryObject> getObjects() {
		return mFObjects;
	}
	
	public ArrayList<FactoryWorker> getWorkers() {
		return mFWorkers;
	}
	
	public FactoryNode[][] getNodes() {
		return mFNodes;
	}

	public String getName() {
		return mFactory.getName();
	}

	public double getWidth() {
		return mFactory.getWidth();
	}
	
	public double getHeight() {
		return mFactory.getHeight();
	}

	public FactoryNode getNode(String key) {
		return mFNodeMap.get(key);
	}

	public FactoryTaskBoard getTaskBoard() {
		return mTaskBoard;
	}
	
	public FactoryMailBox getMailBox(){
		return mMailBox;
	}
	
}
