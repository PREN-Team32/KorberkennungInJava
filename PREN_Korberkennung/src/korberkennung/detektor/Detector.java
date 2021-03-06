/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package korberkennung.detektor;

import com.sun.prism.paint.Color;
import java.awt.image.BufferedImage;
import static korberkennung.detektor.ImageHandler.WIDTH_TO_OBSERVE;

/**
 *
 * @author Nikk
 */
public class Detector {
    private BufferedImage editedImage;
    private BufferedImage originalImage;
    private int brightPixCount = 0;
    private int darkPixCount = 0;
    private int mainAreaX;
    private int mainAreaY;
    private long gebrauchteZeit;
    private int objectBorder;

    public long getGebrauchteZeit() {
        return gebrauchteZeit;
    }

    public int getMainAreaX() {
        return mainAreaX;
    }
    
    //Zu konfigurierende Variabeln
    protected static float LUMINANCETHRESHOLD = 0.5f;
    protected static int VISITED_PIXELS = 3; //Amount of visited adjacent Pixels to determine a shape.
    
    //Zur Zeitmessung
    private long zeitVorher;
    private long zeitNachher;
    
    
    public Detector(String imageName) {
        originalImage = ImageHandler.loadImage(imageName);
        editedImage = originalImage;
    }
    
    public BufferedImage getEditedImage() {
        return editedImage;
    }

    public BufferedImage getOriginalImage() {
        return originalImage;
    }
    
    public int getObjectBorder() {
        return objectBorder;
    }

    public static void setLuminanceThreshold(float LUMINANCETHRESHOLD) {
        Detector.LUMINANCETHRESHOLD = LUMINANCETHRESHOLD;
    }

    public static void setVisitedPixels(int VISITED_PIXELS) {
        Detector.VISITED_PIXELS = VISITED_PIXELS;
    }
    
    public void loadNewImage(String imageName) {
        originalImage = ImageHandler.loadImage(imageName);
        editedImage = originalImage;
    }
    
    public byte start() {
        //Step 1:
        //Looping through all Pixels, determine luminance and evaluate it against LUMINANCETHRESHOLD
        //to determine whether to color the pixel black or white
        //
        //Useful Hint:
        //Make sure to make the outer loop over the y-coordinate. This will likely make the code much 
        //faster, as it will be accessing the image data in the order it's stored in memory. (As rows of pixels.)
        zeitVorher = System.currentTimeMillis();
        for (int y = 0; y < editedImage.getHeight(); y++) {
            for (int x = 0; x < editedImage.getWidth(); x++) {
                int  clr   = editedImage.getRGB(x, y); 
                int  red   = (clr & 0x00ff0000) >> 16;
                int  green = (clr & 0x0000ff00) >> 8;
                int  blue  =  clr & 0x000000ff;
                
                
                //calc luminance in range 0.0 to 1.0; using SRGB luminance constants
                float luminance = (red * 0.2126f + green * 0.7152f + blue * 0.0722f) / 255;

                //choose brightness threshold as appropriate:
                if (luminance >= LUMINANCETHRESHOLD) {
                    editedImage.setRGB(x, y, Color.WHITE.getIntArgbPre());
                    brightPixCount++;
                } else {
                    editedImage.setRGB(x, y, Color.BLACK.getIntArgbPre());
                    darkPixCount++;
                }
            }
        }
        //Step 2:
        //Core detection mechanism
        this.objectBorder = findObject(calculateMainArea());
        
        //Step 3:
        //Evaluate results
        System.out.println("#Detektor: Object detected at X = " + objectBorder);
        System.out.println("#Detektor: Bright | Dark Pixels = " + brightPixCount + " | " + darkPixCount);
        byte steps = AngleCalculator.getSteps(objectBorder);
        zeitNachher = System.currentTimeMillis();
        this.gebrauchteZeit = zeitNachher - zeitVorher;
        System.out.println("#Detektor: Time used: " + gebrauchteZeit + " ms");
        return steps;
    }
    
    private int findObject(int mainArea) {
        int rgbCurrentPixel;
        int xCoordinate;
        System.out.println("#Detektor: Attempting to find bucket..");
        //Seek shape of the basket, starting from the right side.
        if(mainArea < WIDTH_TO_OBSERVE/2) {
            xCoordinate = Integer.MIN_VALUE;
            for (int y = editedImage.getHeight()-1; y > 0; y--) {
                //Care for visitedFields variable (x must be larger!!)
                for (int x = editedImage.getWidth()-5; x > 5; x--) {
                    rgbCurrentPixel = editedImage.getRGB(x, y);
                    if(rgbCurrentPixel == Color.BLACK.getIntArgbPre()) {
                        if(isBucketShape(x, y, false)) {
                            //System.out.print("(" + x + ", " + y + ") /");
                            editedImage.setRGB(x, y, Color.RED.getIntArgbPre());
                            if(x > xCoordinate) {
                                xCoordinate = x;
                            }
                        }
                    }
                }
            }
        }
        //Seek shape of the basket, starting from the left side.
        else if(mainArea > WIDTH_TO_OBSERVE/2) {
            xCoordinate = Integer.MAX_VALUE;
            for (int y = 0; y < editedImage.getHeight(); y++) {
                //Care for visitedFields variable (x must be larger!!)
                for (int x = 5; x < editedImage.getWidth()-5; x++) {
                    if(isBucketShape(x, y, true)) {
                        //System.out.print("(" + x + ", " + y + ") /");
                        editedImage.setRGB(x, y, Color.RED.getIntArgbPre());
                        if(x < xCoordinate) {
                            xCoordinate = x;
                        }
                    }
                }
            }
        }
        //Else, basket must be in the middle.
        else {
            xCoordinate = WIDTH_TO_OBSERVE/2;
        }
        
        if(xCoordinate == Integer.MIN_VALUE || xCoordinate == Integer.MAX_VALUE) {
            System.err.println("#Detektor: NO SHAPE FOUND.");
        }
        return xCoordinate;
    }
    
    private int calculateMainArea() {
        int totalX = 0;
        int totalY = 0;
        int blackPixCount = 0;
        for (int y = 0; y < editedImage.getHeight(); y++) {
            for (int x = 0; x < editedImage.getWidth(); x++) {
                int rgbCode = editedImage.getRGB(x, y);
                if(rgbCode == Color.BLACK.getIntArgbPre()) {
                    totalX += x;
                    totalY+= y;
                    blackPixCount++;                    
                }
            }
        }
        this.mainAreaX = totalX/blackPixCount;
        this.mainAreaY = totalY/blackPixCount;
        System.out.println("Found Main Area: " + mainAreaX);
        return mainAreaX;
    }
    
    private boolean isBucketShape(int x, int y, boolean fromLeft) {
        boolean isBucketShape = true;
        int[] rgbToLeft = new int[VISITED_PIXELS];
        int[] rgbToRight = new int[VISITED_PIXELS];
        
        for(int i = 0; i < VISITED_PIXELS; i++) {
            rgbToLeft[i] = editedImage.getRGB(x - (i+1), y);
            rgbToRight[i] = editedImage.getRGB(x + (i+1), y);
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

    public void drawMainArea() {
        ImageHandler.drawVerticalLine(editedImage, mainAreaX, Color.BLUE);
    }
    
    public void drawObjectBorder() {
        ImageHandler.drawVerticalLine(editedImage, objectBorder, Color.GREEN);
    }
    
    public double calculateAngle(int objectBorder) {
        double angle = 0.0;
        
        
        return angle;
    }
}

