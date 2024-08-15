package com.restaurant.be.point.domain.entity

import com.restaurant.be.point.domain.PointDetail
import com.restaurant.be.user.domain.entity.Member
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
    @JoinColumn(name = "member_id")
    var member: Member?,

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
        member: Member,
        pointDetail: PointDetail
    ) : this (
        member = member,
        deltaPoint = pointDetail.deltaPoint,
        detail = pointDetail.detail,
        currentPoint = getLastUserPoint(member) + pointDetail.deltaPoint
    )

    companion object {
        private fun getLastUserPoint(member: Member): Long {
            // ToDo : 갱신전 사용자의 마지막 포인트를 가져오는 로직 작성
            return 0
        }
    }
}
