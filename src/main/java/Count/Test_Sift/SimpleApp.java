package Count.Test_Sift;

import java.awt.image.WritableRenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class SimpleApp {
	
	public static List<SeriImage> getImages(String path) throws IOException{
		File file=new File(path);
		File[] tempList=file.listFiles();
		List<SeriImage> imageList=new ArrayList<SeriImage>();
		for(int i=0;i<tempList.length;i++){
			if(tempList[i].getName().endsWith(".jpg") || tempList[i].getName().endsWith(".png")){
				SeriImage tmp=new SeriImage(ImageIO.read(tempList[i]));
				imageList.add(tmp);
			}
		}
		return imageList;
	}
	
	public static List<WritableRenderedImage> getImage(String path) throws IOException{
		File file=new File(path);
		File[] tempList=file.listFiles();
		List<WritableRenderedImage> imageList=new ArrayList<WritableRenderedImage>();
		for(int i=0;i<tempList.length;i++){
			if(tempList[i].getName().endsWith(".jpg") || tempList[i].getName().endsWith(".png")){
				WritableRenderedImage tmp=ImageIO.read(tempList[i]);
				imageList.add(tmp);
			}
		}
		return imageList;
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		SparkConf conf=new SparkConf().setAppName("SimpleApp");
		JavaSparkContext sc=new JavaSparkContext(conf);
		String filepath="/root/image/";
		//List<WritableRenderedImage> img=null;
		List<SeriImage> images=null;
		JavaRDD <SeriImage> result=null;
		//JavaRDD<WritableRenderedImage> result1=null;
		images=getImages(filepath);
		result=sc.parallelize(images);
		System.out.println("len:"+images.size());
		System.out.println("rddlen:"+result.count());
	}
}
