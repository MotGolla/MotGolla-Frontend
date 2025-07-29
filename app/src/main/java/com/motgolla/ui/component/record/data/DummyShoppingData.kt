package com.motgolla.ui.component.record.data

import com.motgolla.domain.record.data.ShoppingRecordInfoDto
import com.motgolla.domain.record.data.response.ShoppingRecordInfoResponse

object DummyShoppingData {
    val shoppingRecords = listOf(
        ShoppingRecordInfoResponse(
            recordId = 1,
            state = "COMPLETE",
            imgUrl = "https://image.msscdn.net/thumbnails/images/goods_img/20250429/5076776/5076776_17494260822630_big.png?w=1200",
            productName = "헤비웨이트 원턱 버뮤다",
            brandName = "시티코지",
            brandFloor = "3F",
            productPrice = "19,900원"
        ),
        ShoppingRecordInfoResponse(
            recordId = 2,
            state = "AVAILABLE",
            imgUrl = "https://image.msscdn.net/thumbnails/images/goods_img/20250617/5191260/5191260_17501566559084_big.jpg?w=1200",
            productName = "뉴타운 스케이트 보더 반팔티",
            brandName = "엔티비씨",
            brandFloor = "4F",
            productPrice = "19,900원"
        )
    )
}