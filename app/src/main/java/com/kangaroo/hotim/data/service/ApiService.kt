package com.kangaroo.hotim.data.service

import com.kangaroo.hotim.data.model.params.TokenPostParams
import com.kangaroo.hotim.data.model.reponses.TokenPostResponse
import com.qdedu.baselibcommon.data.model.responses.BasicApiResult
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {



    /**
     * 自动生成：by WaTaNaBe on 2021-06-21 16:18.
     * #tokenPost#
     * #tokenPost#
     */
    @POST(ApiMethods.tokenPost)
    suspend fun tokenPostAsync(@Body params: TokenPostParams): Response<TokenPostResponse>



//#06#
}
