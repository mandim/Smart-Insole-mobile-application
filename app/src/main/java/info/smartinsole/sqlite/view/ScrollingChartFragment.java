package info.smartinsole.sqlite.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import info.smartinsole.sqlite.anan.AAChartCore.AAChartCoreLib.AAChartCreator.AAChartModel;
import info.smartinsole.sqlite.anan.AAChartCore.AAChartCoreLib.AAChartCreator.AAChartView;
import info.smartinsole.sqlite.anan.AAChartCore.AAChartCoreLib.AAChartCreator.AASeriesElement;

import info.smartinsole.sqlite.R;
import info.smartinsole.sqlite.anan.AAChartCore.AAChartCoreLib.AAChartEnum.AAChartType;
import info.smartinsole.sqlite.anan.AAChartCore.AAChartCoreLib.AAChartEnum.AAChartZoomType;
import info.smartinsole.sqlite.anan.AAChartCore.AAChartCoreLib.AAOptionsModel.AAScrollablePlotArea;

import static info.smartinsole.sqlite.anan.AAChartCore.AAChartCoreLib.AAChartEnum.AAChartAnimationType.Bounce;

/**
 * This Fragment shows 3 charts
 * Default chart => line (can also show 5 more charts)
 */
public class ScrollingChartFragment extends Fragment {

    private AAChartView aaChartViewAcc, aaChartViewGyro, aaChartViewMagn;
    private AAChartModel aaChartModelAcc, aaChartModelGyro, aaChartModelMagn;
    private Object[] objAccX, objAccY, objAccZ;
    private Object[] objGyroR, objGyroP, objGyroY;
    private Object[] objMagnX, objMagnY, objMagnZ;

    private final String[] line = {"spline", "column", "pie", "polygon", "waterfall", "line"};
    private static final String ARG_CHART_TYPE = "ARG_CHART_TYPE";
    private int type;

    public ScrollingChartFragment(){
        // Required empty public constructor
    }

    public static ScrollingChartFragment newInstance(int type) {
        ScrollingChartFragment scrollingChartFragment = new ScrollingChartFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_CHART_TYPE, type);
        scrollingChartFragment.setArguments(bundle);
        return scrollingChartFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        type = getArguments().getInt(ARG_CHART_TYPE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_line_chart, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.stage = 0;

        createAccChart(view);
        createGyroChart(view);
        createMagnChart(view);

        return view;
    }

    /**
     * Create the 3 Line Charts
     * @param view the inflated view
     */

    public void createAccChart(View view){
        aaChartViewAcc = view.findViewById(R.id.AAChartViewAcc);

        objAccX = new Object[]{};
        objAccY = new Object[]{};
        objAccZ = new Object[]{};

        aaChartModelAcc = new AAChartModel()
                //.chartType(AAChartType.Spline)
                .chartType(line[type])
                .zoomType(AAChartZoomType.X)
                .animationType(Bounce)
                .scrollablePlotArea(
                        new AAScrollablePlotArea()
                                .minWidth(500)
                                .scrollPositionX(0f)
                )
                .markerRadius(0f)
                .title("Accelerometer Data")
                .subtitle("Virtual Data")
                .backgroundColor("#ffffff")
                //.categories(new String[]{})
                .dataLabelsEnabled(false)
                .yAxisGridLineWidth(0f)
                .yAxisTitle("Accl Values")
                .series(new AASeriesElement[]{
                        new AASeriesElement()
                                .name("ACCL_X")
                                .data(objAccX),
                        new AASeriesElement()
                                .name("ACCL_Y")
                                .data(objAccY),
                        new AASeriesElement()
                                .name("ACCL_Z")
                                .data(objAccZ),
                });

        //The chart view object calls the instance object of AAChartModel and draws the final graphic
        aaChartViewAcc.aa_drawChartWithChartModel(aaChartModelAcc);
    }

    public void createGyroChart(View view){
        aaChartViewGyro = view.findViewById(R.id.AAChartViewGyro);

        objGyroR = new Object[]{};
        objGyroP = new Object[]{};
        objGyroY = new Object[]{};

        aaChartModelGyro = new AAChartModel()
                .chartType(line[type])
                .zoomType(AAChartZoomType.X)
                .animationType(Bounce)
                .scrollablePlotArea(
                        new AAScrollablePlotArea()
                                .minWidth(500)
                                .scrollPositionX(0f)
                )
                .markerRadius(0f)
                .title("Gyroscope Data")
                .subtitle("Virtual Data")
                .backgroundColor("#ffffff")
                //.categories(new String[]{})
                .dataLabelsEnabled(false)
                .yAxisGridLineWidth(0f)
                .series(new AASeriesElement[]{
                        new AASeriesElement()
                                .name("GYRO_R")
                                .data(objGyroR),
                        new AASeriesElement()
                                .name("GYRO_P")
                                .data(objGyroP),
                        new AASeriesElement()
                                .name("GYRO_Y")
                                .data(objGyroY),
                });

        //The chart view object calls the instance object of AAChartModel and draws the final graphic
        aaChartViewGyro.aa_drawChartWithChartModel(aaChartModelGyro);
    }

    public void createMagnChart(View view){
        aaChartViewMagn = view.findViewById(R.id.AAChartViewMagn);

        objMagnX = new Object[]{};
        objMagnY = new Object[]{};
        objMagnZ = new Object[]{};

        aaChartModelMagn = new AAChartModel()
                .chartType(line[type])
                .zoomType(AAChartZoomType.X)
                .animationType(Bounce)
                .scrollablePlotArea(
                        new AAScrollablePlotArea()
                                .minWidth(500)
                                .scrollPositionX(0f)
                )
                .markerRadius(0f)
                .title("Magnetometer Data")
                .subtitle("Virtual Data")
                .backgroundColor("#ffffff")
                //.categories(new String[]{})
                .dataLabelsEnabled(false)
                .yAxisGridLineWidth(0f)
                .series(new AASeriesElement[]{
                        new AASeriesElement()
                                .name("MAGN_X")
                                .data(objMagnX),
                        new AASeriesElement()
                                .name("MAGN_Y")
                                .data(objMagnY),
                        new AASeriesElement()
                                .name("MAGN_Z")
                                .data(objMagnZ),
                });

        //The chart view object calls the instance object of AAChartModel and draws the final graphic
        aaChartViewMagn.aa_drawChartWithChartModel(aaChartModelMagn);
    }

    /**
     * Add random data to Line Charts
     * @params AcclX, GyroR, MagnX -> Data for the first Line
     * @params AcclY, GyroP, MagnY -> Data for the second Line
     * @params AcclZ, GyroY, MagnZ -> Data for the third Line
     */

    public void insertDataToAcc(double AcclX, double AcclY, double AcclZ){
        Object[] newObjAccX = new Object[objAccX.length+1];
        Object[] newObjAccY = new Object[objAccY.length+1];
        Object[] newObjAccZ = new Object[objAccZ.length+1];

        System.arraycopy(objAccX, 0, newObjAccX, 0, objAccX.length);
        System.arraycopy(objAccY, 0, newObjAccY, 0, objAccY.length);
        System.arraycopy(objAccZ, 0, newObjAccZ, 0, objAccZ.length);

        newObjAccX[objAccX.length] = AcclX;
        objAccX = newObjAccX;
        newObjAccY[objAccY.length] = AcclY;
        objAccY = newObjAccY;
        newObjAccZ[objAccZ.length] = AcclZ;
        objAccZ = newObjAccZ;

        aaChartViewAcc.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(new AASeriesElement[]{
                new AASeriesElement()
                        .name("ACCL_X")
                        .data(newObjAccX),
                new AASeriesElement()
                        .name("ACCL_Y")
                        .data(newObjAccY),
                new AASeriesElement()
                        .name("ACCL_Z")
                        .data(newObjAccZ),
        });
    }

    public void insertDataToGyro(double GyroR, double GyroP, double GyroY){
        Object[] newObjGyroR = new Object[objGyroR.length+1];
        Object[] newObjGyroP = new Object[objGyroP.length+1];
        Object[] newObjGyroY = new Object[objGyroY.length+1];

        System.arraycopy(objGyroR, 0, newObjGyroR, 0, objGyroR.length);
        System.arraycopy(objGyroP, 0, newObjGyroP, 0, objGyroP.length);
        System.arraycopy(objGyroY, 0, newObjGyroY, 0, objGyroY.length);

        newObjGyroR[objGyroR.length] = GyroR;
        objGyroR = newObjGyroR;
        newObjGyroP[objGyroP.length] = GyroP;
        objGyroP = newObjGyroP;
        newObjGyroY[objGyroY.length] = GyroY;
        objGyroY = newObjGyroY;

        aaChartViewGyro.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(new AASeriesElement[]{
                new AASeriesElement()
                        .name("GYRO_R")
                        .data(newObjGyroR),
                new AASeriesElement()
                        .name("GYRO_P")
                        .data(newObjGyroP),
                new AASeriesElement()
                        .name("GYRO_Y")
                        .data(newObjGyroY),
        });
    }

    public void insertDataToMagn(double MagnX, double MagnY, double MagnZ){
        Object[] newObjMagnX = new Object[objMagnX.length+1];
        Object[] newObjMagnY = new Object[objMagnY.length+1];
        Object[] newObjMagnZ = new Object[objMagnZ.length+1];

        System.arraycopy(objMagnX, 0, newObjMagnX, 0, objMagnX.length);
        System.arraycopy(objMagnY, 0, newObjMagnY, 0, objMagnY.length);
        System.arraycopy(objMagnZ, 0, newObjMagnZ, 0, objMagnZ.length);

        newObjMagnX[objMagnX.length] = MagnX;
        objMagnX = newObjMagnX;
        newObjMagnY[objMagnY.length] = MagnY;
        objMagnY = newObjMagnY;
        newObjMagnZ[objMagnZ.length] = MagnZ;
        objMagnZ = newObjMagnZ;

        aaChartViewMagn.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(new AASeriesElement[]{
                new AASeriesElement()
                        .name("MAGN_X")
                        .data(newObjMagnX),
                new AASeriesElement()
                        .name("MAGN_Y")
                        .data(newObjMagnY),
                new AASeriesElement()
                        .name("MAGN_Z")
                        .data(newObjMagnZ),
        });
    }

    /**
     * Upload data from DB to Line Charts
     * @param objAccX
     * @param objAccY
     * @param objAccZ
     * @param objGyroR
     * @param objGyroP
     * @param objGyroY
     * @param objMagnX
     * @param objMagnY
     * @param objMagnZ
     */
    public void uploadData(Object[] objAccX, Object[] objAccY, Object[] objAccZ,
                           Object[] objGyroR, Object[] objGyroP, Object[] objGyroY,
                           Object[] objMagnX, Object[] objMagnY, Object[] objMagnZ){

        this.objAccX = objAccX; this.objAccY = objAccY; this.objAccZ = objAccZ;
        this.objGyroR = objGyroR; this.objGyroP = objGyroP; this.objGyroY = objGyroY;
        this.objMagnX = objMagnX; this.objMagnY = objMagnY; this.objMagnZ = objMagnZ;

        aaChartViewAcc.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(new AASeriesElement[]{
                new AASeriesElement()
                        .name("ACCL_X")
                        .data(objAccX),
                new AASeriesElement()
                        .name("ACCL_Y")
                        .data(objAccY),
                new AASeriesElement()
                        .name("ACCL_Z")
                        .data(objAccZ),
        });

        aaChartViewGyro.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(new AASeriesElement[]{
                new AASeriesElement()
                        .name("GYRO_R")
                        .data(objGyroR),
                new AASeriesElement()
                        .name("GYRO_P")
                        .data(objGyroP),
                new AASeriesElement()
                        .name("GYRO_Y")
                        .data(objGyroY),
        });

        aaChartViewMagn.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(new AASeriesElement[]{
                new AASeriesElement()
                        .name("MAGN_X")
                        .data(objMagnX),
                new AASeriesElement()
                        .name("MAGN_Y")
                        .data(objMagnY),
                new AASeriesElement()
                        .name("MAGN_Z")
                        .data(objMagnZ),
        });
    }

}
