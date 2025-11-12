package com.gws.auto.mobile.android.ui.settings.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.gws.auto.mobile.android.R
import java.io.InputStream

class LicensesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_licenses, container, false)
        val licensesText = view.findViewById<TextView>(R.id.licenses_text)
        try {
            val inputStream: InputStream = requireContext().resources.openRawResource(R.raw.third_party_licenses)
            val text = inputStream.bufferedReader().use { it.readText() }
            licensesText.text = text
        } catch (e: Exception) {
            licensesText.text = "Failed to load licenses."
        }
        return view
    }
}
