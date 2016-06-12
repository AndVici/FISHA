/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fisha;
import fisha.Image_x.*;
import java.util.*;
import java.lang.*;
import java.io.*;

/**
 *
 * @author willie
 */
public class DBScan {
    
    public static class Point{
        List<Integer> clust_num;
        ImageX image;
        Boolean classe;     // 0=core, 1=border
        List<Point> neighbors;
        List<Double> dists;
        
        public Point(){
            this.clust_num = null;
            this.image = null;
            this.classe = null;
            this.neighbors = null;
            this.dists = null;
        }
        
        public Point(List<Integer> nu, ImageX img, Boolean c, List<Point> n, List<Double> d){
            this.clust_num = nu;
            this.image = img;
            this.classe = c;
            this.neighbors = n;
            this.dists = d;
        }
    }
    
    public static class DBCluster{
        int num;
        double mean;
        double greatest_distance;
        List<Point> core_pnts;
        List<Point> border_pnts;
        
        public DBCluster(){
            this.num = -1;
            this.core_pnts = null;
            this.border_pnts = null;
            this.mean = -1;
            this.greatest_distance = -1;
        }
        
        public DBCluster(int n, List<Point> cores, List<Point> bp){
            this.num = n;
            this.core_pnts = cores;
            this.border_pnts = bp;
        }
        
    }
    
    public static void Greatest_Distance(DBCluster clust){
        List<Point> temp = new ArrayList();
        temp.addAll(clust.core_pnts);
        temp.addAll(clust.border_pnts);
        double greatest = Double.NEGATIVE_INFINITY;
        for(int i=0; i<temp.size(); i++){
            Point p = temp.get(i);
            for(int j=0; j<p.dists.size(); j++){
                if(p.dists.get(j) > greatest && temp.contains(p.neighbors.get(j)))
                    greatest = p.dists.get(j);
            }
        }
        clust.greatest_distance = greatest;
    }
    
    public static double Variance(Point boarder, DBCluster clust, double mean){
        double variance = 0, t=0;
        List<Point> temp = new ArrayList();
        temp.addAll(clust.core_pnts);
        temp.addAll(clust.border_pnts);
        for(int i=0; i<temp.size(); i++){
            t+= total_dist(boarder.image, temp.get(i).image);
        }
        t = t/temp.size();
        variance = Math.pow(t-mean, 2);
        return variance;
    }
    
    public static double Mean(DBCluster clust){
        double  mean = 0;
        List<Point> temp = new ArrayList();
        temp.addAll(clust.core_pnts);
        temp.addAll(clust.border_pnts);
        
        for(int i =0; i<temp.size(); i++){
            for(int j=0; j<temp.size(); j++){
                mean += total_dist(temp.get(i).image, temp.get(j).image);
            }
        }
        mean = mean/temp.size();
        
        return mean;
    }
    
    public static double hist_dist(ImageX i1, ImageX i2){
        double dist = 0;
        
        dist += FISHA.L1_dist(i1.histograms.red_hist, i2.histograms.red_hist);
        dist += FISHA.L1_dist(i1.histograms.blue_hist, i2.histograms.blue_hist);
        dist += FISHA.L1_dist(i1.histograms.green_hist, i2.histograms.green_hist);
        dist += FISHA.L1_dist(i1.histograms.magnitude, i2.histograms.magnitude);
        dist += FISHA.L1_dist(i1.histograms.direction, i2.histograms.direction);
        
        return dist;
    }
    
    public static double ccv_dist(List<Pair<Integer, Integer>> iccv1, List<Pair<Integer, Integer>> iccv2){
        double dist = 0;
        
        for(int i=0; i<iccv1.size(); i++){
            dist += Math.abs((iccv1.get(i).m - iccv2.get(i).m) + (iccv1.get(i).d - iccv2.get(i).d));
        }
        
        return dist;
    }
    
    public static double total_dist(ImageX i1, ImageX i2){
        double dist = 0;
        
        dist += hist_dist(i1, i2);
        dist += ccv_dist(i1.color_coherence_vectorX, i2.color_coherence_vectorX);
        
        return dist;
    }
    
    public static List<DBCluster> find_core_points(List<Point> imgs, double Eps, int Minpts){
        List<DBCluster> clusts = new ArrayList();
        List<Point> n_pnts, temp;
        DBCluster clst;
        for(int i=0; i<imgs.size(); i++){
            n_pnts = new ArrayList();
            clst = null;
            for(int j=0; j<imgs.size(); j++){
                if(i!=j){
                    double x = total_dist(imgs.get(i).image, imgs.get(j).image);
                    if(x <= Eps){
                        
                        if(imgs.get(j).classe != null)
                            clst = clusts.get(imgs.get(j).clust_num.get(0));
                        else
                            n_pnts.add(imgs.get(j));
                        imgs.get(i).neighbors.add(imgs.get(j));
                        imgs.get(i).dists.add(x);
                    }
                }
            }
            if(imgs.get(i).neighbors.size() >= Minpts){
                imgs.get(i).classe = true;
                if(clst== null){
                    temp = new ArrayList();
                    temp.add(imgs.get(i));
                    clst = new DBCluster(clusts.size(), temp, n_pnts);
                    clusts.add(clst);
                    imgs.get(i).clust_num.clear();
                    imgs.get(i).clust_num.add(clst.num);
                }
                else{
                    if(imgs.get(i).clust_num.size() == 0)
                        imgs.get(i).clust_num.add(clst.num);
                    for(int k=0; k<n_pnts.size(); k++){
                        if(clst.border_pnts.contains(n_pnts.get(k)) == false){
                            clst.border_pnts.add(n_pnts.get(k));
                            n_pnts.get(k).clust_num.add(clst.num);
                            n_pnts.get(k).classe = false;
                        }
                    }
                    if(clst.border_pnts.contains(imgs.get(i)))
                        clst.border_pnts.remove(imgs.get(i));
                    clst.core_pnts.add(imgs.get(i));
                }
            }
        }
        
        return clusts;
    }
    
    
    public static void borders(List<Point> bords, List<DBCluster> a){
        for(int i=0; i<bords.size(); i++){
            int x = bords.get(i).clust_num.size();
            if(bords.get(i).classe == null){
                //bords.remove(i);
                //i = i-1;
                continue;
            }
            if(x > 1){
                double least = Double.POSITIVE_INFINITY;
                int least_in = 0;
                for(int j=0; j<x; j++){
                    int w = bords.get(i).clust_num.get(j);
                    double me = a.get(w).mean;
                    double va = Variance(bords.get(i), a.get(w), me);
                    if(va < least){
                        least = va;
                        least_in = w;
                    }
                }
                for(int j=0; j<a.size(); j++){
                    if(a.get(j).num != least_in){
                        a.get(j).border_pnts.remove(bords.get(i));
                        a.get(j).mean = Mean(a.get(j));
                    }
                }
            }
        }
    }
    
    
    public static List<DBCluster> DBSCAN(List<ImageX> images, double Eps, int Minpts){
        List<DBCluster> clusts;
        Point nP;
        List<Point> points = new ArrayList();
         for(int i=0; i<images.size(); i++){
             nP = new Point();
             nP.image = images.get(i);
             nP.neighbors = new ArrayList();
             nP.dists = new ArrayList();
             nP.clust_num = new ArrayList();
             points.add(nP);
         }
         clusts = find_core_points(points, Eps, Minpts);
         for(int i=0; i< clusts.size(); i++)
             clusts.get(i).mean = Mean(clusts.get(i));
         
         borders(points, clusts);
         for(int i=0; i<clusts.size(); i++)
             Greatest_Distance(clusts.get(i));
         
         return clusts;
    }
}

