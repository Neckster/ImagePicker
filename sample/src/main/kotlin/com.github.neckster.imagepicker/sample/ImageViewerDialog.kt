package com.github.neckster.imagepicker.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.DialogFragment

/**
 * Dialog to View Image
 *
 * @author Dhaval Patel
 * @version 1.6
 * @since 05 January 2019
 *
 * @author Mykola Digtiar
 * @version 1.7.3
 * @since 12 January 2021
 *
 */
class ImageViewerDialog : DialogFragment() {

    companion object {

        private const val EXTRA_IMAGE_RESOURCE = "extra.image_resource"

        fun newInstance(resource: Int) = ImageViewerDialog().apply {
            arguments = Bundle().apply {
                putInt(EXTRA_IMAGE_RESOURCE, resource)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_imageviewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<AppCompatImageView>(R.id.codeImg).run {
            setImageResource(arguments?.getInt(EXTRA_IMAGE_RESOURCE, 0) ?: 0)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.window?.setLayout(width, height)
        }
    }
}
