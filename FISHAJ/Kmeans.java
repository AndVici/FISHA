/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fisha;

import java.util.*;
import java.lang.*;
import java.util.stream.IntStream;
import java.io.*;
import fisha.Image_x.*;
import fisha.DBScan.*;
/**
 *
 * @author willie
 */
public class Kmeans {
    
    public static class KCluster{
        
        ImageX centroid;
        ImageX medoid;
        List<ImageX> R;
        
        public KCluster(ImageX c, ImageX m, List<ImageX> r){
            
            this.centroid = c;
            this.medoid = m;
            this.R = r;
        }
        
        public KCluster(){
            this.centroid = null;
            this.medoid = null;
            this.R = null;
        }
    }
    
    public static List<ImageX> seedGen(List<ImageX> img, int k){
        List<ImageX> seeds = new ArrayList();
        int[] nums = new int[k];
        int max = img.size(), ind;
        Random r = new Random();
        while(k > 0){
             ind = r.nextInt(max);
             if(seeds.contains(img.get(ind)) == false){
                 nums[k-1] = ind;
                 seeds.add(img.get(ind));
                 k--;
             }
        }
        for(int i=0; i < k; i++)
            img.remove(nums[i]);
        
        return seeds;
    }
    
    public static List<KCluster> make_clusts(int k, List<ImageX> imgs){
        List<KCluster> clusts = new ArrayList();
        
        for(int i = 0; i < k; i++){
            KCluster cl = new KCluster(imgs.get(i), imgs.get(i), new ArrayList());
            clusts.add(cl);
        }
        return clusts;
    }
    
    
    public static double distance(KCluster C, ImageX img){
        
        double dist = DBScan.total_dist(C.medoid, img);
        
        return dist;
    }
    
    public static List<KCluster> Kmeans(List<ImageX> img, int k){
        List<ImageX> seeds = seedGen(img, k);
        List<KCluster> clusts = make_clusts(seeds.size(), seeds);
        
        for(int i=0; i<img.size(); i++){
            double least_d = Double.POSITIVE_INFINITY;
            int index = 0;
            for(int j=0; j<clusts.size(); j++){
                double dist = DBScan.total_dist(clusts.get(j).medoid, img.get(i));
                if(dist < least_d){
                    least_d = dist;
                    index = j;
                }
            }
            clusts.get(index).R.add(img.get(i));
            
        }
        return clusts;
        
    }
    
}
