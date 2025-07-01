package com.example.arachismonitoring;

import java.io.Serializable;

public class DeliveryOrderRow implements Serializable {
    private String no;
    private String sifarisTarixi;
    private String sifarisAyi;
    private String ad;
    private String soyad;
    private String elaqeNomresi;
    private String mehsulNovu;
    private String packing;
    private String mehsulSayi;
    private String unvan;
    private String saat;
    private String sifarisStatusu;
    private String paymentStatus;

    public DeliveryOrderRow(String no, String sifarisTarixi, String sifarisAyi, String ad, String soyad, String elaqeNomresi, String mehsulNovu, String packing, String mehsulSayi, String unvan, String saat, String sifarisStatusu, String paymentStatus) {
        this.no = no;
        this.sifarisTarixi = sifarisTarixi;
        this.sifarisAyi = sifarisAyi;
        this.ad = ad;
        this.soyad = soyad;
        this.elaqeNomresi = elaqeNomresi;
        this.mehsulNovu = mehsulNovu;
        this.packing = packing;
        this.mehsulSayi = mehsulSayi;
        this.unvan = unvan;
        this.saat = saat;
        this.sifarisStatusu = sifarisStatusu;
        this.paymentStatus = paymentStatus;
    }

    public String getNo() { return no; }
    public String getSifarisTarixi() { return sifarisTarixi; }
    public String getSifarisAyi() { return sifarisAyi; }
    public String getAd() { return ad; }
    public String getSoyad() { return soyad; }
    public String getElaqeNomresi() { return elaqeNomresi; }
    public String getMehsulNovu() { return mehsulNovu; }
    public String getPacking() { return packing; }
    public String getMehsulSayi() { return mehsulSayi; }
    public String getUnvan() { return unvan; }
    public String getSaat() { return saat; }
    public String getSifarisStatusu() { return sifarisStatusu; }
    public String getPaymentStatus() { return paymentStatus; }
    
    public void setSifarisStatusu(String sifarisStatusu) { this.sifarisStatusu = sifarisStatusu; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
} 