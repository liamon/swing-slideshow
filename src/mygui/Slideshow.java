package mygui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class Slideshow extends JFrame {
	
	private String[] slideshows = new String[]{"Animals", "Flowers", "Food"};
	private int slidesPerShow = 4;
	// At the start, the first option in the zero-indexed JComboBox will be selected.
	private int currentSlideshow = 0;
	
	public static void main(String[] args) {
		Slideshow slideshow = new Slideshow();
		
		// The public static EXIT_ON_CLOSE is inherited by Slideshow,
		// so I do not need to say 'JFrame.' before it.
		slideshow.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// I initially forgot these two lines, so the program terminated instantly.
		slideshow.setSize(800, 600);
		slideshow.setVisible(true);
	}
	
	public Slideshow() {
		super("Slideshow");
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		
		// For a while, I was trying to use the BorderLayout of the JFrame's content
		// pane to hold the different slideshow. However, in contrast with CardLayout,
		// BorderLayout only "remembers" the most recently added element, so it didn't work.
		CardLayout slideshowDeck = new CardLayout();
		JPanel slideSpace = new JPanel(slideshowDeck);
		JPanel[] slidePanels = new JPanel[slideshows.length];
		
		Icon[][] slides = new ImageIcon[slideshows.length][slidesPerShow];
		JLabel[] labelsForSlides = new JLabel[slideshows.length];
		
		for (int i = 0; i < slideshows.length; i++) {
			slidePanels[i] = new JPanel(); // JPanels default to FlowLayout.
			for (int j = 0; j < slidesPerShow; j++) {
				// All images are public domain from snappygoat.com
				// I deliberately made the image file names follow this pattern so
				// creating ImageIcons from them programmatically would be easier.
				slides[i][j] = new ImageIcon(
					"img" + File.separator + slideshows[i] + File.separator + j + ".jpg"
				);
			}
			labelsForSlides[i] = new JLabel(slides[i][0]);
			slidePanels[i].add(labelsForSlides[i]);
			slideSpace.add(slidePanels[i], slideshows[i]);
		}
		container.add(slideSpace, BorderLayout.CENTER);
		
		// JComboBox supports generics, so for holding Strings, I do this:
		JComboBox<String> options = new JComboBox<String>(slideshows);
		options.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				currentSlideshow = options.getSelectedIndex();
				slideshowDeck.show(slideSpace, slideshows[currentSlideshow]);
			}
		});
		JPanel comboBoxPanel = new JPanel();
		comboBoxPanel.add(options);
		container.add(comboBoxPanel, BorderLayout.NORTH);
		
		JPanel buttonPanel = new JPanel(new GridLayout(1, slideshows.length, 4, 0));
		JButton[] buttons = new JButton[slideshows.length];
		
		for (int i = 0; i < slideshows.length; i++) {
			buttons[i] = new JButton(slideshows[i] + " background color");
			buttons[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					for (int i = 0; i < slideshows.length; i++) {
						if (e.getActionCommand().startsWith(slideshows[i])) {
							// Because this is in an inner class, I have to say
							// Slideshow.this to get the instance of the outer Slideshow.
							Color color = JColorChooser.showDialog(
								Slideshow.this,
								"Pick a background color for " + slideshows[i],
								// Default JPanel background, see https://stackoverflow.com/a/9993139
								UIManager.getColor("Panel.background")
							);
							// Even if the chosen panel is not visible, the color change still occurs.
							slidePanels[i].setBackground(color);
							break;
						}
					}
				}
			});
			buttonPanel.add(buttons[i]);
		}
		container.add(buttonPanel, BorderLayout.SOUTH);
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				int currentSlide = 0;
				while (true) {
					labelsForSlides[currentSlideshow].setIcon(slides[currentSlideshow][currentSlide]);
					// Next line prevents currentSlide from being bigger than slidesPerShow - 1.
					currentSlide = (currentSlide + 1) % slidesPerShow;
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();
	}
}
