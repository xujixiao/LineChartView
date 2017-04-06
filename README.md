# LineChartView自定义表格view，具备点击事件使用示例
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/activity_main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:paddingBottom="@dimen/activity_vertical_margin"
    android:paddingLeft="@dimen/activity_horizontal_margin"
    android:paddingRight="@dimen/activity_horizontal_margin"
    android:paddingTop="@dimen/activity_vertical_margin"
    tools:context="cn.test.myapplication.MainActivity">

    <cn.test.myapplication.view.SingleLineChartView
        android:id="@+id/single_line_chart"
        android:layout_width="match_parent"
        android:layout_height="300dp"
        app:custom_title="xml定义标题"/>
</RelativeLayout>


代码
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
