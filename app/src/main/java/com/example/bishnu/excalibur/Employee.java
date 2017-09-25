package com.example.bishnu.excalibur;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Bishnu.Reddy on 9/21/2017.
 */
@DynamoDBTable(tableName = "Employees")
public class Employee {
    private String name;
    private String id;
    private String image;
    private String mobileNumber;
    private String skills;

    public Employee() {

    }

    public Employee(String name, String id, String image, String mobileNumber, String skills) {
        this.name = name;
        this.id = id;
        this.image = image;
        this.mobileNumber = mobileNumber;
        this.skills = skills;
    }

    @DynamoDBAttribute(attributeName = "Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBHashKey(attributeName = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBAttribute(attributeName = "Image")
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @DynamoDBAttribute(attributeName = "MobileNumber")
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @DynamoDBAttribute(attributeName = "Skills")
    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }
}
