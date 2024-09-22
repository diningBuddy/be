package com.restaurant.be.common.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class SeparatorConverter : AttributeConverter<List<Any>, String> {
    override fun convertToDatabaseColumn(attribute: List<Any>): String {
        return attribute.map { it.toString() }.joinToString(",")
    }

    override fun convertToEntityAttribute(dbData: String): List<Any> {
        return dbData.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toList()
    }
}
