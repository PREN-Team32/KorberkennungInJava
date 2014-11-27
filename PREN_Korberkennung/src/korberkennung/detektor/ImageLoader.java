/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package korberkennung.detektor;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Niklaus
 */
public class ImageLoader {        
    protected static int INITIAL_IMAGE_WIDTH = 888;
    protected static int INITIAL_IMAGE_HEIGHT = 500;
    protected static int WIDTH_TO_OBSERVE = 488;
    protected static int HEIGHT_TO_OBSERVE = 500;

    public static void setObservedWidth(int WIDTH_TO_OBSERVE) {
        ImageLoader.WIDTH_TO_OBSERVE = WIDTH_TO_OBSERVE;
    }

    public static void setObservedHeight(int HEIGHT_TO_OBSERVE) {
        ImageLoader.HEIGHT_TO_OBSERVE = HEIGHT_TO_OBSERVE;
    }
    
    public static BufferedImage loadImage(String imageName) {
        File file = new File(imageName);
        BufferedImage image;
        BufferedImage tmp;
        image = new BufferedImage(INITIAL_IMAGE_WIDTH, INITIAL_IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        try {
            tmp = ImageIO.read(file);
            //Resize the picture to 888x500 px (= INITIAL_IMAGE_WIDHT & _HEIGHT)
            Graphics2D g = image.createGraphics();
            g.drawImage(tmp, 0, 0, INITIAL_IMAGE_WIDTH, INITIAL_IMAGE_HEIGHT, null);
            g.dispose();
            //Cut out the black borders (background)
            image = image.getSubimage((INITIAL_IMAGE_WIDTH-WIDTH_TO_OBSERVE)/2,(INITIAL_IMAGE_HEIGHT-HEIGHT_TO_OBSERVE)/2 , WIDTH_TO_OBSERVE, HEIGHT_TO_OBSERVE);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return image;
    }
}
