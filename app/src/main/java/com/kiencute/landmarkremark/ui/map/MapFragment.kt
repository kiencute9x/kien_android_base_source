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
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kiencute.landmarkremark.R
import com.kiencute.landmarkremark.data.entities.Note
import com.kiencute.landmarkremark.databinding.FragmentMapBinding
import com.kiencute.landmarkremark.utils.MAP_NOTE_LAYER
import com.kiencute.landmarkremark.utils.MAP_NOTE_SOURCE
import com.kiencute.landmarkremark.utils.Resource
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.RenderedQueryGeometry
import com.mapbox.maps.RenderedQueryOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.generated.Expression.Companion.interpolate
import com.mapbox.maps.extension.style.image.image
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.OnMapLongClickListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapLongClickListener
import com.mapbox.maps.plugin.locationcomponent.location
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MapFragment : Fragment(), OnMapLongClickListener, OnMapClickListener {
    private var _binding: FragmentMapBinding? = null
    private lateinit var mapView: MapView
    private val binding get() = _binding!!
    private val viewModel: MapViewModel by viewModels()
    private var myLocation: Point? = null

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
        setupObservers()
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
            initLocationComponent()
            viewModel.loadNotesForUser(456)
            mapView.getMapboxMap().addOnMapLongClickListener(this)
            mapView.getMapboxMap().addOnMapClickListener(this)

        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.noteData.collectLatest { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            resource.data?.let { addNotesToMap(it) }
                            binding.progressBar.visibility = View.GONE
                        }

                        is Resource.Err -> {
                            Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT)
                                .show()
                            binding.progressBar.visibility = View.GONE
                        }

                        is Resource.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
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

    private fun addNotesToMap(data: List<Note>) {
        val mapboxMap = mapView.getMapboxMap()
        val features = mutableListOf<Feature>()
        for (note in data) {
            val point = Point.fromLngLat(note.longitude, note.latitude)
            val feature = Feature.fromGeometry(point)
                .also {
                    it.addStringProperty("noteDescription", note.description)
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

    private fun queryMap(point: Point) {
        val screenCoordinate = mapView.getMapboxMap().pixelForCoordinate(point)
        mapView.getMapboxMap().queryRenderedFeatures(
            RenderedQueryGeometry(screenCoordinate), RenderedQueryOptions(
                listOf(
                    MAP_NOTE_LAYER
                ), null
            )
        ) { result ->
            result.value?.let { featureList ->
                if (featureList.isNotEmpty()) {
                    val feature = featureList.first()
                    showBottomSheet(feature.feature.geometry() as Point)
                }
            }
        }
    }


    private fun showBottomSheet(point: Point) {
        val bottomSheetDialog = context?.let { BottomSheetDialog(it) }
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet, null)
        bottomSheetDialog?.setContentView(view)
        val coordinatesText = view.findViewById<TextView>(R.id.coordinator)
        coordinatesText.text = "Lat: ${point.latitude()}, Lon: ${point.longitude()}"
        bottomSheetDialog?.show()
    }


    override fun onMapLongClick(point: Point): Boolean {
        showBottomSheet(point)
        return false
    }

    override fun onMapClick(point: Point): Boolean {
        queryMap(point)
        return true
    }


}