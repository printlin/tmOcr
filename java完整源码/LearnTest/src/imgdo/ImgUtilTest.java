package imgdo;

import java.io.IOException;

import util.ImgUtil;

public class ImgUtilTest {
	public static void main(String[] args) throws IOException {
		/*ImgUtil iu=new ImgUtil();
		String uri1="F:\\tmImg\\all\\零191.jpg";
		String uri2="F:\\tmImg\\test\\零.jpg";
		double[][] data=iu.formatImg(uri1),img=iu.formatImg(uri2);
		System.out.println("pixelMatch  "+iu.pixelMatch(data,img));
		System.out.println("shadowMatch  "+iu.shadowMatch(data,img));
		System.out.println("focusMatch  "+iu.focusMatch(data,img));
		showArr(iu.allMatch2(data, img));
		/*
		int[][] arr={{1,2,3,4},{1,2,3,4},{1,2,3,4},{1,2,3,4},{1,2,3,4},{1,2,3,4}};
		int wid=arr.length;
		int hei=arr[0].length;
		int[] X=new int[wid];
		int[] Y=new int[hei];
		int sum;
		for(int i=0;i<wid;i++){
			sum=0;
			for(int j=0;j<hei;j++){
				sum+=arr[i][j];
				Y[j]+=arr[i][j];
			}
			X[i]=sum;
		}
		showArr(X);
		showArr(Y);*/
	}
	private static void showArr(int[] arr){
		for(int i:arr){
			System.out.print(i+"\t");
		}
		System.out.println();
	}
	private static void showArr(double[] arr){
		for(double i:arr){
			System.out.print(i+"\t");
		}
		System.out.println();
	}
}
