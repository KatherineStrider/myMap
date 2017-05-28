package com.example.kate.mymaps;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.List;

/**
 * Created by Kate on 28.05.2017.
 */

public class AppWidget extends AppWidgetProvider {

    private AppWidgetManager widgetManager = null;
    int[] widgetIds = null;
    private Context context = null;
    private LocationManager locationManager;
    private Location location;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        this.context = context;

        widgetManager = appWidgetManager;

        widgetIds = appWidgetIds.clone();

        locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        updateWidgets(location);

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


    private void updateWidgets(Location location) {

        String address = getAddressStr(location);

        for (int i = 0; i < widgetIds.length; i++) {

            RemoteViews v = new RemoteViews(context.getPackageName(), R.layout.app_widget);
            v.setTextViewText(R.id.textGeo, address);

            widgetManager.updateAppWidget(widgetIds[i], v);
        }

    }

    private String getAddressStr(Location location) {

        String str = "";

        Geocoder geo = new Geocoder(context);

        try {

            List<Address> aList = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (aList.size() > 0) {

                Address a = aList.get(0);
                int maxAddrLine = a.getMaxAddressLineIndex();
                if (maxAddrLine >= 0) {
                    str = a.getAddressLine(maxAddrLine);
                    if (!str.isEmpty())
                        str += ", ";
                }
                str += a.getCountryName() + ", " + a.getAdminArea() + ", " + a.getThoroughfare() + " "
                        + a.getSubThoroughfare();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }

        return str;

    }


    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
}
