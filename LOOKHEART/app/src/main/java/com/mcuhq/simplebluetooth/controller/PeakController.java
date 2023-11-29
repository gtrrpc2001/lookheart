package com.mcuhq.simplebluetooth.controller;

public class PeakController {
    private float[] xx_ecg_outdata = new float[2];
    private float xx_msl_mm = 0;
    private float xx_outdata_itw = 0;
    private float xx_outdata_S_M = 0;
    private float xx_outdata_M_M = 0;
    private float xx_finaloper = 0;
    private long xx_real_finaloper = 0;
    private float xx_msl_pp = 0;

    private float[] xx_itx_1 = new float[13];
    private float[] xx_s_max = new float[13];
    private float[] xx_m_min = new float[13];
    private float[] xx_itx = new float[13];
    private float[] xx_ecgarray = new float[13];

    public PeakController(float[] xx_ecg_outdata,float[] xx_ecgarray,float[] xx_itx,float[] xx_m_min,float[] xx_itx_1,float[] xx_s_max){
        this.xx_ecg_outdata = xx_ecg_outdata;
        this.xx_ecgarray = xx_ecgarray;
        this.xx_itx = xx_itx;
        this.xx_m_min = xx_m_min;
        this.xx_itx_1 = xx_itx_1;
        this.xx_s_max = xx_s_max;
    }

    public PeakController(){

    }

   public double getPeackData(int ecg){
        xx_ecg_outdata[1] = xx_ecg_outdata[0];
        xx_ecg_outdata[0] = ecg;

        xx_msl_mm = Math.abs(xx_ecg_outdata[0] - xx_ecg_outdata[1]);
        xx_msl_pp = xx_maxsmin(xx_msl_mm);
        xx_outdata_itw = xx_iten(xx_msl_pp)/2;
        xx_outdata_S_M  = xx_sum_max(xx_outdata_itw);
        xx_outdata_M_M = xx_max_min(xx_outdata_S_M);
        xx_finaloper = (xx_outdata_S_M - xx_outdata_M_M);
        xx_real_finaloper= (long) (xx_iten_1(xx_finaloper)/8);
        if(xx_real_finaloper >=1024) xx_real_finaloper = 1024;
        return xx_real_finaloper;
    }

    private float xx_maxsmin(float num) // ecg p to p 援ы븯뒗 븿닔
    {
        for(int z=0; z<12; z++){
            xx_ecgarray[z] =  xx_ecgarray[z+1];
        }

        xx_ecgarray[12]=num;

        float maxvalue = xx_ecgarray[0];
        float minvalue = xx_ecgarray[0];
        for (int o=0; o<13; o++){
            if (xx_ecgarray[o]>=maxvalue) maxvalue  = xx_ecgarray[o];
            if (xx_ecgarray[o]<=minvalue) minvalue  = xx_ecgarray[o];
        }

        float finalvalue = maxvalue - minvalue ;
        return finalvalue;
    }

    private float xx_iten(float data)//씠룞빀 35媛 깦뵆
    {
        int tx = 0;
        float sumit = 0;

        xx_itx[12]= data;

        for (tx=0;tx<13;tx++)  //諛곗뿴빀
        {
            sumit += xx_itx[tx];
        }

        for (tx=0;tx<12;tx++)  //諛곗뿴 諛뼱꽌 젙젹
        {
            xx_itx[tx]= xx_itx[tx+1];
        }

        return(sumit);
    }

    private float xx_sum_max(float num) // 理쒕媛 援ы븯뒗 븿닔
    {
        for(int z=0; z<12; z++){
            xx_s_max[z] =  xx_s_max[z+1];
        }

        xx_s_max[12]=num;

        float maxvalue = xx_s_max[0];
        for (int o=0; o<13; o++){
            if (xx_s_max[o]>=maxvalue) maxvalue  = xx_s_max[o];
        }

        float finalvalue1 = maxvalue;
        return finalvalue1;
    }

    private float xx_max_min(float num) // 理쒖냼媛 援ы븯뒗 븿닔
    {
        for(int z=0; z<12; z++){
            xx_m_min[z] =  xx_m_min[z+1];
        }

        xx_m_min[12]=num;

        float minvalue = xx_m_min[0];
        for (int o=0; o<13; o++){
            if (xx_m_min[o]<=minvalue) minvalue  = xx_m_min[o];
        }

        float finalvalue2 = minvalue ;
        return finalvalue2;
    }

    long xx_iten_1(float data)//씠룞빀 35媛 깦뵆
    {
        int tx1 = 0;
        float sumit1 = 0;

        xx_itx_1[12]= data;  //34

        for (tx1=0;tx1<13;tx1++)  //諛곗뿴빀  //35
        {
            sumit1 += xx_itx_1[tx1];
        }

        for (tx1=0;tx1<12;tx1++)  //諛곗뿴 諛뼱꽌 젙젹  34
        {
            xx_itx_1[tx1]= xx_itx_1[tx1+1];
        }

        return (long) sumit1;
    }
}
