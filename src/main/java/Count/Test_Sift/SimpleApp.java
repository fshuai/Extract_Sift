package Count.Test_Sift;

import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import mpi.cbg.fly.Feature;
import mpi.cbg.fly.Filter;
import mpi.cbg.fly.FloatArray2D;
import mpi.cbg.fly.FloatArray2DSIFT;
import mpi.cbg.fly.FloatArray2DScaleOctave;

public class SimpleApp implements Serializable{
	
	// steps
		private static int steps = 3;
		// initial sigma
		private static float initial_sigma = 1.6f;
		// background colour
	    //private double bg = 0.0;
		// feature descriptor size
		private static int fdsize = 4;
		// feature descriptor orientation bins
		private static int fdbins = 8;
		// size restrictions for scale octaves, use octaves < max_size and > min_size only
		private static int min_size = 64;
		private static int max_size = 1024;
		/**
		 * Set true to double the size of the image by linear interpolation to
		 * ( with * 2 + 1 ) * ( height * 2 + 1 ).  Thus we can start identifying
		 * DoG extrema with $\sigma = INITIAL_SIGMA / 2$ like proposed by
		 * \citet{Lowe04}.
		 * 
		 * This is useful for images scmaller than 1000px per side only. 
		 */ 
		   private static boolean upscale = true;
	        private static float normTo1(int b) {
	            return (float) (b / 255.0f);
	        }
	        
	        private static int RGB2Grey(int argb) {
	           // int a = (argb >> 24) & 0xff;
	            int r = (argb >> 16) & 0xff;
	            int g = (argb >> 8) & 0xff;
	            int b = (argb) & 0xff;

	            //int rgb=(0xff000000 | ((r<<16)&0xff0000) | ((g<<8)&0xff00) | (b&0xff));
	            int y = (int) Math.round(0.299f * r + 0.587f * g + 0.114f * b);
	            return y;
	        }

	        private static FloatArray2D convert(Image renderedImage)
	        {
	            
	            FloatArray2D image;
	            PixelGrabber grabber=new PixelGrabber(renderedImage, 0, 0, -1,-1, true);
	            try {
	                grabber.grabPixels();
	            } catch (Exception e) {
	                throw new RuntimeException(e);
	            }
	            int[] data = (int[]) grabber.getPixels();
	            
	            image = new FloatArray2D(grabber.getWidth(),  grabber.getHeight());
	            for (int d=0;d<data.length;d++)
	                        image.data[d] = normTo1(RGB2Grey(data[d]));
	            return image;
	        }
	        
	        private static List<ImageFeature> convert(List<Feature> features)
	        {
	            List<ImageFeature> res=new ArrayList<ImageFeature>();
	            for (Feature f:features)
	            {
	                ImageFeature imageFeature=new ImageFeature();
	                imageFeature.setDescriptor(( f.descriptor));
	                imageFeature.setOrientation(f.orientation);
	                imageFeature.setScale(f.scale);
	                res.add(imageFeature);
	            }
	            return res;
	        }
	
	public static List<SeriImage> getImages(String path) throws IOException{
		File file=new File(path);
		File[] tempList=file.listFiles();
		List<SeriImage> imageList=new ArrayList<SeriImage>();
		for(int i=0;i<tempList.length;i++){
			if(tempList[i].getName().endsWith(".jpg") || tempList[i].getName().endsWith(".png")){
				SeriImage tmp=new SeriImage(ImageIO.read(tempList[i]),tempList[i].length());
				imageList.add(tmp);
			}
		}
		return imageList;
	}
	
	/*public  List<ImageFeature> getFeatures(Image img) {
		String preamb=this.getClass()+": ";
        List<Feature> fs;
	
        FloatArray2DSIFT sift = new FloatArray2DSIFT( fdsize, fdbins );		
        FloatArray2D fa = convert(img);              
        Filter.enhance( fa, 1.0f );

        if ( upscale ){
           FloatArray2D fat = new FloatArray2D( fa.width * 2 - 1, fa.height * 2 - 1 ); 
           FloatArray2DScaleOctave.upsample( fa, fat );
           fa = fat;
           fa = Filter.computeGaussianFastMirror( fa, ( float )Math.sqrt( initial_sigma * initial_sigma - 1.0 ) );
        }
        else
        	fa = Filter.computeGaussianFastMirror( fa, ( float )Math.sqrt( initial_sigma * initial_sigma - 0.25 ) );

        long start_time = System.currentTimeMillis();
        System.out.println(preamb+"processing SIFT ..." );
        sift.init( fa, steps, initial_sigma, min_size, max_size );
        fs = sift.run( max_size );
        Collections.sort( fs );
        System.out.println(preamb+"took " + ( System.currentTimeMillis() - start_time ) + "ms" );		
        System.out.println(preamb+ fs.size() + " features identified and processed" );     
        return convert(fs);
	}*/
	
	public static List<ImageFeature> getFeatures(Image img) {
		String preamb="SimpleApp";
        List<Feature> fs;
	
        FloatArray2DSIFT sift = new FloatArray2DSIFT( fdsize, fdbins );		
        FloatArray2D fa = convert(img);              
        Filter.enhance( fa, 1.0f );

        if ( upscale ){
           FloatArray2D fat = new FloatArray2D( fa.width * 2 - 1, fa.height * 2 - 1 ); 
           FloatArray2DScaleOctave.upsample( fa, fat );
           fa = fat;
           fa = Filter.computeGaussianFastMirror( fa, ( float )Math.sqrt( initial_sigma * initial_sigma - 1.0 ) );
        }
        else
        	fa = Filter.computeGaussianFastMirror( fa, ( float )Math.sqrt( initial_sigma * initial_sigma - 0.25 ) );

        long start_time = System.currentTimeMillis();
        System.out.println(preamb+"processing SIFT ..." );
        sift.init( fa, steps, initial_sigma, min_size, max_size );
        fs = sift.run( max_size );
        Collections.sort( fs );
        System.out.println(preamb+"took " + ( System.currentTimeMillis() - start_time ) + "ms" );		
        System.out.println(preamb+ fs.size() + " features identified and processed" );     
        return convert(fs);
	}
	
	public static JavaRDD<List<ImageFeature>> getSift(JavaRDD<SeriImage> img){
		return img.map(new Function<SeriImage,List<ImageFeature>>(){

			public List<ImageFeature> call(SeriImage arg0) throws Exception {
				// TODO Auto-generated method stub
				List<ImageFeature> list=null;
				RenderedImage tmp=arg0.getRenderedImage();
				list=getFeatures((Image)tmp);
				return list;
			}

			private List<ImageFeature> getFeatures1(RenderedImage renderedImage) {
				// TODO Auto-generated method stub
				return null;
			}
			
		});
	}
	

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		SparkConf conf=new SparkConf().setAppName("SimpleApp");
		JavaSparkContext sc=new JavaSparkContext(conf);
		String filepath="/root/image/";
		List<SeriImage> images=null;
		JavaRDD <SeriImage> result=null;
		JavaRDD<List<ImageFeature>> points=null;
		images=getImages(filepath);
		result=sc.parallelize(images);
		result.cache();
		//points=new SimpleApp().getSift(result);
		System.out.println("len:"+images.size());
		System.out.println("rddlen:"+result.count());
		points=getSift(result);
		System.out.println("pointslen:"+points.count());
	}
}
