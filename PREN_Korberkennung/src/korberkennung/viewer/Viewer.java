/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package korberkennung.viewer;

import java.awt.Graphics;
import java.awt.Panel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import korberkennung.detektor.Detector;

/**
 *
 * @author Niklaus
 */
public class Viewer extends Panel {
    private Detector detector;
    private JFrame frame = new JFrame();
    private ImageIcon image;
    private JLabel label;    
    
    public Viewer(Detector detector) {
        this.detector = detector;
        frame.setSize(700, 700);
        image = new ImageIcon(detector.getOriginal());
        label = new JLabel("", image, JLabel.CENTER);
        frame.add(label);
        frame.setVisible(true);
    }
}
