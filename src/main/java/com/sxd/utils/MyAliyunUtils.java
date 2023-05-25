package com.sxd.utils;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.google.gson.Gson;
import darabonba.core.client.ClientOverrideConfiguration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MyAliyunUtils {
    public static void sentMsg(String phone, String param) throws ExecutionException, InterruptedException {
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId("LTAI5tBNBvH5snKUuCnqTNBS")
                .accessKeySecret("SxL3wuRewOxU6J0ejRrRVY7a9Gxo2r")
                //.securityToken("<your-token>") // use STS token
                .build());

        AsyncClient client = AsyncClient.builder()
                .region("cn-hangzhou")
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dysmsapi.aliyuncs.com")
                )
                .build();


        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .signName("sxd994外卖公司")
                .phoneNumbers(phone)
                .templateCode("SMS_460905148")
                .templateParam("{\"code\":\""+param+"\"}")
                .build();

        CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
        SendSmsResponse resp = null;
        resp = response.get();

        System.out.println(new Gson().toJson(resp));

        client.close();

    }
}
