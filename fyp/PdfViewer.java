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
    private int currentPage = FIRST_PAGE;
    private JButton btnFirstPage;
    private JButton btnPreviousPage;
    private JTextField txtGoPage;
    private JButton btnNextPage;
    private JButton btnLastPage;
    private PagePanel pagePanel;
    private PDFFile pdfFile;
    private JFileChooser fc;
	private static String filename;
	    

    public PdfViewer() {
    	fileChooser();
        initial();
    }
 
    private void initial() {
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
    }
 
    //create button function used to make buttons of same size
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(55, 20));
 
        return button;
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

 
        //function to disbale navigation buttons
    private void disableAllNavigationButton() {
        btnFirstPage.setEnabled(false);
        btnPreviousPage.setEnabled(false);
        btnNextPage.setEnabled(false);
        btnLastPage.setEnabled(false);
    }
 
    
    // checks if there is more than one page
    private boolean isMoreThanOnePage(PDFFile pdfFile) {
        return pdfFile.getNumPages() > 1;
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
        
        
        //command to go to page
        private void goPage(int pageNumber, int numPages) {
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
    }
 
    public PagePanel getPagePanel() {
        return pagePanel;
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
    
    public void fileChooser(){
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
        filename = file.getName();
    
    }
    
    protected void printCheck(){
    	System.out.println("trip");
    }

    public static void main(String[] args) {
    	
    	final MouseController mouse=new MouseController();
		final Controller controller= new Controller();
		controller.setPolicyFlags(PolicyFlag.POLICY_BACKGROUND_FRAMES);
		
		controller.addListener(mouse);
	

    	
            try {
                long heapSize = Runtime.getRuntime().totalMemory();
                System.out.println("Heap Size = " + heapSize);
    
                JFrame frame = new JFrame("PDF Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                //load a pdf from a byte buffer
                PdfViewer pdfViewer = new PdfViewer();
                File file = new File(filename);
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                FileChannel channel = raf.getChannel();
                ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
//                System.out.println("catch7");
                final PDFFile pdffile = new PDFFile(buf);
                pdfViewer.setPDFFile(pdffile);
                frame.add(pdfViewer);
                frame.pack();
                frame.setVisible(true);
//                System.out.println("catch8");
 
                PDFPage page = pdffile.getPage(0);
                pdfViewer.getPagePanel().showPage(page);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}



}


//Read more: http://seanshou.blogspot.com/2012/10/java-swing-pdf-viewer.html#ixzz4PKujk6NX
