package com.example.zombieboy.bdmapdemo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;


public class MainActivity extends AppCompatActivity implements OnGetGeoCoderResultListener {

    // 百度地图控件
    private MapView mMapView = null;
    //private TextureMapView mMapView = null;
    // 百度地图
    private BaiduMap mBdMap;
    // 按钮
    private ImageButton ib_mode, ib_loc, ib_traffic;
    // 模式切换，正常模式
    private boolean modeFlag = true;
    // 定位相关
    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;
    private MyLocationConfiguration.LocationMode mCurrentMode;

    // GEO
    private Marker mMark;
    private GeoCoder mGeoSearch;
    TextView mTvinfo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
        // 注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        // 初始化控件
        InitView();
        // 初始化地图
        InitMap();
        // 定位
        //InitLocal();
        //GEO
        InitGeo();
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

//        ib_loc = (ImageButton)findViewById(R.id.ib_loc);
//        ib_loc.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//
//                showInfo("回到当前位置");
//            }
//        });

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


        mTvinfo =(TextView)findViewById(R.id.tv_info);


    }


    //////////////////////////////////////////////////////////////////////////////////
    /**
     * @author by lhh
     * @brief 定位初始化
     * @method InitLocal()
     * @param 无
     * @return void
     * */
    private void InitLocal() {
        mBdMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(this);
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);

        // 配置定位信息
        initLocation();
    }


    /**
     * @author by lhh
     * @brief 定位SDK监听
     * @method MyLocationListener
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
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            // 设置定位数据
            mBdMap.setMyLocationData(locData);
            // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
            //mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
            MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, null);  //第三个参数是位置图片没有就默认
            mBdMap.setMyLocationConfigeration(config);
            //以我的位置为中心
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            mBdMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(latlng));
        }
    }


    /**
     * @author by lhh
     * @brief 配置定位信息
     * @method initLocation()
     * @param 无
     * @return void
     * */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02 国测局经纬度坐标系；bd09 百度墨卡托坐标系；bd09ll
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
        Log.i("2", "2");
        mLocationClient.start();

    }
    //////////////////////////////////////////////////////////////////////////////////


    /**
     * @author by lhh
     * @brief 监听地图点击事件，将点击获取的经纬度转换为具体地点信息，并在点击的位置做个标记
     * @method InitGeo()
     * @param 无
     * @return void
     * */
    private void InitGeo() {
        //Geo
        mGeoSearch = GeoCoder.newInstance();
        mGeoSearch.setOnGetGeoCodeResultListener(this);
        //////////////////////////////////////////////////////////////////////////////////
     /*地图监听GEO转换*/
        mBdMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                final LatLng lat = latLng;
                mGeoSearch.reverseGeoCode(new ReverseGeoCodeOption()
                        .location(lat));
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }

        });
        //////////////////////////////BaiduMap.OnMapClickListener///////////////////////////////////
    }
    ///////////////OnGetGeoCoderResultListener////////////////////////////////////////////
    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (TextUtils.isEmpty(reverseGeoCodeResult.getAddress())) {
            //Toast.makeText(MainActivity.this, "地点解析失败，请重新选择", Toast.LENGTH_SHORT).show();
            showInfo("地点解析失败，请重新选择");
        } else {
            if (null != mMark) {
                mMark.remove();
            }


            mTvinfo.setText(reverseGeoCodeResult.getAddress());

            /////show pos
            LatLng from = new LatLng(reverseGeoCodeResult.getLocation().latitude,
                    reverseGeoCodeResult.getLocation().longitude);
            BitmapDescriptor bdB = BitmapDescriptorFactory
                    .fromResource(R.mipmap.click_location_blue);
            OverlayOptions ooP = new MarkerOptions().position(from).icon(bdB);
            mMark = (Marker) (mBdMap.addOverlay(ooP));
            MapStatus mMapStatus = new MapStatus.Builder().target(from)
                    .build();
            /////show pos
        }
    }





    /**
     * @author by lhh
     * @brief 显示消息
     * @method InitView()
     * @param 无
     * @return void
     * */
    private void showInfo(String str){
        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
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
}
