package com.restaurant.be.restaurant.repository

import com.jillesvangurp.ktsearch.SearchClient
import com.jillesvangurp.ktsearch.parseHits
import com.jillesvangurp.ktsearch.search
import com.jillesvangurp.searchdsls.querydsl.ESQuery
import com.jillesvangurp.searchdsls.querydsl.SearchDSL
import com.jillesvangurp.searchdsls.querydsl.SortOrder
import com.jillesvangurp.searchdsls.querydsl.bool
import com.jillesvangurp.searchdsls.querydsl.exists
import com.jillesvangurp.searchdsls.querydsl.match
import com.jillesvangurp.searchdsls.querydsl.nested
import com.jillesvangurp.searchdsls.querydsl.range
import com.jillesvangurp.searchdsls.querydsl.sort
import com.jillesvangurp.searchdsls.querydsl.term
import com.jillesvangurp.searchdsls.querydsl.terms
import com.restaurant.be.restaurant.presentation.controller.dto.GetRestaurantsRequest
import com.restaurant.be.restaurant.presentation.controller.dto.Sort
import com.restaurant.be.restaurant.repository.dto.RestaurantEsDocument
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.DayOfWeek

@Repository
class RestaurantEsRepository(
    private val client: SearchClient
) {

    private val searchIndex = "restaurant"

    fun searchRestaurants(
        request: GetRestaurantsRequest,
        pageable: Pageable,
        restaurantIds: List<Long>?,
        bookmark: Boolean?
    ): Pair<List<RestaurantEsDocument>, List<Double>?> {
        val dsl = SearchDSL()
        val termQueries: MutableList<ESQuery> = mutableListOf()
        if (restaurantIds != null) {
            if (bookmark == true) {
                termQueries.add(
                    dsl.terms("id", *restaurantIds.map { it.toString() }.toTypedArray())
                )
            } else {
                termQueries.add(
                    dsl.bool {
                        mustNot(
                            dsl.terms("id", *restaurantIds.map { it.toString() }.toTypedArray())
                        )
                    }
                )
            }
        }

        if (!request.categories.isNullOrEmpty()) {
            termQueries.add(
                dsl.terms("category", *request.categories.toTypedArray())
            )
        }

        if (request.discountForSkku == true) {
            termQueries.add(
                dsl.exists("discount_content")
            )
        } else if (request.discountForSkku == false) {
            termQueries.add(
                dsl.bool {
                    mustNot(
                        dsl.exists("discount_content")
                    )
                }
            )
        }

        if (request.operationDay != null) {
            val dayOfWeek = when (request.operationDay) {
                DayOfWeek.MONDAY -> "월요일"
                DayOfWeek.TUESDAY -> "화요일"
                DayOfWeek.WEDNESDAY -> "수요일"
                DayOfWeek.THURSDAY -> "목요일"
                DayOfWeek.FRIDAY -> "금요일"
                DayOfWeek.SATURDAY -> "토요일"
                DayOfWeek.SUNDAY -> "일요일"
            }

            termQueries.add(
                createTimeBasedQuery(
                    request,
                    dayOfWeek,
                    dsl
                )
            )
        }

        if (request.hasWifi != null) {
            termQueries.add(
                dsl.match("facility_infos.wifi", if (request.hasWifi) "Y" else "N")
            )
        }

        if (request.hasPet != null) {
            termQueries.add(
                dsl.match("facility_infos.pet", if (request.hasPet) "Y" else "N")
            )
        }

        if (request.hasParking != null) {
            termQueries.add(
                dsl.match("facility_infos.parking", if (request.hasParking) "Y" else "N")
            )
        }

        if (request.hasNursery != null) {
            termQueries.add(
                dsl.match("facility_infos.nursery", if (request.hasNursery) "Y" else "N")
            )
        }

        if (request.hasSmokingRoom != null) {
            termQueries.add(
                dsl.match("facility_infos.smokingroom", if (request.hasSmokingRoom) "Y" else "N")
            )
        }

        if (request.hasDisabledFacility != null) {
            termQueries.add(
                dsl.match("facility_infos.fordisabled", if (request.hasDisabledFacility) "Y" else "N")
            )
        }

        if (request.hasAppointment != null) {
            termQueries.add(
                dsl.match("operation_infos.appointment", if (request.hasAppointment) "Y" else "N")
            )
        }

        if (request.hasDelivery != null) {
            termQueries.add(
                dsl.match("operation_infos.delivery", if (request.hasDelivery) "Y" else "N")
            )
        }

        if (request.hasPackagee != null) {
            termQueries.add(
                dsl.match("operation_infos.package", if (request.hasPackagee) "Y" else "N")
            )
        }

        if (request.kakaoRatingAvg != null) {
            termQueries.add(
                dsl.range("kakao_rating_avg") {
                    gte = request.kakaoRatingAvg
                }
            )
        }

        if (request.kakaoRatingCount != null) {
            termQueries.add(
                dsl.range("kakao_rating_count") {
                    gte = request.kakaoRatingCount
                }
            )
        }

        if (request.ratingAvg != null) {
            termQueries.add(
                dsl.range("rating_avg") {
                    gte = request.ratingAvg
                }
            )
        }
        if (request.reviewCount != null) {
            termQueries.add(
                dsl.range("review_count") {
                    gte = request.reviewCount
                }
            )
        }
        if (request.priceMax != null) {
            termQueries.add(
                dsl.nested {
                    path = "menus"
                    query = dsl.bool {
                        filter(
                            dsl.range("menus.price") {
                                lte = request.priceMax
                            }
                        )
                    }
                }
            )
        }
        if (request.priceMin != null) {
            termQueries.add(
                dsl.nested {
                    path = "menus"
                    query = dsl.bool {
                        filter(
                            dsl.range("menus.price") {
                                gte = request.priceMin
                            }
                        )
                    }
                }
            )
        }

        val result = runBlocking {
            val res = client.search(
                target = searchIndex,
                block = {
                    query = bool {
                        filter(
                            termQueries
                        )
                        if (!request.query.isNullOrEmpty()) {
                            should(
                                match("name", request.query) {
                                    boost = 0.1
                                },
                                match("category", request.query) {
                                    boost = 0.03
                                },
                                match("original_category", request.query) {
                                    boost = 0.03
                                },
                                nested {
                                    path = "menus"
                                    query = bool {
                                        should(
                                            match("menus.menu_name", request.query) {
                                                boost = 0.01
                                            },
                                            match("menus.description", request.query) {
                                                boost = 0.01
                                            }
                                        )
                                    }
                                }
                            )
                            minimumShouldMatch(1)
                        }
                        sort {
                            when (request.customSort) {
                                Sort.BASIC -> add("_score", SortOrder.DESC)
                                Sort.CLOSELY_DESC -> add("_geo_distance", SortOrder.ASC) {
                                    this["location"] = mapOf(
                                        "lat" to request.latitude,
                                        "lon" to request.longitude
                                    )
                                    this["unit"] = "m"
                                    this["mode"] = "min"
                                    this["distance_type"] = "arc"
                                }
                                Sort.RATING_DESC -> add("rating_avg", SortOrder.DESC)
                                Sort.REVIEW_COUNT_DESC -> add("review_count", SortOrder.DESC)
                                Sort.BOOKMARK_COUNT_DESC -> add("bookmark_count", SortOrder.DESC)
                                Sort.ID_ASC -> null
                            }

                            add("id", SortOrder.ASC)
                        }
                    }
                },
                size = pageable.pageSize,
                from = pageable.offset.toInt(),
                trackTotalHits = true
            )

            val nextCursor: List<Double>? = res.hits?.hits?.lastOrNull()?.sort?.mapNotNull {
                    jsonElement ->
                jsonElement.jsonPrimitive.contentOrNull?.toDouble()
            }

            Pair(res.parseHits<RestaurantEsDocument>(), nextCursor)
        }

        return result
    }

    private fun createTimeBasedQuery(
        request: GetRestaurantsRequest,
        dayOfWeek: String,
        dsl: SearchDSL
    ): ESQuery {
        return dsl.nested {
            path = "operation_times"
            query = dsl.bool {
                must(dsl.term("operation_times.day_of_the_week", dayOfWeek))

                request.operationStartTime?.let { searchTime ->
                    must(
                        dsl.bool {
                            must(
                                dsl.range("operation_times.operation_time_info.start_time.keyword") {
                                    lte = searchTime
                                },
                                dsl.bool {
                                    should(
                                        dsl.range("operation_times.operation_time_info.end_time.keyword") {
                                            gt = searchTime
                                        },
                                        dsl.term("operation_times.operation_time_info.end_time.keyword", "24:00")
                                    )
                                }
                            )
                        }
                    )
                }

                if (request.breakStartTime != null || request.breakEndTime != null) {
                    must(createBreakTimeQuery(request.breakStartTime, request.breakEndTime, dsl))
                }

                request.lastOrder?.let { lastOrderTime ->
                    must(createLastOrderQuery(lastOrderTime, dsl))
                }
            }
        }
    }

    private fun createBreakTimeQuery(
        breakStartTime: String?,
        breakEndTime: String?,
        dsl: SearchDSL
    ): ESQuery {
        return dsl.bool {
            should(
                dsl.bool {
                    mustNot(dsl.exists("operation_times.operation_time_info.break_start_time.keyword"))
                },
                dsl.bool {
                    breakStartTime?.let { startTime ->
                        must(
                            dsl.range("operation_times.operation_time_info.break_start_time.keyword") {
                                gte = startTime
                            }
                        )
                    }
                    breakEndTime?.let { endTime ->
                        must(
                            dsl.range("operation_times.operation_time_info.break_end_time.keyword") {
                                lte = endTime
                            }
                        )
                    }
                }
            )
        }
    }

    private fun createLastOrderQuery(
        lastOrderTime: String,
        dsl: SearchDSL
    ): ESQuery {
        return dsl.bool {
            should(
                dsl.bool {
                    mustNot(dsl.exists("operation_times.operation_time_info.last_order.keyword"))
                },
                dsl.bool {
                    must(
                        dsl.exists("operation_times.operation_time_info.last_order.keyword"),
                        dsl.range("operation_times.operation_time_info.last_order.keyword") {
                            gte = lastOrderTime
                        }
                    )
                }
            )
        }
    }
}
