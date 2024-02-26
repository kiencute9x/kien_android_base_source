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
import android.widget.Button
import android.widget.EditText
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
import com.kiencute.landmarkremark.utils.USER_ID
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
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
            viewModel.loadNotesForUser(USER_ID)
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
    private var isLocationCentered = false


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
            if (!isLocationCentered) {
                mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
                isLocationCentered = true
            }
        }
    }

    private fun addNotesToMap(data: List<Note>) {
        val mapboxMap = mapView.getMapboxMap()
        val features = mutableListOf<Feature>()
        for (note in data) {
            val point = Point.fromLngLat(note.longitude, note.latitude)
            val feature = Feature.fromGeometry(point).apply {
                addStringProperty("noteDescription", note.description)
                addStringProperty("noteTitle", note.title)
                addNumberProperty("userId", note.userId) // Ví dụ thêm userId như một property
                // Bạn có thể thêm bất kỳ property nào khác từ Note mà bạn muốn hiển thị hoặc sử dụng
            }
            features.add(feature)
        }
        val featureCollection = FeatureCollection.fromFeatures(features)

        mapboxMap.loadStyle(styleExtension = style(styleUri = Style.MAPBOX_STREETS) {
            // Add image to map
            +image("icon-note") {
                bitmap(drawableToBitmap(ContextCompat.getDrawable(requireContext(), R.drawable.icon_note)!!)!!)
            }
            // Create map source
            +geoJsonSource(MAP_NOTE_SOURCE) {
                featureCollection(featureCollection)
            }
            // Create map layer
            +symbolLayer(MAP_NOTE_LAYER, MAP_NOTE_SOURCE) {
                iconImage("icon-note")
                iconAnchor(IconAnchor.BOTTOM)
                iconAllowOverlap(true)
            }
        })
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
                    showInfoBottomSheet(feature.feature)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showBottomSheet(point: Point) {
        val bottomSheetDialog = context?.let { BottomSheetDialog(it) }
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet, null)
        bottomSheetDialog?.setContentView(view)

        val titleEditText = view.findViewById<EditText>(R.id.noteTitle)
        val descriptionEditText = view.findViewById<EditText>(R.id.note_description)
        val saveButton = view.findViewById<Button>(R.id.saveNoteButton)
        val coordinatesText = view.findViewById<TextView>(R.id.coordinator)
        coordinatesText.text = "Lat: ${point.latitude()}, Lon: ${point.longitude()}"

        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()

            val note = Note(
                noteId = 0,
                userId = USER_ID,
                latitude = point.latitude(),
                longitude = point.longitude(),
                title = title,
                description = description
            )
            viewModel.insertNote(note)
            bottomSheetDialog?.dismiss()
        }

        bottomSheetDialog?.show()
    }

    private fun showInfoBottomSheet(feature: Feature) {
        val bottomSheetDialog = context?.let { BottomSheetDialog(it) }
        val view = layoutInflater.inflate(R.layout.layout_bottomsheet2, null)
        bottomSheetDialog?.setContentView(view)

        val titleTextView = view.findViewById<TextView>(R.id.tv_title)
        val descriptionTextView = view.findViewById<TextView>(R.id.tv_des)
        val coordinatesTextView = view.findViewById<TextView>(R.id.tv_coor)
        val tvUserID = view.findViewById<TextView>(R.id.tv_userId)

        tvUserID.text = "User ID: " +  feature.getStringProperty("userId")
        titleTextView.text = "Note Title: " +  feature.getStringProperty("noteTitle")
        descriptionTextView.text = "Note Description: " +  feature.getStringProperty("noteDescription")
        val point = feature.geometry() as? Point
        point?.let {
            coordinatesTextView.text = "Coordinate: " +  "Lat: ${it.latitude()}, Lon: ${it.longitude()}"
        }


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