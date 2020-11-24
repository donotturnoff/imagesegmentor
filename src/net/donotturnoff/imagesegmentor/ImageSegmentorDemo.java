package net.donotturnoff.imagesegmentor;

import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.*;

public class ImageSegmentorDemo extends JFrame implements ActionListener {
	
	public static void main(String[] args) {
		ImageSegmentorDemo demo = new ImageSegmentorDemo();
	}
	
	private ImageSegmentor segmentor;
	private JButton openButton, processButton, saveButton;
	private JSpinner kSpinner, iterationsSpinner;
	private JPanel controlPanel, previewPanel;
	private JLabel beforeLabel, afterLabel;
	private BufferedImage in, out;
	private JProgressBar progressBar;
	
	public ImageSegmentorDemo() {
		super("Image Segmentor");
		setSize(800, 500);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container contentPane = getContentPane();
		
		controlPanel = new JPanel();
		
		openButton = new JButton("Open");
		processButton = new JButton("Process");
		saveButton = new JButton("Save");
		kSpinner = new JSpinner(new SpinnerNumberModel(16, 1, Integer.MAX_VALUE, 1));
		iterationsSpinner = new JSpinner(new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1));
		progressBar = new JProgressBar(0, 100);
		
		openButton.addActionListener(this);
		processButton.addActionListener(this);
		saveButton.addActionListener(this);
		
		processButton.setEnabled(false);
		saveButton.setEnabled(false);
		progressBar.setEnabled(false);
		
		Component kSpinnerEditor = kSpinner.getEditor();
		JFormattedTextField kSpinnerFormattedTextField = ((JSpinner.DefaultEditor) kSpinnerEditor).getTextField();
		kSpinnerFormattedTextField.setColumns(5);
		
		Component iterationsSpinnerEditor = iterationsSpinner.getEditor();
		JFormattedTextField iterationsSpinnerFormattedTextField = ((JSpinner.DefaultEditor) iterationsSpinnerEditor).getTextField();
		iterationsSpinnerFormattedTextField.setColumns(5);
		
		progressBar.setStringPainted(true);
		progressBar.setString("Idle");
		
		controlPanel.add(openButton);
		controlPanel.add(processButton);
		controlPanel.add(saveButton);
		controlPanel.add(new JLabel("Colours"));
		controlPanel.add(kSpinner);
		controlPanel.add(new JLabel("Iterations"));
		controlPanel.add(iterationsSpinner);
		
		previewPanel = new JPanel(new GridLayout(1, 2));
		
		beforeLabel = new JLabel("Open a file using the \"Open button\"");
		afterLabel = new JLabel("Press the \"Process\" button to process the image");
		
		previewPanel.add(beforeLabel);
		previewPanel.add(afterLabel);
		
		contentPane.add("North", controlPanel);
		contentPane.add("Center", previewPanel);
		contentPane.add("South", progressBar);
		
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		JButton source = (JButton) e.getSource();
		if (source == openButton) {
			open();
		} else if (source == processButton) {
			process();
		} else if (source == saveButton) {
			save();
		}
	}
	
	private void open() {
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "jpeg", "tiff", "bmp");
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			BufferedImage prevIn = in;
			try {
				in = ImageIO.read(file);
				beforeLabel = new JLabel(new ImageIcon(getScaledImage(in, 380)));
				afterLabel = new JLabel("Press the \"Process\" button to process the image");
				progressBar.setValue(0);
				progressBar.setString("Idle");
				processButton.setEnabled(true);
			} catch (Exception e) {
				in = prevIn;
				progressBar.setValue(0);
				progressBar.setString("Could not open file");
			} finally {
				previewPanel.removeAll();
				previewPanel.add(beforeLabel);
				previewPanel.add(afterLabel);
				revalidate();
				repaint();
			}
		}
	}
	
	private void process() {
		openButton.setEnabled(false);
		processButton.setEnabled(false);
		saveButton.setEnabled(false);
		kSpinner.setEnabled(false);
		iterationsSpinner.setEnabled(false);
		new Thread(new Runnable() {
			@Override
			public void run() {
				progressBar.setIndeterminate(false);
				progressBar.setEnabled(true);
				progressBar.setValue(0);
				
				progressBar.setString("Initialising");
				int k = (int) kSpinner.getValue();
				int iterations = (int) iterationsSpinner.getValue();
				segmentor = new ImageSegmentor(in, k, iterations);
				progressBar.setValue(5);
				
				progressBar.setString("Parsing");
				segmentor.parse();
				progressBar.setValue(25);
				
				progressBar.setString("Segmenting");
				segmentor.segment();
				progressBar.setValue(55);
				
				progressBar.setString("Mapping");
				segmentor.map();
				progressBar.setValue(65);
				
				progressBar.setString("Updating");
				segmentor.update();
				progressBar.setValue(95);
				
				progressBar.setString("Fetching output");
				out = segmentor.getOutput();
				
				afterLabel = new JLabel(new ImageIcon(getScaledImage(out, 380)));
				previewPanel.removeAll();
				previewPanel.add(beforeLabel);
				previewPanel.add(afterLabel);
				previewPanel.removeAll();
				previewPanel.add(beforeLabel);
				previewPanel.add(afterLabel);
				openButton.setEnabled(true);
				processButton.setEnabled(true);
				saveButton.setEnabled(true);
				kSpinner.setEnabled(true);
				iterationsSpinner.setEnabled(true);
				
				Toolkit.getDefaultToolkit().beep();
				progressBar.setValue(100);
				progressBar.setString("Done");
				
				revalidate();
				repaint();
			}
		}).start();
	}
	
	private void save() {
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "jpeg", "tiff", "bmp");
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(filter);
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			try {
				ImageIO.write(out, "png", file);
				progressBar.setValue(100);
				progressBar.setString("Saved");
			} catch (Exception e) {
				progressBar.setValue(0);
				progressBar.setString("Could not save");
			}
		}
	}
	
	private BufferedImage getScaledImage(BufferedImage src, int w) {
		int h = src.getHeight()*w/src.getWidth();
		
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(src, 0, 0, w, h, null);
		g2.dispose();

		return resizedImg;
	}
}
