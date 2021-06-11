package com.cartoonhero.source.enrollment_android.scene.qrCode

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.cartoonhero.source.enrollment_android.R
import com.cartoonhero.source.props.Singleton
import kotlinx.android.synthetic.main.fragment_qrcode.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class QRCodeFragment:Fragment() {
    private val scenario = QRCodeScenario()
    private var scanBtnTitle = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_qrcode,
            container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scenario.toBeCollectBitmap {
            qrcode_imageView.setImageBitmap(it)
        }
        when(Singleton.instance.currentRole) {
            "Visitor" -> {
                scan_button.visibility = View.GONE
            }
            "Visited_Unit" -> {
                scan_button.setOnClickListener {
                    openPhotoGallery()
                }
            }
        }
    }
    /* --------------------------------------------------------------------- */
    // MARK: - Private
    private fun openPhotoGallery() {
        val intent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }
    /* --------------------------------------------------------------------- */
    // MARK: - Listeners
    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            qrcode_imageView.setImageURI(data?.data)
            /*
            val bitmap: Bitmap = BitmapFactory.decodeStream(data?.data?.let {
                context?.contentResolver?.openInputStream(
                    it
                )
            })*/
        }
    }

}