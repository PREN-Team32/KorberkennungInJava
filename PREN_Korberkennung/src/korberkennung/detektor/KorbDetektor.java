/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package korberkennung.detektor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;
import javax.imageio.ImageIO;

/**
 *
 * @author Nikk
 */
public class KorbDetektor {
    private BufferedImage original;
    
    private long zeitVorher;
    private long zeitNachher;
    
    
    public KorbDetektor(String imageName) {
        File file = new File(imageName);
        try {
            original = ImageIO.read(file);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    public void start() {
        //Make sure to make the outer loop over the y-coordinate. 
        //This will likely make the code much faster, as it will be accessing the image data in the order it's stored in memory. (As rows of pixels.)
        zeitVorher = System.currentTimeMillis();
        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {
                int  clr   = original.getRGB(x, y); 
                int  red   = (clr & 0x00ff0000) >> 16;
                int  green = (clr & 0x0000ff00) >> 8;
                int  blue  =  clr & 0x000000ff;
                original.setRGB(x, y, clr);
                
                //System.out.println("Red Color value = " + red);
                //System.out.println("Green Color value = " + green);
                //System.out.println("Blue Color value = " + blue);
            }
        }
        zeitNachher = System.currentTimeMillis();
        long gebrauchteZeit = zeitNachher - zeitVorher;
        System.out.println("Zeit benötigt: " + gebrauchteZeit + " ms");
    }
}
