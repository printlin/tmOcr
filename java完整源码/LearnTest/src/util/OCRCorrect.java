package util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import imgdo.ImageOCR;
import imgdo.ImgData;

/**
 * @author Administrator
 */
public class OCRCorrect {
	private boolean isDebug=false;//是否输出调试图片
	private static final Color WHITE=new Color(255,255,255),BLACK=new Color(0,0,0);
	private ImgFindRange ifr;
	private ImageOCR ocr;
	private ImgUtil iu;
	public OCRCorrect(ImgFindRange ifr,ImageOCR ocr,ImgUtil iu){
		this.ocr=ocr;
		this.ifr=ifr;
		this.iu=iu;
	}
	public List<ImgData> correct(List<ImgData> list){
		List<ImgData[]> err=findErrorImg(list);//找到错误
		displayErrorRange(err);
		List<ImgData> linkImgs=linkErrorImg(err);//连接错误区域
		linkImgs=clear(linkImgs);//清除括号
		cut(linkImgs);//切割后识别
		err=findErrorImg(list);//再找一次
		linkErrorImgSec(err);//与前后连接，选正确率高的作为结果
		displayErrorRange(err);
		return list;
	}
	/**
	 * @author Administrator
	 * @Description
	 * @param linkImgs
	 * @throws IOException 
	 */
	private void cut(List<ImgData> linkImgs){
		for(ImgData re:linkImgs){
			ImgData next=re.nextImgData;
			double[][] arr=re.data;
			int a=0,b=arr[0].length,c=re.nextSpace,length=arr.length;
			double cf=(b-a)/(length+0.0);//计算字的长宽比
			double cj=cf-(int)cf;//计算长宽比小数部分
			int zs=(int)cf;//计算长宽比整数部分
			int bs=0;//几个字
			if( cj>=0.75 && zs>=1){ //如果是 n.75<x<n+1.25 比如1.85<x<2.25则是两个字,因为一个字的长宽比约为1:1
				bs=zs+1;
				re=cutYAvg(arr,a,b,c,bs,re);
				re.nextImgData=next;
				next.beforeImgData=re;
			}else if( cj<0.25 && zs>=2){
				bs=zs;
				re=cutYAvg(arr,a,b,c,bs,re);
				re.nextImgData=next;
				next.beforeImgData=re;
			}else{
				bs=cj==0.0?zs:zs+1;
				re=cutYBy1(arr,a,b,c,bs,re);
				re.nextImgData=next;
				next.beforeImgData=re;
				/*
				Map<String,Object> ocrRe=ocr.ocr(arr);
				re.re=(String) ocrRe.get("key");
				re.rate=(double) ocrRe.get("ratio");*/
				
			}
		}
	}
	/**
	 *通过计算平均值来切割连接字块
	 *字数据  字数据中起始点  终止点  字数  返回结果集
	 * @throws IOException 
	 **/
	private ImgData cutYAvg(double[][]arr,int begin,int end,int nextBegin,int bs,ImgData re){
		int height=arr.length;
		int length=end-begin;
		int pj=length/bs; //总长度除以几个字得到一个字的长度
		for(int j=1;j<bs;j++){
			re.data=cutArray(arr, 0,height,begin+pj*(j-1),begin+pj*j);
			Map<String,Object> ocrRe=ocr.ocr(re.data);
			re.re=(String) ocrRe.get("key");
			re.rate=(double) ocrRe.get("ratio");
			re.img=arrToImg(re.data);
			wirteImg("cut-"+new Date().getTime(), re.img);
			re.nextSpace=0;
			re.nextImgData=new ImgData();
			re.nextImgData.beforeImgData=re;
			re=re.nextImgData;
		}
		re.data=cutArray(arr,0,height,begin+pj*(bs-1),end);
		Map<String,Object> ocrRe=ocr.ocr(re.data);
		re.re=(String) ocrRe.get("key");
		re.rate=(double) ocrRe.get("ratio");
		re.img=arrToImg(re.data);
		wirteImg("cut-"+new Date().getTime(), re.img);
		re.nextSpace=nextBegin;
		return re;
	}
	private ImgData cutYBy1(double[][]arr,int begin,int end,int nextBegin,int bs,ImgData re){
		int height=arr.length;
		int pj=height;
		for(int j=1;j<bs;j++){
			re.data=cutArray(arr, 0,height,begin+pj*(j-1),begin+pj*j);
			Map<String,Object> ocrRe=ocr.ocr(re.data);
			re.re=(String) ocrRe.get("key");
			re.rate=(double) ocrRe.get("ratio");
			re.img=arrToImg(re.data);
			wirteImg("cut-"+new Date().getTime(), re.img);
			re.nextSpace=0;
			re.nextImgData=new ImgData();
			re.nextImgData.beforeImgData=re;
			re=re.nextImgData;
		}
		re.data=cutArray(arr,0,height,begin+pj*(bs-1),end);
		Map<String,Object> ocrRe=ocr.ocr(re.data);
		re.re=(String) ocrRe.get("key");
		re.rate=(double) ocrRe.get("ratio");
		re.img=arrToImg(re.data);
		wirteImg("cut-"+new Date().getTime(), re.img);
		re.nextSpace=nextBegin;
		return re;
	}
	private BufferedImage arrToImg(double[][] arr){
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
	private double[][] cutArray(double[][] arr,int xB,int xE,int yB,int yE){
		int w=xE-xB,h=yE-yB;
		double[][] re=new double[w][h];
		for(int i=0;i<w;i++){
			for(int j=0;j<h;j++){
				re[i][j]=arr[i+xB][j+yB];
			}
		}
		return re;
	}
	/**
	 * @author Administrator
	 * @Description 清除
	 * @param linkImgs
	 * @throws IOException 
	 */
	private List<ImgData> clear(List<ImgData> linkImgs){
		for(ImgData img:linkImgs){
			List<Map<String,Object>> ranges=ifr.findRange(img.data);
			int height=img.data.length;
			for(Map<String,Object> map:ranges){
				double[][] range=(double[][]) map.get("range");
				int h=range.length,w=range[0].length;
				double r=h/(w+0.0);
				if(height-h<6 && r<4 && r>2.8){
					if(ocr.ocrIsChar(range, '(')){
						List<int[]> points=(List<int[]>) map.get("points");
						img.data=clearPoint(img.data,points);
						ImgData kh=new ImgData();
						kh.re="(";
						kh.rate=0.5;
						kh.nextSpace=0;
						kh.data=range;
						kh.img=arrToImg(range);
						img.beforeImgData.nextImgData=kh;
						kh.nextImgData=img;
						kh.beforeImgData=img.beforeImgData;
						img.beforeImgData=kh;
					}else if(ocr.ocrIsChar(range, ')')){
						List<int[]> points=(List<int[]>) map.get("points");
						img.data=clearPoint(img.data,points);
						ImgData kh=new ImgData();
						kh.re=")";
						kh.rate=0.5;
						kh.data=range;
						kh.img=arrToImg(range);
						kh.nextSpace=img.nextSpace;
						kh.beforeImgData=img;
						kh.nextImgData=img.nextImgData;
						img.nextImgData.beforeImgData=kh;
						img.nextImgData=kh;
						break;
					}
				}
			}
			img.data=iu.cutImage(img.data);
			img.img=arrToImg(img.data);
			wirteImg("clear-"+new Date().getTime(), img.img);
		}
		return linkImgs;
	}
	/**
	 * @author Administrator
	 * @Description
	 * @param range
	 * @param points
	 */
	private double[][] clearPoint(double[][] range, List<int[]> points) {
		for(int[] point:points){
			range[point[0]][point[1]]=0;
		}
		return range;
	}
	/**
	 * @author Administrator
	 * @Description
	 * @param err
	 */
	private List<ImgData> linkErrorImg(List<ImgData[]> err) {
		List<ImgData> re=new ArrayList<ImgData>();
		for(ImgData[] i:err){
			/*ImgData img=new ImgData();
			img=copyData(img, i[0]);*/
			ImgData img;
			img=i[0];
			img.data=iu.twoValueByDouble(img.img, 127);
			if(i[1]!=null){//不只有一个错误数据
				while(img.nextImgData!=i[1].nextImgData){
					img.data=link(img.data, iu.twoValueByDouble(img.nextImgData.img, 127), img.nextSpace);
					img.img=arrToImg(img.data);
					img.nextImgData=img.nextImgData.nextImgData;
					img.nextSpace=img.nextImgData.nextSpace;
					img.nextImgData.beforeImgData=img;
				}
			}
			wirteImg("link-"+new Date().getTime(), img.img);
			re.add(img);
		}
		return re;
	}
	
	private void linkErrorImgSec(List<ImgData[]> err){
		for(ImgData[] i:err){
			ImgData img=i[0];
			if(isFont(img.data)){
				
			}else if(i[1]!=null && img.nextImgData==i[1]){
				img.data=link(iu.twoValueByDouble(img.img, 127), iu.twoValueByDouble(i[1].img, 127), img.nextSpace);
				if(isFont(img.data)){
					img.img=arrToImg(img.data);
					Map<String,Object> ocrRe=ocr.ocr(img.data);
					img.re=(String) ocrRe.get("key");
					wirteImg("sec-one-"+img.re, img.img);
					img.rate=(double) ocrRe.get("ratio");
					i[1].nextImgData.beforeImgData=img;
					img.nextImgData=i[1].nextImgData;
					img.nextSpace=i[1].nextSpace;
				}
			}else if(i[1]==null){
				double[][] linkBefore=link(iu.twoValueByDouble(img.beforeImgData.img, 127), iu.twoValueByDouble(img.img, 127), img.beforeImgData.nextSpace);
				double[][] linkAfter=link(iu.twoValueByDouble(img.img, 127), iu.twoValueByDouble(img.nextImgData.img, 127), img.nextSpace);
				String bRe="",aRe="";
				double bRate=0.0,aRate=0.0;
				if(isFont(linkBefore)){
					Map<String,Object> ocrBRe=ocr.ocr(linkBefore);
					bRe=(String) ocrBRe.get("key");
					bRate=(double) ocrBRe.get("ratio");
				}
				if(isFont(linkAfter)){
					Map<String,Object> ocrARe=ocr.ocr(linkAfter);
					aRe=(String) ocrARe.get("key");
					aRate=(double) ocrARe.get("ratio");
				}
				
				wirteImg("sec-else-B"+new Date().getTime(),arrToImg(linkBefore));
				wirteImg("sec-else-A"+new Date().getTime(),arrToImg(linkAfter));
				if(bRate>aRate){
					img.beforeImgData.re=bRe;
					img.beforeImgData.rate=bRate;
					img.beforeImgData.data=linkBefore;
					img.beforeImgData.img=arrToImg(linkBefore);
					img.nextImgData.beforeImgData=img.beforeImgData;
					img.beforeImgData.nextImgData=img.nextImgData;
					img.beforeImgData.nextSpace=img.nextSpace;
				}else{
					img.re=aRe;
					img.rate=aRate;
					img.data=linkAfter;
					img.img=arrToImg(linkAfter);
					img.nextImgData.nextImgData.beforeImgData=img;
					img.nextImgData=img.nextImgData.nextImgData;
					img.nextSpace=img.nextImgData.nextSpace;
				}
			}
		}
	}
	private boolean isFont(double[][] data){
		double bl=data[0].length/(data.length+0.0);
		return bl>0.8 && bl<1.2;
	}
	
	//连接两张图片  参数：前一张图  后一张图  二者中间空白的距离
	private double[][] link(double[][] befor,double[][] after,int length){
		//创建新图片 高度等于两张图片的最低高度，宽度等于二者宽度之和再加中间空白宽度
		int minH=Math.min(befor.length,after.length),lenB=befor[0].length,lenA=after[0].length;
		double[][] re=new double[minH][lenB+length+lenA];
		
		for(int i=0;i<minH;i++){
			for(int j=0;j<lenB;j++){
				re[i][j]=befor[i][j];
			}
			for(int j=0;j<length;j++){
				re[i][j+lenB]=0;//中间都用空白填充
			}
			for(int j=0;j<lenA;j++){
				re[i][j+lenB+length]=after[i][j];
			}
		}
		return re;
	}
	
	/**
	 * @author Administrator
	 * @Description 寻找图片链表中识别错误的区域
	 * @param List<ImgData> 图片链表
	 * @return ImgData[] 是一个含2个元素的数组，第一个元素表示错误区域的开始数据，第二个表示结束数据。如果错区只有一个数据，则第二个元素为空。
	 */
	private List<ImgData[]> findErrorImg(List<ImgData> list){
		List<ImgData[]> re=new ArrayList<ImgData[]>();
		ImgData[] errImgs=null;
		boolean isFrist=true;//是否是错误区域的第一个
		for(ImgData img : list){
			while(img.data!=null){
				if(img.rate<0.35){//识别错误
					if(isFrist){
						errImgs=new ImgData[2];
						re.add(errImgs);
						errImgs[0]=img;
						isFrist=false;
					}else{
						errImgs[1]=img;
					}
				}else{
					isFrist=true;
				}
				img=img.nextImgData;
			}
		}
		return re;
	}
	
	
	
	/**
	 * @author Administrator
	 * @Description
	 * @param re
	 */
	private void displayErrorRange(List<ImgData[]> re) {
		for(ImgData[] i:re){
			System.out.println("begin:  "+i[0].re+"   "+i[0].rate+"   "+i[0].nextSpace); 
			if(i[1]!=null){
				System.out.println("end  :  "+i[1].re+"   "+i[1].rate+"   "+i[1].nextSpace);
			}
		}
	}
	/**
	 * @author Administrator
	 * @Description
	 * @param string
	 * @param img
	 */
	private void wirteImg(String string, BufferedImage img) {
		if(isDebug){
			try {
				ImageIO.write(img, "jpg", new File("F:\\tmImg\\doc\\wirteImg\\"+string+".jpg"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	@SuppressWarnings("unused")
	private ImgData copyData(ImgData now,ImgData old){
		now.data=old.data;
		now.blockData=old.blockData;
		now.focusX=old.focusX;
		now.focusY=old.focusY;
		now.img=old.img;
		now.nextImgData=old.nextImgData;
		now.nextSpace=old.nextSpace;
		now.rate=old.rate;
		now.shadowX=old.shadowX;
		now.shadowY=old.shadowY;
		now.re=old.re;
		now.beforeImgData=old.beforeImgData;
		return now;
	}
}
