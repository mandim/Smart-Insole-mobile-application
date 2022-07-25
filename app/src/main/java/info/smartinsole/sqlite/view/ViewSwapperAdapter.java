package info.smartinsole.sqlite.view;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.buffer.adaptablebottomnavigation.adapter.FragmentStateAdapter;

/**
 * Adapter for Bottom Navigation Bar
 */
public class ViewSwapperAdapter extends FragmentStateAdapter {

    private static final int INDEX_HEATMAP = 0;
    private static final int INDEX_LINECHART = 1;
    private static final int INDEX_TEST = 2;

    public ViewSwapperAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case INDEX_HEATMAP:
                return HeatMapFragment.newInstance();
            case INDEX_LINECHART:
                // Possibility to add another number for a different type of chart
                // For more: ScrollingChartFragment.java -> String[] line
                return ScrollingChartFragment.newInstance(0);
            case INDEX_TEST:
                return TestFragment.newInstance();
        }
        return HeatMapFragment.newInstance();
    }

    @Override
    public int getCount() {
        return 3;
    }
}