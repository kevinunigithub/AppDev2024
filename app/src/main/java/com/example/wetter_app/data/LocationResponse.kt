package com.example.wetter_app.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class LocationResponse(
    @JsonProperty("type")
    val type: String,
    @JsonProperty("features")
    val features: List<Feature>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Feature(
    @JsonProperty("type")
    val type: String,
    @JsonProperty("properties")
    val properties: Properties,
    @JsonProperty("geometry")
    val geometry: Geometry
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Properties(
    @JsonProperty("place_id")
    val placeId: String,
    @JsonProperty("osm_type")
    val osmType: String,
    @JsonProperty("osm_id")
    val osmId: String,
    @JsonProperty("display_name")
    val displayName: String,
    @JsonProperty("place_rank")
    val placeRank: Int,
    @JsonProperty("category")
    val category: String,
    @JsonProperty("type")
    val type: String,
    @JsonProperty("importance")
    val importance: Double
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Geometry(
    @JsonProperty("type")
    val type: String,
    @JsonProperty("coordinates")
    val coordinates: List<Double>
)
