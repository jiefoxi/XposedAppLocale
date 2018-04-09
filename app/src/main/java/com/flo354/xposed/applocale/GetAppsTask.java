package com.flo354.xposed.applocale;

import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by florian on 3/2/18.
 */

public class GetAppsTask extends AsyncTask<Void, Integer, List<AppItem>> {

    private final PackageManager pm;

    private final ProgressDialog mProgressDialog;

    private final AsyncResponse response;

    public GetAppsTask(MainActivity context, AsyncResponse response) {
        pm = context.getPackageManager();
        this.response = response;

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMessage(context.getString(R.string.loading_apps));
        mProgressDialog.setCancelable(false);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog.show();
    }

    @Override
    protected List<AppItem> doInBackground(Void... params) {
        List<AppItem> appItems = new ArrayList<>();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        mProgressDialog.setMax(packages.size());

        int i = 1;
        for (PackageInfo packageInfo : pm.getInstalledPackages(0)) {
            if (packageInfo.applicationInfo.enabled) {
                appItems.add(new AppItem(packageInfo, pm.getApplicationLabel(packageInfo.applicationInfo).toString()));
            }

            publishProgress(i++);
        }

        return appItems;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mProgressDialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(List<AppItem> appItems) {
        super.onPostExecute(appItems);

        Collections.sort(appItems, new Comparator<AppItem>() {
            @Override
            public int compare(AppItem lhs, AppItem rhs) {
                return lhs.getAppLabel().compareToIgnoreCase(rhs.getAppLabel());
            }
        });

        response.processFinish(appItems);
        mProgressDialog.dismiss();
    }

    public interface AsyncResponse {
        void processFinish(List<AppItem> appItems);
    }
}