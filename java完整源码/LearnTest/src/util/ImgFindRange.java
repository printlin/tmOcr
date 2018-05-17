package util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.junit.Test;

/**
 * @author Administrator
 *
 */
public class ImgFindRange {
	private static final Color WHITE=new Color(255,255,255),BLACK=new Color(0,0,0);
	/**
	 * @author Administrator
	 * @throws IOException 
	 * @Description 测试
	 */
	@Test
	public void test() throws IOException {
		ImgUtil iu=new ImgUtil();
		String uri1="F:\\tmImg\\doc\\4.jpg";
		BufferedImage img = ImageIO.read(new File(uri1));
		double[][] data=iu.twoValueByDouble(img, 127);
		//display(data);
		List<BufferedImage> l=findRange4Img(data);
		int count=0;
		for(BufferedImage d:l){
			ImageIO.write(d, "jpg", new File("F:\\tmImg\\doc\\findRangeTest\\"+count+".jpg"));
			count++;
		}
		/*double[][] img={
				{0,0,1,1,0,1,0,1,0},
				{0,0,0,1,0,1,0,1,0},
				{0,1,1,1,0,1,0,1,0},
				{0,0,0,0,0,0,1,1,0},
				{0,0,1,1,0,1,0,1,0},
				{0,0,1,1,0,1,0,1,0},
				{0,0,1,1,0,1,0,1,0}};
		List<double[][]> l=findRange(img);
		for(double[][] d:l){
			display(d);
			System.out.println("\n");
		}*/
	}
	
	/**
	 * @author Administrator
	 * @Description 格式化输入二维数组
	 */
	private void display(double[][] img) {
		int wLen=img.length,hLen=img[0].length;
		for(int i=0;i<wLen;i++){
			for(int j=0;j<hLen;j++){
				System.out.print(img[i][j]+",");
			}
			System.out.println();
		}
	}
	
	/**
	 * @author Administrator
	 * @Description 搜寻图片中连通的区域（8邻接）
	 * @param img 欲寻找的图片
	 */
	public List<Map<String,Object>> findRange(double[][] img){
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		int wLen=img.length,hLen=img[0].length,tag=2;
		int[] range={wLen,hLen,0,0};//记录连通域的范围，就是一个矩形的左上角与右下角
		for(int i=0;i<wLen;i++){
			for(int j=0;j<hLen;j++){
				if(img[i][j]==1){//如果是一个未扫描过的点（后文会为扫描过的点赋一个新tag值，所以为1就是为扫描过的点）
					List<int[]> s=new ArrayList<int[]>();//保存连通域中点的坐标信息
					range[0]=wLen;range[1]=hLen;range[2]=0;range[3]=0;//重置连通域的大小信息
					findLinkPoint(img,s,i,j,tag,wLen,hLen,range);
					list.add(createImgForMap(s,range));//扫描结果就是一个连通域
					tag++;//生成新标记
				}
			}
		}
		return list;
	}
	
	/**
	 * @author Administrator
	 * @Description 转换数组为图片
	 * @param arr 二值化后的数组
	 */
	public BufferedImage arrToImg(double[][] arr){
		int w=arr.length,h=arr[0].length;
		BufferedImage img=new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for(int i=0;i<w;i++){
			for(int j=0;j<h;j++){
				if(arr[i][j]!=0){
					img.setRGB(i, j, BLACK.getRGB());
				}else{
					img.setRGB(i, j, WHITE.getRGB());
				}
			}
		}
		return img;
	}
	/**
	 * @author Administrator
	 * @Description 搜寻图片中连通的区域（8邻接）,返回图片格式
	 * @param img 欲寻找的图片
	 */
	public List<BufferedImage> findRange4Img(double[][] img){
		List<BufferedImage> list=new ArrayList<BufferedImage>();
		List<int[]> s=new ArrayList<int[]>();//保存连通域中点的坐标信息
		int wLen=img.length,hLen=img[0].length,tag=2;
		int[] range={wLen,hLen,0,0};//记录连通域的范围，就是一个矩形的左上角与右下角
		for(int i=0;i<wLen;i++){
			for(int j=0;j<hLen;j++){
				if(img[i][j]==1){//如果是一个未扫描过的点（后文会为扫描过的点赋一个新tag值，所以为1就是为扫描过的点）
					s.clear();//清空连通域
					range[0]=wLen;range[1]=hLen;range[2]=0;range[3]=0;//重置连通域的大小信息
					findLinkPoint(img,s,i,j,tag,wLen,hLen,range);
					list.add(arrToImg(createImg(s,range)));//扫描结果就是一个连通域
					tag++;//生成新标记
				}
			}
		}
		return list;
	}
	
	/**
	 * @author Administrator
	 * @Description 通过扫描得出的连通点集，创建一个矩形图像
	 * @param s 连通的点阵坐标信息
	 * @param range 连通域的大小信息
	 */
	private double[][] createImg(List<int[]> s, int[] range) {
		int minX=range[0],minY=range[1],maxX=range[2],maxY=range[3];
		double[][] re=new double[maxX-minX+1][maxY-minY+1];
		for(int[] d:s){
			re[d[0]-minX][d[1]-minY]=1;
		}
		return re;
	}
	
	/**
	 * @author Administrator
	 * @Description 通过扫描得出的连通点集，创建一个矩形图像
	 * @param s 连通的点阵坐标信息
	 * @param range 连通域的大小信息
	 */
	private Map<String,Object> createImgForMap(List<int[]> s, int[] range) {
		Map<String,Object> map=new HashMap<String,Object>();
		int minX=range[0],minY=range[1],maxX=range[2],maxY=range[3];
		double[][] re=new double[maxX-minX+1][maxY-minY+1];
		for(int[] d:s){
			re[d[0]-minX][d[1]-minY]=1;
		}
		map.put("range", re);
		map.put("points",s);
		return map;
	}

	/**
	 * @author Administrator
	 * @Description 查找与根节点img[x][y]相连接的点（8邻接）
	 * @param img 欲扫描图片
	 * @param s 所有连接点信息
	 * @param x 根节点横坐标
	 * @param y 根节点纵坐标
	 * @param tag 此连通域的标记
	 * @param wLen 欲扫描图片宽度img.length
	 * @param hLen 欲扫描图片高度img[0].length
	 * @param range 连通域的大小信息
	 */
	private void findLinkPoint(double[][] img,List<int[]> s,int x,int y,int tag,int wLen,int hLen,int[] range) {
		img[x][y]=tag;//给它一个新标记，以免再次被查找
		s.add(new int[]{x,y});//加入连通域
		range[0]=x>range[0]?range[0]:x;//更新连通域的最小x
		range[1]=y>range[1]?range[1]:y;//更新连通域的最小y
		range[2]=x>range[2]?x:range[2];//更新连通域的最大x
		range[3]=y>range[3]?y:range[3];//更新连通域的最大y
		/*int[][] point={{x-1,y-1},
				{x,y-1},
				{x+1,y-1},
				{x+1,y},
				{x+1,y+1},
				{x,y+1},
				{x-1,y+1},
				{x-1,y}};//左上角开始顺时针判断  8邻接*/
		int[][] point={{x-1,y},
				{x,y-1},
				{x+1,y},
				{x,y+1}};//左上角开始顺时针判断  4邻接
		for(int i=0,len=point.length;i<len;i++){//扫描这八个点，如果发现未被记录的点，则以这个点为根节点继续深入扫描
			int xx=point[i][0],yy=point[i][1];
			if(0<=xx && xx<wLen && 0<=yy && yy<hLen && img[xx][yy]==1){//从前向后逐一判断，一个不满足就不再执行后面的，所以此处img[xx][yy]是安全的
				findLinkPoint(img,s,xx,yy,tag,wLen,hLen,range);//递归深度优先扫描
			}
		}
	}
}
