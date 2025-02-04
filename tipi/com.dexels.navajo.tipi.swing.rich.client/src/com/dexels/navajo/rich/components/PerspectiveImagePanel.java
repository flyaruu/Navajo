package com.dexels.navajo.rich.components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Transparency;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import org.jdesktop.animation.timing.interpolation.PropertySetter;

public class PerspectiveImagePanel extends JPanel {
	private static final long serialVersionUID = -7178013826296851126L;
	BufferedImage img, img1, img2;
	public int angle = 0;
	private int previousAngle;
	private BufferedImage currentImg;
	private int direction = PerspectiveTransform.FLIP_RIGHT;
	private Animator a;
	private JComponent c2;
	private int speed = 500;

	public void setComponents(JComponent c1, JComponent c2) {
//		this.c1 = c1;
		this.c2 = c2;

		getGraphicsConfiguration().createCompatibleImage(c1.getWidth(), c1.getHeight());
		BufferedImage b1 = getGraphicsConfiguration().createCompatibleImage(c1.getWidth(), c1.getHeight(), Transparency.TRANSLUCENT);
		BufferedImage b2 = getGraphicsConfiguration().createCompatibleImage(c2.getWidth(), c2.getHeight(), Transparency.TRANSLUCENT);
		Graphics2D b1g = b1.createGraphics();
		Graphics2D b2g = b2.createGraphics();

//		Rectangle c1Bounds = c1.getBounds();
//		Rectangle c2Bounds = c2.getBounds();
//
//		Graphics c1g = c1.getGraphics();

		c1.print(b1g);
		b1g.dispose();
		img1 = b1;
		setAngle(0);

		c1.setVisible(false);
		c2.setVisible(true);
		c2.print(b2g);
		b2g.dispose();
		img2 = b2;

		img = img1;

		c1.setVisible(false);
		c2.setVisible(false);
	}

	public PerspectiveImagePanel() {
		try {
			setOpaque(false);
			a = PropertySetter.createAnimator(speed, this, "angle", 0, 84);
			a.setAcceleration(0.5f);
			a.addTarget(new TimingTargetAdapter() {
				public void end() {
					flipImage();
					setDirection(getOppositeDirection());
					Animator b = PropertySetter.createAnimator(speed, PerspectiveImagePanel.this, "angle", 84, 0);
					b.setDeceleration(0.5f);
					b.addTarget(new TimingTargetAdapter() {
						public void end() {
							setAngle(0);
							setDirection(getOppositeDirection());
							PerspectiveImagePanel.this.setVisible(false);
							c2.setVisible(true);
						}
					});
					b.start();
				}
			});

			addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setSpeed(int halftimeMillis) {
		this.speed = halftimeMillis;
	}

	public PerspectiveImagePanel(int angle) {
		try {
			this.angle = angle;
			img1 = ImageIO.read(new File("/home/aphilip/jmlogo.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setAngle(int angle) {
		this.angle = angle;
		repaint();
	}

	public void paintComponent(Graphics g) {
		Graphics2D graf = (Graphics2D) g;
		Graphics2D g2 = (Graphics2D) graf.create();

//		float grey_factor = (float) angle * 1.0f / 80.0f;
		if (currentImg != null && angle == previousAngle) {
			g2.drawImage(currentImg, 0, 0, currentImg.getWidth(), currentImg.getHeight(), null);
		} else if (img != null) {
			if (angle > 0 && angle < 85) {
				previousAngle = angle;
				currentImg = PerspectiveTransform.transform(img, angle, true, direction);//
				g2.drawImage(currentImg, 0, 0, currentImg.getWidth(), currentImg.getHeight(), null);
				try {
					// ImageIO.write(currentImg, "png", new
					// File("/home/aphilip/Desktop/img" + angle + ".png"));
					// ImageIO.write(img2, "png", new
					// File("/home/aphilip/Desktop/img2.png"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				currentImg = img;
				g2.drawImage(currentImg, 0, 0, currentImg.getWidth(), currentImg.getHeight(), null);
			}
		}
		g2.dispose();
	}

//	private BufferedImage createReflection(BufferedImage img, int reflectionSize) {
//		int height = img.getHeight();
//		BufferedImage result = new BufferedImage(img.getWidth(), (height + reflectionSize), BufferedImage.TYPE_INT_ARGB);
//		Graphics2D g2 = result.createGraphics();
//
//		g2.drawImage(img, 0, 0, null);
//		g2.scale(1.0, -1.01);
//		g2.drawImage(img, 0, -height - height, null);
//		g2.scale(1.0, -1.0);
//		g2.translate(0, height);
//
//		GradientPaint mask = new GradientPaint(0, 0, new Color(1.0f, 1.0f, 1.0f, 0.5f), 0, reflectionSize,
//				new Color(1.0f, 1.0f, 1.0f, 0.0f));
//		g2.setPaint(mask);
//		g2.setComposite(AlphaComposite.DstIn);
//		g2.fillRect(0, 0, img.getWidth(), reflectionSize);
//		g2.dispose();
//		return result;
//	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public void flipImage() {
		if (img == img1) {
			img = img2;
		} else {
			img = img1;
		}
	}

	public int getDirection() {
		return this.direction;
	}

	public int getOppositeDirection() {
		switch (direction) {
		case PerspectiveTransform.FLIP_DOWN:
			return PerspectiveTransform.FLIP_UP;
		case PerspectiveTransform.FLIP_UP:
			return PerspectiveTransform.FLIP_DOWN;
		case PerspectiveTransform.FLIP_LEFT:
			return PerspectiveTransform.FLIP_RIGHT;
		case PerspectiveTransform.FLIP_RIGHT:
			return PerspectiveTransform.FLIP_LEFT;
		default:
			return PerspectiveTransform.FLIP_LEFT;
		}
	}

	public void flip() {
		if (!a.isRunning()) {
			currentImg = null;
			a.setDuration(speed);
			a.start();
		}
	}

	public static void main(String[] args) {

		System.err.println("CENtER " + GridBagConstraints.SOUTH);

		/*
		 * JFrame frame = new JFrame("aap");
		 * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 * frame.getContentPane().setLayout(null); final PerspectiveImagePanel p
		 * = new PerspectiveImagePanel(); p.setBackground(new
		 * Color(80,160,224)); JButton b = new JButton("Go"); b.setBounds(0,
		 * 550, 80, 30); p.setBounds(100, 10, 400, 600);
		 * frame.getContentPane().add(p); frame.getContentPane().add(b);
		 * frame.getContentPane().setBackground(p.getBackground());
		 * 
		 * final JSlider slide = new JSlider(0,84); slide.setBounds(0, 580, 180,
		 * 30); slide.setValue(0); slide.addChangeListener(new ChangeListener(){
		 * 
		 * public void stateChanged(ChangeEvent e) { 
		 * method stub p.setAngle(slide.getValue()); } });
		 * frame.getContentPane().add(slide); // JPanel p1 = new JPanel(); //
		 * JPanel p2 = new JPanel(); // // JButton apenoot = new
		 * JButton("Hallo daar"); // JTextField nootjes = new
		 * JTextField("Hoi!");
		 * 
		 * // p1.setLayout(new BorderLayout()); // p2.setLayout(new
		 * BorderLayout()); // p1.add(apenoot); // p2.add(nootjes); //
		 * p1.setBounds(0,0,200,200); // p2.setBounds(0,0,200,200); //
		 * p.setLayout(new BorderLayout()); // p.add(p1, BorderLayout.CENTER);
		 * // p.add(p2, BorderLayout.CENTER); // p.setComponents(p1, p2);
		 * 
		 * JButton flip = new JButton("flip direction");
		 * flip.addActionListener(new ActionListener(){ public void
		 * actionPerformed(ActionEvent e){
		 * p.setDirection(p.getOppositeDirection());
		 * p.setAngle(slide.getValue()+1); } }); flip.setBounds(0, 610, 180,
		 * 30); frame.getContentPane().add(flip);
		 * 
		 * 
		 * p.setDirection(PerspectiveTransform.FLIP_LEFT);
		 * frame.setSize(1024,768); frame.setVisible(true);
		 */
	}

}
