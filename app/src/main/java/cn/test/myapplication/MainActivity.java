package cn.test.myapplication;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.test.myapplication.view.SingleLineChartView;

public class MainActivity extends Activity {
    private SingleLineChartView mSingleLineChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSingleLineChartView = (SingleLineChartView) findViewById(R.id.single_line_chart);
        List<Double> list = new ArrayList<>();
        List<String> dateList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            list.add(Double.valueOf(new Random().nextInt(23)));
            dateList.add("1月2日");
        }
        mSingleLineChartView.setData(list, dateList, false);
    }
}
