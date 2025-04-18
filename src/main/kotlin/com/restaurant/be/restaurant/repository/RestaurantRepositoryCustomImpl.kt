package com.restaurant.be.restaurant.repository

import com.querydsl.core.types.dsl.PathBuilderFactory
import com.querydsl.jpa.impl.JPAQueryFactory
import com.restaurant.be.category.domain.entity.QCategory.category
import com.restaurant.be.restaurant.domain.entity.QMenu.menu
import com.restaurant.be.restaurant.domain.entity.QRestaurant.restaurant
import com.restaurant.be.restaurant.domain.entity.QRestaurantBookmark.restaurantBookmark
import com.restaurant.be.restaurant.domain.entity.QRestaurantCategory.restaurantCategory
import com.restaurant.be.restaurant.domain.entity.RestaurantBookmark
import com.restaurant.be.restaurant.repository.dto.RestaurantProjectionDto
import com.restaurant.be.review.domain.entity.QReview.review
import com.restaurant.be.user.domain.entity.QUser.user
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class RestaurantRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory
) : RestaurantRepositoryCustom {
    override fun findDtoById(
        restaurantId: Long,
        userId: Long
    ): RestaurantProjectionDto? {
        val restaurantInfo =
            queryFactory
                .select(restaurant)
                .from(restaurant)
                .where(restaurant.id.eq(restaurantId))
                .fetchOne()

        val likedUsers =
            queryFactory
                .select(user.id)
                .from(restaurantBookmark)
                .leftJoin(user)
                .on(restaurantBookmark.userId.eq(user.id))
                .where(
                    restaurantBookmark.restaurantId
                        .eq(restaurantId)
                        .and(restaurantBookmark.userId.eq(userId))
                ).fetch()

        val menus =
            queryFactory
                .select(menu)
                .from(menu)
                .where(menu.restaurantId.eq(restaurantId))
                .fetch()

        val review =
            queryFactory
                .select(review)
                .from(review)
                .where(review.restaurantId.eq(restaurantId))
                .orderBy(review.likeCount.desc())
                .limit(1)
                .fetchOne()

        val categories =
            queryFactory
                .select(category)
                .from(restaurantCategory)
                .leftJoin(category)
                .on(restaurantCategory.category.id.eq(category.id))
                .where(restaurantCategory.restaurant.id.eq(restaurantId))
                .fetch()

        return if (restaurantInfo != null) {
            RestaurantProjectionDto(
                restaurantInfo,
                likedUsers.isNotEmpty(),
                menus,
                review,
                categories
            )
        } else {
            null
        }
    }

    override fun findDtoByIds(
        restaurantIds: List<Long>,
        userId: Long
    ): List<RestaurantProjectionDto> {
        if (restaurantIds.isEmpty()) {
            return emptyList()
        }

        val restaurantInfos =
            queryFactory
                .select(restaurant)
                .from(restaurant)
                .where(restaurant.id.`in`(restaurantIds))
                .fetch()

        val likedUsers =
            queryFactory
                .select(restaurantBookmark)
                .from(restaurantBookmark)
                .where(restaurantBookmark.userId.eq(userId))
                .fetch()

        val menus =
            queryFactory
                .select(menu)
                .from(menu)
                .where(menu.restaurantId.`in`(restaurantIds))
                .fetch()

        val reviews =
            queryFactory
                .select(review)
                .from(review)
                .where(review.restaurantId.`in`(restaurantIds))
                .orderBy(review.likeCount.desc())
                .fetch()

        val categories =
            queryFactory
                .select(restaurantCategory, category)
                .from(restaurantCategory)
                .leftJoin(category)
                .on(restaurantCategory.category.id.eq(category.id))
                .where(restaurantCategory.restaurant.id.`in`(restaurantIds))
                .fetch()

        val restaurantDtos =
            restaurantInfos.map { restaurantInfo ->
                val likedUserIds =
                    likedUsers.filter { it.restaurantId == restaurantInfo.id }.map { it.id }
                val menuList = menus.filter { it.restaurantId == restaurantInfo.id }
                val review = reviews.firstOrNull { it.restaurantId == restaurantInfo.id }
                val categoryList =
                    categories
                        .filter { it.get(restaurantCategory)?.restaurant!!.id == restaurantInfo.id }
                        .mapNotNull { it.get(category) }
                RestaurantProjectionDto(
                    restaurantInfo,
                    likedUserIds.isNotEmpty(),
                    menuList,
                    review,
                    categoryList
                )
            }

        return restaurantDtos
    }

    override fun findMyLikeRestaurants(
        userId: Long,
        pageable: Pageable
    ): Page<RestaurantProjectionDto> {
        val restaurantBookmarkPath = PathBuilderFactory().create(RestaurantBookmark::class.java)
        val orderSpecifier = listOf(restaurantBookmarkPath.getNumber("id", Long::class.java).desc())

        val myLikeQuery =
            queryFactory
                .select(restaurantBookmark.restaurantId)
                .from(restaurantBookmark)
                .where(restaurantBookmark.userId.eq(userId))

        val total = myLikeQuery.fetchCount()

        val restaurantIds =
            myLikeQuery
                .fetch()

        val restaurantInfos =
            queryFactory
                .select(restaurant)
                .from(restaurant)
                .where(restaurant.id.`in`(restaurantIds))
                .leftJoin(restaurantBookmark)
                .on(restaurant.id.eq(restaurantBookmark.restaurantId))
                .orderBy(*orderSpecifier.toTypedArray())
                .fetchJoin()
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetch()

        val menus =
            queryFactory
                .select(menu)
                .from(menu)
                .where(menu.restaurantId.`in`(restaurantIds))
                .fetch()

        val reviews =
            queryFactory
                .select(review)
                .from(review)
                .where(review.restaurantId.`in`(restaurantIds))
                .orderBy(review.likeCount.desc())
                .fetch()

        val categories =
            queryFactory
                .select(restaurantCategory, category)
                .from(restaurantCategory)
                .leftJoin(category)
                .on(restaurantCategory.category.id.eq(category.id))
                .where(restaurantCategory.restaurant.id.`in`(restaurantIds))
                .fetch()

        val restaurantDtos =
            restaurantInfos.distinctBy { it.id }.map { restaurantInfo ->
                val likedUserIds = restaurantIds.map { true }
                val menuList = menus.filter { it.restaurantId == restaurantInfo.id }
                val review = reviews.firstOrNull { it.restaurantId == restaurantInfo.id }
                val categoryList =
                    categories
                        .filter { it.get(restaurantCategory)?.restaurant!!.id == restaurantInfo.id }
                        .mapNotNull { it.get(category) }
                RestaurantProjectionDto(
                    restaurantInfo,
                    likedUserIds.isNotEmpty(),
                    menuList,
                    review,
                    categoryList
                )
            }

        return PageImpl(restaurantDtos, pageable, total)
    }
}
