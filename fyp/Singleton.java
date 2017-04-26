package fyp;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class Singleton {
	
	private static Singleton instance = null;
	
	private Singleton(){ }
	
	private LinkedList<String> commands = new LinkedList<String>();
	
	
	public static Singleton getInstance(){
		synchronized(Singleton.class){
			if(instance == null){
				
				instance = new Singleton();
				
			}
		}
		
		return instance;
	}
	
	//reads the first command from our list of commands	
	public String  read(){
		
		if (commands.size() != 0){		
			String commandtosend = new String();
			commandtosend = instance.commands.remove(0);
//			System.out.println("sent: " + commandtosend);
			return commandtosend;
		} else {
			return null;
		}
		
		
	}
	
//	public void test(){
//		System.out.println("Singleton Test");
//	}
	
	//writes command to end of list
	public void write(String commandtowrite){
		if (instance.commands.contains(commandtowrite) == false){
			instance.commands.add(commandtowrite);}
//		System.out.println("written: " + commandtowrite);

	}

}
