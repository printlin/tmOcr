package util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Test;

public class ImgTextCut {
	private static final Color WHITE=new Color(255,255,255),BLACK=new Color(0,0,0);

	@Test
	public void test() throws IOException{
		int count=0;
		List<List<BufferedImage>> lists=cutToImg("F:\\tmImg\\jpgs\\1.jpg");
		for(List<BufferedImage> list : lists){
			for(BufferedImage data:list){
				ImageIO.write(data, "jpg", new File("F:\\tmImg\\doc\\first-java\\"+count+".jpg"));
				count++;
			}
		}
	}
	
	public List<List<int[][]>> cutToArr(File f) throws IOException{
		int[][] arr=twoValueByInt(ImageIO.read(f), 127);
		return cutToArr(arr);
	}
	public List<List<BufferedImage>> cutToImg(File f) throws IOException{
		int[][] arr=twoValueByInt(ImageIO.read(f), 127);
		return listArrToImg(cutToArr(arr));
	}
	public List<List<int[][]>> cutToArr(String uri) throws IOException{
		int[][] arr=twoValueByInt(ImageIO.read(new File(uri)), 127);
		return cutToArr(arr);
	}
	public List<List<BufferedImage>> cutToImg(String uri) throws IOException{
		int[][] arr=twoValueByInt(ImageIO.read(new File(uri)), 127);
		return listArrToImg(cutToArr(arr));
	}
	public List<List<int[][]>> cutToArr(int[][] arr){
		List<List<int[][]>> re=new ArrayList<List<int[][]>>();
		List<int[][]> reX=cutX(arr);
		for(int i=0,len=reX.size();i<len;i++){
			List<int[][]> reY=cutY(reX.get(i));
			re.add(reY);
		}
		return re;
	}
	public List<List<BufferedImage>> cutToImg(int[][] arr){
		return listArrToImg(cutToArr(arr));
	}
	private List<int[][]> cutX(int[][] arr){
		int length=arr[0].length;
		List<int[][]> re=new ArrayList<int[][]>();
		int[] count =countX(arr);
		List<Integer> point =findPoint(count);
		int lengthP=point.size();
		if(lengthP%2==1)
			lengthP=lengthP-1;
		for(int i=0;i<lengthP;i=i+2)
			re.add(cutArray(arr,point.get(i),point.get(i+1),0,length));
		return re;
	}
	private List<int[][]> cutY(int[][] arr){
		int length=arr.length;
		List<int[][]> re=new ArrayList<int[][]>();
		int[] count =countY(arr);
		List<Integer> point =findPoint(count);
		boolean isBegin=true;
		for(int i=0,len=point.size();i<len;i=i+2){
			int a=point.get(i),b=point.get(i+1);
			re.add(cutArray(arr,0,length,a,b));
			
		}
		return re;
	}
	
	private int[][] cutArray(int[][] arr,int xB,int xE,int yB,int yE){
		int w=xE-xB,h=yE-yB;
		int[][] re=new int[w][h];
		for(int i=0;i<w;i++){
			for(int j=0;j<h;j++){
				re[i][j]=arr[i+xB][j+yB];
			}
		}
		return re;
	}
	
	//统计一行黑色点的个数
	private int[] countX(int[][] arr){
		int[] count=new int[arr.length];
		for(int i=0,len=arr.length;i<len;i++){
			int count2=0;
			for(int j=0,jlen=arr[i].length;j<jlen;j++){
				if(arr[i][j]!=0){
					count2++;
				}
			}
			count[i]=count2;
		}
		return count;
	}
	//统计一列黑色点的个数
	private int[] countY(int[][] arr){
		int[] count=new int[arr[0].length];//列数
		for(int i=0,len=arr[0].length;i<len;i++){
			int count2=0;//每列的黑色点数
			for(int j=0,jlen=arr.length;j<jlen;j++){
				if(arr[j][i]!=0){//不为0则是黑色
					count2++;
				}
			}
			count[i]=count2;
		}
		return count;
	}
	//参数：纵向或横向的映射数组  返回：一维数组，0是开始1是结束，2是开始3是结束。表示行或列的分水岭  
	private List<Integer> findPoint(int[] arr){
		int index=-1;
		List<Integer> re=new ArrayList<Integer>();
		zeroToMore(arr,index,re);
		return re;
	}
	//参数：纵向或横向的映射数组 数组当前下标 返回结果集  返回：re，一维数组   递归调用，记录非零下标，表示一行的开始
	private void zeroToMore(int[] arr,int index,List<Integer> re){
		if(index<(arr.length-1)){//索引到达最后则终止递归
			for(int i=index+1,len=arr.length;i<len;i++){//从索引处向后扫描
				if(arr[i]>1){//如果黑色点数大于1，则记录下标转到下一判断
					index=i;
					re.add(i);
					break;
				}
				if(i==(len-1)){//如果扫描到最后都没有大于1的，则终止递归
					index=i;
				}
			}
			moreToZero(arr,index,re);
		}
	}
	//同上  记录第一个零下标，表示一行的结束
	private void moreToZero(int[] arr,int index,List<Integer> re){
		if(index<(arr.length-1)){
			for(int i=index+1,len=arr.length;i<len;i++){
				if(arr[i]<=1){
					index=i;
					re.add(i);
					break;
				}
				if(i==(len-1)){
					index=i;
				}
			}
			zeroToMore(arr,index,re);
		}
	}
	
	private List<List<BufferedImage>> listArrToImg(List<List<int[][]>> lists){
		List<List<BufferedImage>> re=new ArrayList<List<BufferedImage>>();
		for(List<int[][]> list : lists){
			List<BufferedImage> l=new ArrayList<BufferedImage>();
			for(int[][] data:list){
				l.add(arrToImg(data));
			}
			re.add(l);
		}
		return re;
	}
	private BufferedImage arrToImg(int[][] arr){
		int h=arr.length,w=arr[0].length;
		BufferedImage img=new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for(int i=0;i<h;i++){
			for(int j=0;j<w;j++){
				if(arr[i][j]!=0){
					img.setRGB(j, i, BLACK.getRGB());
				}else{
					img.setRGB(j, i, WHITE.getRGB());
				}
			}
		}
		return img;
	}
	
	/**
     * 二值化
     * flag:阀值   大于为0  小于为1
     */
	private int[][] twoValueByInt(BufferedImage bimg,int flag){
		int wid=bimg.getWidth();
		int hei=bimg.getHeight();
		int[][] re=new int[hei][wid];
		for(int i=0;i<hei;i++){
			for(int j=0;j<wid;j++){
				Color c=new Color(bimg.getRGB(j, i));
				int avg=(c.getBlue()+c.getGreen()+c.getRed())/3;
				if(avg>flag){
					re[i][j]=0;
				}else{
					re[i][j]=1;
				}
				
			}
		}
		return re;
	}
}
