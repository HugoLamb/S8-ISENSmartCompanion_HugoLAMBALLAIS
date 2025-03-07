package fr.isen.hugo.isensmartcompanion.models

import java.io.Serializable

data class EventModel(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val category: String): Serializable
