package fyp;

import com.google.common.base.CharMatcher;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PagePanel;

import javafx.stage.FileChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.io.ByteArrayInputStream;
import javax.swing.filechooser.*;
import javax.jnlp.*;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Controller.PolicyFlag;
import com.leapmotion.leap.Listener;

import static com.google.common.base.Strings.isNullOrEmpty;
 
public class PdfViewer extends JPanel implements ActionListener {
    private static enum Navigation {GO_FIRST_PAGE, FORWARD, BACKWARD, GO_LAST_PAGE, GO_N_PAGE}
 
    private static final CharMatcher POSITIVE_DIGITAL = CharMatcher.anyOf("0123456789");
    private static final String GO_PAGE_TEMPLATE = "%s of %s";
    private static final int FIRST_PAGE = 1;
    public int currentPage = FIRST_PAGE;
    private JButton btnFirstPage;
    private JButton btnPreviousPage;
    private JTextField txtGoPage;
    private JButton btnNextPage;
    private JButton btnLastPage;
    private PagePanel pagePanel;
    public int numPages;
    public PDFFile pdfFile;
    private JFileChooser fc;
	private static String filename;
	
	public PdfViewer(){
        //get singleton instance
        Singleton newInstance = Singleton.getInstance();
        //print singleton ID to check across classes
        System.out.println("GUI Instance ID: " + System.identityHashCode(newInstance));
        
		//choose file
		fileChooser();
		
		//initialize pdf
		initial();
		
	}
	
	private void initial(){
		
	}
	
	private void fileChooser(){
		//Create a file chooser and set it to read files only, makes sure it opens in current directory
		String dir = System.getProperty("user.dir");
		fc = new JFileChooser(dir);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

		//add filter so only PDFs can be chose
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"PDFs", "pdf");
		fc.setFileFilter(filter);


		fc.showOpenDialog(PdfViewer.this);
		File file = fc.getSelectedFile();
		if (file != null){
		filename = file.getName();
		}


	}
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
