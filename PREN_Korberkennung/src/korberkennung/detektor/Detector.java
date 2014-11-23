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
    
    //Zuvor zu konfigurierende Variabeln
    protected static float LUMINANCETHRESHOLD = 0.3f;
    protected static int INITIAL_IMAGE_WIDTH = 888;
    protected static int INITIAL_IMAGE_HEIGHT = 500;
    protected static int FINAL_IMAGE_WIDTH = 488;
    protected static int FINAL_IMAGE_HEIGHT = 500;
    protected static int VISITED_PIXELS = 3; //Amount of visited adjacent Pixels to determine a shape.
    
    //Zur Zeitmessung
    private long zeitVorher;
    private long zeitNachher;
    
    
    public Detector(String imageName) {
        File file = new File(imageName);
        BufferedImage tmp;
        original = new BufferedImage(INITIAL_IMAGE_WIDTH, INITIAL_IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        try {
            tmp = ImageIO.read(file);
            //Resize the picture to 888x500 px (= INITIAL_IMAGE_WIDHT & _HEIGHT)
            Graphics2D g = original.createGraphics();
            g.drawImage(tmp, 0, 0, INITIAL_IMAGE_WIDTH, INITIAL_IMAGE_HEIGHT, null);
            g.dispose();
            //Cut out the black borders (background)
            original = original.getSubimage(200, 0, FINAL_IMAGE_WIDTH, FINAL_IMAGE_HEIGHT);
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
            }
        }
        int objectBorder = findObject(calculateMainArea());
        zeitNachher = System.currentTimeMillis();
        long gebrauchteZeit = zeitNachher - zeitVorher;
        System.out.println("Objekt erkannt bei X = " + objectBorder);
        System.out.println("Bright | Dark Pixels: " + brightPixCount + " | " + darkPixCount);
        System.out.println("Zeit gebraucht: " + gebrauchteZeit + " ms");
    }
    
    private int calculateMainArea() {
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
    
    public int findObject(int mainArea) {
        int rgbCurrentPixel;
        int xCoordinate;
        System.out.println("Attempting to find bucket..");
        //Seek shape of the basket, starting from the right side.
        if(mainArea < FINAL_IMAGE_WIDTH/2) {
            xCoordinate = Integer.MIN_VALUE;
            for (int y = original.getHeight()-1; y > 0; y--) {
                //Care for visitedFields variable (x must be larger!!)
                for (int x = original.getWidth()-5; x > 5; x--) {
                    rgbCurrentPixel = original.getRGB(x, y);
                    if(rgbCurrentPixel == Color.BLACK.getIntArgbPre()) {
                        if(isBucketShape(x, y, false)) {
                            //System.out.print("(" + x + ", " + y + ") /");
                            original.setRGB(x, y, Color.RED.getIntArgbPre());
                            if(x > xCoordinate) {
                                xCoordinate = x;
                            }
                        }
                    }
                }
            }
        }
        //Seek shape of the basket, starting from the left side.
        else if(mainArea > FINAL_IMAGE_WIDTH/2) {
            xCoordinate = Integer.MAX_VALUE;
            for (int y = 0; y < original.getHeight(); y++) {
                //Care for visitedFields variable (x must be larger!!)
                for (int x = 5; x < original.getWidth()-5; x++) {
                    if(isBucketShape(x, y, true)) {
                        //System.out.print("(" + x + ", " + y + ") /");
                        original.setRGB(x, y, Color.RED.getIntArgbPre());
                        if(x < xCoordinate) {
                            xCoordinate = x;
                        }
                    }
                }
            }
        }
        //Else, basket must be in the middle.
        else {
            xCoordinate = FINAL_IMAGE_WIDTH/2;
        }
        
        if(xCoordinate == Integer.MIN_VALUE || xCoordinate == Integer.MAX_VALUE) {
            System.out.println("No shape found.");
        }
        System.out.print("\n");
        return xCoordinate;
    }
    
    private boolean isBucketShape(int x, int y, boolean fromLeft) {
        boolean isBucketShape = true;
        int[] rgbToLeft = new int[VISITED_PIXELS];
        int[] rgbToRight = new int[VISITED_PIXELS];
        
        for(int i = 0; i < VISITED_PIXELS; i++) {
            rgbToLeft[i] = original.getRGB(x - (i+1), y);
            rgbToRight[i] = original.getRGB(x + (i+1), y);
        }
        
        if(fromLeft) {
            for(int i = 0; i < VISITED_PIXELS; i++) {
                if(rgbToLeft[i] != Color.WHITE.getIntArgbPre() || rgbToRight[i] != Color.BLACK.getIntArgbPre()) {
                    isBucketShape = false;
                }
            }
        }
        else {
            for(int i = 0; i < VISITED_PIXELS; i++) {
                if(rgbToLeft[i] != Color.BLACK.getIntArgbPre() || rgbToRight[i] != Color.WHITE.getIntArgbPre()) {
                    isBucketShape = false;
                }
            }
        }
        //System.out.println("(" + x + ", " + y + ", " + isBucketShape + ")");
        return isBucketShape;        
    }
}

