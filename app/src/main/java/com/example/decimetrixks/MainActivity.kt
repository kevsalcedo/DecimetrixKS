package com.example.decimetrixks

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.gestures.gestures
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    var mapView: MapView? = null

    // Create object for marker
    var annotationApi: AnnotationPlugin? = null
    lateinit var annotationConfig: AnnotationConfig
    var layerIDD = "map_annotation" // hard code value

    var pointAnnotationManager: PointAnnotationManager? = null

    // Marker list for displaying multiple marker
    var markerList: ArrayList<PointAnnotationOptions> = ArrayList()

    // Just for testing
    var latitudeList : ArrayList<Double> = ArrayList()
    var longitudeList : ArrayList<Double> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)

        createLatLongForMarker()

        mapView?.getMapboxMap()!!.loadStyleUri(
            Style.MAPBOX_STREETS,
            object : Style.OnStyleLoaded{
                override fun onStyleLoaded(style: Style) {
                    zoomCamera()

                    // Adding the marker
                    annotationApi = mapView?.annotations
                    annotationConfig = AnnotationConfig(
                        layerId = layerIDD
                    )
                    pointAnnotationManager = annotationApi?.createPointAnnotationManager(annotationConfig)!!

                    createMarkerOnMap()

                    try{
                        mapView!!.gestures.pitchEnabled = true
                    }catch (e : Exception){
                        e.printStackTrace()
                    }
                }
            }
        )
    }

    private fun zoomCamera(){
        mapView!!.getMapboxMap().setCamera(
            CameraOptions.Builder().center(Point.fromLngLat(-74.796387, 10.963889))
                .zoom(11.0)
                .build()
        )
    }

    private fun createLatLongForMarker(){
        latitudeList.add(10.963889)
        longitudeList.add(-74.796387)

        latitudeList.add(6.230833)
        longitudeList.add(-75.590553)

        latitudeList.add(4.624335)
        longitudeList.add(-74.063644)

    }

    private fun createMarkerOnMap(){

        clearAnnotation();

        // It will work when we create marker
        pointAnnotationManager?.addClickListener(OnPointAnnotationClickListener {
                annotation: PointAnnotation ->
            onMarkerItemClick(annotation)
            true
        })

        markerList =  ArrayList();
        var bitmpa = convertDrawableToBitmap(AppCompatResources.getDrawable(this, R.drawable.ic_location_pin))
        for (i in 0 until  3){

            var jsonObject = JSONObject();
            jsonObject.put("somekey",i);
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(longitudeList.get(i),latitudeList.get(i)))
                .withData(Gson().fromJson(jsonObject.toString(), JsonElement::class.java))
                .withIconImage(bitmpa!!)
            markerList.add(pointAnnotationOptions);
        }

        pointAnnotationManager?.create(markerList)

    }

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            // copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    private fun onMarkerItemClick(marker: PointAnnotation) {

        val jsonElement: JsonElement? = marker.getData()

        AlertDialog.Builder(this)
            .setTitle("Marker Click")
            .setMessage("Here is the value-- "+jsonElement.toString())
            .setPositiveButton(
                "OK"
            ) { dialog, whichButton ->
                dialog.dismiss()
            }
            .setNegativeButton(
                "Cancel"
            ) { dialog, which -> dialog.dismiss() }.show()

    }

    private fun clearAnnotation(){
        markerList = ArrayList();
        pointAnnotationManager?.deleteAll()
    }


}