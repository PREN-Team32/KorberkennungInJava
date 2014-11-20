/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package korberkennung.detektor;

import com.sun.prism.paint.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Nikk
 */
public class Detector {
    private BufferedImage original;
    private int brightPixCount = 0;
    private int darkPixCount = 0;
    protected static float LUMINANCETHRESHOLD = 0.3f;
    protected static int IMAGE_WIDTH = 488;
    protected static int IMAGE_HEIGHT = 500;
    
    private long zeitVorher;
    private long zeitNachher;
    
    
    public Detector(String imageName) {
        File file = new File(imageName);
        BufferedImage tmp;
        original = new BufferedImage(888, 500, BufferedImage.TYPE_INT_ARGB);
        try {
            tmp = ImageIO.read(file);
            //Resize the picture to 888x500 px
            Graphics2D g = original.createGraphics();
            g.drawImage(tmp, 0, 0, 888, 500, null);
            g.dispose();
            //Cut out the black borders (background)
            original = original.getSubimage(200, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    public BufferedImage getOriginal() {
        return original;
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
                
                
                //calc luminance in range 0.0 to 1.0; using SRGB luminance constants
                float luminance = (red * 0.2126f + green * 0.7152f + blue * 0.0722f) / 255;

                //choose brightness threshold as appropriate:
                if (luminance >= LUMINANCETHRESHOLD) {
                    original.setRGB(x, y, Color.WHITE.getIntArgbPre());
                    brightPixCount++;
                } else {
                    original.setRGB(x, y, Color.BLACK.getIntArgbPre());
                    darkPixCount++;
                }
                //System.out.println("Red Color value = " + red);
                //System.out.println("Green Color value = " + green);
                //System.out.println("Blue Color value = " + blue);
            }
        }
        zeitNachher = System.currentTimeMillis();
        long gebrauchteZeit = zeitNachher - zeitVorher;
        System.out.println("Bright | Dark Pixels: " + brightPixCount + " | " + darkPixCount);
        System.out.println("Zeit zum Analysieren der Pixel: " + gebrauchteZeit + " ms");
    }
    
    public int calculateMainArea() {
        int totalX = 0;
        int blackPixCount = 0;
        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {
                int rgbCode = original.getRGB(x, y);
                if(rgbCode == Color.BLACK.getIntArgbPre()) {
                    totalX += x;
                    blackPixCount++;
                }
            }
        }
        return totalX/blackPixCount;
    }
    
    public int findShape(int mainArea) {            
        //Seek shape of the basket, starting from the right side.
        if(mainArea < IMAGE_WIDTH/2) {
            //TODO
        }
        //Seek shape of the basket, starting from the left side.
        else if(mainArea > IMAGE_WIDTH/2) {
            for (int y = 0; y < original.getHeight(); y++) {
                //Care for visitedFields variable (x must be larger!!)
                for (int x = 5; x < original.getWidth()-5; x++) {
                    if(isBucketShape(x, y, false)) {
                        return x;
                    }
                }
            }
        }
        //Else, basket must be in the middle.
        else {
            return IMAGE_WIDTH/2;
        }
        System.out.println("No Shape found.");
        return Integer.MIN_VALUE;
    }
    
    private boolean isBucketShape(int x, int y, boolean fromLeft) {
        boolean isBucketShape = true;
        int visitedFields = 4;
        int[] rgbToLeft = new int[visitedFields];
        int[] rgbToRight = new int[visitedFields];
        
        for(int i = 0; i <= 4; i++) {
            rgbToLeft[i] = original.getRGB((x-1) - i, i);
            rgbToRight[i] = original.getRGB(x + i, i);
        }
        int rgbCurrentPixel = original.getRGB(x, y);
        
        if(fromLeft) {
            for(int i = 0; i <= 4; i++) {
                if(rgbToLeft[i] != Color.WHITE.getIntArgbPre() || rgbToRight[i] != Color.BLACK.getIntArgbPre()) {
                    isBucketShape = false;
                }
            }
        }
        else {
            for(int i = 0; i <= 4; i++) {
                if(rgbToLeft[i] != Color.BLACK.getIntArgbPre() || rgbToRight[i] != Color.WHITE.getIntArgbPre()) {
                    isBucketShape = false;
                }
            }
        }
        return isBucketShape;
        
    }
    
    
}
