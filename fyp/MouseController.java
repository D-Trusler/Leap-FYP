package fyp;

import static java.lang.Math.abs;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.sql.Time;

import com.leapmotion.leap.ScreenTapGesture;
import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.SwipeGesture;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.InteractionBox;
import com.leapmotion.leap.Vector;
import com.leapmotion.leap.Gesture.Type;

public class MouseController extends Listener{

	//private PdfViewer pdfView;
	public Robot robot;
	// x hand position, x updated, x difference, x screen position.
	public double xhpos, xupd, xdif, yhpos, yupd, ydif, xspos, yspos = 0;
	public long lastGestureTime = 0;
	Singleton newInstace;
	private Singleton newInstance;
	private int swipecd = 250;

	private int mask = InputEvent.BUTTON1_DOWN_MASK;

	public MouseController () {
	}

	public void onInit(Controller controller) {
		//setting up gestures that we're going to use and also adjusting properties

		controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
		controller.config().setFloat("Gesture.ScreenTap.MinDistance", 0.5f);
		controller.enableGesture(Gesture.Type.TYPE_SWIPE);
		controller.config().setFloat("Gesture.Swipe.MinLength", 100);
		
		try
		{
			robot=new Robot();
		}
		catch(Exception e)
		{}
	}

	public void onConnect(Controller controller) {
		//notify connected
		System.out.println("Connected");

		//get singleton instance
		newInstance = Singleton.getInstance();
		//print singleton ID to check across classes
		System.out.println("Leap Instance ID: " + System.identityHashCode(newInstance));

	}

	public void onFrame(Controller controller) {
		Frame frame = controller.frame();
		InteractionBox box = frame.interactionBox();
		//System.out.println("frame " + frame.id());
		//newInstance.write(String.valueOf(frame.id()));


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

															//algorithm to allow smooth mouse movement across multiple screens
															Vector handpos = hand.stabilizedPalmPosition();
															Vector boxHandpos = box.normalizePoint(handpos);
															Dimension screen=java.awt.Toolkit.getDefaultToolkit().getScreenSize();

															xupd = (screen.width*boxHandpos.getX());
															yupd = (screen.height-boxHandpos.getY()*screen.height);
															xdif = xhpos -xupd;
															ydif = yhpos -yupd;

															if(abs(xdif)<100 || abs(ydif)<100){
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



		//right hand gestures
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
													if(ring.type()==Finger.Type.TYPE_PINKY)
													{
														if(pinky.type()==Finger.Type.TYPE_RING)
														{															
															if(thumb.isExtended()&&index.isExtended()&&middle.isExtended()&&pinky.isExtended()&&ring.isExtended())
															{ 																
																if(gesture.type()==Type.TYPE_SWIPE)
																{	
																	SwipeGesture swipe = new SwipeGesture(gesture);
																	if(System.currentTimeMillis() - lastGestureTime > swipecd &&  swipe.direction().getX()<0){
																		newInstance.write("next_page");
																		lastGestureTime = System.currentTimeMillis();
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

		//left hand gestures
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
								for(Finger pinky:hand.fingers())
								{
									for(Finger ring:hand.fingers())
									{
										if(thumb.type()==Finger.Type.TYPE_THUMB)
										{
											if(index.type()==Finger.Type.TYPE_INDEX)
											{
												if(middle.type()==Finger.Type.TYPE_MIDDLE)
												{
													if(pinky.type()==Finger.Type.TYPE_PINKY)
													{
														if(ring.type()==Finger.Type.TYPE_RING)
														{															
															if(thumb.isExtended()&&index.isExtended()&&middle.isExtended()&&pinky.isExtended()&&ring.isExtended())
															{															
																if(gesture.type()==Type.TYPE_SWIPE)
																{	SwipeGesture swipe = new SwipeGesture(gesture);
																if(System.currentTimeMillis() - lastGestureTime > swipecd &&swipe.direction().getX()>0){
																	newInstance.write("previous_page");
																	lastGestureTime = System.currentTimeMillis();
																}

																}

															}
															if(!thumb.isExtended()&&index.isExtended()&&!middle.isExtended()&&!pinky.isExtended()&&!ring.isExtended())
															{ 																
																if(gesture.type()==Type.TYPE_SCREEN_TAP)
																{	
																	ScreenTapGesture tap = new ScreenTapGesture(gesture);
																	robot.mousePress(mask);
																	robot.mouseRelease(mask);
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