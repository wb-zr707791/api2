package test;

/**
 * 二分查找法
 */
public class NumberReturn {
    public int number(int[] list,int key){
        int low =0;
        int high = list.length-1;
        while (high > low) {
            int mid = (high+low)/2;
            if (key<list[mid]){
                high=mid-1;
            }else if (key==list[mid]){
                return mid;
            }else {
                low=mid+1;
            }
        }
        return -low-1;
    }
}
