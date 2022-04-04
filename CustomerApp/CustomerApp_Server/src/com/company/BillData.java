package com.company;

public class BillData {  //class contains info about a bill, so info from table Bill but also info about the client who got this bill, suchs as his first_name etc

    //from the Bill table
    private final int total;
    private final String dueDate;
    private final String status;

    //bill related data from other tabels such as Client_Info
    private final String firstName;
    private final String addressName;

    public BillData(String firstName, int total, String status, String addressName, String dueDate)
    {
        this.firstName = firstName;
        this.total = total;
        this.status = status;
        this.addressName = addressName;
        this.dueDate = dueDate;
    }


    public String getFirstName() {
        return firstName;
    }

    public int getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }

    public String  getAddressName() {
        return addressName;
    }

    public String getDueDate() {return dueDate;}

}
