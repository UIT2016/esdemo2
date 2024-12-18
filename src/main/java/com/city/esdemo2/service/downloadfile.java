package com.city.esdemo2.service;

public class downloadfile {
    public static void main(String[] args) {
        System.out.println(dp(5));
    }
    /*
    递归函数，返回爬楼梯的方法次数
    i 需要爬的层数
     */
    public static int recursive(int i){
        if(i<=2) return i;
        return recursive(i-1)+recursive(i-2);
    }
    public static int dp (int n){
        int i=0,j=1,k=1;
        for(int m=0;m<n;m++){
            k=i+j;
            i=j;
            j=k;
        }
        return k;
    }
}
