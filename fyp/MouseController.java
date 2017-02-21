package fyp;

import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.InputEvent;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.InteractionBox;
import com.leapmotion.leap.Vector;
import com.leapmotion.leap.Gesture.Type;

public class MouseController extends Listener{
	
	public Robot robot;
	
	public void onInit(Controller controller) {
		//setting up gestures that we're going to use and also adjusting properties
		controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
		controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
		controller.config().setFloat("Gesture.ScreenTap.MinDistance", 0.5f);
		controller.enableGesture(Gesture.Type.TYPE_SWIPE);
	}
	
    public void onConnect(Controller controller) {
        System.out.println("Connected");
    }
    
    public void onFrame(Controller controller) {
    	Frame frame = controller.frame();
    	InteractionBox box = frame.interactionBox();
    	
    	try
		{
			robot=new Robot();
		}
		catch(Exception e)
		{}
    	
    	//Right hand, all fingers extended for moving the mouse    	
    	 for (Hand hand:frame.hands())
    	 {
    		 if(hand.isRight())
    		 {
    			 for(Finger thumb:hand.fingers())
    			 {
    				 for(Finger index:hand.fingers())
    				 {
    					 for(Finger middle:hand.fingers())
    					 {
    						 for(Finger ring:hand.fingers())
    						 {
    							 for(Finger pinky:hand.fingers())
    							 {
    								 if(thumb.type()==Finger.Type.TYPE_THUMB)
    								 {
    									 if(index.type()==Finger.Type.TYPE_INDEX)
    									 {
    										 if(middle.type()==Finger.Type.TYPE_MIDDLE)
    										 {
    											 if(ring.type()==Finger.Type.TYPE_RING)
    											 {
    												 if(pinky.type()==Finger.Type.TYPE_PINKY)
    												 {
    													 if(thumb.isExtended()&&index.isExtended()&&middle.isExtended()&&ring.isExtended()&&pinky.isExtended())
    													{
    														 Vector handpos = hand.stabilizedPalmPosition();
    														 Vector boxHandpos = box.normalizePoint(handpos);
    														 Dimension screen=java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    														 robot.mouseMove((int)(screen.width*boxHandpos.getX()),(int)(screen.height-boxHandpos.getY()*screen.height));
    													}
    												 }
    											 }
    										 }
    									 }
    								 }
    							 }
    						 }
    					 }
    				 }
    			 }
    		 }
    	 }
    }

	

}
