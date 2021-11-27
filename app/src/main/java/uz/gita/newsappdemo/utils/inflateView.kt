package uz.gita.newsappdemo.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun ViewGroup.inflateView(resLayout : Int) : View {
    return LayoutInflater.from(this.context).inflate(resLayout,this,false)
}