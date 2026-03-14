package br.com.celestialvip.models.entities;

public class PaymentStatus {
    private String paymentId;
    private String status;
    private String externalReference;

    public PaymentStatus() {}

    public PaymentStatus(String paymentId, String status, String externalReference) {
        this.paymentId = paymentId;
        this.status = status;
        this.externalReference = externalReference;
    }

    public String getPaymentId() { return paymentId; }

    public String getStatus() { return status; }

    public String getExternalReference() { return externalReference; }

    @Override
    public String toString() {
        return "Payment ID: " + paymentId + " | Status: " + status + " | External Reference: " + externalReference;
    }
}
