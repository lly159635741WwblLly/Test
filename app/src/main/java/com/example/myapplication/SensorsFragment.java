package com.example.myapplication;

import static android.hardware.Sensor.TYPE_ALL;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.test02.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

//实现显示各类传感器数据，并且将监听到的数据绘制为折线图

public class SensorsFragment extends Fragment {
    //sensor manager
    private SensorManager mSManager;
    public List<Sensor> listofSensor;
    public BatteryManager mBManager;

    //views
    //private LinearLayout fstLayout;
    private TextView fstLayoutType;
    private TextView tAccelerationData;
    String strAccelerationData;
    private TextView sndLayoutType;
    private TextView tLightSensorData;
    private TextView trdLayoutType;
    private TextView tProximityData;
    String strGyroscopeData;
    private TextView fourthLayoutType;
    private TextView tGyroscopeData;
    String strRotationData;
    private TextView fifthLayoutType;
    private TextView tRotationData;
    private TextView sixthLayoutType;
    private TextView tBatteryData;
    private TextView seventhLayoutType;
    private TextView tLinearAccelerationData;
    String strLinearAccelerationData;

    //color for multiple chart
    private final int[] colors = new int[] {
            ColorTemplate.VORDIPLOM_COLORS[0],
            ColorTemplate.VORDIPLOM_COLORS[1],
            ColorTemplate.VORDIPLOM_COLORS[2]
    };
    //charts
    private LineChart AccelerationChart_0,AccelerationChart_1,AccelerationChart_2;
    private ArrayList<Entry> accXData,accYData,accZData;
    //Acceleration usually refresh to fast to show on the chart,set a count to delay the refresh
    private int nAccDelay = 0;
    private LineChart LinearAccelerationChart_0,LinearAccelerationChart_1,LinearAccelerationChart_2;
    private ArrayList<Entry> LinearaccXData,LinearaccYData,LinearaccZData;
    private int nLinearAccDelay = 0;
    private LineChart LightChart;
    private ArrayList<Entry> lightSensorValues;
    private LineChart GyroscopeChart_0,GyroscopeChart_1,GyroscopeChart_2;
    private ArrayList<Entry> gyroXData,gyroYData,gyroZData;
    private LineChart RotationChart_0,RotationChart_1,RotationChart_2;
    private ArrayList<Entry> rtXData,rtYData,rtZData;
    //rotation delay
    private int nRtDelay = 0;


    //information
    String strSensorList = "Sensor List:\n";
    int nSensorCount = 0;
    //to deal with some phone have 2 similar sensor object
    int nWakeupFlag = 0;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(com.example.test02.R.layout.fragment_sensors, container, false);
        //find views
        fstLayoutType = view.findViewById(R.id.firstLayoutType);
        tAccelerationData = view.findViewById(R.id.accelerationData);
        sndLayoutType = view.findViewById(R.id.secondLayoutType);
        tLightSensorData = view.findViewById(R.id.lightSensorData);
        trdLayoutType = view.findViewById(R.id.trdLayoutType);
        tProximityData = view.findViewById(R.id.proximityData);
        fourthLayoutType = view.findViewById(R.id.fourthLayoutType);
        tGyroscopeData = view.findViewById(R.id.gyroscopeData);
        sixthLayoutType = view.findViewById(R.id.sixthLayoutType);
        tBatteryData = view.findViewById(R.id.batterySensors);
        fifthLayoutType = view.findViewById(R.id.fifthLayoutType);
        tRotationData = view.findViewById(R.id.rotationData);
        seventhLayoutType = view.findViewById(R.id.seventhLayoutType);
        tLinearAccelerationData = view.findViewById(R.id.linearaccelerationData);

        //chart colors
        //charts
        LightChart = view.findViewById(R.id.chart1);
        lightSensorValues = new ArrayList<>();
        AccelerationChart_0 = view.findViewById(R.id.accelerationChart_0);
        AccelerationChart_1 = view.findViewById(R.id.accelerationChart_1);
        AccelerationChart_2 = view.findViewById(R.id.accelerationChart_2);
        accXData = new ArrayList<>();
        accYData = new ArrayList<>();
        accZData = new ArrayList<>();
        LinearAccelerationChart_0 = view.findViewById(R.id.linearaccelerationChart_0);
        LinearAccelerationChart_1 = view.findViewById(R.id.linearaccelerationChart_1);
        LinearAccelerationChart_2 = view.findViewById(R.id.linearaccelerationChart_2);
        LinearaccXData = new ArrayList<>();
        LinearaccYData = new ArrayList<>();
        LinearaccZData = new ArrayList<>();
        GyroscopeChart_0 = view.findViewById(R.id.gyroscopeChart_0);
        GyroscopeChart_1 = view.findViewById(R.id.gyroscopeChart_1);
        GyroscopeChart_2 = view.findViewById(R.id.gyroscopeChart_2);
        gyroXData = new ArrayList<>();
        gyroYData = new ArrayList<>();
        gyroZData = new ArrayList<>();
        RotationChart_0 = view.findViewById(R.id.rotationChart_0);
        RotationChart_1 = view.findViewById(R.id.rotationChart_1);
        RotationChart_2 = view.findViewById(R.id.rotationChart_2);
        rtXData = new ArrayList<>();
        rtYData = new ArrayList<>();
        rtZData = new ArrayList<>();


        //init sensor manager
        mSManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        listofSensor = mSManager.getSensorList(TYPE_ALL);
        mBManager = (BatteryManager) getActivity().getSystemService(Context.BATTERY_SERVICE);

        //show sensors
        for (Sensor sensor : listofSensor) {
            if (sensor.getStringType() != null)
                strSensorList = strSensorList + sensor.getStringType() + "\t";
            if (sensor.getName() != null)
                strSensorList = strSensorList + "Detail: " + sensor.getName() + "\n" + "------------------------------\n";
            if (sensor.getName().indexOf("WAKE_UP") > 0) nWakeupFlag = 1;
            nSensorCount++;
        }









        //1st sensor : acceleration sensor
        //chart
        InitChart(AccelerationChart_0, 20, 5);
        InitChart(AccelerationChart_1, 20, 5);
        InitChart(AccelerationChart_2, 20, 5);
        //设置表样式
        //绘制区域边框、设置边框颜色、边框宽度
        AccelerationChart_0.setDrawBorders(true);
        AccelerationChart_0.setBorderColor(Color.WHITE);
        AccelerationChart_0.setBorderWidth(2);
        AccelerationChart_1.setDrawBorders(true);
        AccelerationChart_1.setBorderColor(Color.WHITE);
        AccelerationChart_1.setBorderWidth(2);
        AccelerationChart_2.setDrawBorders(true);
        AccelerationChart_2.setBorderColor(Color.WHITE);
        AccelerationChart_2.setBorderWidth(2);


        //滑动相关
        AccelerationChart_0.setTouchEnabled(true); // 所有触摸事件,默认true
        AccelerationChart_0.setDragEnabled(true);    // 可拖动,默认true
        AccelerationChart_0.setScaleEnabled(true);   // 两个轴上的缩放,X,Y分别默认为true
        AccelerationChart_0.setScaleXEnabled(true);  // X轴上的缩放,默认true
        AccelerationChart_0.setScaleYEnabled(true);  // Y轴上的缩放,默认true
        AccelerationChart_0.setPinchZoom(true);  // X,Y轴同时缩放，false则X,Y轴单独缩放,默认false
        AccelerationChart_0.setDoubleTapToZoomEnabled(true); // 双击缩放,默认true
        AccelerationChart_0.setDragDecelerationEnabled(true);    // 抬起手指，继续滑动,默认true
        AccelerationChart_0.setDragDecelerationFrictionCoef(0.9f);   // 摩擦系数,[0-1]，较大值速度会缓慢下降，0，立即停止;1,无效值，并转换为0.9999.默认0.9f.
        AccelerationChart_0.setOnChartGestureListener (new OnChartGestureListener() { // 手势监听器
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 按下
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 抬起,取消
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {
                // 长按
            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
                // 双击
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
                // 单击
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
                // 甩动
            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
                // 缩放
            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
                // 移动
            }
        });
        AccelerationChart_1.setTouchEnabled(true); // 所有触摸事件,默认true
        AccelerationChart_1.setDragEnabled(true);    // 可拖动,默认true
        AccelerationChart_1.setScaleEnabled(true);   // 两个轴上的缩放,X,Y分别默认为true
        AccelerationChart_1.setScaleXEnabled(true);  // X轴上的缩放,默认true
        AccelerationChart_1.setScaleYEnabled(true);  // Y轴上的缩放,默认true
        AccelerationChart_1.setPinchZoom(true);  // X,Y轴同时缩放，false则X,Y轴单独缩放,默认false
        AccelerationChart_1.setDoubleTapToZoomEnabled(true); // 双击缩放,默认true
        AccelerationChart_1.setDragDecelerationEnabled(true);    // 抬起手指，继续滑动,默认true
        AccelerationChart_1.setDragDecelerationFrictionCoef(0.9f);   // 摩擦系数,[0-1]，较大值速度会缓慢下降，0，立即停止;1,无效值，并转换为0.9999.默认0.9f.
        AccelerationChart_1.setOnChartGestureListener (new OnChartGestureListener() { // 手势监听器
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 按下
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 抬起,取消
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {
                // 长按
            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
                // 双击
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
                // 单击
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
                // 甩动
            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
                // 缩放
            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
                // 移动
            }
        });
        AccelerationChart_2.setTouchEnabled(true); // 所有触摸事件,默认true
        AccelerationChart_2.setDragEnabled(true);    // 可拖动,默认true
        AccelerationChart_2.setScaleEnabled(true);   // 两个轴上的缩放,X,Y分别默认为true
        AccelerationChart_2.setScaleXEnabled(true);  // X轴上的缩放,默认true
        AccelerationChart_2.setScaleYEnabled(true);  // Y轴上的缩放,默认true
        AccelerationChart_2.setPinchZoom(true);  // X,Y轴同时缩放，false则X,Y轴单独缩放,默认false
        AccelerationChart_2.setDoubleTapToZoomEnabled(true); // 双击缩放,默认true
        AccelerationChart_2.setDragDecelerationEnabled(true);    // 抬起手指，继续滑动,默认true
        AccelerationChart_2.setDragDecelerationFrictionCoef(0.9f);   // 摩擦系数,[0-1]，较大值速度会缓慢下降，0，立即停止;1,无效值，并转换为0.9999.默认0.9f.
        AccelerationChart_2.setOnChartGestureListener (new OnChartGestureListener() { // 手势监听器
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 按下
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 抬起,取消
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {
                // 长按
            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
                // 双击
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
                // 单击
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
                // 甩动
            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
                // 缩放
            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
                // 移动
            }
        });


        //轴相关
        // 由四个元素组成：
        // 标签：即刻度值。也可以自定义，比如时间，距离等等，下面会说一下；
        // 轴线：坐标轴；
        // 网格线：垂直于轴线对应每个值画的轴线；
        // 限制线：最值等线。
        XAxis xAxis1 = AccelerationChart_0.getXAxis();    // 获取X轴
        YAxis yAxis1 = AccelerationChart_0.getAxisLeft(); // 获取Y轴,mLineChart.getAxis(YAxis.AxisDependency.LEFT);也可以获取Y轴
        AccelerationChart_0.getAxisRight().setEnabled(false);    // 不绘制右侧的轴线
        // 轴颜色
        yAxis1.setTextColor(Color.WHITE);  // 标签字体颜色
        yAxis1.setTextSize(10);    // 标签字体大小，dp，6-24之间，默认为10dp
        yAxis1.setTypeface(null);    // 标签字体
        yAxis1.setGridColor(Color.DKGRAY);    // 网格线颜色，默认GRAY
        yAxis1.setGridLineWidth(1);    // 网格线宽度，dp，默认1dp
        yAxis1.setAxisLineColor(Color.WHITE);  // 坐标轴颜色，默认GRAY.测试到一个bug，假如左侧线只有1dp，
        // 那么如果x轴有线且有网格线，当刻度拉的正好位置时会覆盖到y轴的轴线，变为X轴网格线颜色，结局办法是，要么不画轴线，要么就是坐标轴稍微宽点
        xAxis1.setAxisLineColor(Color.WHITE); // 坐标轴颜色，默认GRAY
        xAxis1.setTextColor(Color.WHITE); //刻度文字颜色
        xAxis1.setGridColor(Color.DKGRAY);   // 网格线颜色，默认GRAY
        xAxis1.setGridLineWidth(2); // 网格线宽度，dp，默认1dp
        yAxis1.setAxisLineWidth(2);  // 坐标轴线宽度，dp，默认1dp
        yAxis1.enableGridDashedLine(20, 10, 1);    // 网格线为虚线，lineLength，每段实线长度,spaceLength,虚线间隔长度，phase，起始点（进过测试，最后这个参数也没看出来干啥的）

        XAxis xAxis2 = AccelerationChart_1.getXAxis();
        YAxis yAxis2 = AccelerationChart_1.getAxisLeft();
        AccelerationChart_1.getAxisRight().setEnabled(false);

        yAxis2.setTextColor(Color.WHITE);
        yAxis2.setTextSize(10);
        yAxis2.setTypeface(null);
        yAxis2.setGridColor(Color.DKGRAY);
        yAxis2.setGridLineWidth(1);
        yAxis2.setAxisLineColor(Color.WHITE);
        xAxis2.setAxisLineColor(Color.WHITE);
        xAxis2.setTextColor(Color.WHITE);
        xAxis2.setGridColor(Color.DKGRAY);
        xAxis2.setGridLineWidth(2);
        yAxis2.setAxisLineWidth(2);
        yAxis2.enableGridDashedLine(20, 10, 1);

        XAxis xAxis3 = AccelerationChart_2.getXAxis();
        YAxis yAxis3 = AccelerationChart_2.getAxisLeft();
        AccelerationChart_2.getAxisRight().setEnabled(false);

        yAxis3.setTextColor(Color.WHITE);
        yAxis3.setTextSize(10);
        yAxis3.setTypeface(null);
        yAxis3.setGridColor(Color.DKGRAY);
        yAxis3.setGridLineWidth(1);
        yAxis3.setAxisLineColor(Color.WHITE);
        xAxis3.setAxisLineColor(Color.WHITE);
        xAxis3.setTextColor(Color.WHITE);
        xAxis3.setGridColor(Color.DKGRAY);
        xAxis3.setGridLineWidth(2);
        yAxis3.setAxisLineWidth(2);
        yAxis3.enableGridDashedLine(20, 10, 1);


        //图例相关
        Legend legend1 = AccelerationChart_0.getLegend(); // 获取图例，但是在数据设置给chart之前是不可获取的
        legend1.setEnabled(true);    // 是否绘制图例
        legend1.setTextColor(Color.WHITE);    // 图例标签字体颜色，默认BLACK
        legend1.setTextSize(15); // 图例标签字体大小[6,24]dp,默认10dp
        legend1.setTypeface(null);   // 图例标签字体
        legend1.setWordWrapEnabled(false);    // 当图例超出时是否换行适配，这个配置会降低性能，且只有图例在底部时才可以适配。默认false
        legend1.setMaxSizePercent(1f); // 设置，默认0.95f,图例最大尺寸区域占图表区域之外的比例
        legend1.setForm(Legend.LegendForm.CIRCLE);   // 设置图例的形状，SQUARE, CIRCLE 或者 LINE
        legend1.setFormSize(10); // 图例图形尺寸，dp，默认8dp
        legend1.setXEntrySpace(6);  // 设置水平图例间间距，默认6dp
        legend1.setYEntrySpace(0);  // 设置垂直图例间间距，默认0
        legend1.setFormToTextSpace(5);    // 设置图例的标签与图形之间的距离，默认5dp
        legend1.setWordWrapEnabled(true);   // 图标单词是否适配。只有在底部才会有效，
        legend1.setCustom(new LegendEntry[]{new LegendEntry("Acc X (m/s^2) ", Legend.LegendForm.CIRCLE, 10, 5, null, Color.RED)});

        Legend legend2 = AccelerationChart_1.getLegend();
        legend2.setEnabled(true);
        legend2.setTextColor(Color.WHITE);
        legend2.setTextSize(15);
        legend2.setTypeface(null);
        legend2.setWordWrapEnabled(false);
        legend2.setMaxSizePercent(1f);
        legend2.setForm(Legend.LegendForm.CIRCLE);
        legend2.setFormSize(10);
        legend2.setXEntrySpace(6);
        legend2.setYEntrySpace(0);
        legend2.setFormToTextSpace(5);
        legend2.setWordWrapEnabled(true);
        legend2.setCustom(new LegendEntry[]{new LegendEntry("Acc Y (m/s^2)", Legend.LegendForm.CIRCLE, 10, 5, null, Color.RED)});

        Legend legend3 = AccelerationChart_2.getLegend();
        legend3.setEnabled(true);
        legend3.setTextColor(Color.WHITE);
        legend3.setTextSize(15);
        legend3.setTypeface(null);
        legend3.setWordWrapEnabled(false);
        legend3.setMaxSizePercent(1f);
        legend3.setForm(Legend.LegendForm.CIRCLE);
        legend3.setFormSize(10);
        legend3.setXEntrySpace(6);
        legend3.setYEntrySpace(0);
        legend3.setFormToTextSpace(5);
        legend3.setWordWrapEnabled(true);
        legend3.setCustom(new LegendEntry[]{new LegendEntry("Acc Z (m/s^2)", Legend.LegendForm.CIRCLE, 10, 5, null, Color.RED)});


        fstLayoutType.setText("加速度传感器\nAcceleration Sensor");
        //set the listener of acceleration sensor
        SensorEventListener SEVforAS = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                strAccelerationData = "X: " + sensorEvent.values[0]
                        + "\nY: " + sensorEvent.values[1]
                        + "\nZ: " + sensorEvent.values[2] + "\n";
                tAccelerationData.setText(strAccelerationData);
                if (nAccDelay < 5 && accXData.size() != 0) {
                    nAccDelay++;
                } else {
                    nAccDelay = 0;
                    //设置表中数据样式
                    SetChartData(AccelerationChart_0, accXData, sensorEvent.values[0], 1, Color.RED, "" , 0, true);
                    SetChartData(AccelerationChart_1, accYData, sensorEvent.values[1], 1, Color.RED, "", 0, true);
                    SetChartData(AccelerationChart_2, accZData, sensorEvent.values[2], 1, Color.RED, "", 0, true);

                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        //find the acceleration sensor
        //!!!!!!!!!
        //Notice: the acceleration sensor can be registered
        // as "TYPE_LINEAR_ACCELERATION" or "TYPE_ACCELEROMETER"
        Sensor accelerationSensor;
        for (Sensor sensor : listofSensor) {
            if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION || sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerationSensor = sensor;
                mSManager.registerListener(SEVforAS, accelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
                break;
            }
        }







        //2nd sensor: light sensor
        sndLayoutType.setText("光线传感器\nLight Sensor");
        //set chart
        InitChart(LightChart, 0, 0);
        LightChart.setDrawBorders(true);
        LightChart.setBorderColor(Color.WHITE);
        LightChart.setBorderWidth(2);
        LightChart.setTouchEnabled(true);
        LightChart.setDragEnabled(true);
        LightChart.setScaleEnabled(true);
        LightChart.setScaleXEnabled(true);
        LightChart.setScaleYEnabled(true);
        LightChart.setPinchZoom(true);
        LightChart.setDoubleTapToZoomEnabled(true);
        LightChart.setDragDecelerationEnabled(true);
        LightChart.setDragDecelerationFrictionCoef(0.9f);
        LightChart.setOnChartGestureListener (new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartLongPressed(MotionEvent me) {

            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {

            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {

            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        });
        XAxis xAxis4 = LightChart.getXAxis();
        YAxis yAxis4 = LightChart.getAxisLeft();
        LightChart.getAxisRight().setEnabled(false);
        yAxis4.setTextColor(Color.WHITE);
        yAxis4.setTextSize(10);
        yAxis4.setTypeface(null);
        yAxis4.setGridColor(Color.DKGRAY);
        yAxis4.setGridLineWidth(1);
        yAxis4.setAxisLineColor(Color.WHITE);
        xAxis4.setAxisLineColor(Color.WHITE);
        xAxis4.setTextColor(Color.WHITE);
        xAxis4.setGridColor(Color.DKGRAY);
        xAxis4.setGridLineWidth(2);
        yAxis4.setAxisLineWidth(2);
        yAxis4.enableGridDashedLine(20, 10, 1);

        Legend legend4 = LightChart.getLegend();
        legend4.setEnabled(true);
        legend4.setTextColor(Color.WHITE);
        legend4.setTextSize(15);
        legend4.setTypeface(null);
        legend4.setWordWrapEnabled(false);
        legend4.setMaxSizePercent(1f);
        legend4.setForm(Legend.LegendForm.CIRCLE);
        legend4.setFormSize(10);
        legend4.setXEntrySpace(6);
        legend4.setYEntrySpace(0);
        legend4.setFormToTextSpace(5);
        legend4.setWordWrapEnabled(true);
        legend4.setCustom(new LegendEntry[]{new LegendEntry("Light Strength (lux)", Legend.LegendForm.CIRCLE, 10, 5, null, Color.RED)});


        //set the listener of light sensor
        SensorEventListener SEVforLS = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                tLightSensorData.setText("Light: " + sensorEvent.values[0]);
                SetChartData(LightChart, lightSensorValues, sensorEvent.values[0], 10, Color.RED, "", 0, true);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        Sensor lightSensor;
        for (Sensor sensor : listofSensor) {
            if (sensor.getType() == Sensor.TYPE_LIGHT) {
                lightSensor = sensor;
                mSManager.registerListener(SEVforLS, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
                break;
            }
        }








        //3rd sensor : Proximity Sensor
        trdLayoutType.setText("距离传感器\nProximity Sensor");
        //set the listener of Orientation sensor
        SensorEventListener SEVforPS = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                tProximityData.setText("Proximity:" + sensorEvent.values[0] + "cm");
                if (sensorEvent.values[0] == 0.0) tProximityData.setTextColor(0xffff0000);
                if (sensorEvent.values[0] != 0.0) tProximityData.setTextColor(0xff0000ff);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        Sensor proximitySensor;
        for (Sensor sensor : listofSensor) {
            if (sensor.getType() == Sensor.TYPE_PROXIMITY) {
                proximitySensor = sensor;
                mSManager.registerListener(SEVforPS, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
                break;
            }
        }








        //4th sensor : gyroscope sensor
        //chart
        InitChart(GyroscopeChart_0, 0, 0);
        InitChart(GyroscopeChart_1, 0, 0);
        InitChart(GyroscopeChart_2, 0, 0);
//设置表样式
//绘制区域边框、设置边框颜色、边框宽度
        GyroscopeChart_0.setDrawBorders(true);
        GyroscopeChart_0.setBorderColor(Color.WHITE);
        GyroscopeChart_0.setBorderWidth(2);
        GyroscopeChart_1.setDrawBorders(true);
        GyroscopeChart_1.setBorderColor(Color.WHITE);
        GyroscopeChart_1.setBorderWidth(2);
        GyroscopeChart_2.setDrawBorders(true);
        GyroscopeChart_2.setBorderColor(Color.WHITE);
        GyroscopeChart_2.setBorderWidth(2);


//滑动相关
        GyroscopeChart_0.setTouchEnabled(true); // 所有触摸事件,默认true
        GyroscopeChart_0.setDragEnabled(true);    // 可拖动,默认true
        GyroscopeChart_0.setScaleEnabled(true);   // 两个轴上的缩放,X,Y分别默认为true
        GyroscopeChart_0.setScaleXEnabled(true);  // X轴上的缩放,默认true
        GyroscopeChart_0.setScaleYEnabled(true);  // Y轴上的缩放,默认true
        GyroscopeChart_0.setPinchZoom(true);  // X,Y轴同时缩放，false则X,Y轴单独缩放,默认false
        GyroscopeChart_0.setDoubleTapToZoomEnabled(true); // 双击缩放,默认true
        GyroscopeChart_0.setDragDecelerationEnabled(true);    // 抬起手指，继续滑动,默认true
        GyroscopeChart_0.setDragDecelerationFrictionCoef(0.9f);   // 摩擦系数,[0-1]，较大值速度会缓慢下降，0，立即停止;1,无效值，并转换为0.9999.默认0.9f.
        GyroscopeChart_0.setOnChartGestureListener (new OnChartGestureListener() { // 手势监听器
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 按下
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 抬起,取消
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {
                // 长按
            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
                // 双击
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
                // 单击
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
                // 甩动
            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
                // 缩放
            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
                // 移动
            }
        });
        GyroscopeChart_1.setTouchEnabled(true); // 所有触摸事件,默认true
        GyroscopeChart_1.setDragEnabled(true);    // 可拖动,默认true
        GyroscopeChart_1.setScaleEnabled(true);   // 两个轴上的缩放,X,Y分别默认为true
        GyroscopeChart_1.setScaleXEnabled(true);  // X轴上的缩放,默认true
        GyroscopeChart_1.setScaleYEnabled(true);  // Y轴上的缩放,默认true
        GyroscopeChart_1.setPinchZoom(true);  // X,Y轴同时缩放，false则X,Y轴单独缩放,默认false
        GyroscopeChart_1.setDoubleTapToZoomEnabled(true); // 双击缩放,默认true
        GyroscopeChart_1.setDragDecelerationEnabled(true);    // 抬起手指，继续滑动,默认true
        GyroscopeChart_1.setDragDecelerationFrictionCoef(0.9f);   // 摩擦系数,[0-1]，较大值速度会缓慢下降，0，立即停止;1,无效值，并转换为0.9999.默认0.9f.
        GyroscopeChart_1.setOnChartGestureListener (new OnChartGestureListener() { // 手势监听器
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 按下
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 抬起,取消
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {
                // 长按
            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
                // 双击
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
                // 单击
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
                // 甩动
            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
                // 缩放
            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
                // 移动
            }
        });
        GyroscopeChart_2.setTouchEnabled(true); // 所有触摸事件,默认true
        GyroscopeChart_2.setDragEnabled(true);    // 可拖动,默认true
        GyroscopeChart_2.setScaleEnabled(true);   // 两个轴上的缩放,X,Y分别默认为true
        GyroscopeChart_2.setScaleXEnabled(true);  // X轴上的缩放,默认true
        GyroscopeChart_2.setScaleYEnabled(true);  // Y轴上的缩放,默认true
        GyroscopeChart_2.setPinchZoom(true);  // X,Y轴同时缩放，false则X,Y轴单独缩放,默认false
        GyroscopeChart_2.setDoubleTapToZoomEnabled(true); // 双击缩放,默认true
        GyroscopeChart_2.setDragDecelerationEnabled(true);    // 抬起手指，继续滑动,默认true
        GyroscopeChart_2.setDragDecelerationFrictionCoef(0.9f);   // 摩擦系数,[0-1]，较大值速度会缓慢下降，0，立即停止;1,无效值，并转换为0.9999.默认0.9f.
        GyroscopeChart_2.setOnChartGestureListener (new OnChartGestureListener() { // 手势监听器
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 按下
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 抬起,取消
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {
                // 长按
            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
                // 双击
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
                // 单击
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
                // 甩动
            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
                // 缩放
            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
                // 移动
            }
        });


//轴相关
// 由四个元素组成：
// 标签：即刻度值。也可以自定义，比如时间，距离等等，下面会说一下；
// 轴线：坐标轴；
// 网格线：垂直于轴线对应每个值画的轴线；
// 限制线：最值等线。
        XAxis xAxis5 = GyroscopeChart_0.getXAxis();    // 获取X轴
        YAxis yAxis5 = GyroscopeChart_0.getAxisLeft(); // 获取Y轴,mLineChart.getAxis(YAxis.AxisDependency.LEFT);也可以获取Y轴
        GyroscopeChart_0.getAxisRight().setEnabled(false);    // 不绘制右侧的轴线
// 轴颜色
        yAxis5.setTextColor(Color.WHITE);  // 标签字体颜色
        yAxis5.setTextSize(10);    // 标签字体大小，dp，6-24之间，默认为10dp
        yAxis5.setTypeface(null);    // 标签字体
        yAxis5.setGridColor(Color.DKGRAY);    // 网格线颜色，默认GRAY
        yAxis5.setGridLineWidth(1);    // 网格线宽度，dp，默认1dp
        yAxis5.setAxisLineColor(Color.WHITE);  // 坐标轴颜色，默认GRAY.测试到一个bug，假如左侧线只有1dp，
// 那么如果x轴有线且有网格线，当刻度拉的正好位置时会覆盖到y轴的轴线，变为X轴网格线颜色，结局办法是，要么不画轴线，要么就是坐标轴稍微宽点
        xAxis5.setAxisLineColor(Color.WHITE); // 坐标轴颜色，默认GRAY
        xAxis5.setTextColor(Color.WHITE); //刻度文字颜色
        xAxis5.setGridColor(Color.DKGRAY);   // 网格线颜色，默认GRAY
        xAxis5.setGridLineWidth(2); // 网格线宽度，dp，默认1dp
        yAxis5.setAxisLineWidth(2);  // 坐标轴线宽度，dp，默认1dp
        yAxis5.enableGridDashedLine(20, 10, 1);    // 网格线为虚线，lineLength，每段实线长度,spaceLength,虚线间隔长度，phase，起始点（进过测试，最后这个参数也没看出来干啥的）

        XAxis xAxis6 = GyroscopeChart_1.getXAxis();
        YAxis yAxis6 = GyroscopeChart_1.getAxisLeft();
        GyroscopeChart_1.getAxisRight().setEnabled(false);

        yAxis6.setTextColor(Color.WHITE);
        yAxis6.setTextSize(10);
        yAxis6.setTypeface(null);
        yAxis6.setGridColor(Color.DKGRAY);
        yAxis6.setGridLineWidth(1);
        yAxis6.setAxisLineColor(Color.WHITE);
        xAxis6.setAxisLineColor(Color.WHITE);
        xAxis6.setTextColor(Color.WHITE);
        xAxis6.setGridColor(Color.DKGRAY);
        xAxis6.setGridLineWidth(2);
        yAxis6.setAxisLineWidth(2);
        yAxis6.enableGridDashedLine(20, 10, 1);

        XAxis xAxis7 = GyroscopeChart_2.getXAxis();
        YAxis yAxis7 = GyroscopeChart_2.getAxisLeft();
        GyroscopeChart_2.getAxisRight().setEnabled(false);

        yAxis7.setTextColor(Color.WHITE);
        yAxis7.setTextSize(10);
        yAxis7.setTypeface(null);
        yAxis7.setGridColor(Color.DKGRAY);
        yAxis7.setGridLineWidth(1);
        yAxis7.setAxisLineColor(Color.WHITE);
        xAxis7.setAxisLineColor(Color.WHITE);
        xAxis7.setTextColor(Color.WHITE);
        xAxis7.setGridColor(Color.DKGRAY);
        xAxis7.setGridLineWidth(2);
        yAxis7.setAxisLineWidth(2);
        yAxis7.enableGridDashedLine(20, 10, 1);


//图例相关
        Legend legend5 = GyroscopeChart_0.getLegend(); // 获取图例，但是在数据设置给chart之前是不可获取的
        legend5.setEnabled(true);    // 是否绘制图例
        legend5.setTextColor(Color.WHITE);    // 图例标签字体颜色，默认BLACK
        legend5.setTextSize(15); // 图例标签字体大小[6,24]dp,默认10dp
        legend5.setTypeface(null);   // 图例标签字体
        legend5.setWordWrapEnabled(false);    // 当图例超出时是否换行适配，这个配置会降低性能，且只有图例在底部时才可以适配。默认false
        legend5.setMaxSizePercent(1f); // 设置，默认0.95f,图例最大尺寸区域占图表区域之外的比例
        legend5.setForm(Legend.LegendForm.CIRCLE);   // 设置图例的形状，SQUARE, CIRCLE 或者 LINE
        legend5.setFormSize(10); // 图例图形尺寸，dp，默认8dp
        legend5.setXEntrySpace(6);  // 设置水平图例间间距，默认6dp
        legend5.setYEntrySpace(0);  // 设置垂直图例间间距，默认0
        legend5.setFormToTextSpace(5);    // 设置图例的标签与图形之间的距离，默认5dp
        legend5.setWordWrapEnabled(true);   // 图标单词是否适配。只有在底部才会有效，
        legend5.setCustom(new LegendEntry[]{new LegendEntry("AngSpeed X (rad/s) ", Legend.LegendForm.CIRCLE, 10, 5, null, Color.RED)});

        Legend legend6 = GyroscopeChart_1.getLegend();
        legend6.setEnabled(true);
        legend6.setTextColor(Color.WHITE);
        legend6.setTextSize(15);
        legend6.setTypeface(null);
        legend6.setWordWrapEnabled(false);
        legend6.setMaxSizePercent(1f);
        legend6.setForm(Legend.LegendForm.CIRCLE);
        legend6.setFormSize(10);
        legend6.setXEntrySpace(6);
        legend6.setYEntrySpace(0);
        legend6.setFormToTextSpace(5);
        legend6.setWordWrapEnabled(true);
        legend6.setCustom(new LegendEntry[]{new LegendEntry("AngSpeed Y (rad/s)", Legend.LegendForm.CIRCLE, 10, 5, null, Color.RED)});

        Legend legend7 = GyroscopeChart_2.getLegend();
        legend7.setEnabled(true);
        legend7.setTextColor(Color.WHITE);
        legend7.setTextSize(15);
        legend7.setTypeface(null);
        legend7.setWordWrapEnabled(false);
        legend7.setMaxSizePercent(1f);
        legend7.setForm(Legend.LegendForm.CIRCLE);
        legend7.setFormSize(10);
        legend7.setXEntrySpace(6);
        legend7.setYEntrySpace(0);
        legend7.setFormToTextSpace(5);
        legend7.setWordWrapEnabled(true);
        legend7.setCustom(new LegendEntry[]{new LegendEntry("AngSpeed Z (rad/s)", Legend.LegendForm.CIRCLE, 10, 5, null, Color.RED)});


        fourthLayoutType.setText("陀螺仪\nGyroscope Sensor");
        //set the listener of gyroscope sensor
        SensorEventListener SEVforGY = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                strGyroscopeData = "X: " + sensorEvent.values[0]
                        + "\nY: " + sensorEvent.values[1]
                        + "\nZ: " + sensorEvent.values[2] + "\n";
                tGyroscopeData.setText(strGyroscopeData);
                SetChartData(GyroscopeChart_0, gyroXData, sensorEvent.values[0], 1, Color.RED, "", 0, true);
                SetChartData(GyroscopeChart_1, gyroYData, sensorEvent.values[1], 1, Color.RED, "", 0, true);
                SetChartData(GyroscopeChart_2, gyroZData, sensorEvent.values[2], 1, Color.RED, "", 0, true);

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        //find the gyroscope sensor
        Sensor gyroscopeSensor;
        for (Sensor sensor : listofSensor) {
            if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                gyroscopeSensor = sensor;
                mSManager.registerListener(SEVforGY, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
                break;
            }
        }








        //5th sensor : rotation vector sensor
        //chart
        InitChart(RotationChart_0, 0, 0);
        InitChart(RotationChart_1, 0, 0);
        InitChart(RotationChart_2, 0, 0);
//设置表样式
//绘制区域边框、设置边框颜色、边框宽度
        RotationChart_0.setDrawBorders(true);
        RotationChart_0.setBorderColor(Color.WHITE);
        RotationChart_0.setBorderWidth(2);
        RotationChart_1.setDrawBorders(true);
        RotationChart_1.setBorderColor(Color.WHITE);
        RotationChart_1.setBorderWidth(2);
        RotationChart_2.setDrawBorders(true);
        RotationChart_2.setBorderColor(Color.WHITE);
        RotationChart_2.setBorderWidth(2);


//滑动相关
        RotationChart_0.setTouchEnabled(true); // 所有触摸事件,默认true
        RotationChart_0.setDragEnabled(true);    // 可拖动,默认true
        RotationChart_0.setScaleEnabled(true);   // 两个轴上的缩放,X,Y分别默认为true
        RotationChart_0.setScaleXEnabled(true);  // X轴上的缩放,默认true
        RotationChart_0.setScaleYEnabled(true);  // Y轴上的缩放,默认true
        RotationChart_0.setPinchZoom(true);  // X,Y轴同时缩放，false则X,Y轴单独缩放,默认false
        RotationChart_0.setDoubleTapToZoomEnabled(true); // 双击缩放,默认true
        RotationChart_0.setDragDecelerationEnabled(true);    // 抬起手指，继续滑动,默认true
        RotationChart_0.setDragDecelerationFrictionCoef(0.9f);   // 摩擦系数,[0-1]，较大值速度会缓慢下降，0，立即停止;1,无效值，并转换为0.9999.默认0.9f.
        RotationChart_0.setOnChartGestureListener (new OnChartGestureListener() { // 手势监听器
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 按下
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 抬起,取消
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {
                // 长按
            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
                // 双击
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
                // 单击
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
                // 甩动
            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
                // 缩放
            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
                // 移动
            }
        });
        RotationChart_1.setTouchEnabled(true); // 所有触摸事件,默认true
        RotationChart_1.setDragEnabled(true);    // 可拖动,默认true
        RotationChart_1.setScaleEnabled(true);   // 两个轴上的缩放,X,Y分别默认为true
        RotationChart_1.setScaleXEnabled(true);  // X轴上的缩放,默认true
        RotationChart_1.setScaleYEnabled(true);  // Y轴上的缩放,默认true
        RotationChart_1.setPinchZoom(true);  // X,Y轴同时缩放，false则X,Y轴单独缩放,默认false
        RotationChart_1.setDoubleTapToZoomEnabled(true); // 双击缩放,默认true
        RotationChart_1.setDragDecelerationEnabled(true);    // 抬起手指，继续滑动,默认true
        RotationChart_1.setDragDecelerationFrictionCoef(0.9f);   // 摩擦系数,[0-1]，较大值速度会缓慢下降，0，立即停止;1,无效值，并转换为0.9999.默认0.9f.
        RotationChart_1.setOnChartGestureListener (new OnChartGestureListener() { // 手势监听器
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 按下
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 抬起,取消
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {
                // 长按
            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
                // 双击
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
                // 单击
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
                // 甩动
            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
                // 缩放
            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
                // 移动
            }
        });
        RotationChart_2.setTouchEnabled(true); // 所有触摸事件,默认true
        RotationChart_2.setDragEnabled(true);    // 可拖动,默认true
        RotationChart_2.setScaleEnabled(true);   // 两个轴上的缩放,X,Y分别默认为true
        RotationChart_2.setScaleXEnabled(true);  // X轴上的缩放,默认true
        RotationChart_2.setScaleYEnabled(true);  // Y轴上的缩放,默认true
        RotationChart_2.setPinchZoom(true);  // X,Y轴同时缩放，false则X,Y轴单独缩放,默认false
        RotationChart_2.setDoubleTapToZoomEnabled(true); // 双击缩放,默认true
        RotationChart_2.setDragDecelerationEnabled(true);    // 抬起手指，继续滑动,默认true
        RotationChart_2.setDragDecelerationFrictionCoef(0.9f);   // 摩擦系数,[0-1]，较大值速度会缓慢下降，0，立即停止;1,无效值，并转换为0.9999.默认0.9f.
        RotationChart_2.setOnChartGestureListener (new OnChartGestureListener() { // 手势监听器
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 按下
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 抬起,取消
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {
                // 长按
            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
                // 双击
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
                // 单击
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
                // 甩动
            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
                // 缩放
            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
                // 移动
            }
        });


//轴相关
// 由四个元素组成：
// 标签：即刻度值。也可以自定义，比如时间，距离等等，下面会说一下；
// 轴线：坐标轴；
// 网格线：垂直于轴线对应每个值画的轴线；
// 限制线：最值等线。
        XAxis xAxis8 = RotationChart_0.getXAxis();    // 获取X轴
        YAxis yAxis8 = RotationChart_0.getAxisLeft(); // 获取Y轴,mLineChart.getAxis(YAxis.AxisDependency.LEFT);也可以获取Y轴
        RotationChart_0.getAxisRight().setEnabled(false);    // 不绘制右侧的轴线
// 轴颜色
        yAxis8.setTextColor(Color.WHITE);  // 标签字体颜色
        yAxis8.setTextSize(10);    // 标签字体大小，dp，6-24之间，默认为10dp
        yAxis8.setTypeface(null);    // 标签字体
        yAxis8.setGridColor(Color.DKGRAY);    // 网格线颜色，默认GRAY
        yAxis8.setGridLineWidth(1);    // 网格线宽度，dp，默认1dp
        yAxis8.setAxisLineColor(Color.WHITE);  // 坐标轴颜色，默认GRAY.测试到一个bug，假如左侧线只有1dp，
// 那么如果x轴有线且有网格线，当刻度拉的正好位置时会覆盖到y轴的轴线，变为X轴网格线颜色，结局办法是，要么不画轴线，要么就是坐标轴稍微宽点
        xAxis8.setAxisLineColor(Color.WHITE); // 坐标轴颜色，默认GRAY
        xAxis8.setTextColor(Color.WHITE); //刻度文字颜色
        xAxis8.setGridColor(Color.DKGRAY);   // 网格线颜色，默认GRAY
        xAxis8.setGridLineWidth(2); // 网格线宽度，dp，默认1dp
        yAxis8.setAxisLineWidth(2);  // 坐标轴线宽度，dp，默认1dp
        yAxis8.enableGridDashedLine(20, 10, 1);    // 网格线为虚线，lineLength，每段实线长度,spaceLength,虚线间隔长度，phase，起始点（进过测试，最后这个参数也没看出来干啥的）

        XAxis xAxis9 = RotationChart_1.getXAxis();
        YAxis yAxis9 = RotationChart_1.getAxisLeft();
        RotationChart_1.getAxisRight().setEnabled(false);

        yAxis9.setTextColor(Color.WHITE);
        yAxis9.setTextSize(10);
        yAxis9.setTypeface(null);
        yAxis9.setGridColor(Color.DKGRAY);
        yAxis9.setGridLineWidth(1);
        yAxis9.setAxisLineColor(Color.WHITE);
        xAxis9.setAxisLineColor(Color.WHITE);
        xAxis9.setTextColor(Color.WHITE);
        xAxis9.setGridColor(Color.DKGRAY);
        xAxis9.setGridLineWidth(2);
        yAxis9.setAxisLineWidth(2);
        yAxis9.enableGridDashedLine(20, 10, 1);

        XAxis xAxis10 = RotationChart_2.getXAxis();
        YAxis yAxis10 = RotationChart_2.getAxisLeft();
        RotationChart_2.getAxisRight().setEnabled(false);

        yAxis10.setTextColor(Color.WHITE);
        yAxis10.setTextSize(10);
        yAxis10.setTypeface(null);
        yAxis10.setGridColor(Color.DKGRAY);
        yAxis10.setGridLineWidth(1);
        yAxis10.setAxisLineColor(Color.WHITE);
        xAxis10.setAxisLineColor(Color.WHITE);
        xAxis10.setTextColor(Color.WHITE);
        xAxis10.setGridColor(Color.DKGRAY);
        xAxis10.setGridLineWidth(2);
        yAxis10.setAxisLineWidth(2);
        yAxis10.enableGridDashedLine(20, 10, 1);


//图例相关
        Legend legend8 = RotationChart_0.getLegend(); // 获取图例，但是在数据设置给chart之前是不可获取的
        legend8.setEnabled(true);    // 是否绘制图例
        legend8.setTextColor(Color.WHITE);    // 图例标签字体颜色，默认BLACK
        legend8.setTextSize(15); // 图例标签字体大小[6,24]dp,默认10dp
        legend8.setTypeface(null);   // 图例标签字体
        legend8.setWordWrapEnabled(false);    // 当图例超出时是否换行适配，这个配置会降低性能，且只有图例在底部时才可以适配。默认false
        legend8.setMaxSizePercent(1f); // 设置，默认0.95f,图例最大尺寸区域占图表区域之外的比例
        legend8.setForm(Legend.LegendForm.CIRCLE);   // 设置图例的形状，SQUARE, CIRCLE 或者 LINE
        legend8.setFormSize(10); // 图例图形尺寸，dp，默认8dp
        legend8.setXEntrySpace(6);  // 设置水平图例间间距，默认6dp
        legend8.setYEntrySpace(0);  // 设置垂直图例间间距，默认0
        legend8.setFormToTextSpace(5);    // 设置图例的标签与图形之间的距离，默认5dp
        legend8.setWordWrapEnabled(true);   // 图标单词是否适配。只有在底部才会有效，
        legend8.setCustom(new LegendEntry[]{new LegendEntry("X*sin(theta/2)", Legend.LegendForm.CIRCLE, 10, 5, null, Color.RED)});

        Legend legend9 = RotationChart_1.getLegend();
        legend9.setEnabled(true);
        legend9.setTextColor(Color.WHITE);
        legend9.setTextSize(15);
        legend9.setTypeface(null);
        legend9.setWordWrapEnabled(false);
        legend9.setMaxSizePercent(1f);
        legend9.setForm(Legend.LegendForm.CIRCLE);
        legend9.setFormSize(10);
        legend9.setXEntrySpace(6);
        legend9.setYEntrySpace(0);
        legend9.setFormToTextSpace(5);
        legend9.setWordWrapEnabled(true);
        legend9.setCustom(new LegendEntry[]{new LegendEntry("Y*sin(theta/2)", Legend.LegendForm.CIRCLE, 10, 5, null, Color.RED)});

        Legend legend10 = RotationChart_2.getLegend();
        legend10.setEnabled(true);
        legend10.setTextColor(Color.WHITE);
        legend10.setTextSize(15);
        legend10.setTypeface(null);
        legend10.setWordWrapEnabled(false);
        legend10.setMaxSizePercent(1f);
        legend10.setForm(Legend.LegendForm.CIRCLE);
        legend10.setFormSize(10);
        legend10.setXEntrySpace(6);
        legend10.setYEntrySpace(0);
        legend10.setFormToTextSpace(5);
        legend10.setWordWrapEnabled(true);
        legend10.setCustom(new LegendEntry[]{new LegendEntry("Z*sin(theta/2)", Legend.LegendForm.CIRCLE, 10, 5, null, Color.RED)});


        fifthLayoutType.setText("旋转向量传感器\nRotation Sensor");
        //set the listener of rotation sensor
        SensorEventListener SEVforRS = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                strRotationData = "X: " + sensorEvent.values[0]
                        + "\nY: " + sensorEvent.values[1]
                        + "\nZ: " + sensorEvent.values[2]
                        + "\ncos(θ/2): " + sensorEvent.values[3]
                        + "\nestimated heading Accuracy: " + sensorEvent.values[4]
                        + "\n(in radians) (-1 if unavailable)\n";
                tRotationData.setText(strRotationData);
                if (nRtDelay < 5 && rtXData.size() != 0) {
                    nRtDelay++;
                } else {
                    nRtDelay = 0;
                    SetChartData(RotationChart_0, rtXData, sensorEvent.values[0], 1, Color.RED, "Ang X", 0, true);
                    SetChartData(RotationChart_1, rtYData, sensorEvent.values[1], 1, Color.RED, "Ang Y", 0, true);
                    SetChartData(RotationChart_2, rtZData, sensorEvent.values[2], 1, Color.RED, "Ang Z", 0, true);
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        Sensor rotationSensor;
        for (Sensor sensor : listofSensor) {
            if (sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                rotationSensor = sensor;
                mSManager.registerListener(SEVforRS, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
                break;
            }
        }







//6th sensor : linear acceleration sensor
        //chart
        InitChart(LinearAccelerationChart_0, 20, 5);
        InitChart(LinearAccelerationChart_1, 20, 5);
        InitChart(LinearAccelerationChart_2, 20, 5);
        //设置表样式
        //绘制区域边框、设置边框颜色、边框宽度
        LinearAccelerationChart_0.setDrawBorders(true);
        LinearAccelerationChart_0.setBorderColor(Color.WHITE);
        LinearAccelerationChart_0.setBorderWidth(2);
        LinearAccelerationChart_1.setDrawBorders(true);
        LinearAccelerationChart_1.setBorderColor(Color.WHITE);
        LinearAccelerationChart_1.setBorderWidth(2);
        LinearAccelerationChart_2.setDrawBorders(true);
        LinearAccelerationChart_2.setBorderColor(Color.WHITE);
        LinearAccelerationChart_2.setBorderWidth(2);


        //滑动相关
        LinearAccelerationChart_0.setTouchEnabled(true); // 所有触摸事件,默认true
        LinearAccelerationChart_0.setDragEnabled(true);    // 可拖动,默认true
        LinearAccelerationChart_0.setScaleEnabled(true);   // 两个轴上的缩放,X,Y分别默认为true
        LinearAccelerationChart_0.setScaleXEnabled(true);  // X轴上的缩放,默认true
        LinearAccelerationChart_0.setScaleYEnabled(true);  // Y轴上的缩放,默认true
        LinearAccelerationChart_0.setPinchZoom(true);  // X,Y轴同时缩放，false则X,Y轴单独缩放,默认false
        LinearAccelerationChart_0.setDoubleTapToZoomEnabled(true); // 双击缩放,默认true
        LinearAccelerationChart_0.setDragDecelerationEnabled(true);    // 抬起手指，继续滑动,默认true
        LinearAccelerationChart_0.setDragDecelerationFrictionCoef(0.9f);   // 摩擦系数,[0-1]，较大值速度会缓慢下降，0，立即停止;1,无效值，并转换为0.9999.默认0.9f.
        LinearAccelerationChart_0.setOnChartGestureListener (new OnChartGestureListener() { // 手势监听器
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 按下
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 抬起,取消
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {
                // 长按
            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
                // 双击
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
                // 单击
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
                // 甩动
            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
                // 缩放
            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
                // 移动
            }
        });
        LinearAccelerationChart_1.setTouchEnabled(true); // 所有触摸事件,默认true
        LinearAccelerationChart_1.setDragEnabled(true);    // 可拖动,默认true
        LinearAccelerationChart_1.setScaleEnabled(true);   // 两个轴上的缩放,X,Y分别默认为true
        LinearAccelerationChart_1.setScaleXEnabled(true);  // X轴上的缩放,默认true
        LinearAccelerationChart_1.setScaleYEnabled(true);  // Y轴上的缩放,默认true
        LinearAccelerationChart_1.setPinchZoom(true);  // X,Y轴同时缩放，false则X,Y轴单独缩放,默认false
        LinearAccelerationChart_1.setDoubleTapToZoomEnabled(true); // 双击缩放,默认true
        LinearAccelerationChart_1.setDragDecelerationEnabled(true);    // 抬起手指，继续滑动,默认true
        LinearAccelerationChart_1.setDragDecelerationFrictionCoef(0.9f);   // 摩擦系数,[0-1]，较大值速度会缓慢下降，0，立即停止;1,无效值，并转换为0.9999.默认0.9f.
        LinearAccelerationChart_1.setOnChartGestureListener (new OnChartGestureListener() { // 手势监听器
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 按下
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 抬起,取消
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {
                // 长按
            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
                // 双击
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
                // 单击
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
                // 甩动
            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
                // 缩放
            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
                // 移动
            }
        });
        LinearAccelerationChart_2.setTouchEnabled(true); // 所有触摸事件,默认true
        LinearAccelerationChart_2.setDragEnabled(true);    // 可拖动,默认true
        LinearAccelerationChart_2.setScaleEnabled(true);   // 两个轴上的缩放,X,Y分别默认为true
        LinearAccelerationChart_2.setScaleXEnabled(true);  // X轴上的缩放,默认true
        LinearAccelerationChart_2.setScaleYEnabled(true);  // Y轴上的缩放,默认true
        LinearAccelerationChart_2.setPinchZoom(true);  // X,Y轴同时缩放，false则X,Y轴单独缩放,默认false
        LinearAccelerationChart_2.setDoubleTapToZoomEnabled(true); // 双击缩放,默认true
        LinearAccelerationChart_2.setDragDecelerationEnabled(true);    // 抬起手指，继续滑动,默认true
        LinearAccelerationChart_2.setDragDecelerationFrictionCoef(0.9f);   // 摩擦系数,[0-1]，较大值速度会缓慢下降，0，立即停止;1,无效值，并转换为0.9999.默认0.9f.
        LinearAccelerationChart_2.setOnChartGestureListener (new OnChartGestureListener() { // 手势监听器
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 按下
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                // 抬起,取消
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {
                // 长按
            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
                // 双击
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
                // 单击
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
                // 甩动
            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
                // 缩放
            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
                // 移动
            }
        });


        //轴相关
        // 由四个元素组成：
        // 标签：即刻度值。也可以自定义，比如时间，距离等等，下面会说一下；
        // 轴线：坐标轴；
        // 网格线：垂直于轴线对应每个值画的轴线；
        // 限制线：最值等线。
        XAxis xAxis11 = LinearAccelerationChart_0.getXAxis();    // 获取X轴
        YAxis yAxis11 = LinearAccelerationChart_0.getAxisLeft(); // 获取Y轴,mLineChart.getAxis(YAxis.AxisDependency.LEFT);也可以获取Y轴
        LinearAccelerationChart_0.getAxisRight().setEnabled(false);    // 不绘制右侧的轴线
        // 轴颜色
        yAxis11.setTextColor(Color.WHITE);  // 标签字体颜色
        yAxis11.setTextSize(10);    // 标签字体大小，dp，6-24之间，默认为10dp
        yAxis11.setTypeface(null);    // 标签字体
        yAxis11.setGridColor(Color.DKGRAY);    // 网格线颜色，默认GRAY
        yAxis11.setGridLineWidth(1);    // 网格线宽度，dp，默认1dp
        yAxis11.setAxisLineColor(Color.WHITE);  // 坐标轴颜色，默认GRAY.测试到一个bug，假如左侧线只有1dp，
        // 那么如果x轴有线且有网格线，当刻度拉的正好位置时会覆盖到y轴的轴线，变为X轴网格线颜色，结局办法是，要么不画轴线，要么就是坐标轴稍微宽点
        xAxis11.setAxisLineColor(Color.WHITE); // 坐标轴颜色，默认GRAY
        xAxis11.setTextColor(Color.WHITE); //刻度文字颜色
        xAxis11.setGridColor(Color.DKGRAY);   // 网格线颜色，默认GRAY
        xAxis11.setGridLineWidth(2); // 网格线宽度，dp，默认1dp
        yAxis11.setAxisLineWidth(2);  // 坐标轴线宽度，dp，默认1dp
        yAxis11.enableGridDashedLine(20, 10, 1);    // 网格线为虚线，lineLength，每段实线长度,spaceLength,虚线间隔长度，phase，起始点（进过测试，最后这个参数也没看出来干啥的）

        XAxis xAxis12 = LinearAccelerationChart_1.getXAxis();
        YAxis yAxis12 = LinearAccelerationChart_1.getAxisLeft();
        LinearAccelerationChart_1.getAxisRight().setEnabled(false);

        yAxis12.setTextColor(Color.WHITE);
        yAxis12.setTextSize(10);
        yAxis12.setTypeface(null);
        yAxis12.setGridColor(Color.DKGRAY);
        yAxis12.setGridLineWidth(1);
        yAxis12.setAxisLineColor(Color.WHITE);
        xAxis12.setAxisLineColor(Color.WHITE);
        xAxis12.setTextColor(Color.WHITE);
        xAxis12.setGridColor(Color.DKGRAY);
        xAxis12.setGridLineWidth(2);
        yAxis12.setAxisLineWidth(2);
        yAxis12.enableGridDashedLine(20, 10, 1);

        XAxis xAxis13 = LinearAccelerationChart_2.getXAxis();
        YAxis yAxis13 = LinearAccelerationChart_2.getAxisLeft();
        LinearAccelerationChart_2.getAxisRight().setEnabled(false);

        yAxis13.setTextColor(Color.WHITE);
        yAxis13.setTextSize(10);
        yAxis13.setTypeface(null);
        yAxis13.setGridColor(Color.DKGRAY);
        yAxis13.setGridLineWidth(1);
        yAxis13.setAxisLineColor(Color.WHITE);
        xAxis13.setAxisLineColor(Color.WHITE);
        xAxis13.setTextColor(Color.WHITE);
        xAxis13.setGridColor(Color.DKGRAY);
        xAxis13.setGridLineWidth(2);
        yAxis13.setAxisLineWidth(2);
        yAxis13.enableGridDashedLine(20, 10, 1);


        //图例相关
        Legend legend11 = LinearAccelerationChart_0.getLegend(); // 获取图例，但是在数据设置给chart之前是不可获取的
        legend11.setEnabled(true);    // 是否绘制图例
        legend11.setTextColor(Color.WHITE);    // 图例标签字体颜色，默认BLACK
        legend11.setTextSize(15); // 图例标签字体大小[6,24]dp,默认10dp
        legend11.setTypeface(null);   // 图例标签字体
        legend11.setWordWrapEnabled(false);    // 当图例超出时是否换行适配，这个配置会降低性能，且只有图例在底部时才可以适配。默认false
        legend11.setMaxSizePercent(1f); // 设置，默认0.95f,图例最大尺寸区域占图表区域之外的比例
        legend11.setForm(Legend.LegendForm.CIRCLE);   // 设置图例的形状，SQUARE, CIRCLE 或者 LINE
        legend11.setFormSize(10); // 图例图形尺寸，dp，默认8dp
        legend11.setXEntrySpace(6);  // 设置水平图例间间距，默认6dp
        legend11.setYEntrySpace(0);  // 设置垂直图例间间距，默认0
        legend11.setFormToTextSpace(5);    // 设置图例的标签与图形之间的距离，默认5dp
        legend11.setWordWrapEnabled(true);   // 图标单词是否适配。只有在底部才会有效，
        legend11.setCustom(new LegendEntry[]{new LegendEntry("Linear Acc X (m/s^2) ", Legend.LegendForm.CIRCLE, 10, 5, null, Color.RED)});

        Legend legend12 = LinearAccelerationChart_1.getLegend();
        legend12.setEnabled(true);
        legend12.setTextColor(Color.WHITE);
        legend12.setTextSize(15);
        legend12.setTypeface(null);
        legend12.setWordWrapEnabled(false);
        legend12.setMaxSizePercent(1f);
        legend12.setForm(Legend.LegendForm.CIRCLE);
        legend12.setFormSize(10);
        legend12.setXEntrySpace(6);
        legend12.setYEntrySpace(0);
        legend12.setFormToTextSpace(5);
        legend12.setWordWrapEnabled(true);
        legend12.setCustom(new LegendEntry[]{new LegendEntry("Linear Acc Y (m/s^2)", Legend.LegendForm.CIRCLE, 10, 5, null, Color.RED)});

        Legend legend13 = LinearAccelerationChart_2.getLegend();
        legend13.setEnabled(true);
        legend13.setTextColor(Color.WHITE);
        legend13.setTextSize(15);
        legend13.setTypeface(null);
        legend13.setWordWrapEnabled(false);
        legend13.setMaxSizePercent(1f);
        legend13.setForm(Legend.LegendForm.CIRCLE);
        legend13.setFormSize(10);
        legend13.setXEntrySpace(6);
        legend13.setYEntrySpace(0);
        legend13.setFormToTextSpace(5);
        legend13.setWordWrapEnabled(true);
        legend13.setCustom(new LegendEntry[]{new LegendEntry("Linear Acc Z (m/s^2)", Legend.LegendForm.CIRCLE, 10, 5, null, Color.RED)});


        seventhLayoutType.setText("线性加速度传感器\nLinear Acceleration Sensor");
        //set the listener of acceleration sensor
        SensorEventListener SEVforLAS = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                strLinearAccelerationData = "X: " + sensorEvent.values[0]
                        + "\nY: " + sensorEvent.values[1]
                        + "\nZ: " + sensorEvent.values[2] + "\n";
                tLinearAccelerationData.setText(strLinearAccelerationData);
                if (nLinearAccDelay < 5 && LinearaccXData.size() != 0) {
                    nLinearAccDelay++;
                } else {
                    nLinearAccDelay = 0;
                    //设置表中数据样式
                    SetChartData(LinearAccelerationChart_0, LinearaccXData, sensorEvent.values[0], 1, Color.RED, "" , 0, true);
                    SetChartData(LinearAccelerationChart_1, LinearaccYData, sensorEvent.values[1], 1, Color.RED, "", 0, true);
                    SetChartData(LinearAccelerationChart_2, LinearaccZData, sensorEvent.values[2], 1, Color.RED, "", 0, true);

                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        //find the acceleration sensor
        //!!!!!!!!!
        //Notice: the acceleration sensor can be registered
        // as "TYPE_LINEAR_ACCELERATION" or "TYPE_ACCELEROMETER"
        Sensor linearaccelerationSensor;
        for (Sensor sensor : listofSensor) {
            if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION ) {
                linearaccelerationSensor = sensor;
                mSManager.registerListener(SEVforLAS, linearaccelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
                break;
            }
        }












      //7th sensors about battery manage
        sixthLayoutType.setText("电池及传感器健康管理\nBattery Manager and Sensors");
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(Intent.ACTION_BATTERY_CHANGED);
        String strBatteryState;
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String strBattery;
                //接收到广播后，用getIntExtra("level")和getIntExtra("scale")获得相应值
                strBattery = "Sensors:-----------------\n";
                strBattery += "Battery remaining: " + intent.getIntExtra("level", 0);    ///电池剩余电量
                strBattery += "%";
                int nScale = intent.getIntExtra("scale", 0);  ///获取电池满电量数值
                if (nScale != 100) strBattery += "\nTotal capability: " + nScale;
                strBattery += "\nvoltage: " + intent.getIntExtra("voltage", 0);  ///获取电池电压
                strBattery += " mV";
                int nTemperature = intent.getIntExtra("temperature", 0);  ///获取电池温度
                double dTemperature = nTemperature / 10.0;
                strBattery += "\ntemperature: " + dTemperature + "℃\n";
                strBattery += "More Info:------------------\n";
                strBattery += "technology: " + intent.getStringExtra("technology");  ///获取电池技术支持
                strBattery += "\nstatus: ";
                int nStatus = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN); ///获取电池状态
                String[] strStatus = {"NULL", "Unknown", "Charging", "DisCharging", "Not charging", "Full"};
                strBattery += strStatus[nStatus] + "\n";
                int nPlugged = intent.getIntExtra("plugged", 0);  ///获取电源信息
                String[] strPlugged = {"NULL", "AC", "USB", "NULL", "Wireless"};
                strBattery += "Power source: " + strPlugged[nPlugged] + "\n";
                int nHealth = intent.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN);  ///获取电池健康度
                String[] strHealth = {"NULL", "Unknown", "Good", "Overheat", "Dead", "Unspecified Failure", "Cold"};
                strBattery += "Battery health: " + strHealth[nHealth] + "\n";
                tBatteryData.setText(strBattery);
            }
        };
        getActivity().registerReceiver(receiver, filter2);
        return view;
    }



        public void InitChart(LineChart chart, float Max, float Min)
        {
            YAxis accy = chart.getAxisLeft();
            chart.getAxisRight().setEnabled(false);
            chart.setTouchEnabled(false);
            accy.enableGridDashedLine(10f, 10f, 0f);
            accy.setAxisMaximum(Max);
            accy.setAxisMinimum(Min);
        }
    /*  Function Name: SetData
        Usage: Refresh the Chart with new data
        parameters:
            chart, the chart need to be refreshed
            Values, the ArrayList of the Chart
            value, the value need to be add in the chart
            nExpend, expend the Axis range of max and min value in the chart
            color, the color of the line in the chart( can be null if not at the initialize time
            label, the label of dataset( can be null if not at the initialize time
            nClearSize, when number of data in the list reach the size, clear the list
            isShowValueText, show value on the line or not
            barChart.setData(barData); // 设置数据

        barChart.setDrawBorders(false); //是否在折线图上添加边框

        barChart.setDescription("数据描述");// 数据描述
        barChart.setDescriptionPosition(100,20);//数据描述的位置
        barChart.setDescriptionColor(Color.RED);//数据的颜色
        barChart.setDescriptionTextSize(40);//数据字体大小

        barChart.setDrawGridBackground(false); // 是否显示表格颜色
        barChart.setGridBackgroundColor(Color.RED); // 表格的的颜色
        //barChart.setBackgroundColor(Color.BLACK);// 设置整个图标控件的背景
        barChart.setDrawBarShadow(false);//柱状图没有数据的部分是否显示阴影效果

        barChart.setTouchEnabled(false); // 设置是否可以触摸
        barChart.setDragEnabled(false);// 是否可以拖拽
        barChart.setScaleEnabled(false);// 是否可以缩放
        barChart.setPinchZoom(false);//y轴的值是否跟随图表变换缩放;如果禁止，y轴的值会跟随图表变换缩放

        barChart.setDrawValueAboveBar(true);//柱状图上面的数值显示在柱子上面还是柱子里面

        barChart.getXAxis().setDrawGridLines(false);//是否显示竖直标尺线
        barChart.getXAxis().setLabelsToSkip(11);//设置横坐标显示的间隔
        barChart.getXAxis().setLabelRotationAngle(20);//设置横坐标倾斜角度
        barChart.getXAxis().setSpaceBetweenLabels(50);
        barChart.getXAxis().setDrawLabels(true);//是否显示X轴数值
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置 默认在上方

        barChart.getAxisRight().setDrawLabels(false);//右侧是否显示Y轴数值
        barChart.getAxisRight().setEnabled(false);//是否显示最右侧竖线
        barChart.getAxisRight().setDrawAxisLine(true);
        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.getXAxis().setDrawAxisLine(true);


        YAxisValueFormatter custom = new MyYAxisValueFormatter();//自定义Y轴文字样式
        barChart.getAxisLeft().setValueFormatter(custom);

        barChart.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART);//设置比例图标的位置
        barChart.getLegend().setDirection(Legend.LegendDirection.RIGHT_TO_LEFT);//设置比例图标和文字之间的位置方向
        barChart.getLegend().setTextColor(Color.RED);
     ************************************************/
        public float SetChartData(LineChart chart, ArrayList<Entry> Values, float value, float nExpend, int color, String label,int nClearSize, boolean isShowValueText)
        {
            //设置自动清零时间
            if(nClearSize == 0) nClearSize = 999999999;
            if (Values.size() > nClearSize)
            {
                Values.clear();
                //设置Y轴居于左侧
                YAxis lighty = chart.getAxisLeft();
                //设置轴线最大值最小值
                lighty.setAxisMaximum(0);
                lighty.setAxisMinimum(0);
            }
            Values.add(new Entry(Values.size(),value));
            LineDataSet set1;
            if (chart.getData() != null &&
                    chart.getData().getDataSetCount() >= 0) {
                set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
                set1.setValues(Values);
                set1.notifyDataSetChanged();
                chart.getData().notifyDataChanged();
                chart.notifyDataSetChanged();
            } else {
                //如果颜色==0，统一设置为如下颜色
                if(color == 0) color = Color.RED;
                if(label.isEmpty()) label = "text";
                set1 = new LineDataSet(Values, label);
                set1.setValueTextSize(5f);
                set1.setColor(Color.RED);
                set1.setValueTextColor(Color.RED);
                set1.setDrawIcons(true);
                set1.setCircleColor(color);
                set1.setLineWidth(1f);
                set1.setCircleRadius(1f);
                if(isShowValueText == false) set1.setValueTextSize(0);
                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1);
                LineData lightData;
                lightData = new LineData(dataSets);
                chart.setData(lightData);
            }
            //set Max and Min Y value
            YAxis lighty = chart.getAxisLeft();
            float nMax = lighty.getAxisMaximum();
            float nMin = lighty.getAxisMinimum();
            if(nMax == 0 && nMin == 0){
                lighty.setAxisMaximum(value + nExpend);
                lighty.setAxisMinimum(value - nExpend);
            }else {
                if (value > nMax) {
                    lighty.setAxisMaximum(value + nExpend);
                }
                if (value < nMin) {
                    lighty.setAxisMinimum(value - nExpend);
                }
            }
            //redraw
            chart.invalidate();
            return nMax;
        }

    }

