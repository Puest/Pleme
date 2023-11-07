package com.me.pleme.navigation.model
//설명, 이미지주소, UID, UserID, 컨텐츠 업로드 시간, 좋아요(중복 O),댓글 관리 class
data class PokeDTO (var destinationUid : String? = null,
                    var uid : String? = null,
                    var userId : String? = null,
                    var pokeCount : Int? = 0,
                    var pokerCount : Int? = 0,
                    var poke : Map<String,Boolean> = HashMap())