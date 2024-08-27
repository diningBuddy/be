package com.restaurant.be.point.domain.entity

import com.restaurant.be.point.domain.PointDetail
import com.restaurant.be.user.domain.entity.User
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "point_logs")
class Point(
    @Id
    @GeneratedValue
    @Column(name = "id")
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User?,

    @Column(name = "delta_point")
    var deltaPoint: Long?,

    @Column(name = "detail")
    var detail: String,

    @Column(name = "current_point")
    var currentPoint: Long?,

    @CreatedDate
    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now()

) {
    constructor(
        user: User,
        pointDetail: PointDetail
    ) : this (
        user =user,
        deltaPoint = pointDetail.deltaPoint,
        detail = pointDetail.detail,
        currentPoint = getLastUserPoint(user) + pointDetail.deltaPoint
    )

    companion object {
        private fun getLastUserPoint(user: User): Long {
            // ToDo : 갱신전 사용자의 마지막 포인트를 가져오는 로직 작성
            return 0
        }
    }
}
