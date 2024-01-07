package com.idlegame.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.idlegame.objects.ImageURLs

class ImageViewModel : ViewModel() {
    private val _imageURLs = MutableLiveData<ImageURLs>()
    val imageURLs: LiveData<ImageURLs> = _imageURLs

    init {
        _imageURLs.value = ImageURLs()
    }
}
