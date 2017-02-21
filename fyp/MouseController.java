package fyp;

import static java.lang.Math.abs;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

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
	// x hand position, x updated, x difference, x screen position.
	public double xhpos, xupd, xdif, yhpos, yupd, ydif, xspos, yspos = 0;
	
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
    	
    	//Right hand with fingers not extended moving the mouse    	
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
    													 if(!thumb.isExtended()&&!index.isExtended()&&!middle.isExtended()&&!ring.isExtended()&&!pinky.isExtended())
    													{
    														 Vector handpos = hand.stabilizedPalmPosition();
    														 Vector boxHandpos = box.normalizePoint(handpos);
    														 Dimension screen=java.awt.Toolkit.getDefaultToolkit().getScreenSize();
																
    														 xupd = (screen.width*boxHandpos.getX());
    														 yupd = (screen.height-boxHandpos.getY()*screen.height);
    														 xdif = xhpos -xupd;
    														 ydif = yhpos -yupd;
	
    														 if(abs(xdif)<100 && abs(ydif)<100){
    															 Point pos = MouseInfo.getPointerInfo().getLocation();																
    															 xspos = pos.getX();
    															 yspos = pos.getY();
    															 robot.mouseMove((int)(xspos - xdif),(int)(yspos -ydif));
    														 }
    														 xhpos = xupd;
    														 yhpos = yupd;
    														 
	
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
    	 
    	 //swipe with right hand
    	 for(Gesture gesture:frame.gestures())
  		{
  			for(Hand hand:frame.hands())
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
  																if(gesture.type()==Type.TYPE_SWIPE)
  																{
  																	robot.keyPress(KeyEvent.VK_A);
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
    	 
    	 
    	//swipe with left hand 
    	for(Gesture gesture:frame.gestures())
 		{
 			for(Hand hand:frame.hands())
 			{
 				if(hand.isLeft())
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
 																if(gesture.type()==Type.TYPE_SWIPE)
 																{
 																	robot.keyPress(KeyEvent.VK_B);
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
    }

	

}
