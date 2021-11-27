package uz.gita.newsappdemo.utils

import androidx.room.TypeConverter
import uz.gita.newsappdemo.data.model.Source

class TypeConventors {

    @TypeConverter
    fun fromSource(source: Source) : String = source.name

    @TypeConverter
    fun toSource(name : String) : Source = Source(name,name)
}