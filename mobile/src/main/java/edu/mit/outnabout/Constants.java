/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.mit.outnabout;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Constants used in this sample.
 */
public final class Constants {

    private Constants() {
    }

    public static final String PACKAGE_NAME = "edu.mit.outnabout";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";

    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 50; // in meters

    /**
     * Map for storing information about airports in the San Francisco bay area.
     */
    public static final HashMap<String, LatLng> CAMBRIDGE_LANDMARKS = new HashMap<String, LatLng>();
    static {
        // WILG
        CAMBRIDGE_LANDMARKS.put("WILG", new LatLng(42.363023, -71.099263));

        // MIT Museum
        CAMBRIDGE_LANDMARKS.put("MIT Museum", new LatLng(42.362095, -71.097568));

        // 77 Mass Ave
        CAMBRIDGE_LANDMARKS.put("77 Mass Ave", new LatLng(42.359178, -71.093137));

        // Cambridge City Hall
        CAMBRIDGE_LANDMARKS.put("Cambridge City Hall", new LatLng(42.367002, -71.105840));

        // Stata Center
        CAMBRIDGE_LANDMARKS.put("Stata Center", new LatLng(42.361667, -71.090680));

        // Student Center
        CAMBRIDGE_LANDMARKS.put("Student Center", new LatLng(42.359043, -71.094746));

        // Lobby 10
        CAMBRIDGE_LANDMARKS.put("Lobby 10", new LatLng(42.359558, -71.091967));

        // Flour Bakery
        CAMBRIDGE_LANDMARKS.put("Flour Bakery", new LatLng(42.361033, -71.096784));
    }
}
