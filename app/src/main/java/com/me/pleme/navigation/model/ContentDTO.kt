package com.me.pleme.navigation.model
//설명, 이미지주소, UID, UserID, 컨텐츠 업로드 시간, 좋아요(중복 O),댓글 관리 class
data class ContentDTO (var explain : String? = null,                            // 컨텐츠 설명 관리
                       var imageUrl : String? = null,                           // 이미지 주소 관리
                       var uid : String? = null,                                // 어느 유저(UID) 관리
                       var userId : String? = null,                             // 이미지 관리
                       var timestamp : Long? = null,                            // 컨텐츠 업로드 시간 관리
                       var favoriteCount : Int? = 0,
                       var pokeCount : Int? = 0,
                       var pokerCount : Int? = 0,
                       var favorites : MutableMap<String,Boolean> = HashMap()){        // 좋아요(중복) 방지 - 유저 확인
    //댓글 관리 -(유저(UID), 이메일, 댓글, 댓글 시간)
    data class Comment(var uid : String? = null,
                       var userId : String? = null,
                       var comment: String? = null,
                       var timestamp : Long? = null)
}