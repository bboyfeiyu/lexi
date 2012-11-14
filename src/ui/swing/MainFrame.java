package ui.swing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import model.*;
import controller.*;
import util.*;

public class MainFrame extends JFrame implements ui.IMainFrame, KeyListener, ComponentListener, ActionListener, IObserver, WindowListener, MouseListener{
		
	private static final int TOP_MARGIN = 20;
	private static final int LEFT_MARGIN = 5;	
	private IEditorController controller;
	private Composition document;
	private JFileChooser jFileChooser;
	private JMenuItem imageMenuItem;
	private JMenuItem aboutMenuItem;
	private JMenuItem exitMenuItem;
	private JMenuItem scrollMenuItem;	
	private ICompositor compositor;
	private int x1, y1, x2, y2;
	
	public MainFrame(Composition document, IEditorController controller){		
		super();		
		
		this.document = document;
		this.controller = controller;
		this.document.registerObserver(this);
		this.compositor = new SimpleCompositor();
		
		this.setTitle("Lexi - " + this.getNow());		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setBounds(100, 100, 200, 200);
		this.setLayout(new BorderLayout());
		
		JMenuBar menuBar = new JMenuBar();		
		this.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		this.imageMenuItem = new JMenuItem("Insert Image");
		this.imageMenuItem.addActionListener(this);
		mnFile.add(imageMenuItem);		
		
		this.scrollMenuItem = new JMenuItem(Constants.ScrollOnText);
		this.scrollMenuItem.addActionListener(this);
		mnFile.add(this.scrollMenuItem);
	
		this.exitMenuItem = new JMenuItem("Exit");
		this.exitMenuItem.addActionListener(this);
		mnFile.add(exitMenuItem);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		this.aboutMenuItem = new JMenuItem("About");
		this.aboutMenuItem.addActionListener(this);
		mnHelp.add(aboutMenuItem);
				
		this.addKeyListener(this);
		this.addComponentListener(this);
		this.addWindowListener(this);
		this.addMouseListener(this);
		
		this.setVisible(true);		
		
		this.x1 = this.y1 = -10;
		this.x2 = this.y2 = -20;
	}	

	@Override
	public void componentResized(ComponentEvent e) {	
		this.controller.handleResize();
		this.repaint(1);
	}
	
	@Override
	public void componentMoved(ComponentEvent e) {	
		this.controller.handleResize();
		this.repaint(1);
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	private Boolean scrollOn = false;
	
	@Override
	public void keyPressed(KeyEvent e) {		
			KeyPressedEventArgs param = new KeyPressedEventArgs(new SwingGraphics(this.getGraphics()), this.getTop(), this.getLeft(), this.getContentPane().getWidth(),
					this.getContentPane().getHeight(), e, this.getFont());
			this.controller.onKeyPressed(param);			
			if ((e.getKeyCode() == KeyEvent.VK_PAGE_UP || e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) && this.scrollOn){
				/* This is required because page up and down are not added to glyph model. So, the view update will never
				 * be called and then no repaint. So, if these control keys are pressed, we need to manually repaint and update the 
				 * view if the scroll is on. */
				
			}
			
			this.repaint(1);			
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void updateObserver(ModelChangedEventArgs args) {	
		/*ViewEventArgs param = new ViewEventArgs(new SwingGraphics(this.getGraphics()), this.getTop(), this.getLeft(), this.getContentPane().getWidth(),
				this.getContentPane().getHeight());
		List<Row> rows = this.compositor.compose(args.getGlyphs(), param);
		// System.out.println("at view update !!!");
		this.controller.handleDrawing(rows, param);*/
		this.repaint(1);
		
		
	}	
	
	@Override
	public void update(Graphics g){
		paint(g);
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		ViewEventArgs param = new ViewEventArgs(new SwingGraphics(this.getGraphics()), this.getTop(), this.getLeft(), this.getWidth(),
				this.getHeight());
		List<Row> rows = this.compositor.compose(this.document.getChildren(), param);		
		// System.out.println("from view -->");
		this.controller.handleDrawing(rows, param);	
		
		// draw selection
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(2));
		g2.drawLine(x1, y1, x2, y1);
		g2.drawLine(x2, y1, x2, y2);
		g2.drawLine(x2, y2, x1, y2);
		g2.drawLine(x1, y2, x1, y1);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {		
		if (e.getSource().equals(this.imageMenuItem)){
			this.onInsertImageMenuItemClick(e);
		}
		else if (e.getSource().equals(this.scrollMenuItem)){
			this.handleScrolling();
		}
		else if (e.getSource().equals(this.aboutMenuItem)){
			JOptionPane.showMessageDialog(this, "Lext editor implementation\nDeveloper: Amit Dutta" +
					"\nEmail: adutta@cis.uab.edu\nWeb: http://www.amitdutta.net", "Lexi", JOptionPane.INFORMATION_MESSAGE);
		}
		else if (e.getSource().equals(this.exitMenuItem)){
			this.document.removeObserver(this);
			this.dispose();
		}
	}	

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		this.document.removeObserver(this);		
	}

	@Override
	public void windowClosed(WindowEvent e) {		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private void handleScrolling(){
		this.controller.onMenuItemPressed(new MenuPressedEventArgs(this.scrollMenuItem));
		if (this.scrollMenuItem.getText() == Constants.ScrollOffText){
			this.scrollMenuItem.setText(Constants.ScrollOnText);
			scrollOn = false;
		}
		else{
			this.scrollMenuItem.setText(Constants.ScrollOffText);
			scrollOn = true;
		}
		
		this.repaint();
	}
	
	private String getNow(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	private JFileChooser getJFileChooser(){
		if (this.jFileChooser == null){
			this.jFileChooser = new JFileChooser();			
		}
		
		return this.jFileChooser;
	}
	
	private void onInsertImageMenuItemClick(ActionEvent evt){
		if(this.getJFileChooser().showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
			try {
				BufferedImage image = ImageIO.read(this.getJFileChooser().getSelectedFile());
				InsertImageEventArgs args = new InsertImageEventArgs(new SwingGraphics(this.getGraphics()), this.getTop(), this.getLeft(), this.getContentPane().getWidth(),
						this.getContentPane().getHeight(), image);
				this.controller.onImageInserted(args);
				
			}catch (IOException ex){
				ex.printStackTrace();				
			}
		}
	}
	
	private int getLeft(){
		return this.getInsets().left + LEFT_MARGIN;
	}
	
	private int getTop(){
		return this.getInsets().top + this.getJMenuBar().getHeight() + TOP_MARGIN;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		x1 = e.getX();
		y1 = e.getY();			
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		x2 = e.getX();
		y2 = e.getY();
		this.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}		
}
