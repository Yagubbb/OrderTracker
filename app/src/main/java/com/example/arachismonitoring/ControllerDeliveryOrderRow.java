package com.example.arachismonitoring;

import java.io.Serializable;

public class ControllerDeliveryOrderRow implements Serializable {
    private String no;
    private String sifarisTarixi;
    private String sifarisAyi;
    private String sifarisIli;
    private String ad;
    private String soyad;
    private String elaqeNomresi;
    private String mehsulNovu;
    private String packing;
    private String mehsulSayi;
    private String satisQiymeti;
    private String mayaDeyeri;
    private String xalisGəlir;
    private String xalisGəlirFaiz;
    private String ortaSifarisDeyeri;
    private String unvan;
    private String saat;
    private String sifarisStatusu;
    private String paymentStatus;

    public ControllerDeliveryOrderRow(String no, String sifarisTarixi, String sifarisAyi, String sifarisIli, String ad, String soyad, String elaqeNomresi, String mehsulNovu, String packing, String mehsulSayi, String satisQiymeti, String mayaDeyeri, String xalisGəlir, String xalisGəlirFaiz, String ortaSifarisDeyeri, String unvan, String saat, String sifarisStatusu, String paymentStatus) {
        this.no = no;
        this.sifarisTarixi = sifarisTarixi;
        this.sifarisAyi = sifarisAyi;
        this.sifarisIli = sifarisIli;
        this.ad = ad;
        this.soyad = soyad;
        this.elaqeNomresi = elaqeNomresi;
        this.mehsulNovu = mehsulNovu;
        this.packing = packing;
        this.mehsulSayi = mehsulSayi;
        this.satisQiymeti = satisQiymeti;
        this.mayaDeyeri = mayaDeyeri;
        this.xalisGəlir = xalisGəlir;
        this.xalisGəlirFaiz = xalisGəlirFaiz;
        this.ortaSifarisDeyeri = ortaSifarisDeyeri;
        this.unvan = unvan;
        this.saat = saat;
        this.sifarisStatusu = sifarisStatusu;
        this.paymentStatus = paymentStatus;
    }

    public String getNo() { return no; }
    public String getSifarisTarixi() { return sifarisTarixi; }
    public String getSifarisAyi() { return sifarisAyi; }
    public String getSifarisIli() { return sifarisIli; }
    public String getAd() { return ad; }
    public String getSoyad() { return soyad; }
    public String getElaqeNomresi() { return elaqeNomresi; }
    public String getMehsulNovu() { return mehsulNovu; }
    public String getPacking() { return packing; }
    public String getMehsulSayi() { return mehsulSayi; }
    public String getSatisQiymeti() { return satisQiymeti; }
    public String getMayaDeyeri() { return mayaDeyeri; }
    public String getXalisGəlir() { return xalisGəlir; }
    public String getXalisGəlirFaiz() { return xalisGəlirFaiz; }
    public String getOrtaSifarisDeyeri() { return ortaSifarisDeyeri; }
    public String getUnvan() { return unvan; }
    public String getSaat() { return saat; }
    public String getSifarisStatusu() { return sifarisStatusu; }
    public String getPaymentStatus() { return paymentStatus; }
} 