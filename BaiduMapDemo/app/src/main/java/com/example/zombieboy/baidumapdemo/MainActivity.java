package com.example.zombieboy.baidumapdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;


public class MainActivity extends AppCompatActivity {

    // 百度地图控件
    private MapView mMapView = null;
    // 百度地图
    private BaiduMap mBdMap;
    // 按钮
    private ImageButton ib_large, ib_small, ib_mode, ib_loc, ib_traffic;
    //模式切换，正常模式
    private boolean modeFlag = true;
    //当前地图缩放级别
    private float zoomLevel;
    //定位相关
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
    //是否第一次定位，如果是第一次定位的话要将自己的位置显示在地图 中间
    private boolean isFirstLocation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        //初始化控件
        InitView();
        //初始化地图
        InitMap();
        //定位
        InitLocation();

    }


    // 初始化地图
    private void InitMap(){
        // 获取地图控件
        mMapView = (MapView) findViewById(R.id.bmapView);
        // 不显示缩放比例尺
        //mMapView.showZoomControls(false);
        // 不显示百度地图Logo
        mMapView.removeViewAt(1);
        // 百度地图
        mBdMap = mMapView.getMap();
        // 普通地图
        mBdMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        // 卫星地图
        //mBdMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        // 开启交通图（实时路况图）
        //mBdMap.setTrafficEnabled(true);
        // 开启热力图
        //mBdMap.setBaiduHeatMapEnabled(true);

        // 设定中心点坐标（南京）
        LatLng cenpt = new LatLng(32.04,118.81);
        // 定义地图状态
        //MapStatus mMapStatus = new MapStatus.Builder().zoom(15).build();
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(11)
                .build();

        // 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        // 改变地图状态
        mBdMap.setMapStatus(mMapStatusUpdate);

//        //设置地图状态改变监听器
//        mBdMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {
//            @Override
//            public void onMapStatusChangeStart(MapStatus arg0) {
//            }
//            @Override
//            public void onMapStatusChangeFinish(MapStatus arg0) {
//            }
//            @Override
//            public void onMapStatusChange(MapStatus arg0) {
//                //当地图状态改变的时候，获取放大级别
//                zoomLevel = arg0.zoom;
//            }
//        });
    }


    // 初始化控件
    private void InitView(){
        //地图控制按钮
//        ib_large = (ImageButton)findViewById(R.id.ib_large);
//        ib_large.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (zoomLevel < 18) {
//                    mBdMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
//                    ib_small.setEnabled(true);
//                } else {
//                    showInfo("已经放至最大，可继续滑动操作");
//                    ib_large.setEnabled(false);
//                }
//            }
//        });
//
//        ib_small = (ImageButton)findViewById(R.id.ib_small);
//        ib_small.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                if (zoomLevel > 6) {
//                    mBdMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
//                    ib_large.setEnabled(true);
//                } else {
//                    ib_small.setEnabled(false);
//                    showInfo("已经缩至最小，可继续滑动操作");
//                }
//            }
//        });

        ib_mode = (ImageButton)findViewById(R.id.ib_mode);
        ib_mode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(modeFlag){
                    modeFlag = false;
                    mBdMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    showInfo("卫星模式");
                }else{
                    modeFlag = true;
                    mBdMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    showInfo("普通模式");
                }
            }
        });

        ib_loc = (ImageButton)findViewById(R.id.ib_loc);
        ib_loc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                isFirstLocation = true;
                showInfo("回到当前位置");
            }
        });

        ib_traffic = (ImageButton)findViewById(R.id.ib_traffic);
        ib_traffic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mBdMap.isTrafficEnabled()){
                    mBdMap.setTrafficEnabled(false);
                    //ib_traffic.setBackgroundResource(R.drawable.offtraffic);
                    showInfo("普通地图");
                }else{
                    mBdMap.setTrafficEnabled(true);
                    //ib_traffic.setBackgroundResource(R.drawable.ontraffic);
                    showInfo("实时路况图");
                }
            }
        });


    }

//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.ib_large:
//                if (zoomLevel < 18) {
//                    mBdMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
//                    ib_small.setEnabled(true);
//                } else {
//                    showInfo("已经放至最大，可继续滑动操作");
//                    ib_large.setEnabled(false);
//                }
//                break;
//            case R.id.ib_small:
//                if (zoomLevel > 6) {
//                    mBdMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
//                    ib_large.setEnabled(true);
//                } else {
//                    ib_small.setEnabled(false);
//                    showInfo("已经缩至最小，可继续滑动操作");
//                }
//                break;
//            case R.id.ib_mode://卫星模式和普通模式
//                if(modeFlag){
//                    modeFlag = false;
//                    mBdMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
//                    showInfo("开启卫星模式");
//                }else{
//                    modeFlag = true;
//                    mBdMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
//                    showInfo("开启普通模式");
//                }
//                break;
//            case R.id.ib_loc:
//                isFirstLocation = true;
//                showInfo("返回自己位置");
//                break;
//            case R.id.ib_traffic://是否开启交通图
//                if(mBdMap.isTrafficEnabled()){
//                    mBdMap.setTrafficEnabled(false);
//                    //ib_traffic.setBackgroundResource(R.drawable.offtraffic);
//                    showInfo("关闭实时交通图");
//                }else{
//                    mBdMap.setTrafficEnabled(true);
//                    //ib_traffic.setBackgroundResource(R.drawable.ontraffic);
//                    showInfo("开启实时交通图");
//                }
//                break;
//            default:
//                break;
//        }
//    }


    // 初始化定位
    private void InitLocation(){
        //定位客户端的设置
        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();
        //注册监听
        mLocationClient.registerLocationListener(mLocationListener);
        //配置定位
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");//坐标类型
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//打开Gps
        option.setScanSpan(1000);//1000毫秒定位一次
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mLocationClient.setLocOption(option);
    }

    // 开启定位
    protected void onStart() {
        super.onStart();
        //开启定位
        mBdMap.setMyLocationEnabled(true);
        if(!mLocationClient.isStarted()){
            mLocationClient.start();
        }
    }

    // 关闭定位
    protected void onStop() {
        super.onStop();
        //关闭定位
        mBdMap.setMyLocationEnabled(false);
        if(mLocationClient.isStarted()){
            mLocationClient.stop();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }


    //显示消息
    private void showInfo(String str){
        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
    }


    //自定义的定位监听
    private class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation location) {
            //将获取的location信息给百度map
            MyLocationData data = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            mBdMap.setMyLocationData(data);
            if(isFirstLocation){
                //获取经纬度
                LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
                MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(ll);
                //mBaiduMap.setMapStatus(status);//直接到中间
                mBdMap.animateMapStatus(status);//动画的方式到中间
                isFirstLocation = false;
                showInfo("位置：" + location.getAddrStr());
            }
        }

    }



}
