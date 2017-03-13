package fyp;

import java.io.IOException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.leapmotion.leap.Controller;

public class Main {
	
//    static class ControllerHandler extends SwingWorker<Void, Void>{
//    	
//    	public ControllerHandler()
//    	{
//    		try{
//    			MouseController mouse = new MouseController();
//    			Controller controller = new Controller();
//    			controller.setPolicy(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES);
//    			controller.addListener(mouse);
//    		}
//    		catch(Exception e)
//    		{
//    			System.out.println(e.getMessage());
//    		}
//    	}
//
//		@Override
//		public Void doInBackground(){
//			return null;
//		}
//		
//		@Override
//		public void done()
//		{
//			
//		}
//    	
//    }
	
	public static void main(String[] args){
		
		//creates a singleton
		Singleton newInstance = Singleton.getInstance();
		
		//print out singleton id to properly check across classses
		System.out.println("Main Instance ID: " + System.identityHashCode(newInstance));

		
//    	ControllerHandler controllerhandler = new ControllerHandler();
//    	controllerhandler.execute();
		
		final MouseController mouse = new MouseController();
		final Controller controller = new Controller();
		controller .setPolicy(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES);
		controller.addListener(mouse);
		
		SwingUtilities.invokeLater( new Runnable () {
        	public void run() {
        		PdfViewer pdfview = new PdfViewer();
        		
        	}
		});
		
    	try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println("end");
		

    	
		
	}

}
