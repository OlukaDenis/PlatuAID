package com.mcdenny.coronavirusapp.view.ui.maps;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mcdenny.coronavirusapp.R;
import com.mcdenny.coronavirusapp.utils.Config;
import com.mcdenny.coronavirusapp.view.ui.symptom_form.SymptomFormActivity;

import static com.mcdenny.coronavirusapp.utils.Config.JOS_HOSPITAL;
import static com.mcdenny.coronavirusapp.utils.Config.PLATEAU_SPECIALIST;
import static com.mcdenny.coronavirusapp.utils.Config.VETINARY_INSTITUTE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener,
        SeekBar.OnSeekBarChangeListener,
        OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker plateau, josHospital, vetinayInstitute;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_maps, container, false);

        //Init firebase analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        mFirebaseAnalytics.setCurrentScreen(getActivity(), this.getClass().getSimpleName(), this.getClass().getSimpleName());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        CardView btnSymptom = root.findViewById(R.id.request_test);
        btnSymptom.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SymptomFormActivity.class));

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Open Symptom Form");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
        });

        return root;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation(mMap);

        // Hide the zoom controls as the button panel will cover it.
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Set listeners for marker events.  See the bottom of this class for their behavior.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerDragListener(this);

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        mMap.setContentDescription("Map with lots of markers.");

        // Uses a colored icon.
        josHospital = mMap.addMarker(new MarkerOptions()
                .position(JOS_HOSPITAL)
                .title("Jos University Teaching Hospital")
                .snippet("Testing Center 2")
                .visible(true)
                .zIndex(4));

        // Uses a colored icon.
        vetinayInstitute = mMap.addMarker(new MarkerOptions()
                .position(VETINARY_INSTITUTE)
                .title("National Veterinary Research Institute")
                .snippet("Main testing center")
                .visible(true)
                .zIndex(10)
                .infoWindowAnchor(0.5f, 0.5f));

        plateau = mMap.addMarker(new MarkerOptions()
                .position(PLATEAU_SPECIALIST)
                .title("Plateau specialist Hospital")
                .snippet("Testing Center 1")
                .visible(true)
                .zIndex(4));

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(VETINARY_INSTITUTE)
                .include(JOS_HOSPITAL)
                .include(PLATEAU_SPECIALIST)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
    }

    /**
     * Demonstrates converting a {@link Drawable} to a {@link BitmapDescriptor},
     * for use as a marker icon.
     */
    private BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    /**
     * Checks for location permissions, and requests them if they are missing.
     * Otherwise, enables the location layer.
     */
    private void enableMyLocation(GoogleMap map) {
        if (Config.checkLocationPermissions(getContext())){
            map.setMyLocationEnabled(true);
        } else {
            Config.requestLocationPermissions(getActivity());
        }
    }
}
