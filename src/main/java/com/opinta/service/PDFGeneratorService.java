package com.opinta.service;

public interface PDFGeneratorService {

    byte[] generateLabel(long id, long parcelId);

    byte[] generatePostpay(long id);
}
