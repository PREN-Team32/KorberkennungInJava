/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package korberkennung.main;

import korberkennung.detektor.KorbDetektor;

/**
 *
 * @author Nikk
 */
public class Main {
    private static final String FILEPATH = "test.jpg";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        KorbDetektor detektor = new KorbDetektor(FILEPATH);
        detektor.start();
    }
    
}
