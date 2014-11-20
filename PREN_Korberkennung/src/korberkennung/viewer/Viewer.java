/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package korberkennung.viewer;

import java.awt.Panel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
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
        label.setBounds(0, 0, 1366, 768);
        frame.add(label);
        frame.setTitle("Korberkennung in Java");
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}
