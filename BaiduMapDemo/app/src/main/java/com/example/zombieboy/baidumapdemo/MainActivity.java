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
import com.baidu.mapapi.map.MyLocationConfiguration;
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
    private MyLocationConfiguration.LocationMode mCurrentMode;
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
        InitLocal();

    }


    /**
     * @author by lhh
     * @brief 初始化控件
     * @method InitMap()
     * @param 无
     * @return void
     * */
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

    /**
     * @author by lhh
     * @brief 初始化控件
     * @method InitView()
     * @param 无
     * @return void
     * */
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


    /**
     * @author by lhh
     * @brief 初始化控件
     * @method InitLocal()
     * @param 无
     * @return void
     * */
    private void InitLocal() {
        //定位客户端的设置
        mBdMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(this);
        mLocationListener = new MyLocationListener();
        //注册监听
        mLocationClient.registerLocationListener(mLocationListener);

        // 配置定位
        InitLocation();

    }

    /**
     * @author by lhh
     * @brief 初始化控件
     * @method InitLocation()
     * @param 无
     * @return void
     * */
    private void InitLocation() {
        //配置定位
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 10000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    /**
     * @author by lhh
     * @brief 开启定位
     * @method onStart()
     * @param 无
     * @return void
     * */
    protected void onStart() {
        super.onStart();
        //开启定位
        mBdMap.setMyLocationEnabled(true);
        if(!mLocationClient.isStarted()){
            mLocationClient.start();
        }
    }

    /**
     * @author by lhh
     * @brief 关闭定位
     * @method onStop()
     * @param 无
     * @return void
     * */
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


    /**
     * @author by lhh
     * @brief 定位SDK监听函数
     * @method onReceiveLocation()
     * @param 无
     * @return void
     * */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            mBdMap.setMyLocationEnabled(true);
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            // 设置定位数据
            mBdMap.setMyLocationData(locData);

            if(isFirstLocation){
                // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
                //mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
                //第三个参数是位置图片没有就默认
                MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, null);
                mBdMap.setMyLocationConfigeration(config);
                //获取经纬度，以我的位置为中心
                LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());
                MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(latlng);
                mBdMap.animateMapStatus(status);//动画的方式到中间
                isFirstLocation = false;
                showInfo("位置：" + location.getAddrStr());
            }
        }
    }


}
