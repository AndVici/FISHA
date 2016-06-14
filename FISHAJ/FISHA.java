/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



package fisha;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.awt.image.*;
import java.awt.image.Raster.*;
import java.awt.Color;
import fisha.Image_x.*;
import java.lang.reflect.Array;
import java.nio.file.StandardCopyOption.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import fisha.DBScan.*;
import fisha.OneDclass.*;
import fisha.Kmeans.*;
/**
 *
 * @author willie
 */
public class FISHA {
    
    /**
     * 
     * @param comp
     * @param images
     * @param s
     * @return clusters for one dimensional classification
     */
    public static List<ODclust> ODClust_make(ImageX comp, List<ImageX> images, Boolean s){
       List<ODclust> training = new ArrayList();
       ODclust clust;
       for(int i=0; i<images.size(); i++){
           clust = new ODclust(DBScan.total_dist(comp, images.get(i)), s, images.get(i).ID);
           training.add(clust);
       }
       return training;
   }
    /**
     * 
     * @param imag
     * @return 255 image color set
     */
     public static Hashtable<String, List<String>> color_set(BufferedImage imag){
         Hashtable<String, List<String>> colors = new Hashtable<String, List<String>>();
         List<String> elem;
         String name = null;
         int pixl, r, g, b;
         
         for(int i=0; i<imag.getWidth(); i++){
             for(int j=0; j<imag.getHeight(); j++){
                 pixl = imag.getRGB(i, j);
                 String temp;
                 for(int k=2; k>=0; k--){
                     pixl = ((pixl >> k*8) & 0xff);
                     if( pixl < 64)
                         temp = "0";
                     else if(pixl < 128)
                         temp = "1";
                     else if(pixl < 192)
                         temp = "2";
                     else
                         temp = "3";
                     if(k==2)
                         name = temp;
                     else
                         name = (name + temp);
                 }
                     if(colors.containsKey(name)){
                         elem = colors.get(name);
                         temp = ""+i;
                         temp.concat(",");
                         temp.concat(""+j);
                         elem.add(temp);
                     }
                     else{
                         elem = new ArrayList();
                         temp = ""+i;
                         temp.concat(",");
                         temp.concat(""+j);
                         elem.add(temp);
                         colors.put(name, elem); 
                 }
             }
         }
         
         return colors;
     }
     
     /**
      * 
      * @param colors
      * @param min
      * @return image colors and their coherence 
      */
     public static List<Pair<Integer, Integer>> color_coherence_vector(Hashtable<String, List<String>> colors, int min){
         List<Pair<Integer, Integer>> ccvs = new ArrayList();
         
         for(int i = 0; i < 3; i++){
             for(int j = 0; j < 3; j++){
                 for(int k = 0; k < 3; k++){
                     String color = ""+i;
                     color.concat(""+j);
                     color.concat(""+k);
                     
                     if(colors.containsKey(color) == false){
                         Pair<Integer,Integer> ccv = new Pair<>(0,0);
                         ccvs.add(ccv);
                     }
                     else{
                         String[] pixls = Arrays.copyOf(colors.get(color).toArray(), colors.get(color).size(), String[].class);
                         int am = coherence(pixls,min);
                         Pair<Integer,Integer> ccv = new Pair<>(am, pixls.length - am);
                         ccvs.add(ccv);
                     }
                 }
             }
         }
         
         return ccvs;
     }
     
     /**
      * 
      * @param pixels
      * @param min
      * @return amount of coherent pixels
      */
     public static int coherence(String[] pixels, int min){
         int amount = 0;
         for(int i=0; i<pixels.length; i++){
             if(pixels[i] != "x"){          //x = seen
                 String[] xy = pixels[i].split(",");
                 int x = Integer.parseInt(xy[0]), y = Integer.parseInt(xy[1]);
                 pixels[i] = "x";
                 int local = 0;
                 for(int j = i+1; j < pixels.length; j++){
                     String[] next = pixels[j].split(",");
                     if(next.length == 2){
                         int x1 = Integer.parseInt(next[0]), y1 = Integer.parseInt(next[1]);
                         if(x1 == x+1 && y1 == y || y1 == y-1 || y1 == y+1 ){
                             local +=1;
                             pixels[j] = "x";
                         }
                         else if(x1 == x-1 && y1 == y || y1 == y-1 || y1 == y+1 ){
                             local +=1;
                             pixels[j] = "x";
                         }
                         else if(x1 == x && y1 == y-1 || y1 == y+1 ){
                             local +=1;
                             pixels[j] = "x";
                         }
                     }
                 }
                 if(local >= min)
                     amount+=1;
            }
         }
         
         return amount;
     }
    
    /*
    public static void find_hist(ImageX imgx){
        
        int[] alpha = new int[256];
        int h = imgx.Size.m, w = imgx.Size.d;
        
        for(int i = 0; i < h; i++){
            for(int j = 0; j < w; j++){
                int pix = imgx.original.getRGB(i, j);
                int a = (pix >> 24) & 0xff;

                alpha[a] = pix;
            }
        }
        imgx.histograms.i_hist = alpha;
     }
     */
     
     /**
      * 
      * @param h
      * @return sum of elements in h
      */
     public static double sum_hist(double[] h){
         double sum = 0;
         for(int i=0; i < h.length; i++){
             sum += h[i];
         }
         return sum;
     }
     
     /**
      * Normalize h
      * @param h 
      */
     public static void normalize_hist(double[] h){
         double sum = sum_hist(h);
         
         for(int i=0; i < h.length; i++){
             h[i] = h[i]/sum;
         }
         
     }
    
    /**
     * 
     * @param H - histogram
     * @param p - index
     * @return comulative distance from 0 to p
     */
    public static double comm_dist(double[] H, int p){
        double x = 0;
        for(int i = 0; i < p; i++)
            x+=H[i];
        return x;
    }
    
    /**
     * 
     * @param H1 - histogram
     * @param H2 - histogram
     * @return earth movers distance between two histograms
     */
    public static double EMD(double[]H1, double[]H2){
        double emd = 0;
        for(int i = 0; i<H2.length; i++){
            emd += Math.abs(comm_dist(H1, i) - comm_dist(H2, i)); 
        }
        return emd;
    }
    
    /**
     * 
     * @param H1 - histogram
     * @param H2 - histogram
     * @return earth movers distance between two histograms
     */
    public static double earth_movers_distance(double[] H1, double[] H2){
        int b = H1.length;
        double work = 0;
        for(int i = 0; i < b; i++){
            if(H1[i] != 0){
                for(int j = 0; j < b; j++){
                    if(H2[j] != 0){
                        if(H1[i] > H2[j]){
                            work += 0.1*Math.abs(i-j);
                            H1[i] = H1[i] - H2[j];
                            H2[j] = 0;
                        }
                        else if(H1[i]==H2[j]){
                            work += H1[i]*Math.abs(i-j);
                            H1[i]=0;
                            H2[j] = 0;
                        }
                        else{
                            work += H1[i]*Math.abs(i-j);
                            H2[j] = H2[j] - H1[i];
                            H1[i] = 0;
                        }
                    }
                    if(H1[i] == 0)
                            break;
                }
            }
        }
        return work;
    }
    
    /**
     * 
     * @param H1 - histogram
     * @param H2 - histogram
     * @return distance between two histograms
     */
    public static double L1_dist(double[] H1, double[] H2){
        double dist = 0;
        for(int i = 0; i<H1.length; i++){
            dist += Math.abs(H1[i] - H2[i]);
        }
        return dist;
    }
    
    /**
     * 
     * @param s - image size
     * @param H - image x gradient
     * @param V - image y gradient
     * @return histogram of image magnitudes
     */
    public static double[] magnitude(Pair<Integer,Integer> s, BufferedImage H, BufferedImage V){
        int h = s.m, w = s.d;
        double[] mag = new double[256];
        List<Double> mm = new ArrayList();
        double least = Double.POSITIVE_INFINITY, greatest = Double.NEGATIVE_INFINITY;
        for(int i = 0; i < h; i++){
            for(int j = 0; j < w; j++){
                double x = H.getRGB(i, j), y = V.getRGB(i, j),m;
                m = Math.pow(x, 2) + Math.pow(y, 2);
                if(m < least)
                    least = m;
                if(m > greatest)
                    greatest = m;
                mm.add(m);
            }
        }
        for(int i=0; i<mm.size(); i++){
            int in = (int)((mm.get(i)-least)*(255/(greatest-least)));
            mag[in] += 1;
        }
        normalize_hist(mag);
        return mag;
    }
    
    /**
     * 
     * @param x
     * @param y
     * @return magnitude at pixel
     */
    public static double magnitude(double x, double y){
        double mag = 0;
        mag = Math.pow(x, 2) + Math.pow(y, 2);
        return mag;
    }
    
    /**
     * 
     * @param x
     * @param y
     * @return direction at pixel
     */
    public static double direction(double x, double y){
        double dir = 0;
        dir = Math.atan2(y, x);
        dir = dir*(180/Math.PI);
        dir = Math.abs(dir);
        return dir;
    }
    
    /**
     * 
     * @param s
     * @param H
     * @param V
     * @return histogram of image direction
     */
    public static double[] direction(Pair<Integer,Integer> s, BufferedImage H, BufferedImage V){
        int h = s.m, w = s.d;
        double[] dir = new double[181];
        for(int i = 0; i < h; i++){
            for(int j = 0; j < w; j++){
                double x = H.getRGB(i, j), y = V.getRGB(i, j),d;
                d =  direction(x, y);
                dir[(int)d] +=1;
            }
        }
        normalize_hist(dir);
        return dir;
    }
    
    public static double[] histogramOfgradients(BufferedImage Gx, BufferedImage Gy){
        double[] HOG = new double[9]; //0-19, 20-39, 40-59, 60-79, 80-99, 100-119, 120-139, 140-159, 160-179 
        int h = Gx.getHeight(), w = Gx.getWidth();
        
        for(int i=0; i < w; i++){
            for(int j=0; j < h; j++){
                double x = (double)Gx.getRGB(i, j), y = (double)Gy.getRGB(i, j);
                double dir = direction(x, y), mag = magnitude(x, y);
                dir = Math.abs(dir);
                if(dir < 20)
                    HOG[0] += mag*mag;
                else if(dir < 40)
                    HOG[1] += mag*mag;
                else if(dir < 60)
                    HOG[2] += mag*mag;
                else if(dir < 80)
                    HOG[3] += mag*mag;
                else if(dir < 100)
                    HOG[4] += mag*mag;
                else if(dir < 120)
                    HOG[5] += mag*mag;
                else if(dir < 140)
                    HOG[6] += mag*mag;
                else if(dir < 160)
                    HOG[7] += mag*mag;
                else if(dir < 180)
                    HOG[8] += mag*mag;
            }
        }
        normalize_hist(HOG);
        return HOG; 
    }
    /**
     * 
     * @param images
     * @param comp
     * @return ODclusts
     */
    public static List<ODclust> make_clusts(List<ImageX> images, ImageX comp){
        List<ODclust> ODCs = new ArrayList();
        ODclust temp;
        double dist;
        boolean s = false;
        for(int i=0; i<images.size(); i++){
            dist = DBScan.total_dist(images.get(i), comp);
            temp = new ODclust(dist, s, images.get(i).ID);
            ODCs.add(temp);
        }
        return ODCs;
    }
     
    /*
   
    */
    /**
     * 
     * @param H
     * @return a deep copy H
     */
    public static double[] copyHistogram(double[] H){
        int l = H.length;
        double[] copy = new double[l];
        for(int i=0; i<l; i++)
            copy[i] = H[i];
        return copy;
    }
    /*
    public static void greys(BufferedImage img){
        int w = img.getWidth(), h = img.getHeight(), a, r, g, b, x;
        for(int i =0; i<w; i++){
            for(int j=0; j<h; j++){
                a = new Color(img.getRGB(i, j)).getAlpha();
                r = new Color(img.getRGB(i,j)).getRed();
                g = new Color(img.getRGB(i,j)).getGreen();
                b = new Color(img.getRGB(i,j)).getBlue();
                int temp = (int)(0.299*r+0.587*g+0.114*b);
                x = new Color(temp,temp,temp,a).getRGB();
                img.setRGB(i, j, x);
                //System.out.print(x);
                //System.out.print(" ");
            }
            //System.out.println();
        }
    }
    */
    /**
     * 
     * @param images 
     * prints the distances between each image
     */
    public static void print_dist(List<ImageX> images){
        double dist;
        for(int i=0; i<images.size();i++){
            for(int j=i+1; j<images.size(); j++){
                
                dist = 0;
                System.out.println(images.get(i).ID + " - " + images.get(j).ID);
                dist = DBScan.total_dist(images.get(i), images.get(j));
                System.out.println(dist);
                /*
                System.out.print("EMD: ");
                dist += EMD(images.get(i).histograms.red_hist, images.get(j).histograms.red_hist);
                dist += EMD(images.get(i).histograms.green_hist, images.get(j).histograms.green_hist);
                dist += EMD(images.get(i).histograms.blue_hist, images.get(j).histograms.blue_hist);
                dist += EMD(images.get(i).histograms.magnitude, images.get(j).histograms.magnitude);
                dist += EMD(images.get(i).histograms.direction, images.get(j).histograms.direction);
                System.out.println(dist);
                
                dist = 0;
                
                System.out.print("L1: ");
                dist += L1_dist(images.get(i).histograms.red_hist, images.get(j).histograms.red_hist);
                dist += L1_dist(images.get(i).histograms.green_hist, images.get(j).histograms.green_hist);
                dist += L1_dist(images.get(i).histograms.blue_hist, images.get(j).histograms.blue_hist);
                dist += L1_dist(images.get(i).histograms.magnitude, images.get(j).histograms.magnitude);
                dist += L1_dist(images.get(i).histograms.direction, images.get(j).histograms.direction);
                System.out.println(dist);
                */
            }
        }
    }
    
    public static void get_files_thresh(String pic_path, String[] pic_folders, String data_path){
        List<ImageX> images = new ArrayList();
        BufferedImage H = null, V = null, Blur = null;
        String h, v, b, csv;
        for(int i=0; i<pic_folders.length; i++){
            File folder = new File(pic_path+"\\"+pic_folders[i]);
            File[] filelist = folder.listFiles();
            String comp_img = filelist[1].getName();
            for(int j=0; j<filelist.length; j++){
                
            }
        }
    }
    /**
     * 
     * @param x
     * @param y
     * @param thresh
     * @return Histogram of image magnitudes of 3x3 regions
     */
    public static List<Double> region_magnitude(Raster x, Raster y, double thresh){
        List<Double> edginess = new ArrayList();
        for(int i = 0;i<x.getWidth() ;i+=3){
            if(i+3 > x.getWidth())
                break;
            for(int j = 0;j<x.getHeight() ;j+=3){
                if(j+3 > x.getHeight())
                    break;
                int[]tx = null, ty = null;
                tx = x.getSamples(i, j, 3, 3, 0, tx);
                ty = y.getSamples(i, j, 3, 3, 0, ty);
                double mag = 0;
                for(int k=0; k<9; k++){
                    double temp= magnitude(tx[k], ty[k]);
                    if(temp >= thresh)
                        mag+=temp;
                }
                mag = mag/9;
                edginess.add(mag);
            }
        }
        return edginess;
    }
    /**
     * 
     * @param H
     * @param V
     * @param RGB
     * @param b
     * @return ImageX object
     */
    public static ImageX make_image(BufferedImage H, BufferedImage V, File RGB, BufferedImage b){
        ImageX nw = new ImageX();
        Histograms hi = new Histograms();
        String[] temp = RGB.getName().split(".jpg");
        nw.ID = temp[0] + ".jpg";
        nw.histograms = hi;
        nw.gradient_x = H;
        nw.gradient_y = V;
        nw.original  = b;
        Pair<Integer,Integer> size = new Pair<>(b.getWidth(), b.getHeight());
        nw.Size = size;
        double[] R = new double[256], G = new double[256], B = new double[256], set = null;
        String line = null;
        String[] ln = null;
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new FileReader(RGB));
            int i = 0;
            while((line = reader.readLine())!= null && i < 256){
                ln = line.split(",");
                R[i] = Double.parseDouble(ln[0]);
                G[i] = Double.parseDouble(ln[1]);
                B[i] = Double.parseDouble(ln[2]);
                i++;
            }
            
        }catch(IOException e){
            System.out.println(e);
        }
        normalize_hist(R);
        normalize_hist(G);
        normalize_hist(B);
        nw.histograms.red_hist = R;
        nw.histograms.green_hist = G;
        nw.histograms.blue_hist = B;
        nw.histograms.magnitude = magnitude(size, H, V);
        nw.histograms.direction = direction(size, H, V);
        Raster x = nw.gradient_x.getData();
        Raster y = nw.gradient_y.getData();
        nw.gradient_x = null;
        nw.gradient_y = null;
        nw.magnitude = region_magnitude(x, y, 20000);
        Hashtable<String, List<String>> blur = color_set(b);
        nw.color_coherence_vectorX = color_coherence_vector(blur, 15);
        //nw.blur = color_set(b);
        return nw;
    }
    
    /**
     * 
     * @param images
     * @param compare_image
     * @param outp
     * @param p
     * @param threshold 
     */
    public static void classify(List<ImageX> images, ImageX compare_image, String outp, String p, double threshold){
        List<ODclust> odc = make_clusts(images, compare_image);
        OneDclass.classifier(odc, threshold);
        BufferedImage im = null;
        for (int i = 0; i < odc.size(); i++) {
            if (odc.get(i).similar == true) {
                try {
                    im = ImageIO.read(new File(p + "\\" + odc.get(i).ID));
                    ImageIO.write(im, "jpg", new File(outp + "\\" + i + "c.jpg"));

                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }
    }
    
    /**
     * 
     * @param images
     * @param n number of observations
     */
    public static void one_d_class(List<ImageX> images, int n){
        List<ODclust> odc = new ArrayList(), temp;
        List<Double> best_best = new ArrayList();
        int a=0, b;
        for(int i=0; i<n; i++){
            b = images.indexOf(null);
            temp = ODClust_make(images.get(a), images.subList(a, b), true);
            odc.addAll(temp);
            temp = ODClust_make(images.get(a), images.subList(b+1, images.size()), false);
            odc.addAll(temp);
            List<Double> thresh = OneDclass.OD_classification(odc, 0, 0.1, 10);
            double best_thresh = 0;
            for(int j=0; j<thresh.size(); j++){
                best_thresh+=thresh.get(j);
            }
            best_best.add(best_thresh/thresh.size());
            images.remove(b);
            a=b;
        }
        double best_best_thresh = 0;
        for(int i=0; i<best_best.size(); i++){
            best_best_thresh+=best_best.get(i);
        }
        best_best_thresh = best_best_thresh/best_best.size();
        System.out.print("Best Threshold: ");
        System.out.println(best_best_thresh);
    }
    
    /**
     * 
     * @param outp
     * @param p
     * @param images
     * @param k 
     */
    public static void kmeans(String outp, String p, List<ImageX> images, int k){
        List<KCluster> kc = Kmeans.Kmeans(images, k);
            for(int i=0; i<images.size(); i++){
                System.out.println(images.get(i).ID);
                System.out.println();
                for(int j=0; j<images.get(i).magnitude.size(); j++){
                    if(images.get(i).magnitude.get(j)!= 0.0)
                        System.out.println(images.get(i).magnitude.get(j));
                }
            }
           
            BufferedImage im = null;
            for(int i=0; i<kc.size(); i++){
                String s = outp +"\\Kcluster"+i;
                new File(s).mkdirs();
                for(int j=0; j<kc.get(i).R.size(); j++){
                    String str = s + "\\"+ kc.get(i).R.get(j).ID;
                    try{
                        im = ImageIO.read(new File(p +"\\"+ kc.get(i).R.get(j).ID));
                        ImageIO.write(im, "jpg", new File(s +"\\"+ i+j+"c.jpg"));

                    }catch(IOException e){
                        System.out.println(e);
                    }
                }
            }
      
    }
    /**
     * 
     * @param outp
     * @param p
     * @param images
     * @param Eps  minimum distance
     * @param Minpts minimum points
     */
    public static void dbscan(String outp, String p, List<ImageX> images, double Eps, int Minpts){
        List<DBCluster> results = DBScan.DBSCAN(images, Eps, Minpts
        );
        File out;
        
        BufferedImage im = null;
        for(int i=0; i<results.size(); i++){
            String s = outp +"\\cluster"+i;
            new File(s).mkdirs();
            try{
                PrintWriter writer = new PrintWriter(s + "greatest distance", "UTF-8");
                writer.println(results.get(i).greatest_distance);
                writer.close();
            }catch(IOException e){}
            for(int j=0; j<results.get(i).core_pnts.size(); j++){
                String str ="\\"+ results.get(i).core_pnts.get(j).image.ID;
                try{
                    im = ImageIO.read(new File(p +str));
                    ImageIO.write(im, "jpg", new File(s+ str));
                    
                }catch(IOException e){
                    System.out.println(e);
                }
            }
            for(int j=0; j<results.get(i).border_pnts.size(); j++){
                String str = "\\"+ results.get(i).border_pnts.get(j).image.ID;
                try{
                    im = ImageIO.read(new File(p +str));
                    ImageIO.write(im, "jpg", new File(s + str));
                    
                }catch(IOException e){
                    System.out.println(e);
                }
            }
        }
    }
    
    public static ImageX get_images(List<ImageX> images, String path, String p, String compare){
        ImageX com_i = new ImageX(), temp;
        
        File folder = new File(p);
        File[] filelist = folder.listFiles();
        BufferedImage H = null, V = null, Orig = null;
        File rgb = null;
        String h, v, csv, f;
       
        
        for(int i=0; i < filelist.length; i++){
            h = path + "\\" + filelist[i].getName() + "_H.png";
            v = path + "\\" +  filelist[i].getName() + "_V.png";
            csv = path + "\\" + filelist[i].getName() + "_RGB_h.csv";
            f = path + "\\" + filelist[i].getName() + "_blur.png";
            String ee = null;
            try{
                H = ImageIO.read(new File(h));
                V = ImageIO.read(new File(v));
                rgb = new File(csv);
                Orig = ImageIO.read(new File(f));

            }catch(IOException e){
                if(compare == filelist[i].toString()){
                    System.out.println("Could not retrieve compare image");
                    System.exit(0);
                }
                continue;
            }
                
            if(ee ==null){
                temp = make_image(H, V, rgb, Orig);
                if(compare.equals(filelist[i].toString()))
                    com_i = temp;
                else
                    images.add(temp);
            }
        }
        return com_i;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        List<ImageX> images = new ArrayList(), seeds = new ArrayList();
        List<String> comp_imgs = new ArrayList(), compare_imgsp = new ArrayList();
        int k =0, Minpts = 0, n=1;
        double Eps = 0, threshold=2.3;
        Scanner reader = new Scanner(System.in);
        ImageX compar_image = null;

        System.out.println("Welcome to FISHA!");
        
        String p = "",path = "", outp="", compare_image="", ans="";
        
        System.out.println("Please enter an algorithm: ");
        String inp = reader.nextLine();
        while(true){
            if(inp.equals("dbscan")){
                if(outp.equals("")){
                    System.out.println("please enter image set path: ");
                    p = reader.nextLine();
                    System.out.println("please enter image set data path (location of ouput from FISHAM): ");
                    path = reader.nextLine();
                    System.out.println("please enter desired output path: ");
                    outp = reader.nextLine();
                }
                System.out.println("Please enter greatest distance: ");
                try{
                    Eps = reader.nextDouble();
            
                }catch(InputMismatchException e){
                    System.out.println("Imput must be an number");
                    continue;
                }
                System.out.println("Please enter minimum number of points: ");
                try{
                    Minpts = reader.nextInt();
            
                }catch(InputMismatchException e){
                    System.out.println("Imput must be an number");
                    continue;
                }
                break;
            }
            else if(inp.equals("kmeans")){
                if(outp.equals("")){
                    System.out.println("please enter image set path: ");
                    p = reader.nextLine();
                    System.out.println("please enter image set data path (location of ouput from FISHAM): ");
                    path = reader.nextLine();
                    System.out.println("please enter desired output path: ");
                    outp = reader.nextLine();
                }
                System.out.println("Please enter K: ");
                try{
                    k = reader.nextInt();
            
                }catch(InputMismatchException e){
                    System.out.println("Imput must be an number");
                    continue;
                }
                break;
            }
            else if(inp.equals("classify")){
                if(ans.equals("y") || ans.equals("yes")){
                    System.out.println("Please enter threshold: ");
                    try{
                        threshold = reader.nextDouble();
            
                    }catch(InputMismatchException e){
                        System.out.println("Imput must be an number");
                        continue;
                    }
                    break;
                }
                System.out.println("please enter comparison image path: ");
                compare_image = reader.nextLine();
                System.out.println("please enter image set path: ");
                p = reader.nextLine();
                System.out.println("please enter image set data path (location of ouput from FISHAM): ");
                path = reader.nextLine();
                System.out.println("please enter desired output path: ");
                outp = reader.nextLine();
                System.out.println("Do you want to set a threshold? ");
                ans = reader.nextLine();
                if(ans.equals("y") || ans.equals("yes")){
                    System.out.println("Please enter threshold: ");
                    try{
                        threshold = reader.nextDouble();
            
                    }catch(InputMismatchException e){
                        System.out.println("Imput must be an number");
                        continue;
                    }
                    break;
                }
                
            }
            else if(inp.equals("1D classification")){
                System.out.println("Please enter number of observations: ");
                try{
                    n = reader.nextInt();
            
                }catch(InputMismatchException e){
                    System.out.println("Imput must be an number");
                    continue;
                }
                for(int i=0; i<n; i++){
                    System.out.println("Please enter path to set of similar images");
                    String imag = reader.nextLine();
                    imag.replace("\\", "\\\\");
                    comp_imgs.add(imag);
                    System.out.println("Please enter path to set of similar images data (FISHAM output): ");
                    compare_imgsp.add(reader.nextLine().replaceAll("\\", "\\\\"));
                }
                System.out.println("please enter desired output path: ");
                outp = reader.nextLine();
                break;
            }
            
            else{
                System.out.println("Input: 'dbscan' or 'kmeans' or 'classify' or '1D classification'");
                System.out.println("Please enter an algorithm: ");
                inp = reader.nextLine();
            }
        }
        
        
        p.replace("\\", "\\\\");
        outp.replace("\\", "\\\\");
        path.replace("\\", "\\\\");
        compare_image.replace("\\", "\\\\");
            
        if(inp.equals("dbscan")){
            get_images(images, path, p, compare_image);
            dbscan(outp, p, images, Eps, Minpts);
        }
        else if(inp.equals("kmeans")){
            get_images(images, path, p, compare_image);
            kmeans(outp, p, images, k);
        }
        else if(inp.equals("classify")){
            compar_image = get_images(images, path, p, compare_image);
            classify(images, compar_image, outp, p,threshold);
        }
        else if(inp.equals("1D classification")){
            for(int i=0; i<n; i++){
                get_images(images, comp_imgs.get(i), compare_imgsp.get(i), compare_image);
                ImageX boarder = null;
                images.add(boarder);
            }
        }
       
 
        
        
    }
    
}
