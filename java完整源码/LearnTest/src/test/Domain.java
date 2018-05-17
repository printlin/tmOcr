package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Domain {
	private double[] sts=null;
	private double a=0.01;//学习速率
	private List<Map<String,Object>> list=null;
	public Domain(){
		this.list=new ArrayList<Map<String,Object>>();
		formatData();
	}
	private void formatData() {//数据初始化  {x0,x1,...xn,y}x0默认1，因为他总是x的0次方
		double[][] data={{1,1,2,2.5},
						{1,2,2,3},
						{1,3,3,4.5},
						{1,4,4,6},
						{1,5,5,7.5},
						{1,6,6,9},
						{1,7,7,10.5},
						{1,8,8,12},
						{1,9,9,13.5},
						{1,10,10,15},
						{1,11,11,16.5}};
		

		sts=new double[data[0].length-1];//设置参数的个数：x个数减1
		for(int i=0;i<data.length;i++){//遍历每一排
			double[] ff=new double[data[i].length-1];
			for(int j=0;j<data[i].length-1;j++){
				ff[j]=data[i][j];
			}
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("x", ff);
			map.put("y",data[i][data[i].length-1]);
			list.add(map);
		}
	}
	public double function(double[] xs){//函数模型
		double re=0;
		for(int i=0;i<xs.length;i++){
			re+=xs[i]*sts[i];
		}
		return re;
	}
	public void update(){//模型参数的更新
		double[] stss=new double[sts.length];//新的模型参数，此处单独用数组来装而不是直接对该参数赋值，是为了不影响下一个参数的学习，保证每个参数对应的都是同一个函数
		int len=list.size();
		for(int i=0;i<stss.length;i++){//遍历每一个参数
			double sum=0;
			for(Map<String,Object> map:list){
				double[] xs=(double[])map.get("x");
				double y=(double)map.get("y");
				sum+=(function(xs)-y)*xs[i];
			}
			//System.out.println("js---:"+a*(1.0f/len)*sum);
			stss[i]=sts[i]-a*(1.0/len)*sum;//更新该参数
		}
		sts=stss;//统一更新参数
	}
	public double dj(){//代价函数
		double sum=0;
		for(Map<String,Object> map:list){
			double[] xs=(double[])map.get("x");
			double y=(double)map.get("y");
			sum+=(function(xs)-y)*(function(xs)-y);
		}
		return (1.0/(list.size()*2))*sum;
	}
	public void go(){
		int sum=0;//参数迭代次数
		while(true){
			double oldDj=dj();//迭代前损失
			sum++;
			if(sum>=1000){//迭代次数不超过1000
				break;
			}
			update();//迭代
			double newDj=dj();//迭代后损失
			if(Math.abs(newDj-oldDj)<0.0001){//如果损失小于0.0001则认为已经拟合
				break;
			}
		}
		for(int j=0;j<sts.length;j++){
			System.out.print("st"+j+":"+sts[j]+"  ");
		}
		System.out.println("dj:"+dj()+"   sum:"+sum);
		/*for(int i=0;i<5;i++){
			update();
			for(int j=0;j<sts.length;j++){
				System.out.print("st"+j+":"+sts[j]+"  ");
			}
			System.out.println("dj:"+dj());
		}*/
		/*for(int j=0;j<sts.length;j++){
			System.out.print("st"+j+":"+sts[j]+"  ");
		}
		System.out.println("dj:"+dj());*/
		double[] fff={1,10,10};
		System.out.println(function(fff));
	}
}
