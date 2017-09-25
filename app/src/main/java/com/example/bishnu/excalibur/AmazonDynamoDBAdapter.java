package com.example.bishnu.excalibur;

import android.content.Context;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

/**
 * Created by Bishnu.Reddy on 9/15/2017.
 */

public class AmazonDynamoDBAdapter {
    private static final String IDENTITY_POOL_ID = "us-east-1:058657d2-fb5f-4384-90dc-8a425f4e0257";
    private static AmazonDynamoDBClient amazonDynamoDBClient;

    private AmazonDynamoDBAdapter() {
    }

    public static AmazonDynamoDBClient getAmazonDynamoDBClient(Context context) {
        if (amazonDynamoDBClient == null) {
            amazonDynamoDBClient = new AmazonDynamoDBClient(getCredentialProvider(context));
            return amazonDynamoDBClient;
        } else {
            return amazonDynamoDBClient;
        }
    }

    public static CognitoCachingCredentialsProvider getCredentialProvider(Context context) {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                IDENTITY_POOL_ID // Identity pool ID
                , Regions.US_EAST_1 // Region
                ,new ClientConfiguration().withProtocol(Protocol.HTTPS)
        );
        return credentialsProvider;
    }
}
