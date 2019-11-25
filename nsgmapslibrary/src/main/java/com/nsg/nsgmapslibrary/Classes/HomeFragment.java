package com.nsg.nsgmapslibrary.Classes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.nsg.nsgmapslibrary.R;
import com.nsg.nsgmapslibrary.SupportClasses.DecimalUtils;
import com.nsg.nsgmapslibrary.SupportClasses.ETACalclator;
import com.nsg.nsgmapslibrary.SupportClasses.Util;
import com.nsg.nsgmapslibrary.database.db.SqlHandler;
import com.nsg.nsgmapslibrary.database.dto.EdgeDataT;
import com.nsg.nsgmapslibrary.interfaces.ILoadTiles;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment implements View.OnClickListener{
    private ProgressDialog dialog;
    private TextToSpeech textToSpeech;
    LatLng SourcePosition, DestinationPosition;
    //LatLng convertedSrcPosition,convertedDestinationPoisition;
    double sourceLat, sourceLng, destLat, destLng;
    LatLng dubai;
    String SourcePoint;
    String DestinationPoint,tokenResponse,etaResponse;
    Marker markerSource, markerDestination,mPositionMarker;
    private Polyline mPolyline;
    private GoogleMap mMap;
    private SqlHandler sqlHandler;
    GoogleMap.CancelableCallback callback;
    ILoadTiles mCallback;
    private double userLocatedLat, userLocatedLongi;
    private List points;
    private List<LatLng> convertedPoints;

    HashMap<LatLng,String> PositionMarkingPointsList;
    LatLng currentGpsPosition,lastKnownLocation;
    StringBuilder sb = new StringBuilder();
    private List LocationPerpedicularPoints=new ArrayList();
    private ArrayList<LatLng> currentLocationList=new ArrayList<LatLng>();
    private Marker sourceMarker,destinationMarker;
    private List<EdgeDataT> edgeDataList;
    private Handler handler = new Handler();
    // private int index=0;
    // private int next=0;
    private int enteredMode;
    private int routeDeviationDistance;
    List<LatLng> LatLngDataArray=new ArrayList<LatLng>();
    private String currentGpsPoint;
    private Polyline line;
    private List polyLines;
    private Circle mCircle;
    private List<LatLng>lastKnownPosition;
    private LatLng nearestPositionPoint;
    //  BitmapDescriptor mMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.car_icon_32);
    Bitmap mMarkerIcon;
    int mIndexCurrentPoint=0;
    private List<LatLng> edgeDataPointsList ;
    Map<String, List> mapOfLists = new HashMap<String, List>();
    private List AllPointsList ;
   // HashMap<Integer, String> AllPointEdgeNo;
    private LatLng newCenterLatLng,PointData;
  //  private List distancesList;
    private List distanceValuesList;
    HashMap<String, String> hash_map;
   // private List<LatLng> nearestPointValuesList;
    private Marker gpsMarker;
    private TextView tv,tv1,tv2,tv3,tv4;
    private String MESSAGE;
    LatLng centerFromPoint;
    LatLng point;
    private ImageButton etaListener;
    private ToggleButton fakeGpsListener;
    Marker fakeGpsMarker;
    List<Marker> markerlist;
    ArrayList<String> etaList;
    private ArrayList lastDistancesList;
    private double lastDistance;
    private String geometryText;
    private LocationManager locationManager;
    private Location lastLocation;
    Bitmap tileBitmap;
    private ImageButton change_map_options;
    String tokenNumber,updaterServiceResponse;
    private long startTime,presentTime,previousTime,TimeDelay;
    private  List<LatLng>listOfLatLng;
    HashMap<LatLng,String>edgeDataPointsListData;


   // List<LatLng> LatLngDataArray=new ArrayList<LatLng>();
   // private List<EdgeDataT> edgeDataList;
    HashMap<LatLng,String> AllPointEdgeNo;
    private List<EdgeDataT> edgeOfElementsList;
    private List distancesList;
    private List<EdgeDataT> distanceValuesEdgeList;
    private List<LatLng> nearestPointValuesList;
    EdgeDataT distanceEdge =new EdgeDataT();
    List<EdgeDataT>EdgeWithoutDuplicates;
   // private int mIndexCurrentPoint=0;
    String GeometryDirectionText="";
    private int locationFakeGpsListener=0;
    public interface FragmentToActivity {
    String communicate(String comm);
    }
    private FragmentToActivity Callback;

    public HomeFragment() {
        // Required empty public constructor
    }
    @SuppressLint("ValidFragment")
    public HomeFragment(double v1, double v2, double v3, double v4, int mode, int radius ) {
        //get Cordinates from MainActivity
        SourcePosition = new LatLng(v1, v2);
        DestinationPosition = new LatLng(v4, v3);
        sourceLat = v2;
        sourceLng = v1;
        destLat = v4;
        destLng =v3;
        enteredMode = mode;
        routeDeviationDistance=radius;
        SourcePoint=String.valueOf(v1).concat(" ").concat(String.valueOf(v2));
        DestinationPoint=String.valueOf(v3).concat(" ").concat(String.valueOf(v4));
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);//Menu implementation
        //Speech implemention
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = textToSpeech.setLanguage(Locale.US);
                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");
                } else {
                    Toast.makeText(getContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            sqlHandler = new SqlHandler(getContext());// Sqlite handler
            Callback = (FragmentToActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentToActivity");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //view from fragment
        mMarkerIcon = BitmapFactory.decodeResource(getResources(), R.drawable.car_icon_32);
        tileBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.water_image);
        View rootView = inflater.inflate(R.layout.fragment_map, container,
                false);
        //initialise view from fragment
        tv=(TextView)rootView.findViewById(R.id.tv);
        tv1=(TextView)rootView.findViewById(R.id.tv1);
        tv2=(TextView)rootView.findViewById(R.id.tv2);
       // tv3=(TextView)rootView.findViewById(R.id.tv3);
       // tv4=(TextView)rootView.findViewById(R.id.tv4);
       // etaListener=(ImageButton) rootView.findViewById(R.id.eta);
        fakeGpsListener=(ToggleButton)rootView.findViewById(R.id.fakeGps);
        fakeGpsListener.setOnClickListener(this);
        change_map_options = (ImageButton)rootView.findViewById(R.id.change_map_options);
        change_map_options.setOnClickListener(this);

        //Initialise Map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frg);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment1 = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googlemap) {
                //initialise map
                mMap = googlemap;
               //Initialise tile package
                String BASE_MAP_URL_FORMAT = Environment.getExternalStorageDirectory() + File.separator + "MBTILES" + File.separator + "DubaiBasemap" + ".mbtiles";
                Log.e("BaseMap","BaseMap"+BASE_MAP_URL_FORMAT);
                // Environment.getExternalStorageDirectory() + File.separator + "samples"+ File.separator + sectionName+".mbtiles"
                // Log.e("URL FORMAT","URL FORMAT ****************** "+ BASE_MAP_URL_FORMAT);
                //TileProvider tileProvider = new ExpandedMBTilesTileProvider(new File(BASE_MAP_URL_FORMAT.toString()), 256, 256);
               // TileOverlay tileOverlay = mMap.addTileOverlay(new TileOverlayOptions()
                //       .tileProvider(tileProvider));
               // tileOverlay.setTransparency(0.5f - tileOverlay.getTransparency());
               // tileOverlay.setVisible(true);
              //Get Route Details from Server
                if (Util.isInternetAvailable(getActivity()) == true && mMap != null ) {
                    dialog = new ProgressDialog(getActivity(), R.style.ProgressDialog);
                    dialog.setMessage("Fetching Route");
                    dialog.setMax(100);
                    dialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void run() {
                            GetRouteDetails();
                            if(MESSAGE.equals("Sucess")){
                                sendTokenRequest();
                                getAllEdgesData();
                                addMarkers();
                                getAllRoutePoints();
                                if(edgeOfElementsList!=null && edgeOfElementsList.size()>0){
                                   RoutePointsWithoutDuplicates();
                               }
                                dialog.dismiss();
                                    //   Entered Modee==1

                                if(enteredMode==1 &&edgeDataList!=null && edgeDataList.size()>0){
                                  //  nearestPointValuesList=new ArrayList<>();

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                    // MoveWithGPSMARKER();
                                                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                        // TODO: Consider calling
                                                        //    ActivityCompat#requestPermissions
                                                        // here to request the missing permissions, and then overriding
                                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                        //                                          int[] grantResults)
                                                        // to handle the case where the user grants the permission. See the documentation
                                                        // for ActivityCompat#requestPermissions for more details.
                                                        return;
                                                    }

                                                    mMap.setMyLocationEnabled(true);
                                                    mMap.getUiSettings().setZoomControlsEnabled(false);
                                                   // mMap.getUiSettings().setCompassEnabled(true);
                                                   // mMap.getUiSettings().setMyLocationButtonEnabled(true);
                                                   // mMap.getUiSettings().setMapToolbarEnabled(true);
                                                   // mMap.getUiSettings().setZoomGesturesEnabled(true);
                                                  //  mMap.getUiSettings().setScrollGesturesEnabled(true);
                                                   // mMap.getUiSettings().setTiltGesturesEnabled(true);
                                                   // mMap.getUiSettings().setRotateGesturesEnabled(true);
                                                   // mMap.getUiSettings().setMyLocationButtonEnabled(true);
                                                    mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                                                        @Override
                                                        public void onMyLocationChange(Location location) {
                                                            if (mPositionMarker != null) {
                                                                mPositionMarker.remove();
                                                            }
                                                            // currentGpsPosition=new LatLng(location.getLatitude(),location.getLongitude());
                                                            // updateUI(currentGpsPosition);

                                                            // Log.e("NEAREST POSITION","NEAREST POSITION"+ nearestPositionPoint);
                                                            // MoveInGPSPoints(currentGpsPosition);
                                                            getLatLngPoints();
                                                            currentGpsPosition = LatLngDataArray.get(locationFakeGpsListener);
                                                            GetDistancesPoints(currentGpsPosition);
                                                            locationFakeGpsListener = locationFakeGpsListener + 1;
                                                        }
                                                    });
                                                }


                                }else if(enteredMode==2){
                                   //CalculateNearestViaFakeGPS();
                                }
                            }else{
                                dialog.dismiss();
                                Toast.makeText(getActivity(), "Not Able to get Route from Service", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, 30);
                } else {
                    Toast.makeText(getActivity(), "please turn on wifi/mobiledata", Toast.LENGTH_LONG).show();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onDetach() {
        Callback = null;
        super.onDetach();
    }
    private void sendData(String comm)
    {
        Log.e("SendData","SendData ------- "+ comm);
        Callback.communicate(comm);

    }
    private  List<EdgeDataT> getAllEdgesData() {
        String query = "SELECT * FROM " + EdgeDataT.TABLE_NAME;
        Cursor c1 = sqlHandler.selectQuery(query);
        edgeDataList = (List<EdgeDataT>) SqlHandler.getDataRows(EdgeDataT.MAPPING, EdgeDataT.class, c1);
        sqlHandler.closeDataBaseConnection();
        return edgeDataList;
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(10, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void addMarkers(){
        LatLng position1= new LatLng(sourceLat,sourceLng);
        // Log.e("URL FORMAT","Uposition2 T ****************** "+ position1);
        sourceMarker = mMap.addMarker(new MarkerOptions()
                .position(position1)
                .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.source_red)));
        //CameraPosition googlePlex = CameraPosition.builder()
        //        .target(position1)
        //        .zoom(18)
        //        .tilt(45)
        //        .build();
       // mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);

        LatLng position2= new LatLng(destLat,destLng);
        destinationMarker= mMap.addMarker(new MarkerOptions()
                .position(position2)
                .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.destination_green)));
       // CameraPosition googlePlex1 = CameraPosition.builder()
        //        .target(position2)
        //        .zoom(18)
        //        .tilt(45)
         //       .build();

       // mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex1), 1000, null);
    }

    public List getAllRoutePoints(){
        getAllEdgesData();
        AllPointEdgeNo=new HashMap<>();
        EdgeWithoutDuplicates=new ArrayList<EdgeDataT>();
        edgeOfElementsList=new ArrayList<EdgeDataT>();

        nearestPointValuesList=new ArrayList();
        if (edgeDataList != null && edgeDataList.size() > 0) {
            for (int i = 0; i < edgeDataList.size(); i++) {
                EdgeDataT edge = new EdgeDataT(); //creating object for EDGETABLE
                edge = edgeDataList.get(i);
                int edgeNo = edge.getEdgeNo(); //Edge Number
                String stPoint = edge.getStartPoint(); //Start Point
                String endPoint = edge.getEndPoint();//End Point
                String points = edge.getAllPoints(); // All points in the edge
                String geometryText  = edge.getGeometryText(); // Geometry Direction text
                   if(points!=null){
                    String AllPoints = points.replace("[", "");
                    AllPoints = AllPoints.replace("]", "");
                    String[] AllPointsArray = AllPoints.split(", ");
                    for (int ap = 0; ap < AllPointsArray.length; ap++) {
                        // AllPointsList.add(AllPointsArray[ap]);
                        String[] data=AllPointsArray[ap].split(",");
                        double lat = Double.valueOf(data[0]);
                        double longi = Double.valueOf(data[1]);
                        LatLng newPoint=new LatLng(lat,longi);
                        EdgeDataT element = new EdgeDataT(edgeNo,newPoint.toString(),geometryText);
                        edgeOfElementsList.add(element);

                    }

                }

            }
        }
        return edgeOfElementsList;
    }
    public List RoutePointsWithoutDuplicates(){

            EdgeWithoutDuplicates = removeDuplicates(edgeOfElementsList);
      return EdgeWithoutDuplicates;
    }
    public void GetDistancesPoints(LatLng currentGpsPosition){
            nearestPointValuesList=new ArrayList<>();
            distancesList = new ArrayList();
            distanceValuesEdgeList = new ArrayList<EdgeDataT>();
            LatLng PositionMarkingPointLatLng = null;
            Log.e("currentGpsPosition ", " currentGpsPosition---- " + currentGpsPosition);
            if (EdgeWithoutDuplicates != null && EdgeWithoutDuplicates.size() > 0) {
                for (int i = 0; i < EdgeWithoutDuplicates.size(); i++) {
                    EdgeDataT edgeWithoutDuplicatesObj = new EdgeDataT();
                    edgeWithoutDuplicatesObj = EdgeWithoutDuplicates.get(i);
                    String geometryText = edgeWithoutDuplicatesObj.getGeometryText();
                    //Log.e("EdgeWithoutDuplicates ", " EdgeWithoutDuplicates ALL POINTS----- " + edgeWithoutDuplicatesObj.getAllPoints());
                    String PositionMarkingPointString = edgeWithoutDuplicatesObj.getAllPoints();
                    int EdgeNo = edgeWithoutDuplicatesObj.getEdgeNo();
                    String latlongStr = PositionMarkingPointString.replace("lat/lng: (", "");
                    latlongStr = latlongStr.replace(")", "");

                    String[] latlong = latlongStr.split(",");
                    double latitude = Double.parseDouble(latlong[0]);
                    double longitude = Double.parseDouble(latlong[1]);
                    PositionMarkingPointLatLng = new LatLng(longitude, latitude);

                    double distance = distFrom(PositionMarkingPointLatLng.latitude, PositionMarkingPointLatLng.longitude, currentGpsPosition.longitude, currentGpsPosition.latitude);
                    EdgeDataT edgeDistance = new EdgeDataT(String.valueOf(distance), PositionMarkingPointLatLng.toString(), geometryText);
                    distanceValuesEdgeList.add(edgeDistance);
                    distancesList.add(distance);
                    Collections.sort(distancesList);
                }

            }
       compareLists();
    }
    public void compareLists(){
        LatLng source = null,destination=null;
        String FirstCordinate="",SecondCordinate="";
        String FirstShortestDistance = String.valueOf(distancesList.get(0));
        Log.e("FirstShortestDistance"," FirstShortestDistance " + FirstShortestDistance);
        String SecondShortestDistance = String.valueOf(distancesList.get(1));
        Log.e("SecondShortestDistance"," SecondShortestDistance " + SecondShortestDistance);

        for (EdgeDataT edgeDataObj1 : distanceValuesEdgeList) {
            if (edgeDataObj1.getDistanceInVertex().equals(FirstShortestDistance)) {
                Log.e("EdgeData Obj", "EdgeData OBJ1 ----- " + edgeDataObj1.getDistanceInVertex());
                String str1= edgeDataObj1.getDistanceInVertex();
                GeometryDirectionText=edgeDataObj1.getGeometryText();
                FirstCordinate = edgeDataObj1.getPositionMarkingPoint();
            }
        }
        for (EdgeDataT edgeDataObj2 : distanceValuesEdgeList) {
            if (edgeDataObj2.getDistanceInVertex().equals(SecondShortestDistance)) {
                Log.e("EdgeData Obj", "EdgeData OBJ2 ----- " + edgeDataObj2.getDistanceInVertex());
                String str2= edgeDataObj2.getDistanceInVertex();
                SecondCordinate = edgeDataObj2.getPositionMarkingPoint();

            }
        }

        String First = FirstCordinate.replace("lat/lng: (", "");
        First = First.replace(")", "");
        String[] FirstLatLngsData = First.split(",");
        double FirstLatitude = Double.valueOf(FirstLatLngsData[0]);
        double FirstLongitude = Double.valueOf(FirstLatLngsData[1]);
        source = new LatLng( FirstLatitude,FirstLongitude);
        Log.e("Sorted ArrayList ", "Source----- : " + source);


        String Second = SecondCordinate.replace("lat/lng: (", "");
        Second = Second.replace(")", "");
        String[] SecondLatLngsData = Second.split(",");
        double SecondLatitude = Double.valueOf(SecondLatLngsData[0]);
        double SecondLongitude = Double.valueOf(SecondLatLngsData[1]);
        destination = new LatLng(SecondLatitude,SecondLongitude);
        Log.e("Sorted ArrayList ", "Destination----- : " + destination);


       // LatLng sourceData=new LatLng(FirstLongitude,FirstLatitude);
       // LatLng destinationData=new LatLng(SecondLongitude,SecondLatitude);


        Log.e("Sorted ArrayList ", "Source----- : " + source);
        Log.e("Sorted ArrayList ", "Destination----- : " + destination);

        CalculateNearestPositionPoint(source,destination);
    }
    public void CalculateNearestPositionPoint(LatLng src,LatLng dest){
     //   nearestPositionPoint= findNearestPoint(currentGpsPosition,src,dest);
        String nearestPoint =  GenerateLinePoint(src.latitude, src.longitude, dest.latitude, dest.longitude,currentGpsPosition.longitude,currentGpsPosition.latitude);

        Log.e("Sorted ArrayList ", "Nearest PositionPoint----- : " + nearestPoint);
        String nearPoint = nearestPoint.toString();
        String nearPoint1 = nearestPoint.replace("lat/lng: (", "");
        String nearPoint2 = nearPoint1.replace(")", "");
        String[] nearestPointStr = nearPoint2.split(",");
        double lat = Double.parseDouble(nearestPointStr[0]);
        double longi = Double.parseDouble(nearestPointStr[1]);
        nearestPositionPoint = new LatLng(longi,lat);
        Log.e("Sorted ArrayList ", "Nearest PositionPoint----- : " + nearestPositionPoint);
        nearestPointValuesList.add(nearestPositionPoint);
        calculateNearestList();
    }
    public void calculateNearestList(){
        if(nearestPointValuesList!=null  && nearestPointValuesList.size()>0){
            for(int k=0;k<nearestPointValuesList.size();k++){
                Log.e("NEAREST LIST","NEAREST LIST ---"+nearestPointValuesList.size());
                Log.e("NEAREST LIST","NEAREST LIST ---"+nearestPointValuesList.get(k));
            }
        }

        startTime=System.currentTimeMillis();
        sendTokenRequest();
        Log.e("startTime","StartTime"+startTime);

        Log.e("EdgeSt Point", "End point" + LatLngDataArray.size());
      //  CameraPosition googlePlex = CameraPosition.builder()
      //          .target(nearestPositionPoint)
       //         .zoom(25)
      //          .tilt(45)
      //          .build();
      //  mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);
       mMap.addMarker(new MarkerOptions()
                .position(nearestPositionPoint)
                .title("currentLocation")
                .icon(bitmapDescriptorFromVector(getContext(), R.drawable.circle_red)));
        mPositionMarker = mMap.addMarker(new MarkerOptions()
                .position(nearestPositionPoint)
                .title("currentLocation")
                .icon(bitmapDescriptorFromVector(getContext(), R.drawable.car_icon_32)));
        //  Log.e("Route Deviation ---","Route Deviation "+routeDeviationDistance);
        //  verifyRouteDeviation(routeDeviationDistance);
        Log.e("NEAREST POSITION---","NEAREST POSITION POINT "+ nearestPositionPoint);
        if(nearestPointValuesList.size()>1) {
            //  Log.e("NEAREST POSITION---","NEAREST Source Position ------ "+ nearestPointValuesList.get(0));
            //  Log.e("NEAREST POSITION---","NEAREST Destination Position ------- "+  nearestPointValuesList.get(1));
            animateCarMove(mPositionMarker, nearestPointValuesList.get(0), nearestPointValuesList.get(1), 10000);

        }

    }
    private List<EdgeDataT> removeDuplicates(List<EdgeDataT>edgeOfElementsList) {
        //  Log.e("ALL POINTS ", "EdgePoints Size----- " + edgeOfElementsList.size());
        Iterator<EdgeDataT> it = edgeOfElementsList.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
        for (int i = 0; i < edgeOfElementsList.size(); i++) {
            EdgeDataT edge=new EdgeDataT();
            edge=edgeOfElementsList.get(i);
            String EdgePosition1=edge.getAllPoints();
            // Log.e("List", "List Items -------" + EdgePosition1);
            for (int j = i + 1; j < edgeOfElementsList.size()-1 ; j++) {
                EdgeDataT edge1=edgeOfElementsList.get(j);

                String EdgePosition2=edge1.getAllPoints();
                //  Log.e("List", "List Items -------" + EdgePosition2);
                if (EdgePosition1.equals(EdgePosition2)) {
                    edgeOfElementsList.remove(j);
                }
            }
        }
        System.out.println(edgeOfElementsList);
        // Log.e("List","List"+edgeOfElementsList.size());
        for(int k=0;k<edgeOfElementsList.size();k++) {
            EdgeDataT edge = edgeOfElementsList.get(k);
            // Log.e("List", "List Items -------" + edge.getAllPoints());
            // Log.e("List", "List Items -------" + edge.getGeometryText());
            // Log.e("List", "List Items -------" + edge.getEdgeNo());
            EdgeDataT edgeNewObj=new EdgeDataT( edge.getEdgeNo(), edge.getAllPoints(),edge.getGeometryText());
            EdgeWithoutDuplicates.add(edgeNewObj);
        }
        return EdgeWithoutDuplicates;

    }
    private void GetRouteDetails(){
        try{
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT > 9) {
                        StrictMode.ThreadPolicy policy =
                                new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        try {
                            String httprequest = "http://202.53.11.74/dtnavigation/api/routing/routenavigate";
                            String FeatureResponse = HttpPost(httprequest,SourcePoint,DestinationPoint);
                            Log.e("RESPONSE", "RESPONSE" + FeatureResponse);
                            JSONObject jsonObject = null;
                            try {
                                if(FeatureResponse!=null){
                                    String delQuery = "DELETE  FROM " + EdgeDataT.TABLE_NAME;
                                    Log.e("DEL QUERY","DEL QUERY " + delQuery);
                                    sqlHandler.executeQuery(delQuery.toString());
                                    jsonObject = new JSONObject(FeatureResponse);
                                    String ID = String.valueOf(jsonObject.get("$id"));
                                    MESSAGE = jsonObject.getString("Message");
                                    String Status = jsonObject.getString("Status");
                                    String TotalDistance = jsonObject.getString("TotalDistance");
                                    JSONArray jSonRoutes = new JSONArray(jsonObject.getString("Route"));
                                   // Log.e("jSonRoutes", "jSonRoutes" + jSonRoutes);
                                    for (int i = 0; i < jSonRoutes.length(); i++) {
                                        points=new ArrayList();
                                        convertedPoints=new ArrayList<LatLng>();
                                       // Log.e("jSonRoutes", "jSonRoutes" + jSonRoutes.get(i));
                                        // List Routes=new ArrayList();
                                        // Routes.add(jSonRoutes.get(i));
                                        JSONObject Routes = new JSONObject(jSonRoutes.get(i).toString());
                                        String $id = Routes.getString("$id");
                                        String EdgeNo = Routes.getString("EdgeNo");
                                        String GeometryText = Routes.getString("GeometryText");
                                       // Log.e("GeometryText", "GeometryText" + GeometryText);
                                        String Geometry = Routes.getString("Geometry");
                                       // Log.e("Geometry", "Geometry----" + Geometry);
                                        JSONObject geometryObject = new JSONObject(Routes.getString("Geometry"));
                                        String $id1 = geometryObject.getString("$id");
                                        String type = geometryObject.getString("type");
                                       // Log.e("type", "type----" + type);
                                        String coordinates = geometryObject.getString("coordinates");
                                       // Log.e("coordinates", "coordinates----" + coordinates);
                                        JSONArray jSonLegs = new JSONArray(geometryObject.getString("coordinates"));
                                       // Log.e("jSonLegs", "jSonLegs----" + jSonLegs);
                                        for (int j = 0; j < jSonLegs.length(); j++) {
                                         //   Log.e("JSON LEGS", "JSON CORDINATES" + jSonLegs.get(j));
                                            points.add(jSonLegs.get(j));
                                        //    Log.e("JSON LEGS", " LATLNG RESULT------ " + points.size());
                                        }
                                       // Log.e("JSON LEGS", " LATLNG RESULT------ " + points.size());
                                        String  stPoint=String.valueOf(jSonLegs.get(0));
                                        // String  endPoint=String.valueOf(jSonLegs.get(jSonLegs.length()-1));

                                        stPoint=stPoint.replace("[","");
                                        stPoint=stPoint.replace("]","");
                                        String [] firstPoint=stPoint.split(",");
                                        Double stPointLat= Double.valueOf(firstPoint[0]);
                                        Double stPointLongi= Double.valueOf(firstPoint[1]);
                                        LatLng stVertex=new LatLng(stPointLongi,stPointLat);
                                        //    endPoint=endPoint.replace("[","");
                                        //    endPoint=endPoint.replace("]","");
                                        //    String [] secondPoint=endPoint.split(",");
                                        //   Double endPointLat= Double.valueOf(secondPoint[0]);
                                        //    Double endPointLongi= Double.valueOf(secondPoint[1]);
                                        //    LatLng endVertex=new LatLng(endPointLongi,endPointLat);

                                        //    double distance=showDistance(stVertex,endVertex);
                                        //    String distanceInKM = String.valueOf(distance/1000);
                                        //    Log.e("Distance -----","Distance in KM-------- "+ distanceInKM);
                                        StringBuilder query = new StringBuilder("INSERT INTO ");
                                        query.append(EdgeDataT.TABLE_NAME).append("(edgeNo,distanceInVertex,startPoint,allPoints,geometryText,endPoint) values (")
                                                .append("'").append(EdgeNo).append("',")
                                                .append("'").append("distanceInKM").append("',")
                                                .append("'").append(jSonLegs.get(0)).append("',")
                                                .append("'").append(points).append("',")
                                                .append("'").append(GeometryText).append("',")
                                                .append("'").append(jSonLegs.get(jSonLegs.length()-1)).append("')");
                                        sqlHandler.executeQuery(query.toString());
                                        sqlHandler.closeDataBaseConnection();
                                        for (int p = 0; p < points.size(); p++) {
                                        //    Log.e("JSON LEGS", "JSON POINTS LIST ---- " + points.get(p));
                                            String listItem = points.get(p).toString();
                                            listItem = listItem.replace("[", "");
                                            listItem = listItem.replace("]", "");
                                         //   Log.e("JSON LEGS", "JSON POINTS LIST ---- " + listItem);
                                            String[] subListItem = listItem.split(",");
                                          //  Log.e("JSON LEGS", "JSON POINTS LIST ---- " + subListItem.length);
                                          //  Log.e("JSON LEGS", "JSON POINTS LIST ---- " + subListItem[0]);
                                          //  Log.e("JSON LEGS", "JSON POINTS LIST ---- " + subListItem[1]);
                                            Double y = Double.valueOf(subListItem[0]);
                                            Double x = Double.valueOf(subListItem[1]);
                                            StringBuilder sb=new StringBuilder();
                                            //  sb.append(x).append(",").append(y).append(":");
                                            //  LocationPerpedicularPoints.add(sb.toString());
                                            LatLng latLng = new LatLng(x, y);
                                         //   Log.e("JSON LEGS", " LATLNG RESULT------ " + latLng);
                                            convertedPoints.add(latLng);
                                            for (int k = 0; k < convertedPoints.size(); k++) {
                                                MarkerOptions markerOptions = new MarkerOptions();
                                                PolylineOptions polylineOptions = new PolylineOptions();
                                                if(polylineOptions!=null && mMap!=null) {
                                                    markerOptions.position(convertedPoints.get(k));
                                                    markerOptions.title("Position");
                                                    // polylineOptions.color(Color.RED);
                                                    // polylineOptions.width(6);
                                                    polylineOptions.addAll(convertedPoints);
                                                    // polylineOptions.color(Color.GREEN).width(10);
                                                    // polylineOptions.color(Color.BLACK).width(8);
                                                    // Polyline polyline =
                                                    mMap.addPolyline(polylineOptions);
                                                    polylineOptions.color(Color.CYAN).width(18);
                                                    mMap.addPolyline(polylineOptions);

                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }catch (Exception ex){

                        }
                        dialog.dismiss();
                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
        dialog.dismiss();
    }
    private String HttpPost(String myUrl,String latLng1,String latLng2) throws IOException, JSONException {
        StringBuilder sb = new StringBuilder();
        String LoginResponse = "";
        String result = "";
        URL url = new URL(myUrl);
        Log.v("URL ", " URL: " + url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "text/plain");
        JSONObject jsonObject = buidJsonObject(latLng1,latLng2);
        Log.e(" Message", " jsonObject: " + jsonObject);
        setPostRequestContent(conn, jsonObject);
        conn.connect();
        Log.e("Response Code", "ResponseCode: " + conn.getResponseCode());
        result = conn.getResponseMessage();
        Log.e("Response Message", "Response Message: " + result);

        if (conn.getResponseCode() != 200) {

        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = null;
            //   System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
                LoginResponse = sb.append(output).append(" ").toString();
                //      Log.e("Login Response "," From server ############ "+LoginResponse);
            }
        }
        conn.disconnect();
        return LoginResponse;
    }

    private JSONObject buidJsonObject(String latLng1,String latLng2) throws JSONException {
        JSONObject buidJsonObject = new JSONObject();
        buidJsonObject.accumulate("UserData", buidJsonObject1());
        buidJsonObject.accumulate("StartNode", latLng1);
        buidJsonObject.accumulate("EndNode", latLng2);
        return buidJsonObject;
    }

    private JSONObject buidJsonObject1() throws JSONException {
        JSONObject buidJsonObject1 = new JSONObject();
        buidJsonObject1.accumulate("username", "admin");
        buidJsonObject1.accumulate("password", "admin");
        return buidJsonObject1;
    }

    private void setPostRequestContent(HttpURLConnection conn,
                                       JSONObject jsonObject) throws IOException {
        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
        // Log.i(LoginActivity.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
    }

    private LatLng findNearestPoint(final LatLng p,  LatLng start,  LatLng end) {
        if (start.equals(end)) {
            return start;
        }
        final double s0lat = Math.toRadians(p.latitude);
        final double s0lng = Math.toRadians(p.longitude);
        final double s1lat = Math.toRadians(start.latitude);
        final double s1lng = Math.toRadians(start.longitude);
        final double s2lat = Math.toRadians(end.latitude);
        final double s2lng = Math.toRadians(end.longitude);

        double s2s1lat = s2lat - s1lat;
        double s2s1lng = s2lng - s1lng;
        final double u = ((s0lat - s1lat) * s2s1lat + (s0lng - s1lng) * s2s1lng)
                / (s2s1lat * s2s1lat + s2s1lng * s2s1lng);
        if (u <= 0) {
            return start;
        }
        if (u >= 1) {
            return end;
        }

        return new LatLng(start.latitude + (u * (end.latitude - start.latitude)),
                start.longitude + (u * (end.longitude - start.longitude)));
    }
    private void animateMarker(final Marker marker, final LatLng toPosition,
                               final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1000;

        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));


                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }
    private String GenerateLinePoint(double startPointX, double startPointY, double endPointX, double endPointY, double pointX, double pointY)
    {
        double k = ((endPointY - startPointY) * (pointX - startPointX) - (endPointX - startPointX) * (pointY - startPointY)) / (Math.pow(endPointY - startPointY, 2)
                + Math.pow(endPointX - startPointX, 2));
        double resultX = pointX - k * (endPointY - startPointY);
        double resultY = pointY + k * (endPointX - startPointX);
        StringBuilder sb=new StringBuilder();
        sb.append(resultX).append(",").append(resultY);

        return sb.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void verifyRouteDeviation(int markDistance){
        PolylineOptions polylineOptions = new PolylineOptions();
        //To Verify Route Deviation
        //currentLocationList.add(currentGpsPosition);
        String nearestPoint = GenerateLinePoint( sourceLng,sourceLat,destLng,destLat,currentGpsPosition.longitude,currentGpsPosition.latitude);
        Log.e("NEAREST POINT", "NEAREST POINT----------" + nearestPoint);
        String[] nearestDataStr = nearestPoint.split(",");
        double latitude = Double.parseDouble(nearestDataStr[0]);
        double longitude = Double.parseDouble(nearestDataStr[1]);
        LatLng nearestPosition=new LatLng(longitude,latitude);
        double returnedDistance= showDistance(currentGpsPosition,nearestPosition);
        Log.e("returnedDistance", "returnedDistance --------- "+ returnedDistance);
        drawMarkerWithCircle(nearestPosition,markDistance);
        if(returnedDistance > markDistance){
            Toast toast = Toast.makeText(getContext(), " ROUTE DEVIATED ", Toast.LENGTH_LONG);
            toast.setMargin(100, 100);
            toast.show();
            Log.e("Route Deviation","Route deviation"+"Route Deviated");
            //drawDeviatedRoute(currentGpsPosition, DestinationPosition);
            String cgpsLat= String.valueOf(currentGpsPosition.latitude);
            String cgpsLongi= String.valueOf(currentGpsPosition.longitude);
            currentGpsPoint=cgpsLongi.concat(" ").concat(cgpsLat);
            Log.e("returnedDistance", "nearest Position--------- "+ nearestPosition);
            Log.e("returnedDistance", "Destination Position --------- "+ DestinationPosition);
            DestinationPosition=new LatLng(destLat,destLng);
            Log.e("returnedDistance", "DestinationPosition --------- "+ DestinationPosition);
             MarkerOptions markerOptions = new MarkerOptions();
             markerOptions.position(currentGpsPosition);
             markerOptions.position(DestinationPosition);
             markerOptions.title("Position");
             /*

             ReRouteFeaturesFromServer download=new ReRouteFeaturesFromServer();
              download.execute();

            polylineOptions.color(Color.RED);
            polylineOptions.width(6);
            points.add(nearestPosition);
            points.add(new LatLng(24.987665, 55.060701));
            points.add(new LatLng(24.988843, 55.062091));
            points.add(new LatLng(24.989472, 55.061488));
            points.add(DestinationPosition);
            if(points.size()>0) {
                polylineOptions.addAll(points);
                line = mMap.addPolyline(polylineOptions);
                if (polylineOptions != null) {
                    if (line != null) {
                        line.remove();
                    }
                    line = mMap.addPolyline(polylineOptions);
                } else
                    Toast.makeText(getContext(), "No route is found", Toast.LENGTH_LONG).show();
            }
            */

        }else{

        }
    }
    private void drawMarkerWithCircle(LatLng gpsPosition,double radius){
        // double radiusInMeters = 400.0;
        CircleOptions circleOptions = new CircleOptions().center(gpsPosition).radius(radius).fillColor(Color.parseColor("#2271cce7")).strokeColor(Color.parseColor("#2271cce7")).strokeWidth(3);
        mCircle = mMap.addCircle(circleOptions);

    }
    private double showDistance(LatLng latlng1,LatLng latLng2) {
        double distance = SphericalUtil.computeDistanceBetween(latlng1,latLng2);
        return distance;
    }
    public int getLatLngPoints(){

        LatLngDataArray.add(new LatLng(24.978782,55.067291));
        LatLngDataArray.add(new LatLng(24.9786559890011,55.0669970292443));

        LatLngDataArray.add(new LatLng(24.9784084059624,55.0668971973738));


        LatLngDataArray.add(new LatLng(24.9780250515799,55.0664619304187));
        LatLngDataArray.add(new LatLng(24.9779931053814,55.0662263272045));

        LatLngDataArray.add(new LatLng(24.9776097509989,55.0658150198983));
        LatLngDataArray.add(new LatLng(24.9773577661311,55.0656917069084));
        LatLngDataArray.add(new LatLng(24.9771317173355,55.0654359148503));


        LatLngDataArray.add(new LatLng(24.977125768683,55.0652485322961));
        LatLngDataArray.add(new LatLng(24.9771644349243,55.0651711998134));

        LatLngDataArray.add(new LatLng(24.9772566390383,55.0648737671876));
        LatLngDataArray.add(new LatLng(24.9776314041467,55.0646596156971));
        LatLngDataArray.add(new LatLng(24.9778187867009,55.0642937735675));
        LatLngDataArray.add(new LatLng(24.9780150922339,55.064287824915));

        LatLngDataArray.add(new LatLng(24.9781372775566,55.0643145938513));
        LatLngDataArray.add(new LatLng(24.9783557120769,55.0645658649335));
        LatLngDataArray.add(new LatLng(24.9784799199414,55.0647757333942));

        LatLngDataArray.add(new LatLng(24.9787326187002,55.0650612687149));
        LatLngDataArray.add(new LatLng(24.9789624746334,55.0652568604096));

        LatLngDataArray.add(new LatLng(24.9790310031104,55.0654124771594));
        LatLngDataArray.add(new LatLng(24.9791309404726,55.0655595278495));

        LatLngDataArray.add(new LatLng(24.9793522303461,55.0657065785397));
        LatLngDataArray.add(new LatLng(24.9795049917427,55.06600067992));

        LatLngDataArray.add(new LatLng(24.9797548351483,55.0661562966698));
        LatLngDataArray.add(new LatLng(24.9799104518981,55.0664161338116));
        LatLngDataArray.add(new LatLng(24.9800632132947,55.0665146434973));

        LatLngDataArray.add(new LatLng(24.980123175712,55.0666745432769));
        LatLngDataArray.add(new LatLng(24.9802174023679,55.0667944681116));
        LatLngDataArray.add(new LatLng(24.9802216853977,55.0668301600267));

        LatLngDataArray.add(new LatLng(24.9802145470147,55.0668744180014));

        LatLngDataArray.add(new LatLng(24.9801460185377,55.0669458018315));
        LatLngDataArray.add(new LatLng(24.9800717793543,55.0670728650492));
        LatLngDataArray.add(new LatLng(24.979964703609,55.0671913622073));


       LatLngDataArray.add(new LatLng(24.979878,55.067205));

        return LatLngDataArray.size();

    }
    private double getAngle(LatLng beginLatLng, LatLng endLatLng) {
        double f1 = Math.PI * beginLatLng.latitude / 180;
        double f2 = Math.PI * endLatLng.latitude / 180;
        double dl = Math.PI * (endLatLng.longitude - beginLatLng.longitude) / 180;
        return Math.atan2(Math.sin(dl) * Math.cos(f2) , Math.cos(f1) * Math.sin(f2) - Math.sin(f1) * Math.cos(f2) * Math.cos(dl));
    }
    private static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight, boolean isNecessaryToKeepOrig) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        if (!isNecessaryToKeepOrig) {
            bm.recycle();
        }
        return resizedBitmap;
    }
    public Bitmap addPaddingLeftForBitmap(Bitmap bitmap, int paddingLeft) {
        Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth() + paddingLeft, bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, paddingLeft, 0, null);
        return outputBitmap;
    }

    public Bitmap addPaddingRightForBitmap(Bitmap bitmap, int paddingRight) {

        Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth() + paddingRight, bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);
        canvas.drawColor(Color.RED);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap;
    }
    public Bitmap setBounds(Bitmap bitmap,int paddingRight,int paddingLeft){
        Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth() + paddingLeft - paddingRight, bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);
       // canvas.drawColor(Color.RED);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap ;
    }


    private void animateCarMove(final Marker marker, final LatLng beginLatLng, final LatLng endLatLng, final long duration) {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final Interpolator interpolator = new LinearInterpolator();
        // set car bearing for current part of path
        float angleDeg = (float)(180 * getAngle(beginLatLng, endLatLng) / Math.PI);
        Matrix matrix = new Matrix();
        matrix.postRotate(angleDeg);
       // Bitmap opBitMap= addPaddingLeftForBitmap(mMarkerIcon,60);
        // Bitmap opBitMap= setBounds(mMarkerIcon,10,10);
         marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(mMarkerIcon, 0, 0,mMarkerIcon.getWidth(), mMarkerIcon.getHeight(), matrix, true)));
        //marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(mMarkerIcon, 0, 0, centerX,centerY, matrix, true)));
        handler.post(new Runnable() {
            @Override
            public void run() {
                // calculate phase of animation
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                // calculate new position for marker
                double lat = (endLatLng.latitude - beginLatLng.latitude) * t + beginLatLng.latitude;
                double lngDelta = endLatLng.longitude - beginLatLng.longitude;
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * t + beginLatLng.longitude;
                marker.setPosition(new LatLng(lat, lng));

               // centerMapAt(new LatLng(lat,lng));
                // if not end of line segment of path
                if (t < 1.0) {
                    // call next marker position
                    handler.postDelayed(this, 16);
                } else {
                    // call turn animation
                   nextTurnAnimation();
                }
            }
        });
    }
    private void nextTurnAnimation() {
        mIndexCurrentPoint++;
        Log.e("EdgeListPoints","--------------"+points.size());
        if (mIndexCurrentPoint < nearestPointValuesList.size() - 1) {
            LatLng prevLatLng = nearestPointValuesList.get(mIndexCurrentPoint - 1);
            LatLng currLatLng = nearestPointValuesList.get(mIndexCurrentPoint);
            LatLng nextLatLng = nearestPointValuesList.get(mIndexCurrentPoint + 1);

            float beginAngle = (float)(90 * getAngle(prevLatLng, currLatLng) / Math.PI);
            float endAngle = (float)(90 * getAngle(currLatLng, nextLatLng) / Math.PI);

            animateCarTurn(mPositionMarker, beginAngle, endAngle, 100);
        }
    }
    private void animateCarTurn(final Marker marker, final float startAngle, final float endAngle, final long duration) {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final Interpolator interpolator = new LinearInterpolator();

        final float dAndgle = endAngle - startAngle;

        handler.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                Matrix m = new Matrix();
                float angle=startAngle + dAndgle * t;
                m.postRotate(angle);
              //  Bitmap opBitMap= setBounds(mMarkerIcon,10,10);
              //  marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createBitmap(mMarkerIcon, 0, 0, mMarkerIcon.getWidth(),mMarkerIcon.getHeight(), m, true)));
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                } else {
                    nextMoveAnimation();
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void nextMoveAnimation() {
        lastDistancesList=new ArrayList();
        if (mIndexCurrentPoint < nearestPointValuesList.size()){
            double resultdistance=showDistance(nearestPointValuesList.get(mIndexCurrentPoint),new LatLng(destLat,destLng)); //in km
            //LatLng indexPoint=nearestPointValuesList.get(mIndexCurrentPoint);
            //double resultdistance=distFrom(indexPoint.latitude,indexPoint.longitude,destLat,destLng); //in km
           // double resultMts=resultdistance*1000;
          //  String finalResultMts=String.format("%.2f", resultdistance);
            String finalResultMts=String.format("%.0f", resultdistance);
            double speed=10.0; //kmph
            ETACalclator calculator=new ETACalclator();
            double resultTime=calculator.cal_time(resultdistance, speed);
            resultTime=DecimalUtils.round(resultTime,0);

            int seconds = (int) ((resultTime / 1000) % 60);
            int minutes = (int) ((resultTime / 1000) / 60);
            StringBuilder time= new StringBuilder();
            time.append("Distance").append(finalResultMts+"Meters").append("\n").append("Speed").append(speed +"KMPH").append("\n").append("Estimated Time").append(resultTime+"Sec").append("\n");
            sendData(time.toString());
           // System.out.println("\n Send Data Fragment--- : ");
            System.out.println("\n The calculated Time Minuites : "+ minutes +" SECONDS "+ seconds);
          //  etaList.add(time.toString());


            TimeDelay=presentTime-startTime;
            Log.e("Time Delay","TimeDelay"+TimeDelay);

            presentTime=System.currentTimeMillis();
            Log.e("present Time","present Time"+ presentTime);

            Bundle gameData = new Bundle();
            gameData.putStringArrayList("listEta",etaList);
            verifyRouteDeviation(3);
            tv.setText("Estimated Time : "+ resultTime +"Sec" );
            tv1.setText("Distance : "+ finalResultMts +" Meters ");
            tv2.setText("Speed : "+ speed +"KM ");
            tv3.setText("Direction : "+  GeometryDirectionText);
            tv4.setText("Time : "+ TimeDelay);

            sendTokenRequest();
            //Speech implementation
            /*
            String data=" in "+ finalResultMts +" Meters "+geometryText;
            int speechStatus = textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null);
            if (speechStatus == TextToSpeech.ERROR) {
                Log.e("TTS", "Error in converting Text to Speech!");
            }
            */
            LatLng cameraPosition=nearestPointValuesList.get(mIndexCurrentPoint);
           // CameraPosition cameraPos = new CameraPosition.Builder()
           //         .target(new LatLng(cameraPosition.latitude,cameraPosition.longitude))
           //         .zoom(20).bearing(0).tilt(10).build();
           // mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos), 500, null);
            // mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos), null);
           // Log.e("CameraPOS","CameraPos--------- "+ mIndexCurrentPoint);
          //  Log.e("CameraPOS","CameraPos--------- "+ nearestPointValuesList.size());
            animateCarMove(mPositionMarker, nearestPointValuesList.get(mIndexCurrentPoint), nearestPointValuesList.get(mIndexCurrentPoint+1), 10000);

            LatLng lastPoint=nearestPointValuesList.get(nearestPointValuesList.size()-1);
            Log.e("last Point","last Point--------- "+ lastPoint);
            if (lastPoint.equals(DestinationPosition)) {
                Log.e("last Point","last Point--------- "+ lastPoint);

                lastDistance= showDistance(cameraPosition,DestinationPosition);
                Log.e("lastDistance","lastDistance--------- "+ lastDistance );
                lastDistancesList.add(lastDistance);
                Log.e("lastDistance","lastDistance--------- "+ lastDistance );
                Log.e("lastDistance---","Last Distances List Size--------- "+ lastDistancesList.size());
                if (lastDistance < 5) {
                    //Speech implementation
                    // String data1="Your Destination Reached";
                    // int speechStatus1 = textToSpeech.speak(data1, TextToSpeech.QUEUE_FLUSH, null);
                    //  if (speechStatus1 == TextToSpeech.ERROR) {
                    //     Log.e("TTS", "Error in converting Text to Speech!");
                    //  }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.yourDialog);
                    builder.setTitle("Alert");
                    builder.setIcon(R.drawable.car_icon_32);
                    builder.setMessage("Destination Reached")
                            .setCancelable(false)
                            .setPositiveButton(" STOP ", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    getActivity().getSupportFragmentManager().popBackStack();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }

        }
    }
    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (float) (earthRadius * c);
        return dist;
    }
    /*
    private List<LatLng> removeDuplicates(List<LatLng> EdgeWithoutDuplicates)
    {
        int count = edgeDataPointsList.size();

        for (int i = 0; i < count; i++)
        {
            for (int j = i + 1; j < count; j++)
            {
                if (edgeDataPointsList.get(i).equals(edgeDataPointsList.get(j)))
                {
                    edgeDataPointsList.remove(j--);
                    count--;
                }
            }
        }
        return EdgeWithoutDuplicates;
    }
    */

    public void addFakeGPSMarkers(){
        getLatLngPoints();
        for(int p=0;p<LatLngDataArray.size();p++){
            fakeGpsMarker =mMap.addMarker(new MarkerOptions()
                    .position(LatLngDataArray.get(p))
                    .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.symbol_shackel_point)));
            markerlist= new ArrayList<Marker>();
            markerlist.add(fakeGpsMarker);
        }
        Log.e("MarkerList :", " MarkerList ----- " + markerlist.size());
    }
    public void removeFakeGPSMarkers(){
        getLatLngPoints();
        for(int p=0;p<LatLngDataArray.size();p++) {
            if (markerlist != null && !markerlist.isEmpty()) {
                //  markerlist.get(p).remove(); // Add this line
                markerlist.remove(p);
                if(  fakeGpsMarker.getPosition().equals(LatLngDataArray.get(p))){
                    fakeGpsMarker.remove();
                }
            }
        }
        Log.e("MarkerList :", " MarkerList ----- " + markerlist.size());
    }

    @Override
    public void onClick(View v) {
        if(v==fakeGpsListener){
            String fakeGpsText=fakeGpsListener.getText().toString();
            if(fakeGpsText.equals("Off")){
                fakeGpsListener.setBackgroundColor(Color.RED);
                Log.e("Fake Gps Text :", " Fake Gps Text ----- " + fakeGpsText);
                if(fakeGpsMarker!=null) {
                    removeFakeGPSMarkers();
                }
            }else if(fakeGpsText.equals("On")){
                fakeGpsListener.setBackgroundColor(Color.GREEN);
                Log.e("Fake Gps Text :", " Fake Gps Text ------" + fakeGpsText);
                addFakeGPSMarkers();
            }
        } else if(v==etaListener){

        }else if(v==change_map_options){
            PopupMenu popup = new PopupMenu(getContext(), change_map_options);
            //Inflating the Popup using xml file
            popup.getMenuInflater()
                    .inflate(R.menu.popup_menu, popup.getMenu());
            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.slot1) {
                        if(mMap!=null) {
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            Toast.makeText(getContext(), "NORMAL MAP ENABLED", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    } else if (itemId == R.id.slot2) {
                        if(mMap!=null) {
                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            Toast.makeText(getContext(), "SATELLITE MAP ENABLED", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    } else if (itemId == R.id.slot3) {
                        if(mMap!=null) {
                            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                            Toast.makeText(getContext(), "TERRAIN MAP ENABLED", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }else if (itemId == R.id.slot4) {
                        if(mMap!=null) {
                            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                            Toast.makeText(getContext(), "HYBRID MAP ENABLED", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                    return true;
                }
            });
            popup.show();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
    public void sendTokenRequest(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    String url1 = "http://86.96.196.245/ROROAPI/Login/GetToken";
                    Log.e("SAVING DATA ", "SAVING DATA " + url1);
                    tokenResponse = HttpPost1(url1);
                    Log.e("SAVING DATA ", "RESPONSE DATA " + tokenResponse);
                    JSONObject obj = new JSONObject(tokenResponse);
                    tokenNumber = obj.getString("tokenNumber");

                     if(tokenNumber!=null && !tokenNumber.isEmpty()){
                        String url = "http://86.96.196.245/ROROAPI/NSGMap/AlertDataProcess";
                        Log.e("SAVING DATA ", "SAVING DATA " + url);
                        updaterServiceResponse = HttpPost(url);
                        Log.e("SAVING DATA ", "SAVING DATA " + updaterServiceResponse);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, 10);//just mention the time when you want to launch your action

    }
    private String HttpPost(String myUrl) throws IOException, JSONException {
        StringBuilder sbResponse=new StringBuilder();
        String result = "";
        URL url = new URL(myUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        String basicAuth = "Bearer "+tokenNumber;
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization",basicAuth);
        //; charset=utf-8
        // 2. build JSON object
        JSONObject jsonObject = buidJsonObject();
        Log.e("jsonObject", "jsonObject: " + jsonObject);
        // 3. add JSON content to POST request body
        setPostRequestContent(conn, jsonObject);
        conn.connect();
        result = conn.getResponseMessage();
        Log.e("Response Message", "Response Message: " + conn.getResponseMessage());
        // 5. return response message
        Log.e("Response Code", "ResponseCode: " + conn.getResponseCode());
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output=null;
        System.out.println("Output from Server .... \n");
        while ((output = br.readLine()) != null) {
            System.out.println(output);
            sbResponse.append(output).append(" ");
        }
        return sbResponse.toString();
    }
    private JSONObject buidJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("DriverID", "DVR002");
        jsonObject.accumulate("AlertType", "ETA");
        jsonObject.accumulate("AlertCode",  "ETACroess");
        jsonObject.accumulate("AlertName", "ETA Croessed");
        jsonObject.accumulate("AlertValue", "mobile");
        jsonObject.accumulate("OptionalString1",  "true");
        jsonObject.accumulate("OptionalString1", "");
        jsonObject.accumulate("OptionalInt1",  "");
        jsonObject.accumulate("OptionalInt2", "");
        jsonObject.accumulate("UserID", "nsgadmin");
        jsonObject.accumulate("ApplicationID",  "10");
        jsonObject.accumulate("CompanyUno", "2");
        jsonObject.accumulate("LanguageUno",  "1033");
        jsonObject.accumulate("Condition",  1);
        return jsonObject;
    }
    private String HttpPost1(String myUrl) throws IOException, JSONException {
        StringBuilder sbResponse=new StringBuilder();
        String result = "";
        URL url = new URL(myUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
      //  conn.setRequestProperty("Accept", "application/json");
        //; charset=utf-8
        // 2. build JSON object
        JSONObject jsonObject = buidJsonObjectTokenService();
        //Log.e("jsonObject", "jsonObject: " + jsonObject);
        // 3. add JSON content to POST request body
        setPostRequestContent(conn, jsonObject);
        // 4. make POST request to the given URL
        conn.connect();
        result = conn.getResponseMessage();
        Log.e("Response Message", "Response Message: " + result);
        // 5. return response message
        // Log.e("RequestMethod Code", "RequestMethod: " + conn.getRequestMethod());
        // Log.e("ContentEncoding Code", "ContentEncoding: " + conn.getContentEncoding());
        Log.e("Response Code", "ResponseCode: " + conn.getResponseCode());
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output=null;
        System.out.println("Output from Server .... \n");
        while ((output = br.readLine()) != null) {
            System.out.println(output);
            sbResponse.append(output).append(" ");
        }
        return sbResponse.toString();
    }
    private JSONObject buidJsonObjectTokenService() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("Username","nsgadmin");
        jsonObject.accumulate("Password","nsgadmin");
        return jsonObject;
    }

/*
  Polygon polygon = mMap.addPolygon(new PolygonOptions()
                        .add(   new LatLng(24.997631202047927,55.07903607176733),
                                new LatLng(24.994943800450407,55.08188145253859),
                                new LatLng(24.97188040995721,55.05534878834661),
                                new LatLng(24.974253804379885,55.052859097191366),
                                new LatLng(24.97333303087801,55.051774176738625),
                                new LatLng(24.975922876821002,55.04905955187892),
                                new LatLng(24.97686569894065,55.05012017041875),
                                new LatLng(24.977013208550122,55.04999522736928),
                                new LatLng(24.98105413788837,55.054525318253745),
                                new LatLng(24.981239546680065,55.05459527344463),
                                new LatLng(24.986517227672824,55.05239502998611),
                                new LatLng(24.988904885932133,55.05771226137448),
                                new LatLng(24.988855585254097,55.0577091240746),
                                new LatLng(24.98882002682904,55.057734217250925),
                                new LatLng(24.981821286589813,55.06074200734972),
                                new LatLng(24.982168190131457,55.061141878974404),
                                new LatLng(24.98209526808512,55.061173181159056)
                        )
                        .strokeColor(Color.GRAY)
                        .strokeWidth(2)
                        .fillColor(getResources().getColor(R.color.colorWater)));




                        TranslateAnimation mAnimation ;
                mAnimation = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                        TranslateAnimation.RELATIVE_TO_PARENT,0.5f,
                        TranslateAnimation.RELATIVE_TO_PARENT, 1.1f,
                        TranslateAnimation.RELATIVE_TO_PARENT, -0.3f);
                mAnimation.setDuration(25000);
                mAnimation.setRepeatCount(-1);
                mAnimation.setRepeatMode(Animation.REVERSE);
                mAnimation.setInterpolator(new LinearInterpolator());
                water_ball.setAnimation(mAnimation);


                listOfLatLng=new ArrayList<>();
        listOfLatLng.add(new LatLng(24.997631202047927,55.07903607176733));
        listOfLatLng.add(new LatLng(24.994943800450407,55.08188145253859));
        listOfLatLng.add(new LatLng(24.97188040995721,55.05534878834661));
        listOfLatLng.add(new LatLng(24.974253804379885,55.052859097191366));
        listOfLatLng.add(new LatLng(24.97333303087801,55.051774176738625));
        listOfLatLng.add(new LatLng(24.975922876821002,55.04905955187892));
        listOfLatLng.add(new LatLng(24.97686569894065,55.05012017041875));
        listOfLatLng.add(new LatLng(24.977013208550122,55.04999522736928));
        listOfLatLng.add(new LatLng(24.98105413788837,55.054525318253745));
        listOfLatLng.add(new LatLng(24.981239546680065,55.05459527344463));
        listOfLatLng.add(new LatLng(24.986517227672824,55.05239502998611));
        listOfLatLng.add(new LatLng(24.988904885932133,55.05771226137448));
        listOfLatLng.add(new LatLng(24.988855585254097,55.0577091240746));
        listOfLatLng.add(new LatLng(24.98882002682904,55.057734217250925));
        listOfLatLng.add(new LatLng(24.981821286589813,55.06074200734972));
        listOfLatLng.add(new LatLng(24.982168190131457,55.061141878974404));
        listOfLatLng.add(new LatLng(24.98209526808512,55.061173181159056));




    public void getTextImplementation(LatLng currentGpsPosition,LatLng DestinationPosition){
        int GpsIndex=OldNearestGpsList.indexOf(nearestPositionPoint);
        LatLng cameraPosition=OldNearestGpsList.get(GpsIndex);
        LatLng OldcameraPosition=OldNearestGpsList.get(GpsIndex-1);
        double DistanceInVertex=showDistance(OldcameraPosition,cameraPosition);
        String DitrectionDistance=String.format("%.0f", DistanceInVertex);


        String directionText= String.valueOf(getKeysFromValue(nearestValuesMap,cameraPosition.toString()));
        Log.e("Direction Text","Direction Text " + directionText);

        //Total Distance & Time Calculations
        double resultTotalDistance=showDistance(new LatLng(sourceLat,sourceLng),new LatLng(destLat,destLng));
        String totalDistanceInMts=String.format("%.0f", resultTotalDistance);
        ETACalclator calculator=new ETACalclator();
        double resultTotalTime=calculator.cal_time(resultTotalDistance, maxSpeed);
        resultTotalTime=DecimalUtils.round(resultTotalTime,0);
        int seconds = (int) ((resultTotalTime / 1000) % 60);
        int minutes = (int) ((resultTotalTime / 1000) / 60);

        //Travelled Distance Calculation
        double resultTravelledDistance=showDistance(new LatLng(sourceLat,sourceLng),currentGpsPosition);
        String resultTravelledDistanceInMts=String.format("%.0f", resultTravelledDistance);
        ETACalclator etaCalculator=new ETACalclator();
        double speedKMPH= mphTOkmph(vehicleSpeed);
        double resultTravelledTime=etaCalculator.cal_time(resultTravelledDistance, vehicleSpeed);
        String resultTravelledDistanceMts=String.format("%.0f", resultTravelledDistance);



        double DistanceToTravel=resultTotalDistance-resultTravelledDistance;
        String resultDistanceToTravel=String.format("%.0f", DistanceToTravel);
        ETACalclator calculator1=new ETACalclator();
        double resultRemainingTime=calculator1.cal_time(resultTravelledDistance, vehicleSpeed);
        double RemaininedTime = resultTotalTime-resultTravelledTime;

        //  time.append("Distance").append(totalDistanceInMts +" Meters ").append("\n").append("Total ETA ").append(resultTotalTime +" SEC ").append("\n").append(" Distance To Travel").append(RemaininedTime +"Sec").append("Elapsed Time").append(elapsedTime).append("\n");
        //  sendData(time.toString());
        double presentTime = resultTravelledTime+RemaininedTime;
        double EtaCrossedTime=0.0;
        if(presentTime > resultTotalTime) {
            EtaCrossedTime= presentTime-resultTotalTime;
        }

        tv.setText("Total Distance  : "+ totalDistanceInMts +" MTS" );
        tv1.setText("Total Time To Travel: "+ resultTotalTime +" Sec ");
        tv2.setText("Distance To Travel: "+ resultDistanceToTravel +" KM ");
        tv3.setText("ETA: "+ resultRemainingTime +" KM ");
        tv4.setText("Direction : "+ directionText +" KM ");
     //   tv5.setText("ETA Crossed Alert: "+  EtaCrossedTime);
        /*

        Toast.makeText(getActivity(), ""+ DitrectionDistance+" "+directionText, Toast.LENGTH_SHORT).show();

        LayoutInflater inflater1 = getActivity().getLayoutInflater();

        @SuppressLint("WrongViewCast") View layout = inflater1.inflate(R.layout.custom_toast, (ViewGroup) getActivity().findViewById(R.id.textView_toast));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText("Custom Toast");

// Toast...
        Toast toast = new Toast(getActivity().getApplicationContext());
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

        //Speech implementation
        String data=" in "+ resultDistanceToTravel +" Meters "+ directionText;
        int speechStatus = textToSpeech.speak(data, TextToSpeech.QUEUE_FLUSH, null);
        if (speechStatus == TextToSpeech.ERROR) {
            Log.e("TTS", "Error in converting Text to Speech!");
        }
        /*
        if(time!=null){
            sendTokenRequest();
        }

        Log.e("currentGpsPositiont","currentGpsPosition--------- "+ currentGpsPosition);
        Log.e("currentGpsPositiont","currentGpsPosition--------- "+ DestinationPosition);
        if (currentGpsPosition.equals(DestinationPosition)) {
            Log.e("last Point","last Point--------- "+ "TRUE");
            lastDistance= showDistance(cameraPosition,DestinationPosition);
            Log.e("lastDistance","lastDistance--------- "+ lastDistance );
            if (lastDistance <5) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                mMap.setMyLocationEnabled(false);
                //Speech implementation
                String data1=" Your Destination Reached ";
                int speechStatus1 = textToSpeech.speak(data1, TextToSpeech.QUEUE_FLUSH, null);
                if (speechStatus1 == TextToSpeech.ERROR) {
                    Log.e("TTS", "Error in converting Text to Speech!");
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.yourDialog);
                builder.setTitle("Alert");
                builder.setIcon(R.drawable.car_icon_32);
                builder.setMessage("Destination Reached")
                        .setCancelable(false)
                        .setPositiveButton(" Finish ", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i=new Intent(getActivity(),MainFragment.class);
                                startActivity(i);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }else{
            Log.e("last Point","last Point--------- "+ "FALSE");
        }
    }
 */
}
