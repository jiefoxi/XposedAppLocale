package com.flo354.xposed.applocale;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private final List<AppItem> appItemList;

    private final Context context;

    private final PackageManager pm;

    private final SharedPreferences prefs;

    public MyAdapter(Context context) {
        setHasStableIds(true);

        this.context = context;
        this.pm = context.getPackageManager();
        this.prefs = context.getSharedPreferences(Common.PREFS, Context.MODE_WORLD_READABLE);
        this.appItemList = new LinkedList<>();
    }

    public void add(AppItem appItem) {
        appItemList.add(appItem);
        notifyDataSetChanged();
    }

    public void addAll(Collection<? extends AppItem> collection) {
        if (collection != null) {
            appItemList.addAll(collection);
            notifyDataSetChanged();
        }
    }

    public void addAll(AppItem... appItems) {
        addAll(Arrays.asList(appItems));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.app_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final AppItem app = getItem(position);

        holder.appIcon.setImageDrawable(pm.getApplicationIcon(app.getApplicationInfo()));
        holder.appLabel.setText(app.getAppLabel());
        holder.appPackage.setText(app.getPackageInfo().packageName);

        String appLocale = prefs.getString(app.getPackageInfo().packageName, Common.DEFAULT_LOCALE);
        holder.appLocale.setText(appLocale);
        if (appLocale.equals(Common.DEFAULT_LOCALE)) {
            holder.appLocale.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.appLocale.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }

        holder.itemView.setTag(app);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final AppItem app = (AppItem) v.getTag();
                String currentLocale = prefs.getString(app.getPackageInfo().packageName, Common.DEFAULT_LOCALE);
                String[] defaultLocales = new LocaleList(context, "").getLocaleCodes();
                String languages = prefs.getString("languages", "");
                if (!languages.isEmpty()) {
                    if (!currentLocale.contentEquals(Common.DEFAULT_LOCALE) && !languages.contains(currentLocale)) {
                        languages = currentLocale + "," + languages;
                    }

                    languages = Common.DEFAULT_LOCALE + "," + languages;

                    defaultLocales = languages.split(",");
                }

                final String[] locales = defaultLocales;
                int index = Arrays.asList(defaultLocales).indexOf(currentLocale);

                new AlertDialog.Builder(context)
                        .setTitle(pm.getApplicationLabel(app.getApplicationInfo()))
                        .setSingleChoiceItems(locales, index, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                TextView appLocale = v.findViewById(R.id.app_locale);
                                appLocale.setText(locales[which]);

                                String packageName = app.getPackageInfo().packageName;
                                String currentLocale = prefs.getString(packageName, Common.DEFAULT_LOCALE);

                                SharedPreferences.Editor prefsEditor = prefs.edit();
                                if (which == 0) {
                                    if (!currentLocale.contentEquals(Common.DEFAULT_LOCALE)) {
                                        prefsEditor.remove(packageName);
                                        prefsEditor.apply();
                                    }
                                    appLocale.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                                } else {
                                    prefsEditor.putString(packageName, locales[which]);
                                    prefsEditor.commit();
                                    appLocale.setTextColor(context.getResources().getColor(R.color.colorAccent));
                                }
                            }
                        })
                        .setNegativeButton(R.string.choose_languages_cancel, null)
                        .create().show();
            }
        });
    }

    public void clear() {
        appItemList.clear();
        notifyDataSetChanged();
    }

    public List<AppItem> getAll() {
        return new LinkedList<>(appItemList);
    }

    public AppItem getItem(int position) {
        return appItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return appItemList == null? 0 : appItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appLabel;
        TextView appPackage;
        TextView appLocale;

        public ViewHolder(View view) {
            super(view);

            appIcon = view.findViewById(R.id.app_icon);
            appLabel = view.findViewById(R.id.app_label);
            appPackage = view.findViewById(R.id.app_package);
            appLocale = view.findViewById(R.id.app_locale);
        }
    }
}
