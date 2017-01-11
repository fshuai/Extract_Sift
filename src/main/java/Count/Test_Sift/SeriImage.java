package Count.Test_Sift;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;
import javax.media.jai.remote.SerializableRenderedImage;

public class SeriImage extends Image implements Serializable{
	protected transient RenderedImage image;
	private int size;
	
	public SeriImage(RenderedImage image,long size){
		this.image=image;
		this.size=(int)size;
	}
	
	public RenderedImage getRenderedImage(){
		return image;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException{
		out.defaultWriteObject();
		ByteArrayOutputStream buff = new ByteArrayOutputStream();
		ImageIO.write(image, "JPG", buff);
		byte[] images=buff.toByteArray();
		out.writeInt(size);
		out.write(images);
		//out.writeObject(new SerializableRenderedImage(image,true));
	}
	
	private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException{
		in.defaultReadObject();
		int imageLen=in.readInt();
		ByteArrayOutputStream buff=new ByteArrayOutputStream();
		int readLen=0;
		byte[] readBuff=new byte[1024];
		while((readLen=in.read(readBuff))!=-1){
			buff.write(readBuff,0,readLen);
		}
		ByteArrayInputStream imageReadBuff=new ByteArrayInputStream(buff.toByteArray());
		image=ImageIO.read(imageReadBuff);
		//image=(RenderedImage)in.readObject();
	}

	@Override
	public int getWidth(ImageObserver observer) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight(ImageObserver observer) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ImageProducer getSource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graphics getGraphics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getProperty(String name, ImageObserver observer) {
		// TODO Auto-generated method stub
		return null;
	}
}
