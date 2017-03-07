
package fyp;

import javax.swing.SwingUtilities;


class HelloWorld{
	
	 static Runnable doHelloWorld = new Runnable() {
	     public void run() {
	         System.out.println("Hello World on " + Thread.currentThread());
	     }
	 };

	public static void main(String[] args) {
		SwingUtilities.invokeLater(doHelloWorld);
		 System.out.println("This might well be displayed before the other message.");

	}

}
