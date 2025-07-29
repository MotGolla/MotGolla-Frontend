package com.motgolla.domain.departmentstore.data

val departmentStoresByRegion = mapOf(
    "서울특별시" to listOf(
        listOf("더현대 서울", "현대백화점 압구정본점"),
        listOf("현대백화점 무역센터점", "현대백화점 천호점"),
        listOf("현대백화점 신촌점", "현대백화점 미아점"),
        listOf("현대백화점 목동점")
    ),
    "경기도" to listOf(
        listOf("현대백화점 중동점", "현대백화점 판교점"),
        listOf("현대백화점 킨텍스점")
    ),
    "경상도" to listOf(
        listOf("커넥트현대 부산", "더현대 대구"),
        listOf("현대백화점 울산점", "현대백화점 울산점 동구")
    ),
    "충청도" to listOf(
        listOf("현대백화점 충청점", "커넥트현대 청주")
    )
)


val departmentStoresIDByName = hashMapOf(
    "현대백화점 압구정본점" to 1L,
    "더현대 서울" to 2L,
    "더현대 대구" to 3L,
    "현대백화점 무역센터점" to 4L,
    "현대백화점 천호점" to 5L,
    "현대백화점 신촌점" to 6L,
    "현대백화점 미아점" to 7L,
    "현대백화점 목동점" to 8L,
    "현대백화점 중동점" to 9L,
    "현대백화점 킨텍스점" to 10L,
    "현대백화점 판교점" to 11L,
    "현대백화점 울산점" to 12L,
    "현대백화점 충청점" to 13L,
    "커넥트현대 부산" to 14L,
    "커넥트현대 청주" to 15L
)
