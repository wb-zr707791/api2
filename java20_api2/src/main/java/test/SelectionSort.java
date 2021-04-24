package test;

import java.util.concurrent.ForkJoinPool;

/**
 * 选择排序
 */
public class SelectionSort {
    public int[] selectionSort(int[] list) {
        for (int i = 0; i < list.length - 1; i++) {
            int currentMin = list[i];
            int currentMinIndex = i;
            for (int j = i+1; j < list.length; j++) {

                if (currentMin > list[j]) {
                    currentMin = list[j];
                    currentMinIndex = j;
                }
            }
            if (currentMinIndex!=i){
                list[currentMinIndex] = list[i];
                list[i]=currentMin;
            }
        }
        return list;
    }
}
