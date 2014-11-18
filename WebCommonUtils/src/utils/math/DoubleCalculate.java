package utils.math;

import java.math.BigDecimal;

/*
 * this is a class for double calculation
 * when java cal 3.0 * 16.3 will return 48.900000000006
 * use this class will prevent that case
 */

public class DoubleCalculate {
	
	//�q�{���k�B����
    private static final int DEF_DIV_SCALE = 10;
    
    //�o���������Ҥ�
    private DoubleCalculate()
    {}

    /**
     * ���Ѻ�T���[�k�B��C
     * @param v1 �Q�[��
     * @param v2 �[��
     * @return ��ӰѼƪ��M
     */
    public static double add(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }
 
    /**
     * ���Ѻ�T����k�B��C
     * @param v1 �Q���
     * @param v2 ���
     * @return ��ӰѼƪ��t
     */
    public static double sub(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }
 
    /**
     * ���Ѻ�T�����k�B��C
     * @param v1 �Q����
     * @param v2 ����
     * @return ��ӰѼƪ��n
     */
    public static double mul(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }
 
    /**
     * ���ѡ]�۹�^��T�����k�B��A��o�Ͱ����ɪ����p�ɡA��T��
     * �p���I�H��10�줸�A�H�᪺�Ʀr�|�ˤ��J�C
     * @param v1 �Q����
     * @param v2 ����
     * @return ��ӰѼƪ���
     */
    public static double div(double v1,double v2){
        return div(v1,v2,DEF_DIV_SCALE);
    }
 
    /**
     * ���ѡ]�۹�^��T�����k�B��C��o�Ͱ����ɪ����p�ɡA��scale�Ѽƫ�
     * �w��סA�H�᪺�Ʀr�|�ˤ��J�C
     * @param v1 �Q����
     * @param v2 ����
     * @param scale ��ܪ�ܻݭn��T��p���I�H��X��C
     * @return ��ӰѼƪ���
     */
    public static double div(double v1,double v2,int scale){
        if(scale<0){
            throw new IllegalArgumentException(
                "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
 
    /**
     * ���Ѻ�T���p�Ʀ�|�ˤ��J�B�z�C
     * @param v �ݭn�|�ˤ��J���Ʀ�
     * @param scale �p���I��O�d�X��
     * @return �|�ˤ��J�᪺���G
     */
    public static double round(double v,int scale){
        if(scale<0){
            throw new IllegalArgumentException(
                "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
