package Count.Test_Sift;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class SimpleApp {
	
	public static List<ImageIcon> getImages(String path){
		File file=new File(path);
		File[] tempList=file.listFiles();
		List<ImageIcon> imageList=new ArrayList<ImageIcon>();
		for(int i=0;i<tempList.length;i++){
			if(tempList[i].getName().endsWith(".jpg") || tempList[i].getName().endsWith(".png")){
				ImageIcon tmp=new ImageIcon(tempList[i].getName());
				imageList.add(tmp);
			}
		}
		return imageList;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SparkConf conf=new SparkConf().setAppName("SimpleApp");
		JavaSparkContext sc=new JavaSparkContext(conf);
		String filepath="/root/image/";
		List<ImageIcon> images=null;
		JavaRDD <ImageIcon> result=null;
		images=getImages(filepath);
		result=sc.parallelize(images);
		result.cache();
		System.out.println("len:"+images.size());
		System.out.println("rddlen:"+result.count());
	}

}
