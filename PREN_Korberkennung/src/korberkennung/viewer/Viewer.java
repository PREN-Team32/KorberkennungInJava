/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package korberkennung.viewer;

import java.awt.Panel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.filechooser.FileNameExtensionFilter;
import korberkennung.detektor.Detector;

/**
 *
 * @author Niklaus
 */
public class Viewer extends Panel {
    private Detector detector;
    private JFrame frame = new JFrame();
    private ImageIcon image;
    private JLabel imageLabel;
    private JButton detectButton;
    
    public Viewer(Detector detector) {
        this.detector = detector;
        frame.setSize(700, 700);
        image = new ImageIcon(detector.getOriginal());
        imageLabel = new JLabel("", image, JLabel.CENTER);
        frame.add(imageLabel);
        frame.setTitle("Korberkennung in Java");
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
    
    private void loadPicture() {
        JFileChooser chooser = new JFileChooser(); 
        chooser.setFileFilter(new FileNameExtensionFilter("Pictures", "jpg", "jpeg", "bmp"));
        int r = chooser.showOpenDialog(this); 
        String s = "no File!"; 
        if (r == JFileChooser.APPROVE_OPTION) 
        {          
            s= chooser.getSelectedFile().getPath();
            System.out.println(s);
            
            setVisible(false);
        } 
    }
}
