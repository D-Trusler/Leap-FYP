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
import java.io.FileNotFoundException;
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
	private static String filename = "test-pdf.pdf";
	
	//Command string is pulled from the singleton
	private String command = new String();

	
	public PdfViewer(){
        //get singleton instance
        final Singleton newInstance = Singleton.getInstance();
        //print singleton ID to check across classes
        System.out.println("GUI Instance ID: " + System.identityHashCode(newInstance));

		//choose file
		fileChooser();
		
		//initialize pdf
		initial();

		//changePDF();
		//debug
		//newInstance.write("next_page");
		
        
        //action listener called in our event loop
        // reads from newInstance and triggers runCommand

		ActionListener eventloop = new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				command = newInstance.read();
				if (command != null){
//					System.out.println("read: " + command);
					runCommand(command);
				}
				//System.out.println(command);
				}
		};
        
		Timer timer = new Timer(8, eventloop);
		timer.setRepeats(true);
		timer.start();
        
				
		
		//System.out.println(pdfFile.;
		

	}
	
	private void changePDF(){
		fileChooser();
		manageFile();
		
		numPages = pdfFile.getNumPages();
		
		PDFPage page = pdfFile.getPage(0);
		getPagePanel().showPage(page);
		
	}
	
	private void runCommand(String command){
//		System.out.println("command :" + command);
		if (currentPage<numPages && command == "next_page"){
			goPage(currentPage+1,numPages);	
		}
		if (currentPage>1 && command == "previous_page"){
			goPage(currentPage-1,numPages);	
		}
		
	}
	
	private void initial(){
    	//create layouts and panels for page scrolling
        setLayout(new BorderLayout(0, 0));
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        add(topPanel, BorderLayout.NORTH);
        btnFirstPage = createButton("|<<");
        topPanel.add(btnFirstPage);
        btnPreviousPage = createButton("<<");
        topPanel.add(btnPreviousPage);
        txtGoPage = new JTextField(10);
        txtGoPage.setHorizontalAlignment(JTextField.CENTER);
        topPanel.add(txtGoPage);
        btnNextPage = createButton(">>");
        topPanel.add(btnNextPage);
        btnLastPage = createButton(">>|");
        topPanel.add(btnLastPage);
        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.CENTER);
        JPanel viewPanel = new JPanel(new BorderLayout(0, 0));
        scrollPane.setViewportView(viewPanel);
 
        //create page panel to display current page
        pagePanel = new PagePanel();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        pagePanel.setPreferredSize(screenSize);
        viewPanel.add(pagePanel, BorderLayout.CENTER);
 
        //disables navigation buttons
        disableAllNavigationButton();
 
        //connect buttons to action listeners
        btnFirstPage.addActionListener(new PageNavigationListener(Navigation.GO_FIRST_PAGE));
        btnPreviousPage.addActionListener(new PageNavigationListener(Navigation.BACKWARD));
        btnNextPage.addActionListener(new PageNavigationListener(Navigation.FORWARD));
        btnLastPage.addActionListener(new PageNavigationListener(Navigation.GO_LAST_PAGE));
        txtGoPage.addActionListener(new PageNavigationListener(Navigation.GO_N_PAGE));
        
        manageFile();
		JFrame frame = new JFrame("PDF Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        

		frame.add(this);
		frame.pack();
		frame.setVisible(true);
		
		numPages = pdfFile.getNumPages();
		
		PDFPage page = pdfFile.getPage(0);
		getPagePanel().showPage(page);
	}
	
	private void manageFile(){
		
	
		
		try {
		//file is given by fileChooser() which is called before this
		File file = new File(filename);
		//Access file, gets channels and sets up a bytebuffer then assigns pdfFile variable to read PDF
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		FileChannel channel = raf.getChannel();
		ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		pdfFile = new PDFFile(buf);
		setPDFFile(pdfFile);
		raf.close();

		
		numPages = pdfFile.getNumPages();
		
		PDFPage page = pdfFile.getPage(0);
		getPagePanel().showPage(page);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
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
	
	public void setPDFFile(PDFFile pdfFile) {
		this.pdfFile = pdfFile;
		currentPage = FIRST_PAGE;
		disableAllNavigationButton();
		txtGoPage.setText(format(GO_PAGE_TEMPLATE, FIRST_PAGE, pdfFile.getNumPages()));
		boolean moreThanOnePage = isMoreThanOnePage(pdfFile);
		btnNextPage.setEnabled(moreThanOnePage);
		btnLastPage.setEnabled(moreThanOnePage);
	}	

	
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	//function to disbale navigation buttons
	private void disableAllNavigationButton() {
		btnFirstPage.setEnabled(false);
		btnPreviousPage.setEnabled(false);
		btnNextPage.setEnabled(false);
		btnLastPage.setEnabled(false);
	}
	
    //create button function used to make buttons of same size
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(55, 20));
 
        return button;
    }
    
    //command to go to page
    public void goPage(int pageNumber, int numPages) {
        currentPage = pageNumber;
        PDFPage page = pdfFile.getPage(currentPage);
        pagePanel.showPage(page);
        boolean notFirstPage = isNotFirstPage();
        btnFirstPage.setEnabled(notFirstPage);
        btnPreviousPage.setEnabled(notFirstPage);
        txtGoPage.setText(format(GO_PAGE_TEMPLATE, currentPage, numPages));
        boolean notLastPage = isNotLastPage(numPages);
        btnNextPage.setEnabled(notLastPage);
        btnLastPage.setEnabled(notLastPage);
    }

    //determine if there is a next page
    private boolean hasNextPage(int numPages) {
        return (++currentPage) <= numPages;
    }

    // determine if there is a previous page
    private boolean hasPreviousPage() {
        return (--currentPage) >= FIRST_PAGE;
    }

    // determine if this is not the last page
    private boolean isNotLastPage(int numPages) {
        return currentPage != numPages;
    }

    // determine if this is not the first page
    private boolean isNotFirstPage() {
        return currentPage != FIRST_PAGE;
    }
 
    public PagePanel getPagePanel() {
        return pagePanel;
    }
 
    // checks if there is more than one page
    private boolean isMoreThanOnePage(PDFFile pdfFile) {
        return pdfFile.getNumPages() > 1;
    }
    
    //format function
    public static String format(String template, Object... args) {
    template = String.valueOf(template); // null -> "null"
    // start substituting the arguments into the '%s' placeholders
    StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
    int templateStart = 0;
    int i = 0;
    while (i < args.length) {
        int placeholderStart = template.indexOf("%s", templateStart);
        if (placeholderStart == -1) {
            break;
        }
        builder.append(template.substring(templateStart, placeholderStart));
        builder.append(args[i++]);
        templateStart = placeholderStart + 2;
    }
    builder.append(template.substring(templateStart));

    // if we run out of placeholders, append the extra args in square braces
    if (i < args.length) {
        builder.append(" [");
        builder.append(args[i++]);
        while (i < args.length) {
            builder.append(", ");
            builder.append(args[i++]);
        }
        builder.append(']');
    }

    return builder.toString();
}
	
    //listeners for navigating page
    private class PageNavigationListener implements ActionListener {
        private final Navigation navigation;
 
        private PageNavigationListener(Navigation navigation) {
            this.navigation = navigation;
        }
 
        public void actionPerformed(ActionEvent e) {
            if (pdfFile == null) {
                return;
            }
 
            int numPages = pdfFile.getNumPages();
            if (numPages <= 1) {
                disableAllNavigationButton();
            } else {
                if (navigation == Navigation.FORWARD && hasNextPage(numPages)) {
                    goPage(currentPage, numPages);
                }
 
                if (navigation == Navigation.GO_LAST_PAGE) {
                    goPage(numPages, numPages);
                }
 
                if (navigation == Navigation.BACKWARD && hasPreviousPage()) {
                    goPage(currentPage, numPages);
                }
 
                if (navigation == Navigation.GO_FIRST_PAGE) {
                    goPage(FIRST_PAGE, numPages);
                }
 
                if (navigation == Navigation.GO_N_PAGE) {
                    String text = txtGoPage.getText();
                    boolean isValid = false;
                    if (!isNullOrEmpty(text)) {
                        boolean isNumber = POSITIVE_DIGITAL.matchesAllOf(text);
                        if (isNumber) {
                            int pageNumber = Integer.valueOf(text);
                            if (pageNumber >= 1 && pageNumber <= numPages) {
                                goPage(Integer.valueOf(text), numPages);
                                isValid = true;
                            }
                        }
                    }
 
                    if (!isValid) {
                        JOptionPane.showMessageDialog(PdfViewer.this, format("Invalid page number '%s' in this document", text));
                        txtGoPage.setText(format(GO_PAGE_TEMPLATE, currentPage, numPages));
                    }
                }
            }
        }
    }
	
	
	
}
