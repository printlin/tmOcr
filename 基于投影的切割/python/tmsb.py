import cv2  
import numpy as np  


def cut(image):
	re=[]
	reX=cutX(image)
	length=len(reX)
	for i in range(length):
		reY=cutY(reX[i])
		re.append(reY)
	return re

def cutX(arr):
	length=len(arr[0])
	re=[]
	count =countX(arr)
	point =findPoint(count)
	lengthP=len(point)
	if lengthP%2==1:
		lengthP=lengthP-1
	for i in range(0,lengthP,2):
		re.append(arr[point[i]:point[i+1],0:length])
	return re;

def cutY(arr):
	length=len(arr)
	#print(length)
	re=[]
	count =countY(arr)
	#print(count)
	point =findPoint(count)
	#print(point)
	for i in range(0,len(point),2):
		re.append(arr[0:length,point[i]:point[i+1]])
		
	return re;

#统计一行黑色点的个数
def countX(arr):
	count=[0]*len(arr)
	#print(len(count),len(arr))
	for h in range(len(arr)):
		count2=0
		for l in range(len(arr[h])):
			if arr[h,l,0]!=255:
				count2=count2+1
				arr[h,l]=[1]
		count[h]=count2
	return count
#统计一列黑色点的个数
def countY(arr):
	count=[0]*len(arr[0])
	#print(len(count),len(arr[0]))
	for l in range(len(arr[0])):
		count2=0
		for h in range(len(arr)):
			if arr[h,l,0]!=255:
				count2=count2+1
				arr[h,l]=[1]
		count[l]=count2
	return count

#参数：纵向或横向的映射数组  返回：一维数组，0是开始1是结束，2是开始3是结束。表示行或列的分水岭  
def findPoint(arr):
	index=-1
	re=[]
	zeroToMore(arr,index,re)
	return re
#参数：纵向或横向的映射数组 数组当前下标 返回结果集  返回：re，一维数组   递归调用，记录非零下标，表示一行的开始
def zeroToMore(arr,index,re):
	if index<len(arr)-1:
		for i in range(index+1,len(arr)):
			if arr[i]>1:
				index=i
				re.append(index)
				break
			if i==len(arr)-1:
				index=i
		moreToZero(arr,index,re)
#同上  记录第一个零下标，表示一行的结束
def moreToZero(arr,index,re):
	if index<len(arr)-1:
		for i in range(index+1,len(arr)):
			if arr[i]<=1:
				index=i
				re.append(index)
				break
			if i==len(arr)-1:
				index=i
		zeroToMore(arr,index,re)

image=cv2.imread("F:/tmImg/jpgs/1.jpg")
retval, image = cv2.threshold(image, 127, 255, cv2.THRESH_BINARY)
re=cut(image)
count=0
for i in range(len(re)):
	for j in range(len(re[i])):
		cv2.imwrite("F:\\tmImg\\doc\\first\\"+str(count)+".jpg",re[i][j])
		count=count+1




