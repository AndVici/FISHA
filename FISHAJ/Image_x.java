/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fisha;

import java.util.*;
import java.lang.*;
import java.awt.image.*;

/**
 *
 * @author willie
 */
public class Image_x{
    
    
    public static class Histograms{
        
        //int[] i_hist;
        double[] red_hist;
        double[] green_hist;
        double[] blue_hist;
        double[] magnitude;
        double[] direction;
        
        public Histograms(){
            //this.i_hist = null;
            this.red_hist = null;
            this.green_hist = null;
            this.blue_hist = null;
            this.magnitude = null;
            this.direction = null;
        }
        
        public Histograms(int[] i, double[] red, double[] green, double[] blue){
            //this.i_hist = i;
            this.red_hist = red;
            this.green_hist = green;
            this.blue_hist = blue;
        }
        
    }
    
    public static class Pair<M, D>{
        public M m;
        public D d;
    
        public Pair(){
            this.m = null;
            this.d = null;
        }
        
        public Pair(M mm, D dd){
            this.m = mm;
            this.d = dd;
        }
    }
    
    
    
    public static class ImageX{
        
        String ID;
        Histograms histograms;
        Pair<Integer, Integer> Size;
        BufferedImage original;
        BufferedImage gradient_x;
        BufferedImage gradient_y;
        List<Pair<Integer, Integer>> color_coherence_vectorX;
        List<Double> magnitude;
        
        public ImageX(){
            this.ID = null;
            this.histograms = null;
            this.gradient_x = null;
            this.gradient_y = null;
            this.Size = null;
            this.original = null;
            this.color_coherence_vectorX = null;
            this.magnitude = null;
        }
        
        public ImageX(BufferedImage o, Histograms h, Pair s){
            this.original = o;
            this.histograms = h;
            this.Size = s;
        }
    }
}
    
    
