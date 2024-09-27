package jp.jamsketch.util

class ArrayCalc {
    public static <T> int sum(ArrayList<T> arrays, Factory<T, Integer> factory){
        int sum = 0;
        for(T t: arrays){
            sum += factory.get(t);
        }
        return sum;
    }
    public static <T> int average(ArrayList<T> arrays, Factory<T, Integer> factory){
        return sum(arrays, factory) / arrays.size();
    }


}
