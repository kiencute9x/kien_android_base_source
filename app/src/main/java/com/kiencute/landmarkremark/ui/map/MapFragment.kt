package com.kiencute.landmarkremark.ui.map

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kiencute.landmarkremark.R
import com.kiencute.landmarkremark.data.entities.Note
import com.kiencute.landmarkremark.databinding.FragmentMapBinding
import com.kiencute.landmarkremark.utils.MAP_NOTE_LAYER
import com.kiencute.landmarkremark.utils.MAP_NOTE_SOURCE
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.generated.Expression.Companion.interpolate
import com.mapbox.maps.extension.style.image.image
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.gestures.OnMapLongClickListener
import com.mapbox.maps.plugin.locationcomponent.location
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MapFragment : Fragment(), OnMapLongClickListener {
    private var _binding: FragmentMapBinding? = null
    private lateinit var mapView: MapView
    private val binding get() = _binding!!
    private val viewModel: MapViewModel by viewModels()
    private var myLocation: Point? = null;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = binding.mapView
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS){
            initLocationComponent()
            addNotesToMap(mapView)
        }
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location

        locationComponentPlugin.updateSettings {
            enabled = true
            locationPuck = LocationPuck2D(
                bearingImage = context?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.mapbox_user_puck_icon
                    )
                },
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener {
            myLocation = it
        }
    }

    private fun addNotesToMap(mapView: MapView) {
        val mapboxMap = mapView.getMapboxMap()
        val noteList = listOf(
            Note(1, 1,10.776889, 106.700806, "Hồ Chí Minh"),
            Note(2 ,1, 21.028511, 105.804817, "Hà Nội"),
            Note(3, 2,16.047079, 108.206230, "Đà Nẵng"),
            Note(4, 2,10.045162, 105.746857, "Cần Thơ"),
            Note(5, 3,20.844912, 106.688084, "Hải Phòng"),
            Note(6, 2,13.088186, 109.092876, "Quy Nhơn"),
            Note(7, 3,12.238791, 109.196749, "Nha Trang"),
            Note(8, 3,10.354108, 107.084259, "Vũng Tàu"),
            Note(9, 2,16.463712, 107.590863, "Huế"),
            Note(10, 2,21.594220, 105.848170, "Thái Nguyên")
        )


        val features = mutableListOf<Feature>()
        for (note in noteList) {
            val point = Point.fromLngLat(note.longitude, note.latitude)
            val feature = Feature.fromGeometry(point)
                .also {
                    it.addStringProperty("noteDescription", note.note)
                }
            features.add(feature)
        }
        val featureCollection = FeatureCollection.fromFeatures(features)


        mapboxMap.loadStyle(styleExtension = style(styleUri = Style.MAPBOX_STREETS) {
            //add image to map
            +image("icon-note") {
                bitmap(drawableToBitmap(resources.getDrawable(R.drawable.icon_note))!!)
            }
            // create map source
            +geoJsonSource(MAP_NOTE_SOURCE) {
                featureCollection(featureCollection)
            }
            // create map layer
            +symbolLayer(MAP_NOTE_LAYER, MAP_NOTE_SOURCE) {
                iconImage("icon-note")
                iconAnchor(IconAnchor.BOTTOM)
                iconAllowOverlap(true)
            }
        }) {
            // Các layer và source đã được thêm vào bản đồ
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap? = null
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }
        bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            )
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("Lifecycle")

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    @SuppressLint("Lifecycle")
    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    @SuppressLint("Lifecycle")

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    @SuppressLint("Lifecycle")

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapLongClick(point: Point): Boolean {
        return false
    }

}