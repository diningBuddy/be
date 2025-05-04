package com.restaurant.be.autocomplete.repository

import com.jillesvangurp.ktsearch.SearchClient
import com.jillesvangurp.ktsearch.parseHits
import com.jillesvangurp.ktsearch.search
import com.jillesvangurp.searchdsls.querydsl.*
import com.restaurant.be.autocomplete.repository.dto.AutoCompleteEsDocument
import com.restaurant.be.autocomplete.util.JamoUtils
import com.restaurant.be.autocomplete.util.KeyboardMapper
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Repository

@Repository
class AutoCompleteEsRepository(
    private val client: SearchClient
) {
    private val searchIndex = "autocomplete"
    private val maxSuggestions = 10
    private val MIN_INPUT_LENGTH = 2

    fun getSuggestions(prefix: String, maxSuggestions: Int = this.maxSuggestions): List<AutoCompleteEsDocument> {
        if (prefix.length < MIN_INPUT_LENGTH) return emptyList()

        val cleanedPrefix = prefix.lowercase()
        val chosung = JamoUtils.decomposeToJamo(cleanedPrefix) // 자소매핑
        val mappedHangul = KeyboardMapper.convert(cleanedPrefix) // 영한매핑 -> dbf ㅇㅠㄹ 이어서 매핑안됐는데? 자모 유틸로 변환해서 가능하게 바꿈
        // TODO 1.레디스 저장확인 2. 데이터 es 적재

        return runBlocking {
            val res = client.search(
                target = searchIndex,
                block = {
                    query = bool {
                        should(
                            matchPhrasePrefix("word", cleanedPrefix) {
                                boost = 10.0
                            },
                            match("word", cleanedPrefix) {
                                fuzziness = "AUTO"
                                boost = 1.0
                            },
                            matchPhrasePrefix("chosung", chosung) {
                                boost = 7.0
                            },
                            matchPhrasePrefix("mapped_hangul", mappedHangul) {
                                boost = 5.0
                            }
                        )
                        minimumShouldMatch(1)
                    }
                    sort {
                        add("_score", SortOrder.DESC)
                        add("frequency", SortOrder.DESC)
                    }
                },
                size = maxSuggestions
            )

            res.parseHits()
        }
    }
}
