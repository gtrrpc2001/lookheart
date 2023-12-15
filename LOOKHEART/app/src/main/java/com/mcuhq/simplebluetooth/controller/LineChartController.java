package com.mcuhq.simplebluetooth.controller;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class LineChartController {
    public static void setChartOption(LineChart chart, LineData lineData, ArrayList<String> timeArr){
        chart.setData(lineData);
        chart.setNoDataText("");// 데이터가 없는 경우 차트에 표시되는 텍스트 설정
        chart.getXAxis().setEnabled(true);   // x축 활성화(true)
        chart.getLegend().setTextSize(15f);  // 범례 텍스트 크기 설정("BPM" size)
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(timeArr));    // x축의 값 설정
        chart.setVisibleXRangeMaximum(500);  // 한 번에 보여지는 x축 최대 값
        chart.getXAxis().setGranularity(1f); // 축의 최소 간격
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // x축 위치
        chart.getXAxis().setDrawGridLines(false);    // 축의 그리드 선
        chart.getDescription().setEnabled(false);    // 차트 설명
        chart.getAxisLeft().setAxisMaximum(200f); // y 축 최대값
        chart.getAxisLeft().setAxisMinimum(40f); // y 축 최소값
        chart.getAxisRight().setEnabled(false);  // 참조 반환
        chart.setDrawMarkers(false); // 값 마커
        chart.setDragEnabled(true);  // 드래그 기능
        chart.setPinchZoom(false);   // 줌 기능
        chart.setDoubleTapToZoomEnabled(false);  // 더블 탭 줌 기능
        chart.setHighlightPerDragEnabled(false); // 드래그 시 하이라이트
        chart.getData().notifyDataChanged(); // 차트에게 데이터가 변경되었음을 알림
        chart.notifyDataSetChanged();    // 차트에게 데이터가 변경되었음을 알림
        chart.moveViewToX(0);    // 주어진 x값의 위치로 뷰 이동
        chart.invalidate();
    }

    public static void setChartData(List<Entry> data, ArrayList<Double> ArrayData){
        for (int i = 0; i < ArrayData.size(); i++) {
            data.add(new Entry((float)i, ArrayData.get(i).floatValue()));
        }
    }

    public static LineDataSet getLineData(List<Entry> data, String label, int color){
        LineDataSet targetDataSet = new LineDataSet(data, label);
        targetDataSet.setDrawCircles(false);
        targetDataSet.setColor(color);
        targetDataSet.setLineWidth(0.5f);
        targetDataSet.setDrawValues(true);
        return targetDataSet;
    }

    public static void setZoom(LineChart chart){
        for(int i = 0 ; 20 > i ; i++) {
            chart.zoomOut();
        }
    }
}
