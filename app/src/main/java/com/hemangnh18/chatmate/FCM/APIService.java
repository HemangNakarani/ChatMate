package com.hemangnh18.chatmate.FCM;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA8yzM1yY:APA91bF25INTXJEZIPsvRl5OeeGSFQIR-K6Psi-yREnUOiRNj_1D8RbiFv6siTFlJo_ZnOZD-rIIIc8DCChuE_EwzdzcSdW1jZzLf5Bmicd-V93p5dmJJd04dTt2jf76lAh0dnXpY8OD"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body SenderBox body);


}
