package azuredrop.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2017/3/31.
 */

public class NerdLauncherFragment extends ListFragment {
    private static final String TAG = "NerdLauncherFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);

        Log.i(TAG, "I've found " + activities.size() + " activities.");

        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo o1, ResolveInfo o2) {
                PackageManager pm = getActivity().getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(
                        o1.loadLabel(pm).toString(),
                        o2.loadLabel(pm).toString());
            }
        });

        ArrayAdapter<ResolveInfo> adapter = new ArrayAdapter<ResolveInfo>(
                getActivity(), android.R.layout.simple_list_item_1, activities) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item, null);
                }

                ResolveInfo ri = getItem(position);
                PackageManager pm = getActivity().getPackageManager();

                // APP icon
                if (ri.loadIcon(pm) != null) {
                    ImageView iv = (ImageView) convertView.findViewById(R.id.item_appicon);
                    iv.setImageDrawable(ri.loadIcon(pm));
                }

                // APP name
                TextView tv = (TextView) convertView.findViewById(R.id.item_appname);
                tv.setText(ri.loadLabel(pm));

                return convertView;
            }
        };

        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ResolveInfo ri = (ResolveInfo)l.getAdapter().getItem(position);
        ActivityInfo ai = ri.activityInfo;
        if (ai == null) {
            return;
        }

        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setClassName(ai.applicationInfo.packageName, ai.name);
        // add a flag, so activity can start in new task
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(i);
    }
}
