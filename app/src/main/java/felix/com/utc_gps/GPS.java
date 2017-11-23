package felix.com.utc_gps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

/**
 * Created by Felix on 2017/11/15.
 */

public abstract class GPS {

    public abstract void onResult(PACKAGE result);

    public abstract void onResultFinish(PACKAGE result);

    public PACKAGE RESULT = new PACKAGE();
    private PACKAGE tmp = new PACKAGE();

    @SuppressLint("MissingPermission")
    public GPS(Activity act) {

        LocationListener ll = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                // TODO Auto-generated method stub
                Log.d("GPS-NMEA", location.getLatitude() + "," + location.getLongitude());

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

                // TODO Auto-generated method stub
                Log.d("GPS-NMEA", s + "");
                //GPS狀態提供，這只有提供者為gps時才會動作
                switch (i) {
                    case LocationProvider.OUT_OF_SERVICE:
                        Log.d("GPS-NMEA", "OUT_OF_SERVICE");
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        Log.d("GPS-NMEA", " TEMPORARILY_UNAVAILABLE");
                        break;
                    case LocationProvider.AVAILABLE:
                        Log.d("GPS-NMEA", "" + s + "");

                        break;
                }

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        GpsStatus.NmeaListener gsnl = new GpsStatus.NmeaListener() {
            @Override
            public void onNmeaReceived(long l, String s) {

                //取得系統時間
                long recvTime = System.currentTimeMillis();

                if (tmp.addNMEA(s, recvTime) == true) {
                    RESULT = tmp;
                    onResult(tmp);
                    tmp = new PACKAGE();
                    onResultFinish(RESULT);
                } else {
                    onResult(tmp);
                }

                Log.d("GPS-NMEA", s);

            }
        };


        LocationManager lm = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, ll);
        lm.addNmeaListener(gsnl);


        List<String> ls = lm.getProviders(true);
        for (String s : ls) {
            Log.d("GPS-Providers", s);
        }
    }

    //http://aprs.gids.nl/nmea/
    public enum GGA {
        Sentence_Identifier,
        UTC_of_Position,
        Latitude,
        N_or_S,
        Longitude,
        E_or_W,
        GPS_quality_indicator,//(0=invalid; 1=GPS fix; 2=Diff. GPS fix)
        Number_of_satellites_in_use,//[not those in view]
        Horizontal_dilution_of_position,
        Geoid,//Antenna altitude above/below mean sea level
        Antenna_height_unit,//Meters
        Geoidal_separation,//(Diff. between WGS-84 earth ellipsoid and mean sea level.  -=geoid is below WGS-84 ellipsoid)
        Units_of_geoidal_separation,//Meters
        Age_in_seconds_since_last_update_from_diff,// reference station
        Diff,// reference station ID#
        Checksum
    }

    public enum RMC {
        Sentence_Identifier,
        UTC_of_position_fix,
        Data_status, //(V=navigation receiver warning)
        Latitude_of_fix,
        N_or_S,
        Longitude_of_fix,
        E_or_W,
        Speed_over_ground_in_knots,
        Track_made_good_in_degrees_True,
        UT_date,
        Magnetic_variation_degrees, //(Easterly var. subtracts from true course)
        Magnetic_E_or_W,
        Checksum
    }

    public final class PACKAGE {

        public String[] GGA = null;
        public String[] RMC = null;
        private long RMC_recvTime;

        public double RMC_offsetTime() {
            if (RMC == null) {
                return 0;
            }
            String datetime = RMC[GPS.RMC.UT_date.ordinal()] + RMC[GPS.RMC.UTC_of_position_fix.ordinal()];
            long RMCstamp = time.calendar2stamp(time.string2calendar(datetime, "ddMMyyHHmmss.SSS", "GMT"));

            return (RMCstamp - RMC_recvTime) / 1000.0;
        }

        public boolean addNMEA(String pkg, long tim) {
            if ((pkg.indexOf("$") != 0) || (pkg.indexOf("*") < 0)) {
                return false;
            }

            if (isValidChecksum(pkg) == false) {
                return false;
            }

            String[] raw = pkg.split(",|\\*|\r\n");
            if (raw[0].indexOf("GGA") == 3) {
                this.GGA = raw;
            } else if (raw[0].indexOf("RMC") == 3) {
                this.RMC = raw;
                this.RMC_recvTime = tim;
            } else {
                return false;
            }

            return isFinish();
        }

        private boolean isValidChecksum(String pkg) {
            int cs_idx = pkg.indexOf("*");
            int cs = Integer.parseInt(pkg.substring(cs_idx + 1, pkg.length()).trim(), 16);
            byte xor = 0;
            byte[] byt = pkg.getBytes();
            for (int i = 1; i < cs_idx; i++) {
                xor = (byte) (xor ^ byt[i]);
            }
            if (xor == cs) {
                return true;
            } else {
                return false;
            }
        }

        private boolean isFinish() {
            if (GGA == null || RMC == null) {
                return false;
            }
            return true;
        }

    }


}
