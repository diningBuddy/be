package com.restaurant.be.inquiry.repository

import com.restaurant.be.inquiry.domain.entity.Inquiry
import org.springframework.data.jpa.repository.JpaRepository

interface InquiryRepository : JpaRepository<Inquiry, Long>
