/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fisha;

import fisha.Image_x.*;
import fisha.FISHA.*;
import java.awt.Image.*;
import java.util.*;
import java.lang.*;

/**
 *
 * @author willie
 */
public class OneDclass {
    
    public static class ODclust{
    
        double distance;
        Boolean similar;
        String ID;
        
        public ODclust(){
            this.distance = 0;
            this.similar = null;
            this.ID = null;
        }
        
        public ODclust(double img, Boolean s, String name){
            this.distance = img;
            this.similar = s;
            this.ID = name;
        }
    }
    
    public static List<ODclust>classifier(List<ODclust> images, double thresh){
        for(int i=0; i<images.size(); i++){
            if(images.get(i).distance <= thresh)
                images.get(i).similar = true;
            else
                images.get(i).similar = false;
        }
        
        return images;
    }
    
    
    public static List<Double> OD_classification(List<ODclust> images, int start, double incrmnt, int max){
         double threshold = start, n_fa, n_miss, n_wrong;
         List<Double> best_thresh = new ArrayList();
         double best_miss_class = Double.POSITIVE_INFINITY;
         for(; threshold < max; threshold += incrmnt){
             n_fa = 0;
             n_miss = 0;
            for(int i=0; i<images.size(); i++){
                if(images.get(i).distance <= threshold && images.get(i).similar == false)
                    n_fa += 1;
                else if(images.get(i).distance > threshold && images.get(i).similar == true)
                    n_miss += 1;
            }
            n_wrong = n_fa + n_miss;
            if(n_wrong < best_miss_class){
                best_miss_class = n_wrong;
                best_thresh.clear();
                best_thresh.add(threshold);
            }
            else if(n_wrong == best_miss_class)
                best_thresh.add(threshold);
        }
         
         return best_thresh;
    }
    
}
