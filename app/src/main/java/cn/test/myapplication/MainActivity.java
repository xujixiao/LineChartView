package cn.test.myapplication;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.test.myapplication.view.SingleLineChartView;
import cn.test.myapplication.view.ThreeLineChartView;

public class MainActivity extends Activity {
    private SingleLineChartView mSingleLineChartView;
    private ThreeLineChartView mThreeLineChartView;
    private ThreeLineChartView mTwoLineChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSingleLineChartView = (SingleLineChartView) findViewById(R.id.single_line_chart);
        mThreeLineChartView = (ThreeLineChartView) findViewById(R.id.three_line_chart);
        mTwoLineChartView = (ThreeLineChartView) findViewById(R.id.test);
        List<Double> list = new ArrayList<>();
        List<Double> list2 = new ArrayList<>();
        List<Double> list3 = new ArrayList<>();
        List<String> dateList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            list.add(Double.valueOf(new Random().nextInt(23)));
            list2.add(Double.valueOf(new Random().nextInt(23)));
            list3.add(Double.valueOf(new Random().nextInt(23)));
            dateList.add("1月2日");
        }

        mSingleLineChartView.setData(list, dateList, false);
        mThreeLineChartView.setData(list, list2, list3, dateList, false);
        mTwoLineChartView.setData(list, list2, null, dateList, false);
    }
}
