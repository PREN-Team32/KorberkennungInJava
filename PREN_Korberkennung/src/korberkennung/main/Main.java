/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package korberkennung.main;

import korberkennung.detektor.Detector;
import korberkennung.desktopviewer.Viewer;

/**
 *
 * @author Nikk
 */
public class Main {
    private static final String FILEPATH = "WinPhone1"
            + ".jpg";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        // TODO code application logic here
        Detector detektor = new Detector(FILEPATH);
        Viewer viewer = new Viewer(detektor);
        Thread.sleep(1000);
        detektor.start();
        Viewer viewer2 = new Viewer(detektor);
    }
    
}
